package sst.community.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
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

 // 🚀 뽐낼거리 목록 조회 (catCd: CMM001-인생거리, CMM002-핫플레이스)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<PageResponse<Community>>> getList(
            @RequestParam(name = "catCd") String catCd,
            @RequestParam(name = "useYn", required = false, defaultValue = "Y") String useYn,
            PageRequest pageRequest) { 
        
        PageResponse<Community> result = adminCommunityService.getListPageByCategory(catCd, useYn, pageRequest);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 🚀 휴지통 이동 및 복구를 처리할 상태 변경 API
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{commNo}/status")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable("commNo") Long commNo,
            @RequestParam("useYn") String useYn) {
        
        adminCommunityService.updateCommunityUseYn(commNo, useYn);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 🚀 어드민 뽐낼거리 단건 삭제 (소프트 딜리트)
    @DeleteMapping("/{commNo}")
    public ResponseEntity<ApiResponse<Void>> deleteCommunity(@PathVariable("commNo") Long commNo) {
        adminCommunityService.deleteCommunity(commNo);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    
    
}