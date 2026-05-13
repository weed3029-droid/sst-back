package sst.community.comment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sst.community.comment.dto.AdminCommentResponseDto;
import sst.community.comment.mapper.CommentMapper;
import sst.global.dto.PageRequest;
import sst.global.dto.PageResponse;

@Service
@RequiredArgsConstructor
public class AdminCommentService {

    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    public PageResponse<AdminCommentResponseDto> getCommentsPaged(String useYn, PageRequest pageRequest) {
        int total = commentMapper.countAdminCommentList(pageRequest.getKeyword(), useYn);
        List<AdminCommentResponseDto> list = commentMapper.findAdminCommentListPaged(
                pageRequest.getOffset(), pageRequest.getSize(), pageRequest.getKeyword(), useYn);
        return new PageResponse<>(list, total, pageRequest);
    }

    @Transactional
    public void toggleCommentStatus(Long cmntNo, String useYn) {
        // 1. 상태 업데이트
        commentMapper.updateCommentUseYn(cmntNo, useYn);
        
        // 🚀 2. 상태 변경 후 원문 게시글의 댓글 수 동기화
        Long commNo = commentMapper.findCommNoByCmntNo(cmntNo);
        if (commNo != null) {
            // 원본 글의 댓글 수를 재계산 (Y인 것만)
            commentMapper.syncCommentCount(commNo);
        }
    }
}