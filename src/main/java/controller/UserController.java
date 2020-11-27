package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import domain.PageQuery;
import domain.User;
import exception.AccessException;
import org.apache.commons.beanutils.BeanUtils;
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
import java.util.Date;
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
    @ResponseBody
    public String login(@RequestBody Map<String, Object> params) {
        if(CommonUtils.hasAllRequiredAddAndRemove(params,"userName,password")){
            //登陆代码
            String role = shiroService.login(params.get("userName").toString(),params.get("password").toString());
            return CommonUtils.successJson(role);
        }
        return CommonUtils.errorJson(ErrorEnum.E_90003,"登陆失败");
    }

    @RequestMapping(value = "/unauth.do")
    @ResponseBody
    public String unauth() {
        throw new RuntimeException("权限不够/未登陆，请联系管理员");
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
    public String register(@RequestBody Map<String, Object> params) {
        Session session = SecurityUtils.getSubject().getSession();
        logger.info("user:"+session.getAttribute("user"));
        if (CommonUtils.hasAllRequiredAddAndRemove(params, "userName,password,gender,email,birthday,address")) {
            //注册代码
            if(userService.register(params)){
                return CommonUtils.successJson("注册成功!");
            }
            return CommonUtils.errorJson(ErrorEnum.E_400,null);
        }
        return CommonUtils.errorJson(ErrorEnum.E_90003,"注册失败");
    }

    @RequestMapping(value = "/logout.do",method = RequestMethod.POST)
    @ResponseBody//返回json字符串
    public String loginOut() {
        Session session = SecurityUtils.getSubject().getSession();
        if(redisService.hasKey((String) session.getId())){
            //清除购物车
            redisService.del((String) session.getId());
        }
        shiroService.logout();
        return CommonUtils.successJson("登出成功");
    }

    /**
     * 根据id删除用户,必须是管理员
     * 或者本身注销
     * @param id
     */
    @RequestMapping("/delete.do")
    @ResponseBody
    public String deleteUser(@RequestParam("id") Integer id) {
        Session session =  SecurityUtils.getSubject().getSession();
        logger.info("controller session:"+session);
        //验证权限之后可以删除
        userService.deleteUser(id);
        return CommonUtils.successJson("删除成功!");
    }

    /**
     * 更新用户信息,支持修改部分信息，其他只要置空即可
     * @param params
     */
    @RequestMapping("/update.do")
    @ResponseBody
    public String updateUser(@RequestBody Map<String, Object> params) {
        if(params.get("id") == null){
            return CommonUtils.errorJson(ErrorEnum.E_90003,null);
        }

        if (CommonUtils.hasAllRequiredAndRemove(params,"id,userName,password,gender,birthday,email,userRole,address")) {
            //对管理员和其他用户进行分类
            if (!SecurityUtils.getSubject().hasRole("管理员")) {
                params.put("userRole",null);
            }else {
                //非管理员只能修改自己的信息
                String name = (String) SecurityUtils.getSubject().getPrincipal();
                Integer uId = userService.getUserByName(name).getId();
                Integer id = (Integer) params.get("id");
                if(!uId.equals(id)){
                    throw new AccessException("权限不够");
                }
            }
            userService.updateUser(params);
            return CommonUtils.successJson("更新成功");
        }
        return CommonUtils.errorJson(ErrorEnum.E_400,"更新失败");
    }

    @RequestMapping("/add.do")
    @ResponseBody
    public String add(@RequestBody Map<String,Object> params){
        if (!CommonUtils.hasAllRequiredAddAndRemove(params,"userName,password,gender,email,birthday,address")) {
            return CommonUtils.errorJson(ErrorEnum.E_90003,null);
        }
        params.put("createDate",new Date());
        params.put("userRole",3);
        User user = new User();
       try {
           BeanUtils.populate(user,params);
           userService.add(user);
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
        return CommonUtils.successJson(null);
    }

}
