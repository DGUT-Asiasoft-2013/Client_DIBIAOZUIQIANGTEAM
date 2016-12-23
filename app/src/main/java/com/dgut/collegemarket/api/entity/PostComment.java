package com.dgut.collegemarket.api.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/20.
 */

public class PostComment implements Serializable{
    private int id;
    private String content;//评论内容
    private User commentUser;//发布者
    private Post post;//帖子
    private Date createDate;//评论时间
    private Date editDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(User commentUser) {
        this.commentUser = commentUser;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getEditDate() {
        return editDate;
    }

    public void setEditDate(Date editDate) {
        this.editDate = editDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
