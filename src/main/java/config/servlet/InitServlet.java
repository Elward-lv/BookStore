package config.servlet;

import org.apache.log4j.PropertyConfigurator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * 初始化log4j配置文件和日志位置
 */
public class InitServlet extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext context = config.getServletContext();
        String log4j = config.getInitParameter("log4j");
        String root = config.getInitParameter("path");
        String log = config.getInitParameter("log");
        try {
            FileInputStream fis = new FileInputStream(context.getRealPath(log4j));

            String path = context.getRealPath(root)+log;
            System.out.println("==============>log path:"+path);
            Properties prop = new Properties();
            prop.load(fis);
            prop.setProperty("log4j.appender.D.File",path);//E:\java\tomcat\webapps\CourseDesign_war\WEB-INF\classes\log\log.log
            PropertyConfigurator.configure(prop);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("文件位置加载错误!");
        }
    }
}
