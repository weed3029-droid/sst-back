package sst.content.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sst.content.dto.SeeResponseDto;
import sst.content.dto.SeeUpdateRequestDto;
import sst.content.mapper.PlaceSeeMapper;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;

@Service
@RequiredArgsConstructor
public class AdminSeeService {
    private final PlaceSeeMapper placeSeeMapper;

    
    // 🚀 2. 새롭게 추가되는 상태 토글(삭제/복구) 비즈니스 로직[cite: 9]
    @Transactional
    public void updatePlaceUseYn(Long plcNo, String useYn) {
        int result = placeSeeMapper.updatePlaceUseYn(plcNo, useYn);
        if (result == 0) {
            // 🚀 업데이트된 행이 없다면 존재하지 않는 장소이므로 예외 처리
            throw new RuntimeException("해당 장소를 찾을 수 없거나 상태 변경에 실패했습니다."); 
        }
    }
    
    @Transactional(readOnly = true)
    public PageResponse<SeeResponseDto> getListPageByRegion(Integer rgnCd, String useYn, PageRequest pageRequest) {
        // 🚀 1. useYn 상태값을 파라미터로 넘겨 휴지통과 정상 데이터를 구분해서 카운트
        int total = placeSeeMapper.countSeeListByRegion(rgnCd, pageRequest.getKeyword(), useYn);
        
        List<SeeResponseDto> list = placeSeeMapper.findSeeListPaged(
                rgnCd, pageRequest.getOffset(), pageRequest.getSize(), pageRequest.getKeyword(), useYn
        );
        return new PageResponse<>(list, total, pageRequest);
    }

    @Transactional(readOnly = true)
    public SeeResponseDto getDetail(Long plcNo) {
        SeeResponseDto dto = placeSeeMapper.findById(plcNo);
        return dto;
    }

    @Transactional
    public void updateSeeDetail(Long plcNo, SeeUpdateRequestDto dto) {
        placeSeeMapper.updatePlace(plcNo, dto);
        placeSeeMapper.updatePlaceSee(plcNo, dto);

    }

}