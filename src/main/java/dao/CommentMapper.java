package dao;

import domain.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper {
    List<Comment> getCommentById(@Param("id") Integer id,
                                 @Param("replyId") Integer rId,//0->查询图书的评论，>0查询此书下，某条评论的评论
                                 @Param("size") Integer size,
                                 @Param("start") Integer start,
                                 @Param("order") String order);

    int updateCommentById(Comment comment);

    int deleteCommentById(Integer  id);

    int addComment(Comment comment);

    Integer countCommentById(Integer id);
}
