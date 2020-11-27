package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import domain.Book;
import domain.PageQuery;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.BookServiceImpl;
import service.RedisService;
import service.inter.BookService;
import utils.CommonUtils;
import utils.enums.ErrorEnum;

import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/book")
@CrossOrigin
public class BookController {
    private static Logger logger = Logger.getLogger(BookController.class);
    private static final String PATH = "CourseDesign_war/img/";
    @Autowired
    BookService bookServiceImpl;
    @Autowired
    RedisService redisService;

    /**
     * 获得榜单数据
     */
    @RequestMapping("/topList.do")
    @ResponseBody
    public String getTopList() {
        Set<Object> topList = redisService.sGet("topList");
        return CommonUtils.successJson(topList);
    }

    /**
     * 根据条件查询图书，也可以不指定查询条件
     *
     * @param params
     * @return
     */
    @RequestMapping("/listBooks.do")
    @ResponseBody
    public String listBooks(@RequestBody Map<String, Object> params) {
        if (CommonUtils.hasAllRequiredAndRemove(params, "bookCode,bookName,bookInfo,bookAuthor,bookUser,currentPage,pageSize")) {
            PageQuery<Book> pageQuery = bookServiceImpl.queryBookByCondition(params);
            return CommonUtils.successJson(pageQuery);
        }
        return CommonUtils.errorJson(ErrorEnum.E_90003, null);
    }

    //@RequestParam("types") List<String> conditions, @RequestParam(value = "size",required = false) Integer size
    //
    @RequestMapping(value = "/listIndexBooks.do",method = RequestMethod.POST)
    @ResponseBody
    public String listIndexBooks(@RequestBody Map<String, Object> params) {
        logger.info("=======>用户名:"+SecurityUtils.getSubject().getPrincipal());
        CommonUtils.hasAllRequiredAndRemove(params,"types,size");
        List<String> conditions = (List<String>) params.get("types");
        Integer size = Integer.parseInt(params.get("size").toString());

        logger.info("首页的分类查询条件:" + conditions);
        Map<String, Object> res = bookServiceImpl.queryByKinds(conditions, size);
        return CommonUtils.successJson(res);
    }

    @RequestMapping("/queryBooksForSupply.do")
    @ResponseBody
    public String queryBooksForSupply(@RequestBody Map<String, Object> conditions){
        PageQuery<Book> pageQuery = bookServiceImpl.queryBooksForSupply(conditions);
        return CommonUtils.successJson(pageQuery);
    }

    /**
     * 获取book的详细信息,包括种类
     *
     * @param bookId
     * @return
     */
    @RequestMapping("/getBookInfo.do")
    @ResponseBody
    public String getKindsById(@RequestParam("id") Integer bookId) {
        Book book = bookServiceImpl.getBookById(bookId);
        logger.info("id--------------------------->"+bookId+"\n");
        List<String> kinds = bookServiceImpl.queryKindsById(bookId);
        book.setKinds(kinds);
        return CommonUtils.successJson(book);
    }

    /**
     * 添加图书,需要验证登陆状态,而且必须是商户
     *
     * @param params
     * @param file
     * @param httpServletRequest
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/addBook.do", method = RequestMethod.POST)
    @ResponseBody
    public String addBook(@RequestParam Map<String, Object> params, @RequestPart("bookimg") MultipartFile file, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        String res = bookServiceImpl.saveBookImg(file, httpServletRequest);
        if (res != null) {
            params.put("bookImg", PATH + "/" + res);
            if (CommonUtils.hasAllRequiredAddAndRemove(params, "bookCode,bookName,bookInfo,bookAuthor,bookNums,bookImg,bookPrice")) {
                bookServiceImpl.addBook(params);
                return CommonUtils.successJson(null);
            }
        }
        return CommonUtils.errorJson(ErrorEnum.E_90003, null);
    }

    /**
     * 更新图书，id字段必须,必须是商户
     *
     * @param file
     * @param params
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping("/updateBook.do")
    @ResponseBody
    public String updateBook(@RequestPart(value = "file", required = false) MultipartFile file, @RequestParam Map<String, Object> params, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        String res = bookServiceImpl.saveBookImg(file, httpServletRequest);
        if (res != null) {
            params.put("bookImg",res);
        }
        CommonUtils.hasAllRequiredAndRemove(params, "id,bookCode,bookName,bookInfo,bookAuthor,bookNums,bookImg,bookPrice");
        Object id = params.get("id");
        if (id != null && bookServiceImpl.updateBook(params) >= 0 ) {
            return CommonUtils.successJson(null);
        }

        return CommonUtils.errorJson(ErrorEnum.E_90003, null);
    }

    @RequestMapping("/delete.do")
    @ResponseBody
    public String deleteBook(@RequestParam("id") Integer id, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        System.out.println("删除id:" + id);
        bookServiceImpl.deleteBook(id);
        return CommonUtils.successJson(null);
    }

    /**
     * 测试post请求时，RequestPart接收文件类型参数
     */
    @RequestMapping(value = "/test.do", method = RequestMethod.POST)
    @ResponseBody
    public String getFileAndParam(@RequestPart("file") MultipartFile file, HttpServletRequest hsr) throws JsonProcessingException {
        System.out.println("参数名:" + file.getName());
        System.out.println("文件名:" + file.getOriginalFilename());
        String filename = file.getOriginalFilename();
        String type = filename.split("\\.")[1];
        String path = hsr.getServletContext().getRealPath("img");
        logger.info("img 文件夹path:" + path);
        Date d = new Date();
        long time = d.getTime();
        String filepath = path + "\\" + time + "." + type;
        try (
                FileOutputStream fos = new FileOutputStream(filepath)
        ) {
            fos.write(file.getBytes());
            fos.close();
            logger.info("保存到数据库中的文件名:" + filepath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return CommonUtils.successJson(null);
    }

    /**
     * 测试redis的使用
     *
     * @param key
     * @param field
     * @return
     */
    @RequestMapping(value = "/redis.do")
    @ResponseBody
    public String testRedis(@RequestParam("key") String key, @RequestParam("field") String field) {
        Object value = redisService.hget(key, field);
        return CommonUtils.successJson(value);
    }

    @RequestMapping("/getPrincipal.do")
    @ResponseBody
    public String testShiroSession(){
        return CommonUtils.successJson(SecurityUtils.getSubject().getPrincipal());//userName
    }
}
