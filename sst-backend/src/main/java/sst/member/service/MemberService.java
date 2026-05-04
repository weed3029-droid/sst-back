package sst.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sst.global.exception.CustomException;
import sst.global.exception.ErrorCode;
import sst.member.domain.Member;
import sst.member.dto.MemberUpdateRequest;
import sst.member.mapper.MemberMapper;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberMapper memberMapper;
	
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
}
