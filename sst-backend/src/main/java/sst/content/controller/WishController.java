package sst.content.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sst.content.dto.WishDto;
import sst.content.dto.WishResponseDto;
import sst.content.service.WishService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;

    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Boolean>> toggleWish(
            @RequestBody WishDto dto) {
        boolean isWished = wishService.toggleWish(dto);
        return ResponseEntity.ok(Map.of("isWished", isWished));
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> isWished(
            @RequestParam("mbrId") Long mbrId,
            @RequestParam("plcNo") Long plcNo) {
        WishDto dto = new WishDto();
        dto.setWishMbrId(mbrId);
        dto.setWishPlcNo(plcNo);
        boolean isWished = wishService.isWished(dto);
        return ResponseEntity.ok(Map.of("isWished", isWished));
    }

    @GetMapping
    public ResponseEntity<List<WishResponseDto>> getMyWishlist(
            @RequestParam("mbrId") Long mbrId) {
        return ResponseEntity.ok(wishService.getMyWishlist(mbrId));
    }

    // 여러 장소 찜 상태 한번에 조회
    @GetMapping("/check-bulk")
    public ResponseEntity<List<Long>> checkWishBulk(
            @RequestParam("mbrId") Long mbrId,
            @RequestParam("plcNos") List<Long> plcNos) {
        return ResponseEntity.ok(wishService.getWishedPlcNos(mbrId, plcNos));
    }
}