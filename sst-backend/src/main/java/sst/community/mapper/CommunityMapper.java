package sst.community.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import sst.community.domain.Community;
import sst.community.domain.CommunityFile;
import sst.community.dto.CommunityFileMapDto;
import sst.community.dto.PlaceCategoryDto;
import sst.community.dto.PlaceDto;
import sst.community.dto.RegionDto;

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
    
    // 커뮤니티 게시글 조회수 증가
    void updateViewCount(Long commNo);
    
    // 커뮤니티 게시글 좋아요 여부 확인
    int selectLikeCount(@Param("commNo") Long commNo, @Param("mbrId") Long mbrId);

    // 커뮤니티 게시글 좋아요 추가
    int insertCommunityLike(@Param("commNo") Long commNo, @Param("mbrId") Long mbrId);

    // 커뮤니티 게시글 좋아요 삭제
    int deleteCommunityLike(@Param("commNo") Long commNo, @Param("mbrId") Long mbrId);

    // 커뮤니티 게시글 좋아요 수 증가
    int increaseLikeCount(Long commNo);

    // 커뮤니티 게시글 좋아요 수 감소
    int decreaseLikeCount(Long commNo);
    
    // 해시태그 존재 여부 확인
    Long selectHashtagNo(String tagName);

    // 해시태그 등록
    void insertHashtag(String tagName);

    // 게시글 해시태그 연결
    void insertCommunityHashtag(
            @Param("commNo") Long commNo,
            @Param("tagNo") Long tagNo
    );
    
    // 게시글 좋아요 전체 삭제
    void deleteCommunityLikes(Long commNo);
    
    // 게시글 댓글 전체 삭제
    void deleteCommentsByCommunity(Long commNo);

    // 게시글 파일 매핑 전체 삭제
    void deleteCommunityFileMaps(Long commNo);

    // 게시글 해시태그 전체 삭제
    void deleteCommunityHashtags(Long commNo);
    
    // 파일 정보 등록
    void insertFile(CommunityFile file);
    
    // 커뮤니티 파일 매핑 등록
    void insertCommunityFileMap(CommunityFileMapDto map);
    
    // 게시글 이미지 목록 조회
    List<String> selectCommunityImages(Long commNo);
    
    // 게시글 대표 이미지 조회
    String selectCommunityImageUrl(Long commNo);

    // 게시글 파일 번호 목록 조회
    List<Long> selectCommunityFileNos(Long commNo);

    // 파일 정보 삭제
    void deleteFiles(@Param("fileNos") List<Long> fileNos);
    
    // 지역 조회
    List<RegionDto> selectRegionList();
    
    // 카테고리 조회
    List<PlaceCategoryDto> selectPlaceCategoryList();
    
    // 구제척인 장소 
    List<PlaceDto> selectPlaceList(
    	    @Param("rgnCd") Integer rgnCd,
    	    @Param("catCd") String catCd
    	);
}