package com.podcasts.jpa.config;

import com.podcasts.jpa.handler.*;
import com.podcasts.jpa.handler.validate.ValidateCodeFilter;
import com.podcasts.jpa.service.UserService;
import com.podcasts.jpa.handler.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.userdetails.UserDetailsService;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //开启权限注解,默认是关闭的
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserService userService;
    @Autowired
    AuthenticationSuccess authenticationSuccess;    //登录成功
    @Autowired
    AuthenticationFailure authenticationFailure;    //登录失败
    @Autowired
    AuthenticationLogout authenticationLogout;      //注销
    @Autowired
    AccessDeny accessDeny;      //无权访问
    @Autowired
    AuthenticationEnryPoint authenticationEnryPoint;    //未登录
    @Autowired
    SessionInformationExpiredStrategy sessionInformationExpiredStrategy;    //检测异地登录
    @Autowired
    SelfAuthenticationProvider selfAuthenticationProvider;      //自定义认证逻辑处理
    @Autowired
    private ValidateCodeFilter validateCodeFilter;
//    @Autowired
//    CookieCorsFilter cookieCorsFilter;


    @Bean
    public UserDetailsService userDetailsService() {
        return new UserService();
    }

    //加密方式
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(10);
    }


    @Bean
    RoleHierarchy roleHierarchy() {
        // 角色继承
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_admin > ROLE_assessor > ROLE_user");
        return hierarchy;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userService);
        auth.authenticationProvider(selfAuthenticationProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class); //添加验证码效验过滤器

        http.authorizeRequests()
                .antMatchers("/identity/delete", "/classify/create", "/classify/delete", "/classify/update").hasRole("admin")
                .antMatchers().hasRole("assessor")
                .antMatchers("/personalColumn/update", "/personalColumn/delete", "/personalColumn/create", "/identity/detail", "/identity/update", "/file/upload", "/file/download", "/file/update", "/file/deleteById", "/file/updateMedia", "/file/findAllMediaByUserId", "/personalColumn/create", "/personalColumn/delete", "/personalColumn/update", "/file/uploadImg", "/myself").hasRole("user")
                .antMatchers("/personalColumn/findAll", "/personalColumn/findAllByUserId", "/personalColumn/findById", "/identity/create", "/classify/findAll", "/file/video/player", "/file/findMediaById", "/personalColumn/findAll", "/personalColumn/findAllByUserId", "/personalColumn/findById", "/personalColumn/findAllByClassifyId", "/media/getMediaByArray").permitAll()
                .antMatchers("/code/image"/*验证码*/, "/personalColumn/findDetailById", "/comment/getComment","/register").permitAll()
                .anyRequest().authenticated() //尚未匹配的任何URL都要求用户进行身份验证

                .and()
                .formLogin()  //开启登录
                .permitAll()  //允许所有人访问
                .successHandler(authenticationSuccess) // 登录成功逻辑处理
                .failureHandler(authenticationFailure) // 登录失败逻辑处理
                .and()
                //增加自动登录功能，默认为散列加密
                .rememberMe()
                .tokenValiditySeconds(Math.round(60 * 7 * 24 * 60)) // 7天内记住密码
                .key("podcasts")

                .and()
                .logout()   //开启注销
                .permitAll()    //允许所有人访问
                .logoutSuccessHandler(authenticationLogout) //注销逻辑处理
                .deleteCookies("JSESSIONID")    //删除cookie

                .and().exceptionHandling()
                .accessDeniedHandler(accessDeny)    //权限不足的时候的逻辑处理
                .authenticationEntryPoint(authenticationEnryPoint)  //未登录是的逻辑处理

                .and()
                .sessionManagement()
                .maximumSessions(1)     //最多只能一个用户登录一个账号
                .expiredSessionStrategy(sessionInformationExpiredStrategy);//异地登录的逻辑处理
        http.addFilterBefore(new WebSecurityCorsFilter(), ChannelProcessingFilter.class); // 保证跨域的过滤器首先触发
    }
}
