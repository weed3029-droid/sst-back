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

    // 🚀 관리자: 신고 처리 상태 업데이트
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{rptNo}/status")
    public ResponseEntity<ApiResponse<Void>> updateReportStatus(
            // 🚀 수정: PathVariable과 RequestParam에도 각각 이름을 명확히 지정해 줍니다.
            @PathVariable(name = "rptNo") Long rptNo,
            @RequestParam(name = "statusCd") String statusCd,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        adminReportService.processReport(rptNo, statusCd, userDetails.getMember().getMbrId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}