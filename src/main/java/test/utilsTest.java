package test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.io.Resources;
import org.junit.Test;
import utils.CommonUtils;

import java.io.*;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class utilsTest {
    //测试通过
    @Test
    public void test1(){
        String parmas = "username,password,gender,birthday,email";
        Map<String,Object> map = new HashMap<>();
        map.put("username","lvyanwei");
        map.put("password","66666");
        map.put("gender",1);
        boolean res = CommonUtils.hasAllRequiredAddAndRemove(map, parmas);
        for (Object o : map.keySet()) {
            System.out.println(o+":"+map.get(o));
        }
        System.out.println(res);
    }

    @Test
    public void test2(){
        Integer i = 10;
        getInteger(i);
        System.out.println(i);//10
    }

    @Test
    public void test4(){
        Map<String,String> map = new HashMap<>();
        map.put("name","lvyanwei");
        map.put("age","10");
        Map<String ,String > map2 = new HashMap<>(map);
        System.out.println(map == map2);
        System.out.println(map2.get("age"));
    }

    @Test
    public void test5() throws JsonProcessingException {
        Map<String,String> map = new HashMap<>();
        map.put("name","lvyanwei");
        map.put("age","10");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(map);
        System.out.println(json);
        Map map2 = objectMapper.readValue(json, Map.class);
        for (Object o : map2.keySet()) {
            System.out.println(o+":"+map2.get(o));
        }
    }

    @Test
    public void test3() throws IOException {
        String s = "1.jpg";
        String[] strings = s.split("\\.");
        for (String string : strings) {
            System.out.println(string);
        }
        //测试logger路径
        URL url = utilsTest.class.getClassLoader().getResource("");
        System.out.println("loadStream:"+url);
        //      file:/F:/IJ-idea/CourseDesign/target/classes/
        File f = new File("");
        System.out.println(f.getAbsolutePath());
    }

    private static void getInteger(Integer i){
        i = 1;
    }
}
