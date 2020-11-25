package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.RedisService;
import service.inter.BillService;
import service.inter.BookService;
import utils.CommonUtils;
import utils.enums.ErrorEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/bill")
@CrossOrigin
public class BillController {

    @Autowired
    BillService billService;
    @Autowired
    RedisService redisService;
    @Autowired
    BookService bookServiceImpl;

    /**
     * 查询账单信息,登陆之后可以访问,支持条件查询
     *  支持分页
     * @param params
     * @return
     */
    @RequestMapping(value = "/listBills.do",method = RequestMethod.POST)
    @ResponseBody
    public String listBills(@RequestBody Map<String, Object> params, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        CommonUtils.hasAllRequiredAndRemove(params, "isPayed,billInfo,billBook,currentPage,pageSize");
        billService.queryByConditions(params);
        return CommonUtils.successJson(null);
    }

    /**
     * 已经确定的账单,提交了的账单，只差支付(现在还未实现支付)
     * 不需要参数校验，因为参数是未知的
     * service里面会校验数量的合法性
     * @param params  map类型：图书id -> 图书购买数量
     * @return json
     */
    @RequestMapping(value = "/addBill.do",method = RequestMethod.POST)
    @ResponseBody
    public String addBill(@RequestBody Map<String, Object> params) {
        if(billService.addBill(params)){
            return CommonUtils.successJson(null);
        }
        return CommonUtils.errorJson(ErrorEnum.E_90003, null);
    }

    /**
     * 根据map<id,nums></>加入购物车，但是未确定是账单，存入redis中
     * 把用户名作为键值，hash表保存用户的购物车
     * @param params map<id,nums>
     * @return
     */
    @RequestMapping("/addBookToCart.do")
    @ResponseBody
    public String addBookToCart(@RequestParam Map<String,Object> params) {
        Subject subject = SecurityUtils.getSubject();
        checkCart(subject.getSession());
        //bookService.
        String userName = (String) subject.getPrincipal();
        if(userName != null){
            redisService.increNumsByMap(userName,params);
            return CommonUtils.successJson(null);
        }
        return CommonUtils.errorJson(ErrorEnum.E_400,null);
    }

    /**
     * 购买全部购物车的商品
     * @return
     */
    @RequestMapping("/buyAndCearCart.do")
    @ResponseBody
    public String buyAndCearCart() {
        Subject subject = SecurityUtils.getSubject();
        checkCart(subject.getSession());
        String userName = (String) subject.getPrincipal();
        if(userName != null){
            Map<String, Object> cart = Collections.synchronizedMap(new HashMap<>());
            CommonUtils.convertMap(redisService.hmget(userName),cart);
            if (billService.addBill(cart)) {
                CommonUtils.successJson(null);
            }
        }
        return CommonUtils.errorJson(ErrorEnum.E_10009,null);
    }

    /**
     * 清理购物车的某一项或者全部,不会购买变成订单
     * @param ids  需要清除的一个或多个书的id
     * @return
     */
    @RequestMapping("/clearCart.do")
    @ResponseBody
    public String clearCart(@RequestParam("id")List<String> ids) {
        String userName = (String) SecurityUtils.getSubject().getPrincipal();
        if(userName != null){
            for (String id : ids) {
                redisService.hdel(id);
            }
            return  CommonUtils.successJson(null);
        }
        return CommonUtils.errorJson(ErrorEnum.E_90003,null);
    }

    /**
     * 直接购买图书，不需要经历购物车;
     * 需要添加bill账单，减少book数目，并且刷新redis
     * @param id 一次购买一本书的id
     * @return
     */
    @RequestMapping("/payBill.do")
    @ResponseBody
    public String payBill(@RequestParam (value = "id",required = true) Integer id) {
        Map<String, Object> map = Collections.synchronizedMap(new HashMap<String, Object>());
        map.put(id.toString(),1);
        if(billService.addBill(map)){
            return CommonUtils.successJson(null);
        }
        return CommonUtils.errorJson(ErrorEnum.E_400,null);
    }

    /**
     * 获取和组装购物车信息，并且检查是否超出数量
     * @param session
     * @return
     */
    public void checkCart(Session session){
        Map<Object,Object> cart = redisService.hmget((String) session.getId());
        if( cart != null){
            for (Object key : cart.keySet()) {
                Integer bookNum = Integer.parseInt(redisService.hget("bookNums", key.toString()).toString());
                if(bookNum < Integer.parseInt(cart.get(key).toString())){
                    //刷新redis的书本数量
                    redisService.hset((String) session.getId(),key.toString(),bookNum);
                }
            }
        }
    }
}
