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
        report.setRptStatusCd("RST001");

        // 중복 신고 확인
        int duplicateCount = reportMapper.countDuplicateReport(report);
        if (duplicateCount > 0) {
            throw new CustomException(ErrorCode.ALREADY_REPORTED);
        }

        // 신고 데이터 DB 저장
        reportMapper.insertReport(report);

        // 유효 신고 횟수 조회 (RST004 반려 제외)
        int reportCount = reportMapper.countValidReportsByTarget(report);

        // 누적 5회 이상 시 자동 블라인드 처리
        if (reportCount >= 5) {
            if ("RPT001".equals(report.getRptTypeCd())) {
                reportMapper.blindReview(report.getRptReviewNo());
            } else if ("RPT002".equals(report.getRptTypeCd())) {
                reportMapper.blindCommunity(report.getRptCommNo());
            } else if ("RPT003".equals(report.getRptTypeCd())) {
                reportMapper.blindComment(report.getRptCmntNo());
            }
            return 2; // 블라인드 처리됨
        }

        return 1; // 일반 신고 접수
    }

    // 관리자 신고 반려 처리
    @Transactional
    public void rejectReport(Long rptNo, Long adminId) {
        // 1. 신고 상태를 RST004(반려)로 변경
        reportMapper.updateReportStatus(rptNo, "RST004", adminId);

        // 2. 해당 대상의 유효 신고 수 재확인
        Report report = reportMapper.findReportById(rptNo);
        int remainCount = reportMapper.countValidReportsByTarget(report);

        // 3. 유효 신고 수가 5 미만이면 블라인드 해제
        if (remainCount < 5) {
            if ("RPT001".equals(report.getRptTypeCd())) {
                reportMapper.unblindReview(report.getRptReviewNo());
            } else if ("RPT002".equals(report.getRptTypeCd())) {
                reportMapper.unblindCommunity(report.getRptCommNo());
            } else if ("RPT003".equals(report.getRptTypeCd())) {
                reportMapper.unblindComment(report.getRptCmntNo());
            }
        }
    }
    
    // 내가 해당 대상을 이미 신고했는지 여부 확인
    public boolean isReported(
            Long reporterNo,
            String type,
            Long commNo,
            Long cmntNo,
            Long reviewNo) {

        ReportRequest request = new ReportRequest();
        request.setRptReporterNo(reporterNo);

        if ("post".equals(type)) {
            if (commNo == null) return false;
            request.setRptTypeCd("RPT002");
            request.setRptCommNo(commNo);

        } else if ("comment".equals(type)) {
            if (cmntNo == null) return false;
            request.setRptTypeCd("RPT003");
            request.setRptCmntNo(cmntNo);

        } else if ("review".equals(type)) {
            if (reviewNo == null) return false;
            request.setRptTypeCd("RPT001");
            request.setRptReviewNo(reviewNo);

        } else {
            return false;
        }

        return reportMapper.existsReport(request) > 0;
    }
}