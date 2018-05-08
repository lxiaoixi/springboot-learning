package com.example.helloworld.Filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

//实现 Filter 接口，实现 Filter 方法 添加@Configuration 注解，将自定义 Filter 加入过滤链
public class MyFilter implements javax.servlet.Filter {

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

    @Override
    public void doFilter(ServletRequest srequest, ServletResponse sresponse, FilterChain filterChain)
            throws IOException, ServletException {
        // TODO Auto-generated method stub
        HttpServletRequest request = (HttpServletRequest) srequest;
        System.out.println("this is MyFilter,url :"+request.getRequestURI());
        filterChain.doFilter(srequest, sresponse);
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub
    }
}
