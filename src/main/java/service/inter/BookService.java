package service.inter;

import domain.Book;
import domain.PageQuery;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

public interface BookService {

    public PageQuery<Book> queryBookByCondition(Map<String, Object> conditions);

    public PageQuery<Book> queryBooksForSupply(Map<String,Object> conditions);

    public void addBook(Map<String, Object> params);

    public int updateBook(Map<String, Object> params);

    public String saveBookImg(MultipartFile bookImg, HttpServletRequest httpServletRequest);

    public void deleteBook(Integer id);

    public List<Book> getBookPricesAndNums();

    public Map<String, Object> queryByKinds(List<String> kinds, Integer size);

    public List<String> queryKindsById(Integer id);

    public Book getBookById(Integer id);

    public List<Book> getTopList(int size);
}
