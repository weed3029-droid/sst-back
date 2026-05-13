package sst.content.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import sst.content.dto.ReviewRequestDto;
import sst.content.dto.ReviewResponseDto;

@Mapper
public interface ReviewMapper {

    /** 리뷰 목록 조회 */
    List<ReviewResponseDto> selectReviews(Long plcNo);

    /** 리뷰 등록 */
    int insertReview(ReviewRequestDto dto);

    /** 리뷰 수정 */
    int updateReview(ReviewRequestDto dto);

    /** 리뷰 삭제 (소프트 딜리트) */
    int deleteReview(ReviewRequestDto dto);

    /** 평점 캐시 동기화 */
    int syncRatingCache(Long plcNo);
    
    List<ReviewResponseDto> findAdminReviewListPaged(@Param("offset") int offset, @Param("size") int size, @Param("keyword") String keyword, @Param("useYn") String useYn);
    int countAdminReviewList(@Param("keyword") String keyword, @Param("useYn") String useYn);
    int updateReviewUseYn(@Param("rvwNo") Long rvwNo, @Param("useYn") String useYn);
    Long findPlcNoByRvwNo(Long rvwNo);
}