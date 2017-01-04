package com.dgut.collegemarket.app;

import android.app.Application;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;

/**
 * Created by ${chenyn} on 16/3/22.
 *
 * @desc :
 */
public class MyApplication extends Application {

    private static MyApplication instance;
    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Log.i("MyApplication", "init");
        JMessageClient.init(getApplicationContext());
        JPushInterface.init(this);     		// 初始化 JPush
    }
}

