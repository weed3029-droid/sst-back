package sst.content.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import sst.content.dto.SleepResponseDto;

@Mapper
public interface PlaceSleepMapper {
    List<SleepResponseDto> findByRegion(@Param("rgnCd") Integer rgnCd);
    
    // 🚀 1. 검색어(keyword) 파라미터 추가
    int countSleepListByRegion(
            @Param("rgnCd") Integer rgnCd, 
            @Param("keyword") String keyword
    );

    // 🚀 2. 검색어(keyword) 파라미터 추가
    List<SleepResponseDto> findSleepListPaged(
            @Param("rgnCd") Integer rgnCd, 
            @Param("offset") int offset, 
            @Param("size") int size,
            @Param("keyword") String keyword
    );

    SleepResponseDto findById(@Param("plcNo") Long plcNo);
    
}