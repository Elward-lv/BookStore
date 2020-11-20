package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.BillMapper;
import dao.BookMapper;
import dao.UserMapper;
import domain.Bill;
import domain.PageQuery;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import service.inter.BillService;
import utils.CommonUtils;

import javax.servlet.http.HttpSession;
import java.util.*;

public class BillServiceImpl implements BillService {
    private static Logger logger = Logger.getLogger(BillServiceImpl.class);
    private static ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    BillMapper billMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    BookMapper bookMapper;
    @Autowired
    RedisService redisService;

    /**
     * 如果是管理员可以查询所有账单，否则其他用户只能查询自己的账单
     * @param conditions
     * @param session
     */
    public PageQuery<Bill> queryByConditions(Map<String, Object> conditions, HttpSession session) {
        CommonUtils.fillPageParams(conditions);
        String userRoleName = (String) session.getAttribute("user");
        Integer currentPage = (Integer) conditions.get("currentPage");
        Integer pageSize = (Integer) conditions.get("pageSize");

        if("管理员".equals(userRoleName)){
            conditions.remove("billUser");
        }else {
            conditions.put("billUser",userMapper.getUserIdByName(userRoleName));
        }
        CommonUtils.hasAllRequiredAndRemove(conditions,"isPayed,billInfo,billBook,billUser");
        List<Bill> result = billMapper.queryByConditions(conditions,(currentPage-1)*pageSize,pageSize);
        PageQuery<Bill> pageQuery = new PageQuery<>();
        int totalCount = result.size();
        pageQuery.setTotalCount(totalCount);
        pageQuery.setPageSize(pageSize);
        pageQuery.setCurrentPage(currentPage);
        pageQuery.setList(result);
        pageQuery.setTotalPage(totalCount%pageSize == 0?totalCount/pageSize:(totalCount/pageSize+1));
        return pageQuery;
    }

    /**
     * 可以把价钱存入缓存之中,不过每次book改变时需要修改
     * 需要把商品和个数对应的map，转化为json字符串
     * @param params 一般包含个数和id对应map，以及billInfo账单留言
     * @param session
     * @return
     */
    public boolean addBill(Map<String, Object> params, HttpSession session) {
        boolean res = false;
        //开始执行更新
        String billInfo = (String) params.remove("billInfo");
        String userName = (String) session.getAttribute("user");
        try {
            if (!convertIntToJsonAndCheckEnough(params)) {
                return res;//数量校验不通过,说明书籍容量不够
            }
            Double billPrices = (Double) params.get("billPrices");
            String billBook = (String) params.get("billBook");
            Bill bill = new Bill();
            bill.setBillBook(billBook);
            bill.setBillInfo(billInfo);
            bill.setBillPrices(billPrices);
            bill.setIsPayed(0);
            bill.setBillUser(userMapper.getUserIdByName(userName));
            billMapper.addBill(bill);
            //刷新书本数量
            descBookNums(billBook);
            res = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 把map中的键值对：string->int转化为json保存到数据库,以后可以改成redis
     * 并且计算总价钱
     * @param params
     * @return json，并且把总价钱结果加入map之中
     */
    public boolean convertIntToJsonAndCheckEnough(Map<String, Object> params) throws JsonProcessingException {
        Map<Integer,Integer> json = Collections.synchronizedMap(new HashMap<Integer, Integer>());
        Double countMoney = 0.0;
        for (String key : params.keySet()) {
            logger.info(key+":"+params.get(key));
            int b_id = Integer.parseInt(key);
            String p_count = (String) params.get(key);
            int count = Integer.parseInt(p_count);
            //剩余数量校验
            Object bookNums = redisService.hget("bookNums", key);
            if(count > Integer.parseInt(bookNums.toString())){
                return false;
            }
            json.put(b_id, count);
            countMoney += bookMapper.getPricesById(b_id) * count;
        }
        params.put("billPrices",countMoney);
        params.put("billBook",objectMapper.writeValueAsString(json));
        logger.info("转化的josn如下:"+objectMapper.writeValueAsString(json));
        return true;
    }

    /**
     *
     * @param json 存放图书数量和图书id的对应关系,id为key
     */
    public void descBookNums(String json){
        try {
            Map map = objectMapper.readValue(json,Map.class);
            for (Object key : map.keySet()) {
                Integer b_id = Integer.parseInt(key.toString());
                Integer b_num = Integer.parseInt(map.get(key).toString());
                //更新售卖数据
                bookMapper.descBookNumById(b_id,b_num);
                //刷新redis
                Object nums = redisService.hget("bookNums", b_id.toString());
                redisService.hset("bookNums",b_id.toString(),nums.toString());
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }

    }
}
