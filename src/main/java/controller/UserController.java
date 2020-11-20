package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import domain.PageQuery;
import domain.User;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.RedisService;
import service.inter.ShiroService;
import service.inter.UserService;
import utils.CommonUtils;
import utils.enums.ErrorEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 注解标注在controller上可能导致访问失败 404
 */
@Controller
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    private static final Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;
    @Autowired
    ShiroService shiroService;

    @RequestMapping(value = "/login.do",method = RequestMethod.POST)
    @ResponseBody//返回json字符串
    public String login(HttpServletRequest httpServletRequest, @RequestBody Map<String, Object> params) throws JsonProcessingException {
        HttpSession session = httpServletRequest.getSession();
        if(CommonUtils.hasAllRequiredAddAndRemove(params,"userName,password")){
            //登陆代码
            String role = shiroService.login(params.get("userName").toString(),params.get("password").toString());
            return CommonUtils.successJson(role);
            /*if(userService.login(session,params)){
                return CommonUtils.successJson(session.getAttribute("roleName"));
            }*/
        }
        return CommonUtils.errorJson(ErrorEnum.E_90003,"登陆失败");
    }

    @RequestMapping(value = "/unauth.do")
    @ResponseBody//返回json字符串
    public String unauth() {
        throw new RuntimeException("请登录！");
    }

    /**
     * 注意这里使用了@RequestParam  参数可以为空
     * @param params
     * @return
     */
    @RequestMapping(value = "/queryUserByCondition.do",method = RequestMethod.POST)
    @ResponseBody
    public String queryUserByCondition(@RequestBody Map<String,Object> params) {
        PageQuery<User> pageQuery = userService.queryUserByCondition(params);
        return CommonUtils.successJson(pageQuery);
    }

    @RequestMapping(value = "/register.do",method = RequestMethod.POST)
    @ResponseBody//返回json字符串
    public String register(HttpServletRequest httpServletRequest, @RequestBody Map<String,Object> params) {
        HttpSession session = httpServletRequest.getSession();
        logger.info("user:"+session.getAttribute("user"));
        if (CommonUtils.hasAllRequiredAddAndRemove(params, "userName,password,gender,email,birthday,address")) {
            //注册代码
            if(userService.register(session,params)){
                return CommonUtils.successJson("注册成功!");
            }
            return CommonUtils.errorJson(ErrorEnum.E_400,null);
        }
        return CommonUtils.errorJson(ErrorEnum.E_90003,"注册失败");
    }

    @RequestMapping(value = "/logout.do",method = RequestMethod.POST)
    @ResponseBody//返回json字符串
    public String loginOut(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession();
        redisService.del((String) session.getAttribute("user"));
        shiroService.logout();
        return CommonUtils.successJson("登出成功");
    }

    /**
     * 根据id删除用户
     * @param id
     */
    @RequestMapping("/delete.do")
    @ResponseBody
    public String deleteUser(@RequestParam("id") Integer id) {
        HttpSession session = (HttpSession) SecurityUtils.getSubject().getSession();
        //验证权限之后可以删除
        userService.deleteUser(session,id);
        return CommonUtils.successJson("删除成功!");
    }

    /**
     * 更新用户信息
     * @param params
     */
    @RequestMapping("/update.do")
    public String updateUser(@RequestBody Map<String, Object> params) {
        if (CommonUtils.hasAllRequiredAddAndRemove(params,"id,userName,password,gender,birthday,email,userRole,address")) {
            userService.updateUser(params);
            return CommonUtils.successJson("更新成功");
        }
        return CommonUtils.errorJson(ErrorEnum.E_400,"更新失败");
    }

}
