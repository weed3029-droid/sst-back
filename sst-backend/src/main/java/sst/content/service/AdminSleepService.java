package sst.content.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sst.content.dto.SleepResponseDto;
import sst.content.dto.SleepUpdateRequestDto;
import sst.content.mapper.PlaceSleepMapper;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;

@Service
@RequiredArgsConstructor
public class AdminSleepService {

    private final PlaceSleepMapper placeSleepMapper;

    @Transactional(readOnly = true)
    public PageResponse<SleepResponseDto> getListPageByRegion(Integer rgnCd, PageRequest pageRequest) {
        
        // 🚀 1. 카운트 쿼리에 keyword 전달
        int total = placeSleepMapper.countSleepListByRegion(rgnCd, pageRequest.getKeyword());
        
        // 🚀 2. 리스트 조회 쿼리에 keyword 전달
        List<SleepResponseDto> list = placeSleepMapper.findSleepListPaged(
                rgnCd, 
                pageRequest.getOffset(), 
                pageRequest.getSize(),
                pageRequest.getKeyword()
        );

        return new PageResponse<>(list, total, pageRequest);
    }

    @Transactional(readOnly = true)
    public SleepResponseDto getDetail(Long plcNo) {
        return placeSleepMapper.findById(plcNo);
    }
    
    // 🚀 트랜잭션 보장
    @Transactional
    public void updateSleep(Long plcNo, SleepUpdateRequestDto dto) {
        placeSleepMapper.updatePlace(plcNo, dto);
        placeSleepMapper.updatePlaceSleep(plcNo, dto);
    }
}