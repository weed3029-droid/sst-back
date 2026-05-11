package sst.content.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sst.content.dto.SleepResponseDto;
import sst.content.service.AdminSleepService;

@RestController
@RequestMapping("/api/admin/sleep")
@RequiredArgsConstructor
public class AdminSleepController {

    private final AdminSleepService sleepService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getList(
            @RequestParam(name = "rgnCd", required = false) Integer rgnCd,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        
        // Service에서 PageImpl을 받아옵니다.
        PageImpl<SleepResponseDto> resultPage = sleepService.getListPageByRegion(rgnCd, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("data", resultPage.getContent());
        response.put("total", resultPage.getTotalElements()); // PageHelper가 계산해준 총 개수

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{plcNo}")
    public ResponseEntity<SleepResponseDto> getDetail(
            @PathVariable(name = "plcNo") Long plcNo) {
        return ResponseEntity.ok(sleepService.getDetail(plcNo));
    }
}