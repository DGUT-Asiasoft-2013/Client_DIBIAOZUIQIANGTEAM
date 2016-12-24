package com.dgut.collegemarket.api.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/24.
 */

public class Subscriber implements Serializable {

    int id;
    Date createDate;

    User publishers;//发布者
    User subscribers;//订阅者

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

    public User getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(User subscribers) {
        this.subscribers = subscribers;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

}
