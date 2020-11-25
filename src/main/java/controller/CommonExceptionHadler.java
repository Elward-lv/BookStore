package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import exception.AccessException;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import utils.CommonUtils;
import utils.enums.ErrorEnum;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义异常处理
 */
@ControllerAdvice
public class CommonExceptionHadler {
    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)
    @ResponseBody
    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)
    public String CommonExcetption(Exception e) {
        e.printStackTrace();
        return CommonUtils.errorJson(ErrorEnum.E_400,e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String AccessOutOfPower(Exception e) {
        e.printStackTrace();
        return CommonUtils.errorJson(ErrorEnum.E_503,e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public String CommonException(Exception e){
        Map<String,Object> info = Collections.synchronizedMap(new HashMap<>());
        info.put("错误信息:",e.getMessage());
        info.put("错误原因:",e.getCause());
        e.printStackTrace();
        return CommonUtils.errorJson(ErrorEnum.E_400,info);
    }
}
