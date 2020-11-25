package service.inter;

import com.fasterxml.jackson.core.JsonProcessingException;
import domain.Bill;
import domain.PageQuery;
import org.apache.shiro.session.Session;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface BillService {

    public PageQuery<Bill> queryByConditions(Map<String, Object> conditions);

    public boolean addBill(Map<String, Object> params);

    public boolean convertIntToJsonAndCheckEnough(Map<String, Object> params) throws JsonProcessingException;

    public void descBookNums(String json);
}
