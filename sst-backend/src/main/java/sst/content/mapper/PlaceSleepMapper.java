package sst.content.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import sst.content.dto.SleepResponseDto;

@Mapper
public interface PlaceSleepMapper {
    List<SleepResponseDto> findByRegion(@Param("rgnCd") Integer rgnCd);
    
    // 🚀 1. 조건에 맞는 전체 데이터 개수 조회
    int countSleepListByRegion(@Param("rgnCd") Integer rgnCd);

    // 🚀 2. 조건에 맞고 페이징(LIMIT)이 적용된 데이터 목록 조회
    List<SleepResponseDto> findSleepListPaged(
            @Param("rgnCd") Integer rgnCd, 
            @Param("offset") int offset, 
            @Param("size") int size
    );

    SleepResponseDto findById(@Param("plcNo") Long plcNo);
    
}