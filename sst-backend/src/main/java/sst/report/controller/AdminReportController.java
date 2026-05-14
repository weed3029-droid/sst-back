// src/main/java/sst/report/controller/AdminReportController.java
package sst.report.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;
import sst.global.response.ApiResponse;
import sst.global.security.domain.CustomUserDetails;
import sst.report.dto.AdminReportResponseDto;
import sst.report.service.AdminReportService;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {
    
    private final AdminReportService adminReportService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AdminReportResponseDto>>> getReports(
            @RequestParam(name = "statusCd", required = false) String statusCd,
            @RequestParam(name = "rptTypeCd", required = false) String rptTypeCd, // 🚀 대상 유형 파라미터 추가
            PageRequest pageRequest) { // 🚀 내부적으로 page, size, searchType, keyword를 매핑받음
        return ResponseEntity.ok(ApiResponse.success(adminReportService.getReportsPaged(statusCd, rptTypeCd, pageRequest)));
    }

    // 🚀 [수정 완료] updateStatus와 updateReportStatus로 중복되어 있던 메서드를 하나로 통합했습니다.
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{rptNo}/status")
    public ResponseEntity<ApiResponse<Void>> updateReportStatus(
            @PathVariable("rptNo") Long rptNo,
            @RequestParam("statusCd") String statusCd, // 🚀 쿼리 파라미터명은 프론트와 맞추세요 (예: statusCd)
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // 🚀 처리자(관리자) 번호를 함께 넘겨 처리 상태 무결성을 유지합니다.
        adminReportService.updateReportStatus(rptNo, statusCd, userDetails.getMember().getMbrId());
        
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}