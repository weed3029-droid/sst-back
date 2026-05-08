package sst.content.service;

import lombok.RequiredArgsConstructor;
import sst.content.dto.SeeResponseDto;
import sst.content.mapper.PlaceSeeMapper;

import org.springframework.stereotype.Service;

import java.util.List;

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