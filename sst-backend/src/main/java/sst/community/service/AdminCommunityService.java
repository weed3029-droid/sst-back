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
    public PageResponse<Community> getListPage(String catCd, PageRequest pageRequest) {
        int total = communityMapper.countAdminCommunityList(catCd, pageRequest.getKeyword());
        List<Community> list = communityMapper.selectAdminCommunityListPaged(
                catCd, pageRequest.getOffset(), pageRequest.getSize(), pageRequest.getKeyword()
        );
        return new PageResponse<>(list, total, pageRequest);
    }

    @Transactional
    public void deleteCommunity(Long commNo) {
        // 🚀 소프트 삭제 처리 (DB에서 물리적 삭제가 아닌 COMM_USE_YN='N' 갱신)
        communityMapper.deleteCommunity(commNo);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<Community> getListPageByCategory(String catCd, String useYn, PageRequest pageRequest) {
        
        int total = communityMapper.countCommunityList(catCd, pageRequest.getKeyword(), useYn);
        
        List<Community> list = communityMapper.findCommunityListPaged(
                catCd, pageRequest.getOffset(), pageRequest.getSize(), pageRequest.getKeyword(), useYn
        );
        return new PageResponse<>(list, total, pageRequest);
    }

    @Transactional
    public void updateCommunityUseYn(Long commNo, String useYn) {
        int result = communityMapper.updateCommunityUseYn(commNo, useYn);
        if (result == 0) {
            throw new RuntimeException("해당 게시글을 찾을 수 없거나 상태 변경에 실패했습니다."); 
        }
    }
}