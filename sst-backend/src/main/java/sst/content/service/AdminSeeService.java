package sst.content.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import sst.content.dto.SeeResponseDto;
import sst.content.mapper.PlaceSeeMapper;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;

@Service
@RequiredArgsConstructor
public class AdminSeeService {
    private final PlaceSeeMapper placeSeeMapper;

    @Transactional(readOnly = true)
    public PageResponse<SeeResponseDto> getListPageByRegion(Integer rgnCd, PageRequest pageRequest) {
        int total = placeSeeMapper.countSeeListByRegion(rgnCd, pageRequest.getKeyword());
        List<SeeResponseDto> list = placeSeeMapper.findSeeListPaged(
                rgnCd, pageRequest.getOffset(), pageRequest.getSize(), pageRequest.getKeyword()
        );
        return new PageResponse<>(list, total, pageRequest);
    }

    @Transactional(readOnly = true)
    public SeeResponseDto getDetail(Long plcNo) {
        return placeSeeMapper.findById(plcNo);
    }
    
    
}