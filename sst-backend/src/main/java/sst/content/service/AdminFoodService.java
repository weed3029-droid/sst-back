package sst.content.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import sst.content.dto.FoodResponseDto;
import sst.content.mapper.PlaceFoodMapper;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;

@Service
@RequiredArgsConstructor
public class AdminFoodService {
    private final PlaceFoodMapper placeFoodMapper;

    @Transactional(readOnly = true)
    public PageResponse<FoodResponseDto> getListPageByRegion(Integer rgnCd, PageRequest pageRequest) {
        // 🚀 1. 카운트 쿼리에 keyword 전달
        int total = placeFoodMapper.countFoodListByRegion(rgnCd, pageRequest.getKeyword());
        
        // 🚀 2. 리스트 조회 쿼리에 keyword 전달
        List<FoodResponseDto> list = placeFoodMapper.findFoodListPaged(
                rgnCd, 
                pageRequest.getOffset(), 
                pageRequest.getSize(),
                pageRequest.getKeyword()
        );
        return new PageResponse<>(list, total, pageRequest);
    }

    @Transactional(readOnly = true)
    public FoodResponseDto getDetail(Long plcNo) {
        return placeFoodMapper.findById(plcNo);
    }
}