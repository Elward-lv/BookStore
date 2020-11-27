package dao;

import domain.Book;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BookMapper {
    int addBook(Book book);

    List<Book> queryBookByCondition(@Param("conditions") Map<String, Object> conditions,
                                    @Param("start") int start, @Param("size") int size
                                    ,@Param("uId") int uId);

    int updateBook(Book book);

    int delete(Integer id);

    Double getPricesById(int id);

    List<Book> getPricesAndNums();

    List<Book> queryByKind(@Param("kind") String kind, @Param("size") Integer size );

    List<String> queryKindsById(Integer id);

    int descBookNumById(@Param("id") int b_id,@Param("num") int b_num);

    List<Book> queryTopList(int size);

}
