package sst.report.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sst.report.domain.Report;
import sst.report.dto.AdminReportResponseDto;
import sst.report.dto.ReportRequest;

@Mapper
public interface ReportMapper {

    // 신고 등록
    int insertReport(Report report);

    // 중복 신고 확인
    int countDuplicateReport(Report report);
    
    // 신고 이력 존재 여부 확인
    int existsReport(ReportRequest request);

    // 신고 목록 페이징 조회
    List<AdminReportResponseDto> findAllReportsPaged(
            @Param("statusCd") String statusCd,
            @Param("rptTypeCd") String rptTypeCd,
            @Param("searchType") String searchType,
            @Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("size") int size);

    // 신고 전체 개수 조회
    int countAllReports(
            @Param("statusCd") String statusCd,
            @Param("rptTypeCd") String rptTypeCd,
            @Param("searchType") String searchType,
            @Param("keyword") String keyword);

    // 신고 상태 변경
    int updateReportStatus(
            @Param("rptNo") Long rptNo,
            @Param("statusCd") String statusCd,
            @Param("adminId") Long adminId);

    // 신고 상세 조회
    Report findReportById(@Param("rptNo") Long rptNo);

    // 타겟별 유효 신고 수 조회 (RST004 반려 제외)
    int countValidReportsByTarget(Report report);

    // 대상별 신고 누적 수 조회
    int countReportsByTarget(Report report);

    // 블라인드 처리
    int blindReview(@Param("rvwNo") Long rvwNo);
    int blindCommunity(@Param("commNo") Long commNo);
    int blindComment(@Param("cmntNo") Long cmntNo);

    // 블라인드 해제
    int unblindReview(@Param("rvwNo") Long rvwNo);
    int unblindCommunity(@Param("commNo") Long commNo);
    int unblindComment(@Param("cmntNo") Long cmntNo);
}