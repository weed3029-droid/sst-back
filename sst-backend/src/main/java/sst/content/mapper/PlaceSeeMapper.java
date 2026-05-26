package sst.content.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import sst.content.dto.SeeResponseDto;
import sst.content.dto.SeeUpdateRequestDto;

/**
 * 볼거리 MyBatis Mapper 인터페이스
 * - PlaceSeeMapper.xml의 SQL과 연결되는 인터페이스
 * - Service에서 호출 → XML의 SQL 실행 → DB 조회
 */


@Mapper
public interface PlaceSeeMapper {
    List<SeeResponseDto> findByRegion(@Param("rgnCd") Integer rgnCd);
    SeeResponseDto findById(@Param("plcNo") Long plcNo);
    
   
    int countSeeListByRegion(@Param("rgnCd") Integer rgnCd, @Param("keyword") String keyword);
    
    List<SeeResponseDto> findSeeListPaged(
            @Param("rgnCd") Integer rgnCd, 
            @Param("offset") int offset, 
            @Param("size") int size,
            @Param("keyword") String keyword
    );
    
    // 🚀 추가: PLACE 공통 테이블 업데이트
    int updatePlace(@Param("plcNo") Long plcNo, @Param("dto") SeeUpdateRequestDto dto);
    
    // 🚀 추가: PLACE_SEE 상세 테이블 업데이트
    int updatePlaceSee(@Param("plcNo") Long plcNo, @Param("dto") SeeUpdateRequestDto dto);
    
    int countSeeListByRegion(@Param("rgnCd") Integer rgnCd, @Param("keyword") String keyword, @Param("useYn") String useYn);
    
    List<SeeResponseDto> findSeeListPaged(
            @Param("rgnCd") Integer rgnCd, 
            @Param("offset") int offset, 
            @Param("size") int size,
            @Param("keyword") String keyword,
            @Param("useYn") String useYn
    );

    // 🚀 3. 상태 업데이트 Mapper 선언 (XML에서 이 아이디로 쿼리 작성 필요)
    int updatePlaceUseYn(@Param("plcNo") Long plcNo, @Param("useYn") String useYn);
    
}