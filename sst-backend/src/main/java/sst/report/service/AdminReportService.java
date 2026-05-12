package sst.report.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;
import sst.global.exception.CustomException;
import sst.global.exception.ErrorCode;
import sst.report.dto.AdminReportResponseDto;
import sst.report.mapper.ReportMapper;

//src/main/java/sst/report/service/AdminReportService.java 생성
@Service
@RequiredArgsConstructor
public class AdminReportService {
 private final ReportMapper reportMapper;

 @Transactional(readOnly = true)
 public PageResponse<AdminReportResponseDto> getReportsPaged(String statusCd, String rptTypeCd, PageRequest pageRequest) {
     // 🚀 pageRequest 안에 들어있는 searchType과 keyword를 꺼내어 Mapper로 전달합니다.
     List<AdminReportResponseDto> list = reportMapper.findAllReportsPaged(
             statusCd, rptTypeCd, pageRequest.getSearchType(), pageRequest.getKeyword(), 
             pageRequest.getOffset(), pageRequest.getSize());
     int total = reportMapper.countAllReports(
             statusCd, rptTypeCd, pageRequest.getSearchType(), pageRequest.getKeyword());
     return new PageResponse<>(list, total, pageRequest);
 }

 @Transactional
 public void processReport(Long rptNo, String statusCd, Long adminId) {
     // 🚀 DB 무결성 제약조건에 맞춰 처리(RST003) 또는 반려(RST004) 시 관리자 ID를 함께 저장
     int result = reportMapper.updateReportStatus(rptNo, statusCd, adminId);
     if (result == 0) {
         throw new CustomException(ErrorCode.NOT_FOUND);
     }
 }
}