package dao;

import domain.Permission;
import domain.User;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserMapper {
    public Integer countUserByEquals(@Param("conditions") Map<String,Object> conditions);

    public Integer addUser(User user);

    public String getRoleName(String userName);

    /**
     * 分页条件模糊查询,支持以下字段模糊查询
     *       userName,password,gender,email,address,roleName
     * @param conditions
     * @param start
     * @param size
     * @return
     */
    public List<User> queryUserByCondition(@Param("conditions") Map<String,Object> conditions,
                                           @Param("start") Integer start, @Param("size") Integer size);

    /**
     * 分页条件模糊总数查询，支持以下字段模糊查询
     *  userName,password,gender,email,address,roleName
     * @param conditions
     * @return
     */
    public Integer countUserByCondition(@Param("conditions")Map<String,Object> conditions);

    void deleteUser(Integer id);

    void updateUser(User u);

    Integer getUserIdByName(String userName);

    /**
     * 查询 权限
     */
    public List<Permission> queryPermission();

    public User getUserByName(String userName);
}
