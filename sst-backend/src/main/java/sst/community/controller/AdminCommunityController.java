package sst.community.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sst.community.domain.Community;
import sst.community.service.AdminCommunityService;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;
import sst.global.response.ApiResponse;

@RestController
@RequestMapping("/api/admin/community")
@RequiredArgsConstructor
public class AdminCommunityController {

    private final AdminCommunityService adminCommunityService;

    // 🚀 어드민 뽐낼거리 리스트 페이징 조회
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<PageResponse<Community>>> getList(
            @RequestParam("catCd") String catCd,
            PageRequest pageRequest) {
        return ResponseEntity.ok(ApiResponse.success(adminCommunityService.getListPage(catCd, pageRequest)));
    }

    // 🚀 어드민 뽐낼거리 단건 삭제 (소프트 딜리트)
    @DeleteMapping("/{commNo}")
    public ResponseEntity<ApiResponse<Void>> deleteCommunity(@PathVariable("commNo") Long commNo) {
        adminCommunityService.deleteCommunity(commNo);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    
}