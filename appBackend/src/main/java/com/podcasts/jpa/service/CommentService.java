package com.podcasts.jpa.service;

import com.podcasts.jpa.mapper.CommentMapper;
import com.podcasts.jpa.mapper.MediaMapper;
import com.podcasts.jpa.mapper.UserMapper;
import com.podcasts.jpa.pojo.Comment;
import com.podcasts.jpa.pojo.Media;
import com.podcasts.jpa.pojo.PersonalColumn;
import com.podcasts.jpa.pojo.User;
import com.podcasts.jpa.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.lang.reflect.Field;
import java.util.*;

@Service
public class CommentService {
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    MediaMapper mediaMapper;
    @Autowired
    UserService userService;
    @Autowired(required = false)
    Message message;


    public Message addComment(Comment comment) {
        List<String> require = Arrays.asList("userId", "content", "parentId", "type");
        // 判断参数个数正确
        for (String item : require) {
            try {
                Field f = comment.getClass().getDeclaredField(item);
                f.setAccessible(true);
                try {
                    Object r = f.get(comment);
                    if (r == null) {
                        message = new Message(false, 400, "类型错误！", "");
                        return message;
                    }
                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
                    message = new Message(false, 400, "类型错误！", "");
                    return message;
                }
            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
                message = new Message(false, 400, "类型错误！", "");
                return message;
            }
        }
        if (comment.getType().equals("media")) {
            comment.setTime(new Date());
            Comment res = commentMapper.save(comment);
            message = new Message(true, 200, "", res);
            return message;
        } else if (comment.getType().equals("comment")) {
            comment.setTime(new Date());
            Comment res = commentMapper.save(comment);
            message = new Message(true, 200, "", res);
            return message;
        }
        message = new Message(false, 400, "类型错误！", "");
        return message;
    }

    public Message findAllComment(Long parentId, String type) {
        List<Comment> res = commentMapper.findAllComment(parentId, type);
        HashMap<Long, User> cache = new HashMap<Long, User>(); // 用户缓存
        HashMap<Long, Comment> parentCache = new HashMap<>(); // 父留言缓存
        getCommentUser(cache, res); // 获取用户信息，存入Comment中

        for (Comment out : res) { // 遍历第一层留言，把第一层留言的所有回复按照时间顺序打平，方便展示，主流留言均是这么做的
            List<Comment> temp; // 临时变量，用来存储每次循环的值，比如第一层留言中第一个留言有3条回复，那么temp就是这3条回复
            // temp每次循环均会更新，直至temp数量为空为止
            List<Comment> children = commentMapper.findAllComment(out.getId(), "comment"); // 查询第一层留言的直接孩子
            temp = children; // 将第一层留言的直接孩子赋值给temp，用做temp的初始遍历值
            while (!temp.isEmpty()) { // 一致循环，直至temp数量为空
                List<Comment> tt_temp = new LinkedList<>(); // 循环中临时变量
                getCommentUser(cache, temp); // 查询用户信息，将用户信息存入Comment和缓存中
                for (Comment inner : temp) { // 遍历每层子留言
                    parentCache.put(inner.getId(), inner); // 将留言存入缓存，方便后续查找
                    Comment parentComment = parentCache.get(inner.getParentId()); // 从缓存中查找父留言
                    if (parentComment == null) { // 如果没有找到
                        Comment comment1 = commentMapper.getById(inner.getParentId()); // 去查询父留言comment
                        parentCache.put(comment1.getId(), comment1); // 将查询结果，放入缓存，方便下次查询
                        getCommentReply(cache, parentCache, inner, comment1); // 获取回复用户信息，添加到Comment中
                    } else { // 找到了
                        getCommentReply(cache, parentCache, inner, parentComment); // 获取回复用户信息，添加到Comment中
                    }
                    List<Comment> tt = commentMapper.findAllComment(inner.getId(), "comment"); // 查询子留言的子留言
                    tt_temp.addAll(tt); // 将子留言的子留言的查询结果，存入外循环临时变量，方便将外循环临时变量添加到它的父留言中
                } // 孩子的孩子遍历完
                children.addAll(tt_temp); // 将后续的孩子放入最初查询的第一层留言直接孩子的list中
                temp = tt_temp; // 将孩子的孩子赋值给temp，为了遍历孩子的孩子的孩子，直至没有孩子
            } // 没有孩子了，退出循环
            getCommentUser(cache, children); // 查询用户信息，将用户信息存入Comment和缓存中
            out.setAllChildren(children); // 将第一层留言所有的孩子拍平后分别存入AllChildren中
        }
        message = new Message(true, 200, "", res);
        return message;
    }

    private void getCommentReply(HashMap<Long, User> cache, HashMap<Long, Comment> parentCache, Comment inner, Comment parentComment) {
        User id = cache.get(parentComment.getUserId()); // 找到父comment的userid
        if (id != null) {
            inner.setReplay(id);
        } else {
            User user1 = (User) userService.getUserDetail(null, parentComment.getUserId()).getObjectData();
            inner.setReplay(user1);
            cache.put(parentComment.getUserId(), user1);
        }
    }

    private void getCommentUser(HashMap<Long, User> cache, List<Comment> temp) {
        for (Comment inner : temp) {
            User user = cache.get(inner.getUserId());
            if (user != null) {
                inner.setUser(user);
            } else {
                User user1 = (User) userService.getUserDetail(null, inner.getUserId()).getObjectData();
                inner.setUser(user1);
                cache.put(inner.getUserId(), user1);
            }
        }
    }

    public Message delete(Long id) {
        try {
            commentMapper.deleteById(id);
            message = new Message(true, 200, "删除成功", "");
        } catch (Exception e) {
            message = new Message(false, 400, "参数错误", "");
        }
        return message;
    }
}
