package service.inter;

import domain.Book;
import domain.Comment;
import domain.PageQuery;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

public interface CommentService {

    public boolean addComment(Map<String, Object> params);

    public PageQuery<Comment> getCommentByConditions(Map<String,Object> params);

    public int deleteCommentById(Integer id);
}
