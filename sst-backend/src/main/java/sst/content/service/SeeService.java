package sst.content.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sst.content.dto.SeeResponseDto;
import sst.content.dto.SeeUpdateRequestDto;
import sst.content.mapper.PlaceSeeMapper;

@Service
@RequiredArgsConstructor
public class SeeService {

    private final PlaceSeeMapper placeSeeMapper;

    public List<SeeResponseDto> getListByRegion(Integer rgnCd) {
        return placeSeeMapper.findByRegion(rgnCd);
    }

    public SeeResponseDto getDetail(Long plcNo) {
        return placeSeeMapper.findById(plcNo);
    }
    
}