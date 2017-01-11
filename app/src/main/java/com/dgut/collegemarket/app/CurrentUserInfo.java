package com.dgut.collegemarket.app;

import com.dgut.collegemarket.api.entity.User;

/**
 * Created by Administrator on 2016/12/26.
 */

public class CurrentUserInfo {
    public static boolean online = false;
    public static int user_id =0;
    public static User user;

    public static User getUser() {
        return user;
    }

    public static void setUser(User users) {
        user = users;
    }
}
