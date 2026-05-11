package sst.community.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import sst.community.domain.Community;

@Mapper
public interface CommunityMapper {

    List<Community> selectCommunityList(@Param("catCd") String catCd);
    
    Community selectCommunityDetail(Long commNo);

}