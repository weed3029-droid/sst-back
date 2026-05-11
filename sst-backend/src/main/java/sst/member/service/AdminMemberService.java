package sst.member.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;
import sst.global.exception.CustomException;
import sst.global.exception.ErrorCode;
import sst.member.domain.Member;
import sst.member.mapper.MemberMapper;

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
    
    @Transactional
    public void withdrawMemberByAdmin(Long memberId) {
        // 1. 회원 존재 여부 확인
        Member member = memberMapper.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. 이미 탈퇴한 회원인지 확인
        if ("N".equals(member.getMbrUseYn())) {
            throw new CustomException(ErrorCode.ALREADY_PROCESSED);
        }

        // 🚀 3. DB 설계 원칙에 따른 마스킹 데이터 생성 (유닉스 타임스탬프 활용)
        long unixTime = System.currentTimeMillis();
        String maskedEmail = "del_" + memberId + "_" + unixTime + "@deleted.local";
        String maskedNickname = "del_" + memberId + "_" + unixTime;

        // 4. 탈퇴 쿼리 실행
        memberMapper.withdrawMember(memberId, maskedEmail, maskedNickname);
    }
    /**
     * 🚀 서버 단 페이징 처리 로직 (공통 DTO 적용)
     */
    @Transactional(readOnly = true)
    public PageResponse<Member> getMembersPaged(PageRequest pageRequest) {
        
        // 1. MyBatis에는 DTO의 필드값(offset, size)을 꺼내서 던집니다.
        List<Member> list = memberMapper.findAllMembersPaged(pageRequest.getOffset(), pageRequest.getSize());
        int total = memberMapper.countAllMembers();

        // 2. 공통 응답 DTO에 담아서 반환 (내부에서 totalPages 자동 계산됨)
        return new PageResponse<>(list, total, pageRequest);
    }
    
}