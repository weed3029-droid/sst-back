package sst.community.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sst.community.domain.Community;
import sst.community.domain.CommunityFile;
import sst.community.dto.CommunityFileMapDto;
import sst.community.dto.PlaceCategoryDto;
import sst.community.dto.PlaceDto;
import sst.community.dto.RegionDto;
import sst.community.mapper.CommunityMapper;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityMapper communityMapper;

    // 커뮤니티 게시글 목록 조회
    public List<Community> getCommunityList(String catCd) {
        return communityMapper.selectCommunityList(catCd);
    }
    
    // 커뮤니티 게시글 상세 조회
    public Community getCommunityDetail(Long commNo) {
        Community community = communityMapper.selectCommunityDetail(commNo);

        if (community != null) {
            List<String> images = communityMapper.selectCommunityImages(commNo);
            community.setImages(images);
        }

        return community;
    }
    
 // 커뮤니티 게시글 등록
    public void createCommunity(Community community) {

        communityMapper.insertCommunity(community); // 실제 DB INSERT 실행

        // 해시태그 등록
        if (community.getHashtags() != null) {
            for (String tagName : community.getHashtags()) {
                Long tagNo = communityMapper.selectHashtagNo(tagName);
                if (tagNo == null) {
                    communityMapper.insertHashtag(tagName);
                    tagNo = communityMapper.selectHashtagNo(tagName);
                }
                communityMapper.insertCommunityHashtag(community.getCommNo(), tagNo);
            }
        }
        // 파일 등록 + 커뮤니티 파일 매핑
        if (community.getFiles() != null) {
            for (int i = 0; i < community.getFiles().size(); i++) {
                CommunityFile file = community.getFiles().get(i);
                communityMapper.insertFile(file);
                
                // 여기서 file.getFileNo() 생성됨
                CommunityFileMapDto map = new CommunityFileMapDto();
                map.setCommNo(community.getCommNo());
                map.setFileNo(file.getFileNo());
                map.setSortOrdr(i);
                communityMapper.insertCommunityFileMap(map);
            }
        }
    }
    
    // 커뮤니티 게시글 수정
    public void modifyCommunity(Community community) {

        // 기존 대표 이미지 경로 조회
        String oldImageUrl = communityMapper.selectCommunityImageUrl(community.getCommNo());

        // 기존 파일 번호 목록 조회
        List<Long> oldFileNos = communityMapper.selectCommunityFileNos(community.getCommNo());

        // 기존 이미지와 새 이미지가 다르면 기존 실제 파일 삭제
        if (oldImageUrl != null 
                && !oldImageUrl.isBlank()
                && community.getCommMainImgUrl() != null
                && !oldImageUrl.equals(community.getCommMainImgUrl())) {

            String filePath = oldImageUrl.replace("/uploads/", "");
            java.io.File file = new java.io.File("uploads/" + filePath);

            if (file.exists()) {
                file.delete();
            }
        }

        // 게시글 제목/내용/대표이미지 수정
        communityMapper.updateCommunity(community);

        // 기존 해시태그 연결 전체 삭제
        communityMapper.deleteCommunityHashtags(community.getCommNo());

        // 새 해시태그 다시 연결
        if (community.getHashtags() != null) {
            for (String tagName : community.getHashtags()) {

                Long tagNo = communityMapper.selectHashtagNo(tagName);

                if (tagNo == null) {
                    communityMapper.insertHashtag(tagName);
                    tagNo = communityMapper.selectHashtagNo(tagName);
                }

                communityMapper.insertCommunityHashtag(community.getCommNo(), tagNo);
            }
        }

        // 기존 파일 매핑 삭제
        communityMapper.deleteCommunityFileMaps(community.getCommNo());

        // 기존 FILE 테이블 삭제
        if (oldFileNos != null && !oldFileNos.isEmpty()) {
            communityMapper.deleteFiles(oldFileNos);
        }

        // 새 파일 정보 등록 + 매핑
        if (community.getFiles() != null) {
            for (int i = 0; i < community.getFiles().size(); i++) {
                CommunityFile file = community.getFiles().get(i);

                communityMapper.insertFile(file);

                CommunityFileMapDto map = new CommunityFileMapDto();
                map.setCommNo(community.getCommNo());
                map.setFileNo(file.getFileNo());
                map.setSortOrdr(i);

                communityMapper.insertCommunityFileMap(map);
            }
        }
    }
    
    public void removeCommunity(Long commNo) {
    	 // 게시글 대표 이미지 경로 조회
	        String imageUrl = communityMapper.selectCommunityImageUrl(commNo);
	        // 실제 파일 삭제
	        if (imageUrl != null && !imageUrl.isBlank()) {
	            String filePath = imageUrl.replace("/uploads/", "");
	            java.io.File file = new java.io.File("uploads/" + filePath);
	            if (file.exists()) {
	                file.delete();
	            }
	        }
	
	        // FILE 테이블 삭제용 파일 번호 조회
	        List<Long> fileNos = communityMapper.selectCommunityFileNos(commNo);
	        // 좋아요 삭제
	        communityMapper.deleteCommunityLikes(commNo);
	        // 댓글 삭제
	        communityMapper.deleteCommentsByCommunity(commNo);
	        // 해시태그 연결 삭제
	        communityMapper.deleteCommunityHashtags(commNo);
	        // 파일 매핑 삭제
	        communityMapper.deleteCommunityFileMaps(commNo);
	        // FILE 테이블 삭제
	        if (fileNos != null && !fileNos.isEmpty()) {
	            communityMapper.deleteFiles(fileNos);
	        }
	        // 게시글 삭제
	        communityMapper.deleteCommunity(commNo);
	    }
    
    // 커뮤니티 게시글 조회수 증가
    public void increaseViewCount(Long commNo) {
        communityMapper.updateViewCount(commNo);
    }
    
    // 커뮤니티 게시글 좋아요 처리
    public boolean toggleLike(Long commNo, Long mbrId) {
        // 이미 좋아요 눌렀는지 확인
        int likeCount = communityMapper.selectLikeCount(commNo, mbrId);
        // 좋아요 이미 눌렀으면 취소
        if (likeCount > 0) {
            communityMapper.deleteCommunityLike(commNo, mbrId);
            // 게시글 좋아요 수 감소
            communityMapper.decreaseLikeCount(commNo);
            return false;
        }
        // 좋아요 안 눌렀으면 추가
        communityMapper.insertCommunityLike(commNo, mbrId);
        // 게시글 좋아요 수 증가
        communityMapper.increaseLikeCount(commNo);
        return true;
    }
    
    // 지역 조회
    public List<RegionDto> getRegionList() {
        return communityMapper.selectRegionList();
    }
    
    // 카테고리 조회
    public List<PlaceCategoryDto> getPlaceCategoryList() {
        return communityMapper.selectPlaceCategoryList();
    }
    
    // 구제척인 장소
    public List<PlaceDto> getPlaceList(Integer rgnCd, String catCd) {
        return communityMapper.selectPlaceList(rgnCd, catCd);
    }
}