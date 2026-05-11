package sst.plan.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AiPlanController {
	
	@GetMapping("/ai/travel/list")
	public Map<String,String> AiPlanResponse(@RequestParam("region") String region, @RequestParam("style") String style){
		System.out.println(region + style);
		return Map.of("region",region,"Style",style);
	}
}
