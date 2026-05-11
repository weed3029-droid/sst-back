package sst.content.mapper;

import org.apache.ibatis.annotations.Mapper;
import sst.content.dto.ReviewRequestDto;
import sst.content.dto.ReviewResponseDto;
import java.util.List;

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
}