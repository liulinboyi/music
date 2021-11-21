package com.podcasts.jpa.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class JsonAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        if (!"POST".equals(request.getMethod())) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        // 判断 ContentType 类型
        if (request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {

            // 获取请求内容
            Map<String, String> loginData = new HashMap<>(2);

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IOUtils.copy(request.getInputStream(), baos);
                byte[] requestBody = baos.toByteArray();
                final ByteArrayInputStream bais = new ByteArrayInputStream(requestBody);

                loginData = new ObjectMapper().readValue(bais, Map.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String username = String.valueOf(loginData.get(getUsernameParameter()));
            String password = String.valueOf(loginData.get(getPasswordParameter()));
            username = username != null ? username : "";
            username = username.trim();
            password = password != null ? password : "";

            // 创建 Authentication
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, password);
            setDetails(request, authentication);

            // 执行身份验证
            return this.getAuthenticationManager().authenticate(authentication);
        } else {
            return super.attemptAuthentication(request, response);
        }
    }
}
