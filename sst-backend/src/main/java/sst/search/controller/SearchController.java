package sst.search.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sst.global.response.ApiResponse;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;
import sst.content.dto.PlaceCardDto;
import sst.community.domain.Community;
import sst.search.service.SearchService;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/communities")
    public ResponseEntity<ApiResponse<PageResponse<Community>>> searchCommunitiesPaged(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "region", required = false) String region, // ✅ 추가
            PageRequest pageRequest) {
        PageResponse<Community> result = searchService.searchCommunitiesPaged(keyword, region, pageRequest);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/places")
    public ResponseEntity<ApiResponse<PageResponse<PlaceCardDto>>> searchPlacesPaged(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "category", defaultValue = "ALL") String category,
            @RequestParam(value = "region", required = false) String region,
            PageRequest pageRequest) {
        PageResponse<PlaceCardDto> result = searchService.searchPlacesPaged(keyword, category, region, pageRequest);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}