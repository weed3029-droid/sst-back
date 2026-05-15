package sst.report.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import sst.report.domain.Report;
import sst.report.dto.AdminReportResponseDto;

@Mapper
public interface ReportMapper {

    // 신고 등록
    int insertReport(Report report);

    // 중복 신고 확인
    int countDuplicateReport(Report report);
    

    int updateReportStatus(@Param("rptNo") Long rptNo, @Param("statusCd") String statusCd, @Param("adminId") Long adminId);
    
    List<AdminReportResponseDto> findAllReportsPaged(
            @Param("statusCd") String statusCd, 
            @Param("rptTypeCd") String rptTypeCd, 
            @Param("searchType") String searchType, 
            @Param("keyword") String keyword, 
            @Param("offset") int offset, 
            @Param("size") int size);

    int countAllReports(
            @Param("statusCd") String statusCd, 
            @Param("rptTypeCd") String rptTypeCd, 
            @Param("searchType") String searchType, 
            @Param("keyword") String keyword);
    
    // 대상별 신고 누적 수 조회
    int countReportsByTarget(Report report);

    // 커뮤니티 블라인드 처리
    int blindCommunity(Long commNo);

    // 댓글 블라인드 처리
    int blindComment(Long cmntNo);

    // 리뷰 블라인드 처리
    int blindReview(Long reviewNo);
    
}