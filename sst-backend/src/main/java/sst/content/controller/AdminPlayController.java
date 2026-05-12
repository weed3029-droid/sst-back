package sst.content.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sst.content.dto.PlayResponseDto;
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
}