package com.podcasts.jpa;

import com.podcasts.jpa.mapper.ClassifyMapper;
import com.podcasts.jpa.mapper.MediaMapper;
import com.podcasts.jpa.mapper.RoleMapper;
import com.podcasts.jpa.pojo.User;
import com.podcasts.jpa.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
class JpaApplicationTests {
    @Autowired
    ClassifyMapper classifyMapper;
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    UserService userService;
    @Autowired
    MediaMapper mediaMapper;

    @Test
    void mediaMapper() {
//        getByPersonalColumnId
    }

    @Test
    void contextLoads() {
        long now1 = System.currentTimeMillis();
        long now2 = new Date().getTime();
        long time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse("2018-06-30 20:00:00", new ParsePosition(0)).getTime() / 1000;
        System.out.println("获取指定时间的时间戳:" + time);
        System.out.println("当前时间戳:" + now1);
        System.out.println("当前时间戳:" + now2);
        System.out.println(new Date());
    }

    @Test
    void changePassword() {
        User user = new User();
        user.setId(5L);
        user.setPassword("123456789");

        userService.save(user, user);
    }


    @Test
    void changePower() {
        User user = new User();
        user.setId(5L);
        user.setPower(1L);

        userService.save(user, user);
    }
}
