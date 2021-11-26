package com.podcasts.jpa.pojo;

import lombok.Data;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.util.List;

@Data
@Entity(name = "t_media")
@Proxy(lazy = false)
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // id
    private String title; // 媒体文件的名称
    private Long classifyId; // 所属的大分类
    private String img; // 媒体文件的介绍图片

    // 上传文件时同时就自动获取了
    private String path; // 媒体文件的服务器路径
    private String type; // 媒体文件的类型
    private Long time; // 上传时间
    private Long size; // 大小
    private Long userId; // 创建者的id
    private String username; // 创建者的名字
    private String fileName; // 文件名称
    private boolean isShow; // 媒体是否审核通过
    //    private String comments; // *重要 所有评论，以字符串形式存储，以逗号隔开
    @Transient
    private List<Comment> allComments;
//                // 获取文件后缀
//                String line = path;
//                String pattern = "(?:FileStorage/.*/)(.*\\..*$)";
//                // 创建 Pattern 对象
//                Pattern r = Pattern.compile(pattern);
//                // 现在创建 matcher 对象
//                Matcher m = r.matcher(line);
//                String fileNames = new String();
//                if (m.find()) {
//                    fileNames = m.group(1);
//                }

    public Media(String path, String type, Long time, Long size, Long userId, String username, String fileName) {
        this.path = path;
        this.type = type;
        this.time = time;
        this.size = size;
        this.userId = userId;
        this.username = username;
        this.fileName = fileName;
        this.isShow = false;
    }

    public Media(String title, String path, String type, Long time, Long size, Long classifyId, Long userId, String username) {
        this.title = title;
        this.path = path;
        this.type = type;
        this.time = time;
        this.size = size;
        this.classifyId = classifyId;
        this.userId = userId;
        this.username = username;
        this.isShow = false;
    }

    public Media() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(Long classifyId) {
        this.classifyId = classifyId;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public List<Comment> getAllComments() {
        return allComments;
    }

    public void setAllComments(List<Comment> allComments) {
        this.allComments = allComments;
    }
}
