package com.dgut.collegemarket.api.entity;

import java.io.Serializable;
import java.net.UnknownServiceException;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/20.
 */

public class Goods  implements Serializable{
    int id;
    String title;
    String content;
    int quantity;
    double price;
    Date createDate;
    Date editDate;
    String albums;
    User publishers;//发布者


    public User getPublishers() {
        return publishers;
    }

    public void setPublishers(User publishers) {
        this.publishers = publishers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public String getAlbums() {
        return albums;
    }

    public void setAlbums(String albums) {
        this.albums = albums;
    }
}
