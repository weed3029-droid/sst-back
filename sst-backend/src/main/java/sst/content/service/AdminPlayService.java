package sst.content.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sst.content.dto.PlayResponseDto;
import sst.content.dto.PlayUpdateRequestDto;
import sst.content.mapper.PlacePlayMapper;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;

@Service
@RequiredArgsConstructor
public class AdminPlayService {
    private final PlacePlayMapper placePlayMapper;

    @Transactional(readOnly = true)
    public PageResponse<PlayResponseDto> getListPageByRegion(Integer rgnCd, PageRequest pageRequest) {
        int total = placePlayMapper.countPlayListByRegion(rgnCd, pageRequest.getKeyword());
        List<PlayResponseDto> list = placePlayMapper.findPlayListPaged(
                rgnCd, pageRequest.getOffset(), pageRequest.getSize(), pageRequest.getKeyword()
        );
        return new PageResponse<>(list, total, pageRequest);
    }

    @Transactional(readOnly = true)
    public PlayResponseDto getDetail(Long plcNo) {
        return placePlayMapper.findById(plcNo);
    }
    
    // 🚀 추가: 두 쿼리를 하나의 트랜잭션으로 처리
    @Transactional
    public void updatePlay(Long plcNo, PlayUpdateRequestDto dto) {
        placePlayMapper.updatePlace(plcNo, dto);
        placePlayMapper.updatePlacePlay(plcNo, dto);
    }
}