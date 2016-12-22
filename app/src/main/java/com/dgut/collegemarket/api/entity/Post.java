package com.dgut.collegemarket.api.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/20.
 */

public class Post implements Serializable{
    private int id;
    private User publishers;//发布者
    private String title;//标题
    private String content;//内容
    private String albums;//帖子图集
    private int quantity;//商品数量
    private double reward;//报酬
    private boolean issolve;//是否解决
    private Date createDate;
    private Date editDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getPublishers() {
        return publishers;
    }

    public void setPublishers(User publishers) {
        this.publishers = publishers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAlbums() {
        return albums;
    }

    public void setAlbums(String albums) {
        this.albums = albums;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public boolean issolve() {
        return issolve;
    }

    public void setIssolve(boolean issolve) {
        this.issolve = issolve;
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
}
