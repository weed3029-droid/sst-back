package sst.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sst.community.domain.Community;
import sst.community.mapper.CommunityMapper;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCommunityService {

    private final CommunityMapper communityMapper;

    @Transactional(readOnly = true)
    public PageResponse<Community> getAdminCommunityListPaged(String catCd, String useYn, PageRequest pageRequest) {
        // 1. 전체 개수 조회
        int total = communityMapper.countAdminCommunityList(catCd, useYn, pageRequest.getKeyword());
        
        // 2. 페이징 데이터 조회
        List<Community> list = communityMapper.selectAdminCommunityListPaged(
                catCd, 
                useYn, 
                pageRequest.getKeyword(), 
                pageRequest.getOffset(), 
                pageRequest.getSize()
        );

        return new PageResponse<>(list, total, pageRequest);
    }

    @Transactional
    public void updateCommunityStatus(Long commNo, String useYn) {
        // 🚀 Mapper 네이밍 규칙 일치
        communityMapper.updateAdminCommunityStatus(commNo, useYn);
    }

    @Transactional
    public void deleteCommunity(Long commNo) {
        // 완전한 삭제가 필요한 경우 기존 deleteCommunity 사용
        communityMapper.deleteCommunity(commNo);
    }
}