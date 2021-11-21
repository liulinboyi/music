//package com.podcasts.jpa.handler;
//
//
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseCookie;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.time.Duration;
//import java.util.Collection;
//
///**
// * @Author: Jet
// * @Description: 手动生成过滤器，目的是跨域
// * @Date: 2018/5/23 16:55
// */
//@Component
//public class CookieCorsFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
//
//        HttpServletResponse rep = response;
//        HttpServletResponse res = response;
//
//        Collection<String> headers = rep.getHeaders(HttpHeaders.SET_COOKIE);
//        System.out.println("CookieCorsFilter");
//        System.out.println(headers);
////        ResponseCookie cookie = ResponseCookie.from("myCookie", "myCookieValue") // key & value
////                .httpOnly(true)     // 禁止js读取
////                .secure(false)      // 在http下也传输
//////                .domain("localhost")// 域名
//////                .path("/")          // path
////                .maxAge(Duration.ofHours(1))    // 1个小时候过期
////                .sameSite("None")    // 大多数情况也是不发送第三方 Cookie，但是导航到目标网址的 Get 请求除外
////                .build();
//
//        boolean firstHeader = true;
//
//        for (String header : headers) {
//            if (firstHeader) {
//                if (header.endsWith("SameSite=None; Secure")) {
//                    continue;
//                }
//                rep.setHeader(HttpHeaders.SET_COOKIE,
//                        String.format("%s; %s", header, "SameSite=None; Secure"));
//                firstHeader = false;
//                continue;
//            }
//            if (header.endsWith("SameSite=None; Secure")) {
//                continue;
//            }
//            rep.addHeader(HttpHeaders.SET_COOKIE,
//                    String.format("%s; %s", header, "SameSite=None; Secure"));
//        }
////        HttpServletRequest req = (HttpServletRequest) request;
////        String origin = ((HttpServletRequest) request).getHeader("Origin");
////        res.setHeader("Access-Control-Allow-Origin", origin);
//////        res.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:8080");
////        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
////        res.setHeader("Access-Control-Max-Age", "3600");
////        res.setHeader("Access-Control-Allow-Credentials", "true");
//////        res.setHeader("Vary", "Origin");
////        res.setHeader("Access-Control-Allow-Headers", "Origin, Authorization, Content-Type, Accept, x-requested-with, Cache-Control");
//
//        chain.doFilter(request, res);
//    }
//}