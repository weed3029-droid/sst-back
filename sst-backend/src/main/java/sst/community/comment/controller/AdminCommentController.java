package sst.community.comment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sst.community.comment.dto.AdminCommentResponseDto;
import sst.community.comment.service.AdminCommentService;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;
import sst.global.response.ApiResponse;

@RestController
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    // 🚀 댓글 목록 페이징 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AdminCommentResponseDto>>> getComments(
            @RequestParam(name = "useYn", defaultValue = "ALL") String useYn,
            PageRequest pageRequest) {
        return ResponseEntity.ok(ApiResponse.success(adminCommentService.getCommentsPaged(useYn, pageRequest)));
    }

    // 🚀 댓글 상태 변경 (숨김/복구)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{cmntNo}/status")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable("cmntNo") Long cmntNo,
            @RequestParam("useYn") String useYn) {
        adminCommentService.toggleCommentStatus(cmntNo, useYn);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}