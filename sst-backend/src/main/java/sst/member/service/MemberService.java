package sst.member.service;

import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import sst.global.exception.CustomException;
import sst.global.exception.ErrorCode;
import sst.global.utils.CookieUtil;
import sst.member.domain.Member;
import sst.member.dto.MemberUpdateRequest;
import sst.member.dto.PasswordChangeRequest;
import sst.member.mapper.MemberMapper;
import sst.uploads.domain.FileDomain;
import sst.uploads.service.FileService;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberMapper memberMapper;
	private final PasswordEncoder passwordEncoder;
	
	private final CookieUtil cookieUtil;
	private final FileService fileService;
	
	@Transactional
	public Member getMemberInfoByEmail(String email) {
        return memberMapper.findMemberByEmail(email)
                           .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
	
	@Transactional
	public void updateMemberInfo(Long mbrId, MemberUpdateRequest request) {
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

	    // 2. 프로필 이미지 처리
	    if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
	        // 파일 데이터 저장 및 DB 등록 완료
	        FileDomain savedFile = fileService.saveFile(request.getProfileImage(), "member", "profile");

	        // 3. 회원 정보에 파일 번호 최종 연결
	        memberMapper.updateMemberProfileFileNo(mbrId, savedFile.getFileNo());
	    }
	    
	    // 나중에 배경 파일 추가 시에도 동일한 패턴으로 아래에 추가하면 됩니다.
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
	
	@Transactional
    public void withdrawMember(Long mbrId, HttpServletResponse response) {
        // 1. DB 논리 삭제 및 마스킹 처리
        memberMapper.withdrawMember(mbrId);

        // 2. 🚀 프론트엔드 브라우저의 HttpOnly 쿠키(액세스/리프레시 토큰) 즉시 만료
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtil.deleteAccessTokenCookie().toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtil.deleteRefreshTokenCookie().toString());
    }
}
