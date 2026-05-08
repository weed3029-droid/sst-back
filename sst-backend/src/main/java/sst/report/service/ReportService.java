package sst.report.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sst.global.exception.CustomException;
import sst.global.exception.ErrorCode;
import sst.report.domain.Report;
import sst.report.dto.ReportRequest;
import sst.report.mapper.ReportMapper;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportMapper reportMapper;

    // 신고 등록
    @Transactional
    public int addReport(Long reporterNo, ReportRequest request) {

        Report report = new Report();

        report.setRptReporterNo(reporterNo);
        report.setRptTypeCd(request.getRptTypeCd());
        report.setRptReviewNo(request.getRptReviewNo());
        report.setRptCommNo(request.getRptCommNo());
        report.setRptCmntNo(request.getRptCmntNo());
        report.setRptReasonCd(request.getRptReasonCd());
        report.setRptReasonContent(request.getRptReasonContent());

        int duplicateCount = reportMapper.countDuplicateReport(report);

        if (duplicateCount > 0) {
            throw new CustomException(ErrorCode.ALREADY_REPORTED);
        }

        return reportMapper.insertReport(report);
    }
}