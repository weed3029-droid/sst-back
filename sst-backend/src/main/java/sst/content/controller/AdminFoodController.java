package sst.content.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sst.content.dto.FoodResponseDto;
import sst.content.service.AdminFoodService;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;
import sst.global.response.ApiResponse;

@RestController
@RequestMapping("/api/admin/food")
@RequiredArgsConstructor
public class AdminFoodController {

    private final AdminFoodService adminFoodService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<PageResponse<FoodResponseDto>>> getList(
            @RequestParam(name = "rgnCd", required = false) Integer rgnCd,
            PageRequest pageRequest) { 
        // 🚀 공통 ApiResponse 및 PageResponse 적용
        PageResponse<FoodResponseDto> result = adminFoodService.getListPageByRegion(rgnCd, pageRequest);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{plcNo}")
    public ResponseEntity<ApiResponse<FoodResponseDto>> getDetail(
            @PathVariable(name = "plcNo") Long plcNo) {
        return ResponseEntity.ok(ApiResponse.success(adminFoodService.getDetail(plcNo)));
    }
}