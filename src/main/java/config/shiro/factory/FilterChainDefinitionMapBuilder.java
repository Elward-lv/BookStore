package config.shiro.factory;

import domain.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import service.inter.UserService;

import java.util.LinkedHashMap;
import java.util.List;

public class FilterChainDefinitionMapBuilder {
    @Autowired
    UserService userService;

    public LinkedHashMap<String,String> buildFilterChainDefinitionMap(){
        LinkedHashMap<String ,String > permission = new LinkedHashMap<>();
        //查询权限
//        permission.put("/user/login1.do","anon");
//        permission.put("/user/register.do","anon");
//        permission.put("/user/logout.do","anon");
//        permission.put("/**","authc");
        List<Permission> list = userService.queryPermission();
        for (Permission p : list) {
            permission.put(p.getUrl(),p.getPermi());
        }
        return permission;
    }
}
