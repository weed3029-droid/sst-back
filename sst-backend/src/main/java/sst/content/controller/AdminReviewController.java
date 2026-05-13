// 🚀 src/main/java/sst/content/controller/AdminReviewController.java (신규 생성)
package sst.content.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sst.content.dto.ReviewResponseDto;
import sst.content.service.AdminReviewService;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;
import sst.global.response.ApiResponse;

@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {
    private final AdminReviewService adminReviewService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponseDto>>> getReviews(
            @RequestParam(name = "useYn", defaultValue = "Y") String useYn,
            PageRequest pageRequest) {
        return ResponseEntity.ok(ApiResponse.success(adminReviewService.getReviewsPaged(useYn, pageRequest)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{rvwNo}/status")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable("rvwNo") Long rvwNo,
            @RequestParam("useYn") String useYn) {
        adminReviewService.toggleReviewStatus(rvwNo, useYn);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}