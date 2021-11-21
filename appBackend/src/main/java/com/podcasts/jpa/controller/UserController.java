package com.podcasts.jpa.controller;

import com.podcasts.jpa.pojo.User;
import com.podcasts.jpa.service.UserService;
import com.podcasts.jpa.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController

public class UserController {
    @Autowired
    UserService userService;

    RememberMeServices rememberMeServices;


    @GetMapping("/myself")
    public void my(HttpServletRequest request, HttpServletResponse response, Authentication aut) throws IOException {
        System.out.println("aut" + aut.getCredentials());
        UserDetails nowUser = null;
        Message message = null;
        try {
            // 登陆状态不可以创建新用户
            String username = "";
// 获取安全上下文对象，就是那个保存在 ThreadLocal 里面的安全上下文对象
// 总是不为null(如果不存在，则创建一个authentication属性为null的empty安全上下文对象)
            SecurityContext securityContext = SecurityContextHolder.getContext();
// 获取当前认证了的 principal(当事人),或者 request token (令牌)
// 如果没有认证，会是 null,该例子是认证之后的情况
            Authentication authentication = securityContext.getAuthentication();
// 获取当事人信息对象，返回结果是Object类型，但实际上可以是应用程序自定义的带有更多应用相关信息的某个类型。
// 很多情况下，该对象是Spring Security核心接口UserDetails的一个实现类，
// 可以把UserDetails想像成数据库中保存的一个用户信息到SecurityContextHolder中Spring Security需要的用户信息格式的一个适配器。
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
            System.out.println(username);
            message = userService.getUserDetail(username, null);
        } catch (Exception e) {
            // 创建新用户
            System.out.println(e);
            message = new Message(false, 400, "失败", "");
        }
        message.returnJson(response);
    }

    /**
     * showdoc
     *
     * @param id       可选 Long 用户id
     * @param username 可选 String 用户名
     * @return {"data":{"id":1,"power":1,"username":"podcasts","password":"********","accountNonExpired":true,"accountNonLocked":true,"credentialsNonExpired":true,"enabled":true,"roles":[{"id":1,"name":"ROLE_admin","nameZh":"管理员"}],"authorities":[{"authority":"ROLE_admin"}]},"success":true,"message":"","status":200}
     * @catalog 用户接口
     * @title 用户详情
     * @description 访问权限：用户；通过id或username查找用户详细信息
     * @method get
     * @url /identity/detail
     * @return_param status int 状态码
     * @return_param data String 数据
     * @return_param success Boolean 是否成功
     * @return_param message String 消息
     * @remark null
     * @number null
     */
    @GetMapping("/identity/detail") // 查找用户接口，by id，username
    public void detail(@RequestParam(name = "username", required = false) String username, @RequestParam(name = "id", required = false) Long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Message message = userService.getUserDetail(username, id);
        message.returnJson(response);
    }

    /**
     * showdoc
     *
     * @param "username" 必选 String 用户名
     * @param "password" 必选 String 密码
     * @return {"data":{"id":1,"power":1,"username":"podcasts","password":"********","accountNonExpired":true,"accountNonLocked":true,"credentialsNonExpired":true,"enabled":true,"roles":[{"id":1,"name":"ROLE_admin","nameZh":"管理员"}],"authorities":[{"authority":"ROLE_admin"}]},"success":true,"message":"","status":200}
     * @catalog 用户接口
     * @title 创建用户
     * @description 访问权限：任意；传入用户名、密码、即可创建用户,权限默认为 user
     * @method post
     * @url /identity/create
     * @return_param status int 状态码
     * @return_param data String 数据
     * @return_param success Boolean 是否成功
     * @return_param message String 消息
     * @remark null
     * @number null
     */
    @PostMapping("/identity/create")
    public void create(@RequestBody User user, HttpServletResponse resp) throws IOException {
        // user 是要修改的用户信息，user1 是当前登陆用户的身份信息
        User nowUser = null;
        Message message = null;
        try {
            // 登陆状态不可以创建新用户
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            nowUser = (User) principal;
            message = new Message(false, 400, "请先退出登录，在创建新用户", "");
        } catch (Exception e) {
            // 创建新用户
            message = userService.save(user, nowUser);
        }
        message.returnJson(resp);
    }

    /**
     * showdoc
     *
     * @param "id"       必选 Long 用户id
     * @param "password" 可选 String 密码
     * @param "power"    可选 Long 用户的权限 1-admin 或 2-user
     * @return {"data":"","success":true,"message":"修改成功","status":200}
     * @catalog 用户接口
     * @title 修改用户权限或密码
     * @description 访问权限：用户或管理员；传入用户id既可修改密码或权利，二者必选一
     * @method post
     * @url /identity/update
     * @return_param status int 状态码
     * @return_param data String 数据
     * @return_param success Boolean 是否成功
     * @return_param message String 消息
     * @remark null
     * @number null
     */
    @PostMapping("/identity/update")
    public void update(@RequestBody User user, HttpServletResponse resp, Authentication authentication) throws IOException {
        // user 是要修改的用户信息，user1 是当前登陆用户的身份信息
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User nowUser = (User) principal;
        // TO do 管理员权限相关
        Message message = userService.save(user, nowUser);
        message.returnJson(resp);
    }

    /**
     * showdoc
     *
     * @param "id" 可选 Long 用户id
     * @return {"data":"","success":true,"message":"删除成功","status":200}
     * @catalog 用户接口
     * @title 删除用户
     * @description 访问权限：管理员；通过id删除用户
     * @method get
     * @url /identity/delete
     * @return_param status int 状态码
     * @return_param data String 数据
     * @return_param success Boolean 是否成功
     * @return_param message String 消息
     * @remark null
     * @number null
     */
    @GetMapping("/identity/delete")
    public void delete(Long id, HttpServletResponse resp) throws IOException {
        Message message = userService.delete(id);
        message.returnJson(resp);
    }

    /**
     * showdoc
     *
     * @param "username" 必选 String 用户名
     * @param "password" 必选 String 密码
     * @return {"id":1,"power":1,"username":"podcasts","password":"$2a$10$bnxzjFG5UKK7bG5shwuqSubRm0Z.6b3SsdtSUr6m3wP/ekBxj6Yam","accountNonExpired":true,"accountNonLocked":true,"credentialsNonExpired":true,"enabled":true,"roles":[{"id":1,"name":"ROLE_admin","nameZh":"管理员"}],"authorities":[{"authority":"ROLE_admin"}]}
     * @catalog 用户接口
     * @title 用户登陆
     * @description 访问权限：任意；传入用户名、密码、即可登陆, 请求方式：form表单提交.
     * @method post
     * @url /login
     * @return_param status int 状态码
     * @return_param data String 数据
     * @return_param success Boolean 是否成功
     * @return_param message String 消息
     * @remark null
     * @number null
     */

//    @PostMapping("/login")
//    public void login(@RequestBody User user, HttpServletResponse resp, Authentication authentication) throws IOException {
//        if (user.getUsername() != null) {
//            UserDetails u = userService.loadUserByUsername(user.getUsername());
//            Message message = new Message(true, 200, "", u);
//            message.returnJson(resp);
//        } else {
//            Message message = new Message(false, 400, "清输入用户信息，在进行登录！", "");
//            message.returnJson(resp);
//        }
//    }
    /**
     * showdoc
     *
     * @return "注销成功"
     * @catalog 用户接口
     * @title 用户退出
     * @description 访问权限：用户；
     * @method get
     * @url /logout
     * @return_param status int 状态码
     * @return_param data String 数据
     * @remark null
     * @number null
     */

}
