package service;

import dao.CommentMapper;
import dao.UserMapper;
import domain.Comment;
import domain.PageQuery;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import service.inter.CommentService;
import utils.CommonUtils;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    UserMapper userMapper;

    /**
     * 处理添加评论的信息
     * @param params
     * @param session
     * @return
     */
    public boolean addComment(Map<String, Object> params, HttpSession session) {
        String userName = (String) session.getAttribute("user");
        Integer u_id = userMapper.getUserIdByName(userName);
        params.put("commentUser",u_id);
        Comment comment = new Comment();
        try {
            BeanUtils.populate(comment,params);
            comment.setModifyDate(new Date());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("bean属性注入出问题,"+e);
        }

        return (commentMapper.addComment(comment)>0);
    }


    /**
     * 根据图书id查询评论，可选条件为是否为replyUser 是否大于0
     * @param params
     * @return
     */
    public PageQuery<Comment> getCommentByConditions(Map<String,Object> params) {
        CommonUtils.fillPageParams(params);
        Integer bId = (Integer) params.get("id");
        Integer currentPage = (Integer) params.get("currentPage");
        Integer pageSize = (Integer) params.get("pageSize");
        //获取排序参数
        String order = (String) params.get("order");
        if(!"stars".equals(order) && !"modifyDate".equals(order)){
            order = null;
        }

        if(bId <= 0){return null;}
        Integer replyUserId =  params.get("replyUser") == null ? 0:(Integer)params.get("replyUser");
        List<Comment> comments = commentMapper.getCommentById(bId,replyUserId,pageSize,(currentPage-1)*pageSize,order);
        Integer totalCount = comments.size();
        Integer totalPage = totalCount % pageSize == 0 ? totalCount /pageSize : totalCount/pageSize +1;
        PageQuery<Comment> pageQuery = new PageQuery<>();
        pageQuery.setList(comments);
        pageQuery.setTotalPage(totalPage);
        pageQuery.setCurrentPage(currentPage);
        pageQuery.setPageSize(pageSize);
        pageQuery.setTotalCount(totalCount);
        return pageQuery;
    }
    public int deleteCommentById(Integer id){
        if(id <= 0 ) return -1;
        return commentMapper.deleteCommentById(id);
    }
}
