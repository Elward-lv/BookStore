package service;

import dao.UserMapper;
import domain.PageQuery;
import domain.Permission;
import domain.User;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import service.inter.ShiroService;
import service.inter.UserService;
import utils.CommonUtils;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    ShiroService shiroService;

    static {
        //beanUtils中Date的字符串问题
        DateConverter converter = new DateConverter();
        converter.setPattern(new String("yyyy-MM-dd"));
        ConvertUtils.register(converter,Date.class);
    }

    /**
     * 登陆,并且可以把用户角色设置进session之中
     * @param session
     * @param conditions 同时也是返回值，使用完成之后清空
     */
    @Deprecated
    public boolean login(HttpSession session, Map<String,Object> conditions){
        Integer count = userMapper.countUserByEquals(conditions);
        if(count > 0){
            String userName = (String) conditions.get("userName");
            session.setAttribute("user",userName);
            session.setAttribute("roleName",userMapper.getRoleName(userName));
            return true;
        }
        return false;
    }

    @Deprecated
    public void loginOut(HttpSession session){
        session.removeAttribute("user");
        session.removeAttribute("roleName");
    }

    /**
     * 注册账户,防止重复注册
     * @param session
     * @param params
     */
    public boolean register(HttpSession session, Map<String,Object> params){
        Map<String,Object> conditions = new HashMap<>();
        Object userName = params.get("userName");
        Object password = params.get("password");
        conditions.put("userName",userName);
        conditions.put("password",password);
        String gender = (String) params.get("gender");
        if("男".equals(gender)){
            params.put("gender",1);
        }else {
            params.put("gender",2);
        }
        User user = new User();
        if(userMapper.countUserByEquals(conditions) <= 0){
            try {
                BeanUtils.populate(user,params);
                user.setCreateDate(new Date());
                user.setUserRole(3);
                if(userMapper.addUser(user)>0){
                    //session.setAttribute("user",user.getUserName());
                    //session.setAttribute("roleName",userMapper.getRoleName(user.getUserName()));
                    shiroService.login(userName.toString(),password.toString());
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 参数可以有页码等
     * 返回值为一个分页对象，包含有所有信息，如数据的条数，页数等
     * @return
     */
    public PageQuery<User> queryUserByCondition(Map<String,Object> conditions){
        CommonUtils.fillPageParams(conditions);
        int currentPage = (int) conditions.get("currentPage");
        int pageSize = (int) conditions.get("pageSize");
        //移除分页参数
        CommonUtils.hasAllRequiredAndRemove(conditions,"userName,gender,email,address,roleName");

        List<User> users = userMapper.queryUserByCondition(conditions,(currentPage-1)*pageSize,pageSize);
        int totalCount = users.size();
        //装配到分页插件中
        PageQuery<User> pageQuery = new PageQuery<>();
        pageQuery.setCurrentPage(currentPage);
        pageQuery.setList(users);
        pageQuery.setPageSize(pageSize);
        pageQuery.setTotalCount(totalCount);
        pageQuery.setTotalPage(totalCount%pageSize==0 ?totalCount/pageSize:(totalCount/pageSize+1));
        return pageQuery;
    }
    public void deleteUser(HttpSession session, Integer id) {
        String roleName = (String) session.getAttribute("roleName");
        userMapper.deleteUser(id);
    }
    public void updateUser(Map<String, Object> params) {
        User u = new User();
        try {
            BeanUtils.populate(u,params);
            userMapper.updateUser(u);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Permission> queryPermission() {
        return userMapper.queryPermission();
    }

    @Override
    public User getUserByName(String userName) {
        return userName==null?null:userMapper.getUserByName(userName);
    }
}
