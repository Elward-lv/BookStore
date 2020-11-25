package controller;

import domain.Comment;
import domain.PageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import service.inter.CommentService;
import utils.CommonUtils;
import utils.enums.ErrorEnum;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/comment")
@CrossOrigin
public class CommentController {
    @Autowired
    CommentService commentService;

    @RequestMapping("/addComment.do")
    @ResponseBody
    public String addComment(@RequestBody Map<String,Object> params, HttpServletRequest hsr) {
        if(CommonUtils.hasAllRequiredAndRemove(params,"commentInfo,commentBook,replyUser,stars")){
            if(commentService.addComment(params)){
                return CommonUtils.successJson(null);
            }
            return CommonUtils.errorJson(ErrorEnum.E_400,null);
        }
        return CommonUtils.errorJson(ErrorEnum.E_90003,null);
    }

    @RequestMapping("/getComment.do")
    @ResponseBody
    public String showComment(@RequestBody Map<String,Object> params){
        CommonUtils.hasAllRequiredAndRemove(params,"id,currentPage,pageSize,replyUser,order");
        PageQuery<Comment> res = commentService.getCommentByConditions(params);
        if(res != null)
            return CommonUtils.successJson(res);
        else
            return CommonUtils.errorJson(ErrorEnum.E_90004,null);
    }

    @RequestMapping("/deleteComment.do")
    @ResponseBody
    public String deleteComment(Integer id){
        if(commentService.deleteCommentById(id) > 0){
            return CommonUtils.successJson(null);
        }
        return CommonUtils.errorJson(ErrorEnum.E_400,null);
    }

}
