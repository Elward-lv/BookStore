package config.shiro.realms;

import dao.UserMapper;
import domain.User;
import org.apache.log4j.Logger;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import service.inter.UserService;

import java.util.HashSet;
import java.util.Set;

public class ShiroRealm extends AuthorizingRealm {
    @Autowired
    UserService userService;
    private static Logger logger = Logger.getLogger(ShiroRealm.class);

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        logger.info("===========>[first realm 身份认证]<==================== ");
        UsernamePasswordToken upToken  = (UsernamePasswordToken) authenticationToken;
        String username = upToken.getUsername();

        if("unknown".equals(username)){
            throw  new UnknownAccountException("用户不存在");
        }
        if("monster".equals(username)){
            throw new LockedAccountException("用户被锁定");
        }

        User u = userService.getUserByName(username);
        Object principal = username;
        Object credentials = u.getPassword();

        String realmName = getName();
        SimpleAuthenticationInfo info  = new SimpleAuthenticationInfo(principal,credentials,realmName);
        return info;
    }

    /**
     * 授权需要回调的方法
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Object principal = principals.getPrimaryPrincipal();
        logger.info("====================>权限认证<====================");
        Set<String> roles = new HashSet<>();
        User u = userService.getUserByName(principal.toString());
        roles.add(u.getRoleName());
        logger.info("================>为用户:"+principal+"，添加角色:"+u.getRoleName());
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roles);
        return info;
    }
}
