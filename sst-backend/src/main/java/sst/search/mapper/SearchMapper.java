package sst.search.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sst.community.domain.Community;
import sst.content.dto.PlaceCardDto;

@Mapper
public interface SearchMapper {
    // 장소 검색
    int countPlacesByKeyword(@Param("keyword") String keyword, @Param("category") String category, @Param("region") String region);
    List<PlaceCardDto> selectPlacesByKeywordPaged(@Param("keyword") String keyword, @Param("category") String category, @Param("region") String region, @Param("offset") int offset, @Param("size") int size);

    // 커뮤니티 검색
    int countCommunitiesByKeyword(@Param("keyword") String keyword);
    List<Community> selectCommunitiesByKeywordPaged(@Param("keyword") String keyword, @Param("offset") int offset, @Param("size") int size);
}