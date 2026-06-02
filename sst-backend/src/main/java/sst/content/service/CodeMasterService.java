package sst.content.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sst.content.dto.PlaceCardDto;
import sst.content.mapper.CodeMasterMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CodeMasterService {

    private final CodeMasterMapper codeMasterMapper;

    public List<PlaceCardDto> getPlaceCardsByRegionCode(int regionCode) {
        return codeMasterMapper.selectPlaceCardsByRegionCode(regionCode);
    }

    public List<PlaceCardDto> getTopPickPlaceCardsByRegionCode(int regionCode) {
        return codeMasterMapper.selectTopPickPlaceCardsByRegionCode(regionCode);
    }
}