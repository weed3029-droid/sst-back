package sst.content.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sst.content.dto.PlayResponseDto;
import sst.content.dto.PlayUpdateRequestDto;
import sst.content.service.AdminPlayService;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;
import sst.global.response.ApiResponse;

@RestController
@RequestMapping("/api/admin/play")
@RequiredArgsConstructor
public class AdminPlayController {

    private final AdminPlayService adminPlayService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<PageResponse<PlayResponseDto>>> getList(
            @RequestParam(name = "rgnCd", required = false) Integer rgnCd,
            PageRequest pageRequest) { 
        PageResponse<PlayResponseDto> result = adminPlayService.getListPageByRegion(rgnCd, pageRequest);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{plcNo}")
    public ResponseEntity<ApiResponse<PlayResponseDto>> getDetail(@PathVariable(name = "plcNo") Long plcNo) {
        return ResponseEntity.ok(ApiResponse.success(adminPlayService.getDetail(plcNo)));
    }
    
    /**
     * 놀거리 수정
     * PUT /api/admin/play/{plcNo}
     */
    @PutMapping("/{plcNo}")
    public ResponseEntity<ApiResponse<Void>> updateDetail(
            @PathVariable(name = "plcNo") Long plcNo,
            @RequestBody PlayUpdateRequestDto requestDto) {
        
        // 🚀 공통 규격인 ApiResponse 사용
        adminPlayService.updatePlay(plcNo, requestDto);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}