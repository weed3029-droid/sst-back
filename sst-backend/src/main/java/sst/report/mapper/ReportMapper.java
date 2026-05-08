package sst.report.mapper;

import org.apache.ibatis.annotations.Mapper;

import sst.report.domain.Report;

@Mapper
public interface ReportMapper {

    // 신고 등록
    int insertReport(Report report);

    // 중복 신고 확인
    int countDuplicateReport(Report report);
}