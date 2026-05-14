package sst.community.comment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sst.community.comment.domain.Comment;
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
    
    @Transactional(readOnly = true)
    public PageResponse<Comment> getAdminCommentListPaged(String useYn, PageRequest pageRequest) {
        
        // 🚀 검색어, 타입, offset, size 추출
        String searchType = pageRequest.getSearchType();
        String keyword = pageRequest.getKeyword();
        int offset = pageRequest.getOffset();
        int size = pageRequest.getSize();

        // 🚀 데이터 조회 및 카운트
        List<Comment> list = commentMapper.selectAdminCommentListPaged(searchType, keyword, useYn, offset, size);
        int totalCount = commentMapper.countAdminCommentList(searchType, keyword, useYn);

        return new PageResponse<>(list, totalCount, pageRequest);
    }

    @Transactional
    public void updateCommentStatus(Long cmntNo, String useYn) {
        // 🚀 [중요] 상태 변경 전 댓글 원본을 조회하여 게시글 번호(COMM_NO) 획득
        Comment comment = commentMapper.getCommentById(cmntNo);
        
        if (comment != null) {
            // 🚀 1. 댓글 상태(Y/N) 업데이트
            commentMapper.updateCommentStatus(cmntNo, useYn);
            
            // 🚀 2. 상태에 따른 게시글의 댓글 수(COMM_CMNT_CNT) 캐시 동기화 로직
            if ("Y".equals(useYn)) {
                commentMapper.increaseCommentCount(comment.getCmntCommNo()); // 복구 시 +1
            } else {
                commentMapper.decreaseCommentCount(comment.getCmntCommNo()); // 삭제 시 -1
            }
        }
    }
}