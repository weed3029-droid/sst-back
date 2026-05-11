package sst.plan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sst.plan.dto.PlaceResponseDto;
import sst.plan.mapper.AiPlanMapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiPlanService {

    private final AiPlanMapper aiPlanMapper;

    public List<PlaceResponseDto> getTravelPlaces(String region, String themes) {

        // 지역명 -> 지역코드 변환
        Integer rgnCd = aiPlanMapper.findRgnCdByRgnName(region);
        if (rgnCd == null) throw new IllegalArgumentException("알 수 없는 지역: " + region);

        // 테마명 -> 테마코드 변환
        List<String> themeNames = Arrays.stream(themes.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        List<String> themeCodes = aiPlanMapper.findThemeCodesByNames(themeNames);
        if (themeCodes.isEmpty()) throw new IllegalArgumentException("알 수 없는 테마: " + themes);

        // 장소 조회
        List<PlaceResponseDto> places = aiPlanMapper.findPlacesByRegionAndThemes(rgnCd, themeCodes);

        // 장소별 테마 정보 세팅
        for (PlaceResponseDto place : places) {
            List<String> tagCodes = aiPlanMapper.findThemeCodesByPlcNo(place.getId());
            List<String> tagNames = tagCodes.isEmpty()
                    ? List.of()
                    : aiPlanMapper.findThemeNamesByCodes(tagCodes);
            place.setPlaceTheme(tagCodes);
            place.setPlaceThemeName(tagNames);
        }

        return places;
    }
}