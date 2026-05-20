package sst.member.service;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sst.global.exception.CustomException;
import sst.global.exception.ErrorCode;
import sst.global.files.domain.FileDomain;
import sst.global.files.dto.FileUploadResult;
import sst.global.files.mapper.FileMapper;
import sst.global.files.storage.FileStorage;
import sst.global.utils.CookieUtil;
import sst.member.domain.Member;
import sst.member.dto.MemberUpdateRequest;
import sst.member.dto.PasswordChangeRequest;
import sst.member.dto.WithdrawalRequest;
import sst.member.mapper.MemberMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberMapper memberMapper;
	private final PasswordEncoder passwordEncoder;
	
	private final CookieUtil cookieUtil;
	
	private final FileMapper fileMapper;
	//private final FileServiceComponent fileProvider;
	private final FileStorage fileStorage;
	
	@Transactional
	public Member getMemberInfoByEmail(String email) {
        return memberMapper.findMemberByEmail(email)
                           .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
	
	@Transactional
	public Member getMemberInfoById(Long mbrId) {
		return memberMapper.findMemberById(mbrId)
				.orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
	}
	
	/**
	 * 회원 정보 수정 
	 */
	@Transactional
	public void updateMemberInfo(Long mbrId, MemberUpdateRequest request) {
		Member currentMember = memberMapper.findMemberById(mbrId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		
	    // 1. 일반 회원 정보(텍스트) 먼저 업데이트
	    Member updateParam = Member.builder()
	            .mbrId(mbrId)
	            .mbrName(request.getMbrName())
	            .mbrNickname(request.getMbrNickname())
	            .mbrTelno(request.getMbrTelno())
	            .mbrZip(request.getMbrZip())
	            .mbrAddr(request.getMbrAddr())
	            .mbrDaddr(request.getMbrDaddr())
	            .build();
	            
	    memberMapper.updateMemberInfo(updateParam);
	    // 아이디 
	    // 값 -> 맞다면 지우고

	    // 2. 프로필 이미지 처리 (Fluent API 활용)
	    if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
	        // 기존 파일 경로 가져오기
	        String oldProfilePath = currentMember.getMbrProfileIcon() != null 
	                ? currentMember.getMbrProfileIcon().getFilePath() : null;

	        fileStorage.setup("member")
	            .subPath("profile")
	            .allow(List.of("jpg", "jpeg", "png", "webp"))
	            .maxSize("10MB") // 문자열로 가독성 있게 설정 가능
	            .onSuccess(context -> {
	            	// 저장된 파일
	                FileUploadResult saved = context.getSavedFiles().get(0);
	                // 삭제된 파일
	                FileUploadResult removed = context.getDeletedFiles().get(0);
	                
	                
	                FileDomain fileDomain = FileDomain.builder()
	                        .fileOrgNm(saved.getFileOrgNm())
	                        .fileSaveNm(saved.getFileSaveNm())
	                        .filePath(saved.getFilePath())
	                        .fileExt(saved.getFileExt())
	                        .fileSize(saved.getFileSize())
	                        .fileMimeType(saved.getContentType())
	                        .fileType("IMAGE")
	                        .build();
	                
	                fileMapper.insertFile(fileDomain);
	                
	                if(fileDomain.getFileNo() != null) {
	                    memberMapper.updateMemberProfileFileNo(mbrId, fileDomain.getFileNo());
	                }
	            })
	            .updateFile(request.getProfileImage(), oldProfilePath); // [수정] 메서드명 확인
	    }

	    // 3. 배경 이미지 처리 (절차적 후처리 방식)
	    if (request.getBackgroundImage() != null && !request.getBackgroundImage().isEmpty()) {
	        String oldProfilePath = currentMember.getMbrProfileBg() != null 
	                ? currentMember.getMbrProfileBg().getFilePath() : null;

	        try {
	            // [Step 1] 파일 실행 및 결과 리턴
	            List<FileUploadResult> results = fileStorage.setup("member")
	                    .subPath("profile/bg")
	                    .allow(List.of("jpg", "jpeg", "png", "webp"))
	                    .maxSize("10MB")
	                    .replaceFile(request.getBackgroundImage(), oldProfilePath);

	            // [Step 2] 성공 시 조건문 처리
	            if (results != null && !results.isEmpty()) {
	                FileUploadResult result = results.get(0);
	                
	                FileDomain fileDomain = FileDomain.builder()
	                        .fileOrgNm(result.getFileOrgNm())
	                        .fileSaveNm(result.getFileSaveNm())
	                        .filePath(result.getFilePath())
	                        .fileExt(result.getFileExt())
	                        .fileSize(result.getFileSize())
	                        .fileMimeType(result.getContentType())
	                        .fileType("IMAGE")
	                        .build();
	                
	                // DB 데이터 추가
	                fileMapper.insertFile(fileDomain);
	                
	                // 회원 배경 이미지 정보 업데이트
	                if (fileDomain.getFileNo() != null) {
	                    memberMapper.updateMemberProfileBgFileNo(mbrId, fileDomain.getFileNo());
	                }
	                
	                log.info("배경 이미지 업로드 및 DB 갱신 성공: {}", result.getFilePath());
	            }

	        } catch (Exception e) {
	            // [Step 3] 실패 시 처리 로직 (onFailure 역할을 수행)
	            log.error("배경 이미지 처리 중 오류 발생: {}", e.getMessage());
	            
	            // 필요 시 사용자 정의 예외를 다시 던져 트랜잭션 롤백 유도
	            throw new RuntimeException("파일 처리 실패로 인해 작업을 중단합니다.", e);
	        }
	    }
        
        
        
	}
	
	@Transactional
	public void changePassword(String email, PasswordChangeRequest request) {
	    // 이메일로 회원 조회
	    Member member = memberMapper.findMemberByEmail(email)
	            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

	    // 소셜 로그인(KAKAO) 가입자는 비밀번호 변경 불가
	    if (!"LOCAL".equals(member.getMbrProviderCd())) {
	        throw new CustomException(ErrorCode.BAD_REQUEST); 
	    }

	    // 기존 비밀번호 검증
	    if (!passwordEncoder.matches(request.getCurrentPassword(), member.getMbrPassword())) {
	        throw new CustomException(ErrorCode.PASSWORD_MISMATCH); 
	    }

	    // 새 비밀번호 암호화 후 DB 업데이트
	    String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
	    
	    memberMapper.updatePassword(member.getMbrId(), encodedNewPassword);
	}
	
	/**
     *  일반 회원 자진 탈퇴 (탈퇴 사유 수집 및 마스킹)
     */
	@Transactional
    public void withdrawMember(Long mbrId, WithdrawalRequest request, HttpServletResponse response) {
        
        //  1. 백엔드에서 안전하게 마스킹 데이터 직접 생성 (유닉스 타임스탬프 활용)
        long unixTime = System.currentTimeMillis();
        String maskedEmail = "del_" + mbrId + "_" + unixTime + "@deleted.local";
        String maskedNickname = "del_" + mbrId + "_" + unixTime;

        //  2. DB 논리 삭제 및 마스킹 처리 (DTO가 아닌 위에서 생성한 로컬 변수 사용)
        memberMapper.withdrawMember(mbrId, maskedEmail, maskedNickname);

        //  3. 탈퇴 이력(사유) INSERT 
        // 프론트엔드에서 빈 값이 넘어오거나 에러가 발생할 경우를 대비한 방어 로직 추가
        if (request != null) {
            memberMapper.insertWithdrawalLog(mbrId, request.getReasonCd(), request.getReasonDesc());
        }

        //  4. 프론트엔드 브라우저의 HttpOnly 쿠키 즉시 만료 처리
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtil.deleteAccessTokenCookie().toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtil.deleteRefreshTokenCookie().toString());
    }
	
	@Transactional // 🚀 상태 업데이트와 로그 저장이 하나의 트랜잭션으로 묶임
    public void updateMemberStatus(Long memberId, String useYn, String reason, Long adminId) {
        // 1. 순수하게 상태값만 변경하는 Mapper 호출
        int result = memberMapper.updateMemberStatusByAdmin(memberId, useYn);
        if (result == 0) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        
        // 🚀 2. 상태 변경 이력 저장 (방금 만든 쿼리 호출)
        memberMapper.insertMemberStatusLog(memberId, adminId, useYn, reason);
    }
}
