package sst.community.comment.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import sst.community.comment.domain.Comment;
import sst.community.comment.dto.AdminCommentResponseDto;

@Mapper
public interface CommentMapper {

    // 댓글 목록 조회
    List<Comment> getCommentList(@Param("commNo") Long commNo);

    // 댓글 등록
    int insertComment(Comment comment);

    // 댓글 수정
    int updateComment(Comment comment);

    // 댓글 삭제
    int deleteComment(Long cmntNo);

    // 댓글 단건 조회
    Comment getCommentById(Long cmntNo);

    // 댓글 수 증가
    int increaseCommentCount(Long commNo);

    // 댓글 수 감소
    int decreaseCommentCount(Long commNo);
    
    List<AdminCommentResponseDto> findAdminCommentListPaged(
            @Param("offset") int offset, 
            @Param("size") int size, 
            @Param("keyword") String keyword, 
            @Param("useYn") String useYn);

    // 🚀 관리자: 전체 댓글 수 조회 (페이징용)
    int countAdminCommentList(
            @Param("keyword") String keyword, 
            @Param("useYn") String useYn);

    // 🚀 관리자: 상태 변경 (소프트 삭제/복구)
    int updateCommentUseYn(@Param("cmntNo") Long cmntNo, @Param("useYn") String useYn);

    // 🚀 댓글 번호로 원문 게시글 번호 조회 (카운트 동기화용)
    Long findCommNoByCmntNo(Long cmntNo);

    // 🚀 커뮤니티 게시글의 실제 댓글 수 동기화
    int syncCommentCount(Long commNo);
    
 // 🚀 관리자: 댓글 페이징 목록 조회
    List<Comment> selectAdminCommentListPaged(
            @Param("searchType") String searchType, 
            @Param("keyword") String keyword, 
            @Param("useYn") String useYn, 
            @Param("offset") int offset, 
            @Param("size") int size);

    // 🚀 관리자: 댓글 총 개수
    int countAdminCommentList(
            @Param("searchType") String searchType, 
            @Param("keyword") String keyword, 
            @Param("useYn") String useYn);

    // 🚀 관리자: 댓글 상태(Y/N) 토글
    int updateCommentStatus(
            @Param("cmntNo") Long cmntNo, 
            @Param("useYn") String useYn);
}