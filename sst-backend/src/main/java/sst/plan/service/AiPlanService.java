package sst.plan.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sst.plan.dto.AiScheduleDayInsertDto;
import sst.plan.dto.AiScheduleInsertDto;
import sst.plan.dto.AiSchedulePlaceInsertDto;
import sst.plan.dto.AiScheduleSaveRequestDto;
import sst.plan.dto.PlaceResponseDto;
import sst.plan.mapper.AiPlanMapper;

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
    
    public void saveSchedule(Long mbrId, AiScheduleSaveRequestDto request) {

        // 지역명 -> 지역번호 조회
        Long rgnNo = aiPlanMapper.findRgnNoByRgnName(request.getRgnName());

        // 테마명 -> 테마코드 변환
        List<String> themeNames = request.getThemes();
        List<String> themeCodes = (themeNames == null || themeNames.isEmpty()) ? List.of()
                : aiPlanMapper.findThemeCodesByNames(themeNames);

        String theme1 = themeCodes.size() > 0 ? themeCodes.get(0) : null;
        String theme2 = themeCodes.size() > 1 ? themeCodes.get(1) : null;
        String theme3 = themeCodes.size() > 2 ? themeCodes.get(2) : null;

        // AI_SCHEDULE 저장
        AiScheduleInsertDto scheduleDto = new AiScheduleInsertDto();
        scheduleDto.setMbrId(mbrId);
        scheduleDto.setScheduleName(request.getScheduleName());
        scheduleDto.setStartDate(request.getStartDate());
        scheduleDto.setEndDate(request.getEndDate());
        scheduleDto.setTotalDays(request.getTotalDays());
        scheduleDto.setRgnNo(rgnNo);
        scheduleDto.setTheme1(theme1);
        scheduleDto.setTheme2(theme2);
        scheduleDto.setTheme3(theme3);

        aiPlanMapper.insertAiSchedule(scheduleDto);
        Long aisNo = scheduleDto.getAisNo();

        // AI_SCHEDULE_DAY + AI_SCHEDULE_PLACE 저장
        for (AiScheduleSaveRequestDto.DayDto day : request.getSchedule()) {

            // 날짜 계산 (startDate + day - 1)
            LocalDate travelDate = LocalDate.parse(request.getStartDate())
                    .plusDays(day.getDay() - 1);

            AiScheduleDayInsertDto dayDto = new AiScheduleDayInsertDto();
            dayDto.setAisNo(aisNo);
            dayDto.setTravelDate(travelDate.toString());
            dayDto.setDayNo(day.getDay());

            aiPlanMapper.insertAiScheduleDay(dayDto);
            Long aisdNo = dayDto.getAisdNo();

            // AI_SCHEDULE_PLACE 저장
            int order = 1;
            for (AiScheduleSaveRequestDto.PlanDto plan : day.getPlans()) {
                AiSchedulePlaceInsertDto placeDto = AiSchedulePlaceInsertDto.builder()
                        .aisdNo(aisdNo)
                        .plcNo(plan.getPlaceId())
                        .visitOrder(order++)
                        .build();
                aiPlanMapper.insertAiSchedulePlace(placeDto);
            }
        }
    }
}