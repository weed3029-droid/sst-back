package sst.community.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;
import sst.community.domain.Community;
import sst.community.service.CommunityService;

@RestController
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    // 사용자가 선택한 카테고리에 맞는 게시판 목록 보여주기
    @GetMapping("/api/community")
    public List<Community> communityList(@RequestParam("catCd") String catCd) {
        return communityService.getCommunityList(catCd);
    }
    
    // 커뮤니티 게시글 상세페이지
    @GetMapping("/api/community/{commNo}")
    public Community communityDetail(@PathVariable("commNo") Long commNo) {
        return communityService.getCommunityDetail(commNo);
    }
}