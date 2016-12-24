package com.dgut.collegemarket.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Page;
import com.dgut.collegemarket.api.entity.Subscriber;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.util.CommonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

//查找人界面点击事件—实现他人主页—实现点击私信—订阅功能
public class ContentSearchActivity extends Activity {
    Subscriber subscriber;
    User user;
    Button subscribeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_search);
        user = (User) getIntent().getSerializableExtra("data");

        TextView contentName = (TextView) findViewById(R.id.name);
        TextView contentLv = (TextView) findViewById(R.id.level);
        TextView textDate = (TextView) findViewById(R.id.date);
        ImageView avatar = (ImageView) findViewById(R.id.image_avatar);

        contentName.setText(user.getName().toString());
        String dateStr = DateFormat.format("yyyy-MM-dd hh:mm", user.getCreateDate()).toString();
        textDate.setText(dateStr);

        Button messageButton = (Button) findViewById(R.id.messages_button);
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.isFastDoubleClick()) {
                    return;
                } else {
                    startActivity(new Intent(ContentSearchActivity.this, SubscribeMessageActivity.class));
                }
            }
        });

        subscribeButton = (Button) findViewById(R.id.subscribe_button);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    toggleSubscribed();
            }
        });
    }

    private boolean isSubscribed;

    void checkSubscribed(){
        Request request = Server.requestBuilderWithApi("record/isSubscribed/"+user.getId()).get().build();
        Server.getSharedClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                try{
                    final boolean responseString =  Boolean.valueOf( arg1.body().string());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onCheckSubscribedResult(responseString);
                        }
                    });
                }catch(final Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onCheckSubscribedResult(false);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call arg0, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onCheckSubscribedResult(false);
                    }
                });
            }
        });
    }

    void onCheckSubscribedResult(boolean result){
        isSubscribed = result;
        subscribeButton.setTextColor(result ? Color.BLUE : Color.BLACK);
    }

    void reloadSubscribed(){
        Request request = Server.requestBuilderWithApi("record/"+"subscribe/"+user.getId())
                .get()
                .build();

        Server.getSharedClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                try{
                    String responseString = arg1.body().string();
                    final Integer count = new ObjectMapper().readValue(responseString, Integer.class);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onReloadSubscribedResult(count);
                        }
                    });
                }catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onReloadSubscribedResult(0);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call arg0, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onReloadSubscribedResult(0);
                    }
                });
            }
        });
    }

    void onReloadSubscribedResult(int count){
        if(count>0){
            subscribeButton.setText("订阅("+count+")");
        }else{
            subscribeButton.setText("订阅(0)");
        }
    }

    void toggleSubscribed(){
        MultipartBody body = new MultipartBody.Builder()
                .addFormDataPart("subscribe", String.valueOf(!isSubscribed))
                .build();

        Request request = Server.requestBuilderWithApi("record/"+"subscribe/"+user.getId())
                .post(body)
                .build();

        Server.getSharedClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                runOnUiThread(new Runnable() {
                    public void run() {
                        reload();
                    }
                });
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        reload();
                    }
                });
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        reload();
    }

    void reload(){
        reloadSubscribed();
        checkSubscribed();
    }
}
