package com.dgut.collegemarket.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dgut.collegemarket.R;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by ${chenyn} on 16/4/1.
 *
 * @desc :创建单聊自定义消息
 */
public class CreateSigMsg {
    public static Context context;


    public static void CreateSigCustomMsg(String name,  Map<String, String> valuesMap) {
        Message customMessage = JMessageClient.createSingleCustomMessage(name, valuesMap);

        customMessage.setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "发送失败"+i+":"+s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        JMessageClient.sendMessage(customMessage);
    }

    public static void CreateSigTextMsg(String name,String customName, String text, Map<String, String> valuesMap) {

        final MessageContent content = new TextContent(text);
        if(valuesMap!=null)
        content.setExtras(valuesMap);

        Conversation mConversation = JMessageClient.getSingleConversation(name, null);
        if (mConversation == null) {
            mConversation = Conversation.createSingleConversation(name, null);
        }
        Message message = mConversation.createSendMessage(content, customName);
        JMessageClient.sendMessage(message);
        message.setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {

                } else {
                    Toast.makeText(context, "发送失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
