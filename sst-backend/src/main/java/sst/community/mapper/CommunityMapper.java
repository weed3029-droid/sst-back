package sst.community.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import sst.community.domain.Community;

@Mapper
public interface CommunityMapper {

	// 커뮤니티 게시글 목록 조회
    List<Community> selectCommunityList(@Param("catCd") String catCd);

    // 커뮤니티 게시글 상세 조회
    Community selectCommunityDetail(Long commNo);

    // 커뮤니티 게시글 등록
    void insertCommunity(Community community);

    // 게시글 수정
    int updateCommunity(Community community);
    
    // 커뮤니티 게시글 삭제
    int deleteCommunity(Long commNo);

}