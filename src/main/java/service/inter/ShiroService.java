package service.inter;

public interface ShiroService {

    /**
     * 返回   用户角色
     * @param username
     * @param password
     * @return
     */
    public String login(String username, String password);

    public void  logout();

}
