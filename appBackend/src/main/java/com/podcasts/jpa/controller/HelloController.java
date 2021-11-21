package com.podcasts.jpa.controller;

import com.podcasts.jpa.util.Message;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class HelloController {
    Message message;

    @GetMapping("/hello")
    public void hello(HttpServletResponse resp) throws IOException {
//        message.returnJson(200, "", "data", resp);
    }
}
