package sst.global.code.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sst.global.code.dto.CommonCodeRequest;
import sst.global.code.dto.CommonCodeResponse;
import sst.global.code.dto.CommonCodeSearchRequest;
import sst.global.code.dto.GroupCodeResponse;
import sst.global.code.dto.PageResponse;
import sst.global.code.service.CommonCodeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/common-codes")
public class CommonCodeController {

    private final CommonCodeService commonCodeService;

    // 코드 중복 확인
    @GetMapping("/check")
    public boolean checkCommonCodeDuplicate(
            @RequestParam String code
    ) {
        return commonCodeService.existsCode(code);
    }

    // 그룹코드 목록 조회
    @GetMapping("/groups")
    public List<GroupCodeResponse> getGroupCodeList() {
        return commonCodeService.getGroupCodeList();
    }

    // 페이징 공통코드 조회
    @GetMapping("/page")
    public PageResponse<CommonCodeResponse> getCommonCodePage(
            @ModelAttribute CommonCodeSearchRequest searchRequest
    ) {
        return commonCodeService.getCommonCodePage(searchRequest);
    }

    // 공통코드 전체 조회 또는 그룹별 조회
    @GetMapping
    public List<CommonCodeResponse> getCommonCodeList(
            @RequestParam(required = false) String groupCode
    ) {
        return commonCodeService.getCommonCodeList(groupCode);
    }

    // 공통코드 등록
    @PostMapping
    public void addCommonCode(@RequestBody CommonCodeRequest request) {
        commonCodeService.addCommonCode(request);
    }

    // 공통코드 수정
    @PutMapping("/{code}")
    public void modifyCommonCode(
            @PathVariable String code,
            @RequestBody CommonCodeRequest request
    ) {
        request.setCode(code);
        commonCodeService.modifyCommonCode(request);
    }

    // 사용 여부 변경
    @PatchMapping("/{code}/use-yn")
    public void changeUseYn(
            @PathVariable String code,
            @RequestParam String useYn
    ) {
        commonCodeService.changeUseYn(code, useYn);
    }
}