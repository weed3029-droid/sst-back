package sst.plan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sst.plan.dto.PlaceResponseDto;

import java.util.List;

@Mapper
public interface AiPlanMapper {

    // 지역코드 + 테마코드 목록으로 장소 조회
    List<PlaceResponseDto> findPlacesByRegionAndThemes(
        @Param("rgnCd")      Integer      rgnCd,
        @Param("themeCodes") List<String> themeCodes
    );

    // 지역명 -> 지역코드 조회
    Integer findRgnCdByRgnName(@Param("rgnName") String rgnName);

    // 테마명 -> 테마코드 목록 조회
    List<String> findThemeCodesByNames(@Param("themeNames") List<String> themeNames);

    // 장소별 태그 조회
    List<String> findThemeCodesByPlcNo(@Param("plcNo") Long plcNo);

    // 태그코드 -> 태그명 목록 조회
    List<String> findThemeNamesByCodes(@Param("themeCodes") List<String> themeCodes);
}