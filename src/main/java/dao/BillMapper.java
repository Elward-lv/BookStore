package dao;

import domain.Bill;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BillMapper {
    List<Bill> queryByConditions(@Param("conditions") Map<String, Object> conditions,@Param("start") Integer start,@Param("size") Integer size);

    int addBill(Bill bill);
}
