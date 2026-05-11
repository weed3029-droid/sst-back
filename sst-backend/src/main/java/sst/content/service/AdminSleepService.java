package sst.content.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sst.content.dto.SleepResponseDto;
import sst.content.mapper.PlaceSleepMapper;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;

@Service
@RequiredArgsConstructor
public class AdminSleepService {

    private final PlaceSleepMapper placeSleepMapper;

    @Transactional(readOnly = true)
    public PageResponse<SleepResponseDto> getListPageByRegion(Integer rgnCd, PageRequest pageRequest) {
        
        // 🚀 1. 전체 개수를 먼저 조회합니다 (rgnCd 조건 포함)
        int total = placeSleepMapper.countSleepListByRegion(rgnCd);
        
        // 🚀 2. offset과 size를 넘겨 페이징된 데이터만 가져옵니다.
        List<SleepResponseDto> list = placeSleepMapper.findSleepListPaged(
                rgnCd, 
                pageRequest.getOffset(), 
                pageRequest.getSize()
        );

        // 🚀 3. 우리가 만든 공통 응답 DTO에 담아 반환 (내부에서 totalPages 자동 계산)
        return new PageResponse<>(list, total, pageRequest);
    }

    @Transactional(readOnly = true)
    public SleepResponseDto getDetail(Long plcNo) {
        return placeSleepMapper.findById(plcNo);
    }
    
}