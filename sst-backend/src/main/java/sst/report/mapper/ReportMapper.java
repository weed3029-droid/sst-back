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
    
}