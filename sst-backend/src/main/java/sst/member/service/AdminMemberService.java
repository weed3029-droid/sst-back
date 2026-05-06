package sst.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sst.member.domain.Member;
import sst.member.mapper.MemberMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

    // 🚀 기존 MemberMapper를 그대로 재사용하여 응집도를 높임
    private final MemberMapper memberMapper;

    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        // TODO: MemberMapper에 회원 목록 전체 조회 쿼리 추가 (필요시 검색 조건, 페이징 추가)
        return memberMapper.findAllMembers(); 
    }

//    @Transactional
//    public void forceDeleteMember(Long mbrId) {
//        // 🚀 정책: 관리자 삭제도 MBR_USE_YN = 'N' 처리 및 개인정보 마스킹 (소프트 삭제)
//        // TODO: MemberMapper에 강제 탈퇴용 update 쿼리 추가
//        memberMapper.updateMemberUseYnToNo(mbrId);
//    }
}