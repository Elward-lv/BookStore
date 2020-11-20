package test;

import domain.Book;
import domain.PageQuery;
import domain.Permission;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.BookServiceImpl;
import service.inter.UserService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapperTest {
    /**
     * 测试获取从数据库读取图片并且加载到文件之中
     */
    @Test
    public void test1() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/application.xml");
        UserService userService = (UserService) applicationContext.getBean("userService");
        List<Permission> map = userService.queryPermission();
        for (Permission permission : map) {
            System.out.println(permission);
        }
    }
}
