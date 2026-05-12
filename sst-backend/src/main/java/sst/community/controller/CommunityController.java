package sst.community.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import sst.community.domain.Community;
import sst.community.domain.CommunityFile;
import sst.community.dto.PlaceCategoryDto;
import sst.community.dto.PlaceDto;
import sst.community.dto.RegionDto;
import sst.community.service.CommunityService;

@RestController
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    // 사용자가 선택한 카테고리에 맞는 게시판 목록 조회
    @GetMapping("/api/community")
    public List<Community> communityList(@RequestParam("catCd") String catCd) {
        return communityService.getCommunityList(catCd);
    }
    
    // 커뮤니티 게시글 상세 조회
    @GetMapping("/api/community/{commNo}")
    public Community communityDetail(@PathVariable("commNo") Long commNo) {
        return communityService.getCommunityDetail(commNo);
    }
    
    // 커뮤니티 게시글 조회수 증가
    @PutMapping("/api/community/{commNo}/view")
    public void increaseViewCount(@PathVariable("commNo") Long commNo) {
        communityService.increaseViewCount(commNo);
    }
    
    // 커뮤니티 게시글 등록
    @PostMapping("/api/community")
    public void createCommunity(@RequestBody Community community) {
        communityService.createCommunity(community);
    }

    // 커뮤니티 게시글 등록 + 이미지 업로드
    @PostMapping(value = "/api/community/with-images", consumes = "multipart/form-data")
    public void createCommunityWithImages(
            @RequestPart("community") Community community,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {

        List<CommunityFile> files = saveCommunityImages(images);

        if (!files.isEmpty()) {
            community.setCommMainImgUrl(files.get(0).getFilePath());
            community.setFiles(files);
        }

        communityService.createCommunity(community);
    }
    
    // 커뮤니티 게시글 수정
    @PutMapping("/api/community/{commNo}")
    public void modifyCommunity(
            @PathVariable("commNo") Long commNo,
            @RequestBody Community community) {
    	
    	// URL의 게시글 번호를 DTO에 세팅
        community.setCommNo(commNo);

        communityService.modifyCommunity(community);
    }

    // 커뮤니티 게시글 수정 + 이미지 업로드
    @PutMapping(value = "/api/community/{commNo}/with-images", consumes = "multipart/form-data")
    public void modifyCommunityWithImages(
            @PathVariable("commNo") Long commNo,
            @RequestPart("community") Community community,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {

        community.setCommNo(commNo);

        List<CommunityFile> files = saveCommunityImages(images);

        if (!files.isEmpty()) {
            community.setCommMainImgUrl(files.get(0).getFilePath());
            community.setFiles(files);
        }

        communityService.modifyCommunity(community);
    }
    
    // 커뮤니티 게시글 삭제
    @DeleteMapping("/api/community/{commNo}")
    public void removeCommunity(@PathVariable("commNo") Long commNo) {
        communityService.removeCommunity(commNo);
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

    // 커뮤니티 이미지 저장
    private List<CommunityFile> saveCommunityImages(List<MultipartFile> images) throws IOException {
        List<CommunityFile> files = new ArrayList<>();

        if (images == null || images.isEmpty()) {
            return files;
        }

        File uploadDir = new File(System.getProperty("user.dir"), "uploads/community");

        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        for (MultipartFile image : images) {
            if (image == null || image.isEmpty()) continue;

            String originalName = image.getOriginalFilename();
            String ext = "";

            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf(".") + 1);
            }

            String saveName = UUID.randomUUID().toString() + (ext.isBlank() ? "" : "." + ext);

            File saveFile = new File(uploadDir, saveName);
            image.transferTo(saveFile.getAbsoluteFile());

            CommunityFile file = new CommunityFile();
            file.setFileOrgNm(originalName);
            file.setFileSaveNm(saveName);
            file.setFilePath("/uploads/community/" + saveName);
            file.setFileExt(ext);
            file.setFileSize(image.getSize());
            file.setFileMimeType(image.getContentType());
            file.setFileType("IMAGE");

            files.add(file);
        }

        return files;
    }
}