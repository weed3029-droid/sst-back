package sst.content.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import sst.content.dto.SleepResponseDto;

@Mapper
public interface PlaceSleepMapper {
    List<SleepResponseDto> findByRegion(@Param("rgnCd") Integer rgnCd);
    SleepResponseDto findById(@Param("plcNo") Long plcNo);
}