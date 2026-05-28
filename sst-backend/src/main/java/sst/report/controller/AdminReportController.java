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
import sst.report.service.ReportService;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final AdminReportService adminReportService;
    private final ReportService reportService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AdminReportResponseDto>>> getReports(
            @RequestParam(name = "statusCd", required = false) String statusCd,
            @RequestParam(name = "rptTypeCd", required = false) String rptTypeCd,
            PageRequest pageRequest) {
        return ResponseEntity.ok(ApiResponse.success(adminReportService.getReportsPaged(statusCd, rptTypeCd, pageRequest)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{rptNo}/status")
    public ResponseEntity<ApiResponse<String>> updateReportStatus(
            @PathVariable("rptNo") Long rptNo,
            @RequestParam("statusCd") String statusCd,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String message = adminReportService.updateReportStatus(rptNo, statusCd, userDetails.getMember().getMbrId());
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    // 신고 반려 처리
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{rptNo}/reject")
    public ResponseEntity<Void> rejectReport(
            @PathVariable("rptNo") Long rptNo,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        reportService.rejectReport(rptNo, userDetails.getMember().getMbrId());
        return ResponseEntity.ok().build();
    }
}