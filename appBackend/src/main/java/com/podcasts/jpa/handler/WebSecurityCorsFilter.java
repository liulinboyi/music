package com.podcasts.jpa.handler;


import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

/**
 * @Author: Jet
 * @Description: 手动生成过滤器，目的是跨域
 * @Date: 2018/5/23 16:55
 */
public class WebSecurityCorsFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;
        String origin = ((HttpServletRequest) request).getHeader("Origin");
        res.setHeader("Access-Control-Allow-Origin", origin);
//        res.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:8080");
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        res.setHeader("Access-Control-Max-Age", "3600");
        res.setHeader("Access-Control-Allow-Credentials", "true");
//        res.setHeader("Vary", "Origin");
        res.setHeader("Access-Control-Allow-Headers", "Origin, Authorization, Content-Type, Accept, x-requested-with, Cache-Control");

        chain.doFilter(request, res);
    }

    @Override
    public void destroy() {
    }
}