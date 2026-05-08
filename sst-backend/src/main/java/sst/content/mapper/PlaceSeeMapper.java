package sst.content.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import sst.content.dto.SeeResponseDto;

import java.util.List;

/**
 * 볼거리 MyBatis Mapper 인터페이스
 * - PlaceSeeMapper.xml의 SQL과 연결되는 인터페이스
 * - Service에서 호출 → XML의 SQL 실행 → DB 조회
 */


@Mapper
public interface PlaceSeeMapper {
    List<SeeResponseDto> findByRegion(@Param("rgnCd") Integer rgnCd);
    SeeResponseDto findById(@Param("plcNo") Long plcNo);
}