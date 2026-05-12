package sst.content.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sst.content.dto.SeeResponseDto;
import sst.content.dto.SeeUpdateRequestDto;
import sst.content.service.AdminSeeService;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;
import sst.global.response.ApiResponse;

@RestController
@RequestMapping("/api/admin/see")
@RequiredArgsConstructor
public class AdminSeeController {

    private final AdminSeeService adminSeeService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<PageResponse<SeeResponseDto>>> getList(
            @RequestParam(name = "rgnCd", required = false) Integer rgnCd,
            PageRequest pageRequest) { 
        PageResponse<SeeResponseDto> result = adminSeeService.getListPageByRegion(rgnCd, pageRequest);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{plcNo}")
    public ResponseEntity<ApiResponse<SeeResponseDto>> getDetail(@PathVariable(name = "plcNo") Long plcNo) {
        return ResponseEntity.ok(ApiResponse.success(adminSeeService.getDetail(plcNo)));
    }
    
    /**
     * 볼거리 수정
     * PUT /api/admin/see/{plcNo}
     */
    @PutMapping("/{plcNo}")
    public ResponseEntity<ApiResponse<Void>> updateDetail(
            @PathVariable(name = "plcNo") Long plcNo,
            @RequestBody SeeUpdateRequestDto requestDto) {
        
        // 🚀 버그 수정: 주입받은 adminSeeService를 정상적으로 호출하도록 변경
        adminSeeService.updateSeeDetail(plcNo, requestDto);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @DeleteMapping("/{plcNo}")
    public ResponseEntity<ApiResponse<Void>> deleteDetail(@PathVariable(name = "plcNo") Long plcNo) {
        adminSeeService.deleteSee(plcNo);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
}