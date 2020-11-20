package config;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import service.BookServiceImpl;
import service.RedisService;
import service.inter.BookService;

/**
 * 根据数据库book和
 */
public class InitBean  {
    private static Logger logger = Logger.getLogger(InitBean.class);
    @Autowired
    RedisService redisService;
    @Autowired
    BookService bookServiceImpl;

    public void init(){
        try {
            logger.info("init Bean init redis............");
            //重新添加redis信息
            redisService.refreshRedisBookStatus();
            logger.info("init Bean end............");
            initTopList();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void initTopList()  {
        logger.info("删除榜单.........");
        redisService.refreshTopList(6);
        logger.info("重新创建榜单");
    }
}
