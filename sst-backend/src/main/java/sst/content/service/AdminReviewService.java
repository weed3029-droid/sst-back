// 🚀 src/main/java/sst/content/service/AdminReviewService.java (신규 생성)
package sst.content.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sst.content.dto.ReviewResponseDto;
import sst.content.mapper.ReviewMapper;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReviewService {
    private final ReviewMapper reviewMapper;

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponseDto> getReviewsPaged(String useYn, PageRequest pageRequest) {
        int total = reviewMapper.countAdminReviewList(pageRequest.getKeyword(), useYn);
        List<ReviewResponseDto> list = reviewMapper.findAdminReviewListPaged(pageRequest.getOffset(), pageRequest.getSize(), pageRequest.getKeyword(), useYn);
        return new PageResponse<>(list, total, pageRequest);
    }

    @Transactional
    public void toggleReviewStatus(Long rvwNo, String useYn) {
        // 1. 상태 변경
        reviewMapper.updateReviewUseYn(rvwNo, useYn);
        // 🚀 2. 상태 변경 후 반드시 PLACE 테이블의 평점/리뷰수 동기화!
        Long plcNo = reviewMapper.findPlcNoByRvwNo(rvwNo);
        if (plcNo != null) reviewMapper.syncRatingCache(plcNo); 
    }
}