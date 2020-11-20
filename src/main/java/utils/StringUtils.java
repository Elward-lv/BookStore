package utils;

public class StringUtils {
    /**
     * 判断字符串是否为空或者null
     * 是返回true
     * @param s
     * @return
     */
    public static boolean isNullOrEmpty(String s){
        if(s == null || "".equals(s)){
            return true;
        }
        return false;
    }
}
