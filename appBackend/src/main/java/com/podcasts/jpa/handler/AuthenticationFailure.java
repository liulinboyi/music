package com.podcasts.jpa.handler;


import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//登录失败返回给前端消息
@Component
public class AuthenticationFailure implements AuthenticationFailureHandler {
    @Autowired
    Gson gson;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        Msg msg = null;
        if (e instanceof UsernameNotFoundException) {
            msg = Msg.fail(e.getMessage());
        } else if (e instanceof BadCredentialsException) {
            msg = Msg.fail("密码错误!!");
        } else {
            msg = Msg.fail(e.getMessage());
        }
        //处理编码方式，防止中文乱码的情况
        response.setContentType("application/json;charset=utf-8");
        //返回给前台
        response.getWriter().write(gson.toJson(msg));
    }
}
