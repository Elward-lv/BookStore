package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import utils.enums.Constants;
import utils.enums.ErrorEnum;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CommonUtils {
    private static Logger logger = Logger.getLogger(CommonUtils.class);
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final Integer defaultPageSize = 6;
    private static final Integer defaultCurrentPage = 1;

    /**
     * 返回成功的Json字符串
     *
     * @param info
     * @return
     */
    public static String successJson(Object info) {
        ConcurrentHashMap<String, Object> res = new ConcurrentHashMap<>();
        res.put("msg", Constants.SUCCESS_MSG);
        res.put("code", Constants.SUCCESS_CODE);
        if(info!=null){
            res.put("info", info);
        }
        return catchException(res);
    }

    /**
     * 根据失败的原因返回失败Json字符串
     *
     * @param errorEnum
     * @param info
     * @return
     */
    public static String errorJson(ErrorEnum errorEnum, Object info) {
        ConcurrentHashMap<String, Object> res = new ConcurrentHashMap<>();
        res.put("msg", errorEnum.getErrorMsg());
        res.put("code", errorEnum.getErrorCode());
        if(info!=null){
            res.put("info", info);
        }
        return catchException(res);
    }

    public static String catchException(Object obj){
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            //e.printStackTrace();
            throw new RuntimeException("json转化异常,"+e);
        }
    }


    /**
     * 查看是否缺少对应的参数，缺少的话直接在map填写对应的缺少信息,返回false
     * 如果不缺少，把多余的参数去除
     *
     * @param params        map
     * @param requeiredCols 使用，隔开
     */
    public static boolean hasAllRequiredAddAndRemove(Map<String, Object> params, String requeiredCols) {
        boolean res = true;
        if (!StringUtils.isNullOrEmpty(requeiredCols)) {
            StringBuilder missCols = new StringBuilder();
            String[] req_params = requeiredCols.split(",");
            for (String req_param : req_params) {
                if (params.get(req_param) == null) {
                    missCols.append(req_param).append("  ");
                    res = false;
                }
            }
            //缺少必要字段
            if (!StringUtils.isNullOrEmpty(missCols.toString())) {
                params.clear();
                params.put("msg", "缺少必要参数 " + missCols.toString());
                params.put("code", ErrorEnum.E_90003.getErrorCode());
            } else {
                //去除多余的参数,保证安全
                hasAllRequiredAndRemove(params,requeiredCols);
            }
        }
        return res;
    }

    /**
     * 判断map中是否含有所有的参数，可能范围外参数的去除
     * 和上边类似，但是此函数不会往map添加新的值,也就是说不会返回异常信息
     *      这里使用foreach遍历，并删除的话，报异常ConcurrentModificationException
     * @param params
     * @param requeiredCols
     * @return
     */
    public static boolean hasAllRequiredAndRemove(Map<String, Object> params, String requeiredCols) {
        boolean res = true;
        if (!StringUtils.isNullOrEmpty(requeiredCols)) {
            //去除多余的参数,保证安全
            Set<Map.Entry<String, Object>> entrySet = params.entrySet();
            Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();
            while (iterator.hasNext()){
                Map.Entry<String, Object> next = iterator.next();
                String key = next.getKey();
                if(!requeiredCols.contains(key)){
                    //特别注意：不能使用map.remove(name)  否则会报同样的错误
                    res = false;
                    iterator.remove();//Iterator.remove()是删除最近（最后）使用next()方法的元素。
                }
            }
        }
        return res;
    }

    /**
     * 把分页的默认参数填充到map中,填充参数用户不指定的话这里设置为-1，service可以通过此知道未设置此参数，从而设置为默认值
     * @param conditions
     */
    public static void fillPageParams(Map<String,Object> conditions){
        Object cp =  conditions.get("currentPage");
        Object ps =  conditions.get("pageSize");
        if(cp != null && ps   != null){
            //转化数字
            try{
                Integer i = (Integer)cp;
                Integer j = (Integer)ps;
                if(i >= 0 && j > 0){
                    int currentPage = i;
                    int pageSize = j;
                    conditions.put("currentPage",currentPage);
                    conditions.put("pageSize",pageSize);
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        conditions.put("currentPage",defaultCurrentPage);
        conditions.put("pageSize",defaultPageSize);
    }

    public static void removeNotIntFromMap(Map<String, Object> cart){
        Iterator<String> iterator = cart.keySet().iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            String value = (String) cart.get(key);
            try{
                //if(Integer.parseInt(value) < 0)
            }catch (Exception e){
                System.out.println("<======="+value+":转化异常=======>");
            }
        }
    }

    /**
     * map之间的转化
     * @param orgin
     * @param res
     */
    public static void convertMap(Map<Object,Object> orgin,Map<String,Object> res){
        try {
            for (Object o : orgin.keySet()) {
                String s = (String) o;
                res.put(s,orgin.get(o));
            }
        } catch (Exception e) {
            logger.error("Map<Object,Object> ---> Map<String,Object> error!");
            e.printStackTrace();
        }
    }
}
