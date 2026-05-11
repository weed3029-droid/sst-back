package sst.content.service;

import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.RequiredArgsConstructor;
import sst.content.dto.SleepResponseDto;
import sst.content.mapper.PlaceSleepMapper;

@Service
@RequiredArgsConstructor
public class AdminSleepService {

    private final PlaceSleepMapper placeSleepMapper;

    public List<SleepResponseDto> getListByRegion(Integer rgnCd) {
        return placeSleepMapper.findByRegion(rgnCd);
    }

    public SleepResponseDto getDetail(Long plcNo) {
        return placeSleepMapper.findById(plcNo);
    }
    
    public PageImpl<SleepResponseDto> getListPageByRegion(Integer rgnCd, Pageable pageable) {
        
        // 1. 반드시 호출 직전에 배치!
        PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize());

        // 2. 바로 다음에 Mapper가 와야 함
        // 만약 이 사이에 "if (rgnCd == null) {...}" 등 내부에서 다른 쿼리를 날리면 페이징이 깨집니다.
        List<SleepResponseDto> list = placeSleepMapper.findByRegion(rgnCd);

        // 3. 결과 분석
        PageInfo<SleepResponseDto> pageInfo = new PageInfo<>(list);
        return new PageImpl<>(pageInfo.getList(), pageable, pageInfo.getTotal());
    }
    
}