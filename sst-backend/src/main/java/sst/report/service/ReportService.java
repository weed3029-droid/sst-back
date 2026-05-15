package sst.report.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sst.global.exception.CustomException;
import sst.global.exception.ErrorCode;
import sst.report.domain.Report;
import sst.report.dto.ReportRequest;
import sst.report.mapper.ReportMapper;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportMapper reportMapper;

    // 신고 등록
    @Transactional
    public boolean addReport(Long reporterNo, ReportRequest request) {

        Report report = new Report();

        report.setRptReporterNo(reporterNo);
        report.setRptTypeCd(request.getRptTypeCd());
        report.setRptReviewNo(request.getRptReviewNo());
        report.setRptCommNo(request.getRptCommNo());
        report.setRptCmntNo(request.getRptCmntNo());
        report.setRptReasonCd(request.getRptReasonCd());
        report.setRptReasonContent(request.getRptReasonContent());

        if ("RSN001".equals(request.getRptReasonCd())
                || "RSN002".equals(request.getRptReasonCd())) {
            report.setRptStatusCd("RST003");
            report.setRptProsrNo(reporterNo);
            report.setRptProsrDate(LocalDateTime.now());

        } else {
            report.setRptStatusCd("RST001");
        }

        int duplicateCount = reportMapper.countDuplicateReport(report);

        if (duplicateCount > 0) {
            throw new CustomException(ErrorCode.ALREADY_REPORTED);
        }

        reportMapper.insertReport(report);

        int reportCount = reportMapper.countReportsByTarget(report);

        if (reportCount >= 5) {
            if ("RPT002".equals(report.getRptTypeCd())) {
                reportMapper.blindCommunity(report.getRptCommNo());
            } else if ("RPT003".equals(report.getRptTypeCd())) {
                reportMapper.blindComment(report.getRptCmntNo());
            } else if ("RPT001".equals(report.getRptTypeCd())) {
                reportMapper.blindReview(report.getRptReviewNo());
            }

            return true;
        }

        return false;
    }
}