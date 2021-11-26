package com.podcasts.jpa.controller;

import com.podcasts.jpa.pojo.Comment;
import com.podcasts.jpa.pojo.PersonalColumn;
import com.podcasts.jpa.service.CommentService;
import com.podcasts.jpa.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController

@RequestMapping("/comment")
public class CommentController {
    @Autowired
    CommentService commentService;

    @PostMapping("/addComment")
    void add(@RequestBody Comment comment, HttpServletResponse resp) throws IOException {
        Message message;
        message = commentService.addComment(comment);
        message.returnJson(resp);
    }

    @GetMapping("/getComment")
    public void getMediaByArray(@RequestParam(name = "id") Long id, @RequestParam(name = "type") String type, HttpServletResponse resp) throws IOException {
        Message message;
        message = commentService.findAllComment(id, type);
        message.returnJson(resp);
    }

    @GetMapping("/delete")
    public void delete(@RequestParam(name = "id") Long id, HttpServletResponse resp) throws IOException {
        Message res = commentService.delete(id);
        res.returnJson(resp);
    }
}
