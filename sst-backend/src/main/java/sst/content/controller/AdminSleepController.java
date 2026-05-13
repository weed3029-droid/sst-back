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
import sst.content.dto.SleepResponseDto;
import sst.content.dto.SleepUpdateRequestDto;
import sst.content.service.AdminSleepService;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;
import sst.global.response.ApiResponse;

@RestController
@RequestMapping("/api/admin/sleep")
@RequiredArgsConstructor
public class AdminSleepController {

    private final AdminSleepService adminSleepService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<PageResponse<SleepResponseDto>>> getList(
            @RequestParam(name = "rgnCd", required = false) Integer rgnCd,
            PageRequest pageRequest) { 
        // 🚀 1. ?page=1&size=10 파라미터는 Spring이 PageRequest 객체에 자동으로 바인딩해 줍니다.
        // 🚀 2. Map을 쓰지 않고 명확한 타입(PageResponse)과 공통 응답(ApiResponse)으로 감싸 반환합니다.

        PageResponse<SleepResponseDto> result = adminSleepService.getListPageByRegion(rgnCd, pageRequest);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{plcNo}")
    public ResponseEntity<ApiResponse<SleepResponseDto>> getDetail(
            @PathVariable(name = "plcNo") Long plcNo) {
        // 🚀 3. 단건 조회도 ApiResponse 규격을 맞추어 프론트엔드 연동의 일관성을 유지합니다.
        return ResponseEntity.ok(ApiResponse.success(adminSleepService.getDetail(plcNo)));
    }
    
    /**
     * 잘거리 수정
     * PUT /api/admin/sleep/{plcNo}
     */
    @PutMapping("/{plcNo}")
    public ResponseEntity<ApiResponse<Void>> updateDetail(
            @PathVariable(name = "plcNo") Long plcNo,
            @RequestBody SleepUpdateRequestDto requestDto) {
        
        adminSleepService.updateSleep(plcNo, requestDto);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}