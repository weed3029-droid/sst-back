package sst.community.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import sst.community.domain.Community;
import sst.community.dto.CommunityDto;
import sst.community.dto.PlaceCategoryDto;
import sst.community.dto.PlaceDto;
import sst.community.dto.RegionDto;
import sst.community.service.CommunityService;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;

@RestController
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    // 사용자가 선택한 카테고리에 맞는 게시판 목록 조회
    @GetMapping("/api/community")
    public PageResponse<Community> getCommunityList(
            @RequestParam("catCd") String catCd,
            PageRequest pageRequest
    ) {
        return communityService.getCommunityList(catCd, pageRequest);
    }
    
    // 인기 해시태그 TOP5 조회
    @GetMapping("/api/community/popular-hashtags")
    public List<String> getPopularHashtags(
            @RequestParam("catCd") String catCd
    ) {
        return communityService.getPopularHashtags(catCd);
    }
    
    // 커뮤니티 게시글 상세 조회
    @GetMapping("/api/community/{commNo}")
    public Community getCommunityDetail(@PathVariable("commNo") Long commNo) {
        return communityService.getCommunityDetail(commNo);
    }
    
    // 커뮤니티 게시글 조회수 증가 - 세션 기준 중복 방지
    @PutMapping("/api/community/{commNo}/view")
    public void increaseViewCount(
            @PathVariable("commNo") Long commNo,
            HttpSession session
    ) {
        String viewKey = "VIEWED_COMMUNITY_" + commNo;

        if (session.getAttribute(viewKey) == null) {
            communityService.increaseViewCount(commNo);
            session.setAttribute(viewKey, true);
        }
    }
    
    // 커뮤니티 게시글 좋아요 여부 조회
    @GetMapping("/api/community/{commNo}/like")
    public boolean isLiked(
            @PathVariable("commNo") Long commNo,
            @RequestParam("mbrId") Long mbrId
    ) {
        return communityService.isLiked(commNo, mbrId);
    }
    
    // 게시글 목록 좋아요 여부 조회
    @GetMapping("/api/community/likes")
    public List<Long> getLikedCommunityNos(
            @RequestParam("commNos") List<Long> commNos,
            @RequestParam("mbrId") Long mbrId
    ) {
        return communityService.getLikedCommunityNos(commNos, mbrId);
    }
    
    // 커뮤니티 게시글 삭제
    @DeleteMapping("/api/community/{commNo}")
    public void deleteCommunity(@PathVariable("commNo") Long commNo) {
        communityService.deleteCommunity(commNo);
    }
    
    // 커뮤니티 게시글 좋아요 처리
    @PostMapping("/api/community/{commNo}/like")
    public boolean toggleLike(
            @PathVariable("commNo") Long commNo,
            @RequestParam("mbrId") Long mbrId) {

        return communityService.toggleLike(commNo, mbrId);
    }
    
    // 지역 조회
    @GetMapping("/api/regions")
    public List<RegionDto> getRegionList() {
        return communityService.getRegionList();
    }
    
    // 카테고리 조회
    @GetMapping("/api/place-categories")
    public List<PlaceCategoryDto> getPlaceCategoryList() {
        return communityService.getPlaceCategoryList();
    }
    
    // 구제적인 장소
    @GetMapping("/api/places")
    public List<PlaceDto> getPlaceList(
            @RequestParam("rgnCd") Integer rgnCd,
            @RequestParam("catCd") String catCd
    ) {
        return communityService.getPlaceList(rgnCd, catCd);
    }

    // 등록 multipart
    @PostMapping(value = "/api/community", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createCommunity(
            @RequestPart("community") CommunityDto communityDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        communityService.createCommunity(communityDto, files);
    }
    
    // 수정 multipart
    @PutMapping(value = "/api/community/{commNo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateCommunity(
            @PathVariable("commNo") Long commNo,
            @RequestPart("community") CommunityDto communityDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        communityDto.setCommNo(commNo);
        communityService.updateCommunity(communityDto, files);
    }
    
}