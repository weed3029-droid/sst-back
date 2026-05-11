package sst.community.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import lombok.RequiredArgsConstructor;
import sst.community.domain.Community;
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
    
    // 커뮤니티 게시글 등록
    @PostMapping("/api/community")
    public void createCommunity(@RequestBody Community community) {
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
    
    // 커뮤니티 게시글 삭제
    @DeleteMapping("/api/community/{commNo}")
    public void removeCommunity(@PathVariable("commNo") Long commNo) {

        communityService.removeCommunity(commNo);
    }
}