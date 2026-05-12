package sst.content.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import sst.content.dto.PlayResponseDto;

@Mapper
public interface PlacePlayMapper {
    List<PlayResponseDto> findByRegion(@Param("rgnCd") Integer rgnCd);
    PlayResponseDto findById(@Param("plcNo") Long plcNo);
    
    int countPlayListByRegion(@Param("rgnCd") Integer rgnCd, @Param("keyword") String keyword);
    
    List<PlayResponseDto> findPlayListPaged(
            @Param("rgnCd") Integer rgnCd, 
            @Param("offset") int offset, 
            @Param("size") int size,
            @Param("keyword") String keyword
    );
}