package com.example.mybatis.shiro.config;

import com.alibaba.fastjson.JSON;
import com.example.mybatis.common.response.ErrorResponse;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JWTStatelessAccessControlFilter extends AccessControlFilter {
    @Override
    public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        // 允许跨域
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        httpServletResponse.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return isAccessAllowed(request, response, mappedValue) || onAccessDenied(request, response, mappedValue);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o){
        return false;
    }

    /**
     *
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        // 获取header
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //获取web端传来的token
        String token = request.getHeader("x-auth");
        JWTStatelessAuthenticationToken jwtStatelessAuthenticationToken = new JWTStatelessAuthenticationToken(token);
        try {
            // 委托给Realm进行登录,在realm进行身份认证
            super.getSubject(servletRequest, servletResponse).login(jwtStatelessAuthenticationToken);
        } catch (Exception e) {
            // e.printStackTrace();
            // 登录失败.
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            //返回401编码.
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ErrorResponse errorResponse = new ErrorResponse(1002,"未授权");
            httpServletResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
            httpServletResponse.getWriter().write(JSON.toJSONString(errorResponse));
            return false;// 直接返回请求者.
        }
        return true;// 登录成功.
    }
}
