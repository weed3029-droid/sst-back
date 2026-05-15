package sst.report.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import sst.global.security.domain.CustomUserDetails;
import sst.member.domain.Member;
import sst.report.dto.ReportRequest;
import sst.report.service.ReportService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    // 신고 등록
    @PostMapping
    public ResponseEntity<Boolean> addReport(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ReportRequest request) {

        Member member = userDetails.getMember();

        boolean blinded = reportService.addReport(
                member.getMbrId(),
                request
        );

        return ResponseEntity.ok(blinded);
    }
}