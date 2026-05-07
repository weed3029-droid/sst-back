package sst.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sst.global.exception.CustomException;
import sst.global.exception.ErrorCode;
import sst.member.domain.Member;
import sst.member.dto.MemberUpdateRequest;
import sst.member.dto.PasswordChangeRequest;
import sst.member.mapper.MemberMapper;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberMapper memberMapper;
	private final PasswordEncoder passwordEncoder;
	
	@Transactional
	public Member getMemberInfoByEmail(String email) {
        return memberMapper.findMemberByEmail(email)
                           .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
	
	@Transactional
    public void updateMemberInfo(Long mbrId, MemberUpdateRequest request) {
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
}
