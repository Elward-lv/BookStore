package service;

import dao.BookMapper;
import dao.UserMapper;
import domain.Book;
import domain.PageQuery;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import service.inter.BookService;
import utils.CommonUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class BookServiceImpl implements BookService {
    private static Logger logger = Logger.getLogger(BookServiceImpl.class);
    private static String bookNumsStr = "bookNums";
    private static String bookPricesStr = "bookPrices";

    @Autowired
    UserMapper userMapper;
    @Autowired
    BookMapper bookMapper;
    @Autowired
    RedisService redisService;

    /**
     * 查询所有的书籍，可以使用模糊查询实现，查询条件包括图书的名称，作者，图书简介，图书码，等
     * @param conditions
     * @return
     */
    public PageQuery<Book> queryBookByCondition(Map<String, Object> conditions) {

        CommonUtils.fillPageParams(conditions);
        int currentPage = (int) conditions.get("currentPage");
        int pageSize = (int) conditions.get("pageSize");

        CommonUtils.hasAllRequiredAndRemove(conditions,"bookCode,bookName,bookInfo,bookAuthor,bookUser");

        List<Book> books = bookMapper.queryBookByCondition(conditions,(currentPage-1)*pageSize,pageSize);
        for (Book book : books) {
            System.out.println(book);
        }
        Integer totalCount =  books.size();

        PageQuery<Book> pageQuery =new PageQuery<>();
        pageQuery.setList(books);
        pageQuery.setTotalPage(totalCount%pageSize== 0 ?totalCount/pageSize:(totalCount/pageSize+1));
        pageQuery.setPageSize(pageSize);
        pageQuery.setCurrentPage(currentPage);
        pageQuery.setTotalCount(totalCount);
        return pageQuery;
    }

    public void addBook(Map<String, Object> params, HttpSession session) {
        String user = (String) session.getAttribute("user");
        Integer uid = userMapper.getUserIdByName(user);
        Date now = new Date();
        Book book = new Book();
        book.setModifyDate(now);
        book.setBookUser(uid);
        try {
            BeanUtils.populate(book,params);
            logger.info(book);
            bookMapper.addBook(book);
            redisService.refreshRedisBookStatus();
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public int updateBook(Map<String, Object> params, HttpSession session) {
        Book book = new Book();
        String user = (String) session.getAttribute("user");
        Integer uid = userMapper.getUserIdByName(user);
        book.setBookUser(uid);
        book.setModifyDate(new Date());
        try {
            BeanUtils.populate(book,params);
            int res = bookMapper.updateBook(book);
            //清除有关redis，重新添加
            if(res > 0){
                if(params.containsKey("bookNums") ||params.containsKey("bookPrices")){
                    //根据id,让此书的缓存数量和价格无效,从数据库读取
                    redisService.hdelNumsAndPricesByBookId(Integer.parseInt(params.get("id").toString()));
                }
            }
            return res;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 存储书籍的图片，返回书的存储路径
     * @param bookImg
     * @param httpServletRequest
     * @return
     */
    public String saveBookImg(MultipartFile bookImg, HttpServletRequest httpServletRequest){
        if(bookImg == null){return null;}
        FileOutputStream fos = null;
        try {
            //图片的地址，需要保存图片到static目录
            String type = bookImg.getOriginalFilename().split("\\.")[1];
            if (type == null) {
                return null;
            }
            long time = new Date().getTime();
            String imgPath = httpServletRequest.getServletContext().getRealPath("img");
            String path = imgPath + "\\" + time + "." + type;
            logger.info("存储文件路径:" + path);
            fos = new FileOutputStream(path);
            fos.write(bookImg.getBytes());
            fos.close();
            return time+"."+type;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void deleteBook(Integer id, HttpSession session) {
        if(session.getAttribute("user") != null){
            bookMapper.delete(id);
            redisService.hdelNumsAndPricesByBookId(id);
        }
    }


    public List<Book> getBookPricesAndNums() {
        return bookMapper.getPricesAndNums();
    }

    /**
     * 首页的分类查询列表,即根据图书的分类进行查询
     * @param kinds
     * @param size
     * @return
     */
    public Map<String, Object> queryByKinds(List<String> kinds, Integer size) {
        Map<String,Object> ress = new HashMap<>();
        if (size == 0){
            size = 4;
        }
        for (String kind : kinds) {
            List<Book> res =  bookMapper.queryByKind(kind,size);
            if(res != null  && res.size() != 0){
                switch (kind){
                    case "文学":
                        ress.put("literary",res);break;
                    case "社科":
                        ress.put("science",res);break;
                    case "经管":
                        ress.put("manage",res);break;
                    case "少儿":
                        ress.put("children",res);break;
                    case "文教":
                        ress.put("teach",res);break;
                    case "科技":
                        ress.put("technology",res);break;
                    case "生活":
                        ress.put("life",res);break;
                    case "艺术":
                        ress.put("art",res);break;
                    case "店铺优选":
                        ress.put("better",res);break;
                    default:
                        ress.put("better",res);
                }
            }

        }
        return ress;
    }

    /**
     * 查询一本书对应的所有分类
     * @param id
     * @return
     */
    public List<String> queryKindsById(Integer id){
        return bookMapper.queryKindsById(id);
    }

    /**
     * 查询一本书的信息
     * @param id
     * @return
     */
    public Book getBookById(Integer id) {
        Map<String,Object> conditions = new WeakHashMap<>();
        conditions.put("id",id);
        List<Book> books = bookMapper.queryBookByCondition(conditions, 0, 1);
        return books.get(0);
    }

    /**
     * 榜单的实现
     * @param size
     * @return
     */
    public List<Book> getTopList(int size) {
        return bookMapper.queryTopList(size);
    }
}
