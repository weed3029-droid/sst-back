package sst.content.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import sst.content.dto.FoodResponseDto;
import sst.content.dto.FoodUpdateRequestDto;

@Mapper
public interface PlaceFoodMapper {
    List<FoodResponseDto> findByRegion(@Param("rgnCd") Integer rgnCd);
    FoodResponseDto findById(@Param("plcNo") Long plcNo);
    
    int countFoodListByRegion(@Param("rgnCd") Integer rgnCd, @Param("keyword") String keyword);

    List<FoodResponseDto> findFoodListPaged(
            @Param("rgnCd") Integer rgnCd, 
            @Param("offset") int offset, 
            @Param("size") int size,
            @Param("keyword") String keyword
    );
    
    // 🚀 추가: PLACE 공통 테이블 업데이트
    int updatePlace(@Param("plcNo") Long plcNo, @Param("dto") FoodUpdateRequestDto dto);
    
    // 🚀 추가: PLACE_FOOD 상세 테이블 업데이트
    int updatePlaceFood(@Param("plcNo") Long plcNo, @Param("dto") FoodUpdateRequestDto dto);
    
}