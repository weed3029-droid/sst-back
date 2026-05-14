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
import sst.community.comment.domain.Comment;
import sst.community.comment.service.AdminCommentService;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;
import sst.global.response.ApiResponse;

@RestController
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

 // 🚀 관리자: 댓글 목록 조회 (검색/페이징/상태 탭 연동)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<PageResponse<Comment>>> getAdminCommentList(
            @RequestParam(value = "useYn", defaultValue = "Y") String useYn,
            PageRequest pageRequest) {

        PageResponse<Comment> result = adminCommentService.getAdminCommentListPaged(useYn, pageRequest);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 🚀 관리자: 댓글 상태 변경 (삭제/복구)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{cmntNo}/status")
    public ResponseEntity<ApiResponse<Void>> updateCommentStatus(
            @PathVariable("cmntNo") Long cmntNo,
            @RequestParam("useYn") String useYn) {

        adminCommentService.updateCommentStatus(cmntNo, useYn);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}