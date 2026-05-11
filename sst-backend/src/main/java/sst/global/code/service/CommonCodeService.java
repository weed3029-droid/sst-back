package sst.global.code.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sst.global.code.dto.CommonCodeRequest;
import sst.global.code.dto.CommonCodeResponse;
import sst.global.code.dto.CommonCodeSearchRequest;
import sst.global.code.dto.GroupCodeResponse;
import sst.global.code.dto.PageResponse;
import sst.global.code.mapper.CommonCodeMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonCodeService {

    private final CommonCodeMapper commonCodeMapper;

    // 코드 중복 확인
    public boolean existsCode(String code) {
        return commonCodeMapper.countByCode(code) > 0;
    }

    // 그룹코드 목록 조회
    public List<GroupCodeResponse> getGroupCodeList() {
        return commonCodeMapper.selectGroupCodeList();
    }

    // 페이징 공통코드 조회
    public PageResponse<CommonCodeResponse> getCommonCodePage(
            CommonCodeSearchRequest searchRequest
    ) {

        List<CommonCodeResponse> content =
                commonCodeMapper.selectCommonCodePage(searchRequest);

        int totalElements =
                commonCodeMapper.countCommonCodeList(searchRequest);

        return new PageResponse<>(
                content,
                searchRequest.getPage(),
                searchRequest.getSize(),
                totalElements
        );
    }

    // 전체 조회 또는 그룹 조회
    public List<CommonCodeResponse> getCommonCodeList(String groupCode) {

        if (groupCode == null || groupCode.isBlank()) {
            return commonCodeMapper.selectCommonCodeList();
        }

        return commonCodeMapper.selectCommonCodeListByGroupCode(groupCode);
    }

    // 등록
    public void addCommonCode(CommonCodeRequest request) {
        commonCodeMapper.insertCommonCode(request);
    }

    // 수정
    public void modifyCommonCode(CommonCodeRequest request) {
        commonCodeMapper.updateCommonCode(request);
    }

    // 사용 여부 변경
    public void changeUseYn(String code, String useYn) {
        commonCodeMapper.updateUseYn(code, useYn);
    }
}