package sst.content.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import sst.content.dto.FoodResponseDto;

import java.util.List;

@Mapper
public interface PlaceFoodMapper {
    List<FoodResponseDto> findByRegion(@Param("rgnCd") Integer rgnCd);
    FoodResponseDto findById(@Param("plcNo") Long plcNo);
}