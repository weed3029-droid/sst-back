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

    // 🚀 [수정 완료] 기존의 getList와 중복되던 것을 지우고, 탭(Y/N/B) 분류를 지원하는 이 메서드 하나로 통합했습니다.
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<PageResponse<Community>>> getAdminCommunityList(
            @RequestParam("catCd") String catCd,
            @RequestParam("useYn") String useYn,
            PageRequest pageRequest) {
        
        PageResponse<Community> result = adminCommunityService.getAdminCommunityListPaged(catCd, useYn, pageRequest);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 🚀 [수정 완료] 기존의 toggleStatus와 중복되던 것을 지우고, 이 메서드 하나로 통합했습니다.
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{commNo}/status")
    public ResponseEntity<ApiResponse<Void>> updateCommunityStatus(
            @PathVariable("commNo") Long commNo,
            @RequestParam("useYn") String useYn) {
        
        adminCommunityService.updateCommunityStatus(commNo, useYn);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 🚀 [유지] 물리적 완전 삭제 등 별도의 DeleteMapping이 필요하다면 기존 로직을 그대로 사용합니다.
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{commNo}")
    public ResponseEntity<ApiResponse<Void>> deleteCommunity(@PathVariable("commNo") Long commNo) {
        adminCommunityService.deleteCommunity(commNo);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}