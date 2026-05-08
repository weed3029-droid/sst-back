package sst.community.comment.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import sst.community.comment.domain.Comment;

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
}