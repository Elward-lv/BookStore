package service;

import dao.UserMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import service.inter.ShiroService;

public class ShiroServiceImpl implements ShiroService {
    @Autowired
    UserMapper userMapper;

    public String login(String username, String password) {

        Subject currentUser = SecurityUtils.getSubject();
        if (!currentUser.isAuthenticated()) {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            try {
                currentUser.login(token);
            }catch (AuthenticationException e){
                throw new RuntimeException(e);
            }
        }
        return userMapper.getRoleName(username);
    }

    public void  logout(){
        Subject currentUser = SecurityUtils.getSubject();
        if(!currentUser.isAuthenticated()){
            return;
        }
        try {
            currentUser.logout();
        }catch (Exception e){
            throw new RuntimeException("登出失败!");
        }
    }

}
