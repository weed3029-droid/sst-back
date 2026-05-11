package sst.plan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sst.plan.dto.PlaceResponseDto;
import sst.plan.service.AiPlanService;

import java.util.List;

@RestController
@RequestMapping("/ai")
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
}