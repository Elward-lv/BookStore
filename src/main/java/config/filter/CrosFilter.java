package config.filter;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CrosFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String originHeader=((HttpServletRequest) request).getHeader("Origin");
        httpServletResponse.setHeader("Access-Control-Allow-Origin", originHeader);
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "x-requested-with,Authorization,content-type");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");

        String requestMethod = httpRequest.getMethod();
        if (requestMethod.equals(RequestMethod.OPTIONS.name())) {
            setHeader(httpRequest, httpServletResponse);
            return;
        }

        Cookie[] cookies = httpRequest.getCookies();
        if(cookies != null && cookies.length>0){
            int count = 0 ;
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    break;
                }{
                    count++;
                }
            }
            if(count >= cookies.length){
                addCookie(httpRequest,httpServletResponse);
            }
        }else {
            addCookie(httpRequest,httpServletResponse);
        }

        chain.doFilter(request, httpServletResponse);
    }

    private void setHeader(HttpServletRequest request, HttpServletResponse response) {
        //跨域的header设置
        String origin = request.getHeader("origin");
        response.setHeader("Access-Control-Allow-Origin", origin);
        //这句可以解决当请求为put和delete时的跨域问题
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        //防止乱码，适用于传输JSON数据
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.setStatus(HttpStatus.OK.value());
    }

    /**
     * 添加cookie JSESSIONID
     * @param httpRequest
     * @param httpServletResponse
     */
    private void addCookie(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse){
        System.out.println("===================>add cookie to user agent");
        Cookie cookie = new Cookie("JSESSIONID", httpRequest.getSession().getId());
        cookie.setPath(httpRequest.getContextPath());
        httpServletResponse.addCookie(cookie);
    }

    @Override
    public void destroy() {

    }
}
