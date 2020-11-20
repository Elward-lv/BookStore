package service.inter;

import domain.PageQuery;
import domain.Permission;
import domain.User;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

public interface UserService {
    public boolean login(HttpSession session, Map<String,Object> conditions);

    public void loginOut(HttpSession session);

    public boolean register(HttpSession session, Map<String,Object> params);

    public PageQuery<User> queryUserByCondition(Map<String,Object> conditions);

    public void deleteUser(HttpSession session, Integer id);

    public void updateUser(Map<String, Object> params);

    public List<Permission> queryPermission();

    public User getUserByName(String userName);
}