package com.podcasts.jpa.pojo;

import lombok.Data;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 音频评论
 */
@Data
@Entity(name = "t_comment")
@Proxy(lazy = false)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // id
    private Long userId; // 评论用户Id
    private String content;// 评论内容
    //    private String children; // *重要 所有当前评论的第下一层回复，以字符串形式存储，以逗号隔开
    private Long parentId; // 可能是媒体，可能是评论 只保存父亲的id，在查询时，只需要查询父亲的id
    private String type; // 在添加评论时，后端查看一下父id是否为媒体或者是回复 我的系统里面media的id和comment的id很有可能会重复，只能根据前端提供的类型来做处理了
    private Date time;
    @Transient
    private List<Comment> AllChildren; // 所有当前评论的第下一层回复
    @Transient
    private User user;
    @Transient
    private User replay; // 回复

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Comment> getAllChildren() {
        return AllChildren;
    }

    public void setAllChildren(List<Comment> allChildren) {
        AllChildren = allChildren;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getReplay() {
        return replay;
    }

    public void setReplay(User replay) {
        this.replay = replay;
    }
}
