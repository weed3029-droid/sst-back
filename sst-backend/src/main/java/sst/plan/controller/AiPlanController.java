package sst.plan.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sst.global.security.domain.CustomUserDetails;
import sst.plan.dto.AiScheduleSaveRequestDto;
import sst.plan.dto.PlaceResponseDto;
import sst.plan.service.AiPlanService;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiPlanController {

    private final AiPlanService aiPlanService;

    @GetMapping("/travel/list")
    public List<PlaceResponseDto> AiPlanResponse(
            @RequestParam("region") String region,
            @RequestParam("themes") String themes
    ) {
        return aiPlanService.getTravelPlaces(region, themes);
    }

    @PostMapping("/schedule/save")
    public ResponseEntity<?> saveSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AiScheduleSaveRequestDto request
    ) {
        Long mbrId = userDetails.getMember().getMbrId();
        aiPlanService.saveSchedule(mbrId, request);
        return ResponseEntity.ok().build();
    }
    
}