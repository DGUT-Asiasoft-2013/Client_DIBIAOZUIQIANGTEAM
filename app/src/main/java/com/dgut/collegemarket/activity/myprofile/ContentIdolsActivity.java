package com.dgut.collegemarket.activity.myprofile;

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
import com.dgut.collegemarket.activity.common.SendMessageActivity;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Records;
import com.dgut.collegemarket.api.entity.Subscriber;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.fragment.widgets.AvatarView;
import com.dgut.collegemarket.util.CommonUtils;
import com.dgut.collegemarket.util.JudgeLevel;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class ContentIdolsActivity extends Activity {

    TextView text;
    ImageView imageView_turnBack;
    Button subscribeButton;
    private Subscriber subscriber;
    List<Subscriber> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_idols);

        subscriber = (Subscriber) getIntent().getSerializableExtra("data");


        text = (TextView) findViewById(R.id.text);
        TextView contentName = (TextView) findViewById(R.id.name);
        TextView contentLv = (TextView) findViewById(R.id.level);
        TextView textDate = (TextView) findViewById(R.id.date);
        AvatarView avatar = (AvatarView) findViewById(R.id.image_avatar);

        avatar.load(subscriber.getId().getPublishers().getAvatar());
        contentLv.setText("Lv:" + JudgeLevel.judege(subscriber.getId().getPublishers().getXp()));
        contentName.setText(subscriber.getId().getPublishers().getName());
        text.setText(subscriber.getId().getPublishers().getName());
        String dateStr = DateFormat.format("yyyy-MM-dd hh:mm", subscriber.getCreateDate()).toString();
        textDate.setText(dateStr);

        Button messageButton = (Button) findViewById(R.id.messages_button);
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.isFastDoubleClick()) {
                    return;
                } else {
                    Intent itnt = new Intent(ContentIdolsActivity.this, SendMessageActivity.class);
                     String account = subscriber.getId().getPublishers().getAccount().toString();
                    itnt.putExtra("account", account);
                    startActivity(itnt);
                }
            }
        });

        imageView_turnBack = (ImageView) findViewById(R.id.imageView_turnBack);
        imageView_turnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        subscribeButton = (Button) findViewById(R.id.subscribe_button);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CommonUtils.isFastDoubleClick()) {
                    return;
                } else {

                    toggleSubscribed();
                }
            }
        });
    }

    private boolean isSubscribed;

    void checkSubscribed() {

        Request request = Server.requestBuilderWithApi("subscribe/isSubscribed/" + subscriber.getId().getPublishers().getId()).get().build();

        Server.getSharedClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                try {
                    final boolean responseString = Boolean.valueOf(arg1.body().string());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            onCheckSubscribedResult(responseString);
                        }
                    });
                } catch (final Exception e) {
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

    void onCheckSubscribedResult(boolean result) {

        isSubscribed = result;
        subscribeButton.setTextColor(result ? Color.BLUE : Color.WHITE);
    }

    void reloadSubscribed() {

        Request request = Server.requestBuilderWithApi("subscribe/" + subscriber.getId().getPublishers().getId())
                .get()
                .build();

        Server.getSharedClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                try {
                    String responseString = arg1.body().string();
                    final Integer count = new ObjectMapper().readValue(responseString, Integer.class);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            onReloadSubscribedResult(count);
                        }
                    });
                } catch (Exception e) {
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

    void onReloadSubscribedResult(int count) {

        if (count > 0) {
            subscribeButton.setText("已关注");
        } else {
            subscribeButton.setText("+关注");
        }
    }

    void toggleSubscribed() {

        MultipartBody body = new MultipartBody.Builder()
                .addFormDataPart("subscribe", String.valueOf(!isSubscribed))
                .build();

        Request request = Server.requestBuilderWithApi("subscribe/" + subscriber.getId().getPublishers().getId())
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

    void reload() {

        reloadSubscribed();
        checkSubscribed();
    }

//        button = (Button)findViewById(R.id.send_messages);
//        money = (TextView) findViewById(R.id.money);
//        cause = (TextView) findViewById(R.id.cause);
//        date = (TextView) findViewById(R.id.date);
//        imageView = (AvatarView) findViewById(R.id.idols_image);
//
//        money.setText("我关注了");
//        cause.setText(subscriber.getId().getPublishers().getName());
//
//        String dateStr = DateFormat.format("yyyy-MM-dd hh:mm", subscriber.getCreateDate()).toString();
//        date.setText(dateStr);
//        imageView.load(subscriber.getId().getPublishers().getAvatar());
//
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (CommonUtils.isFastDoubleClick()) {
//                    return;
//                } else {
//                    Intent intent=  new Intent(ContentIdolsActivity.this, SendMessageActivity.class);
//                    intent.putExtra("account",subscriber.getId().getPublishers().getAccount());
//                    startActivity(intent);
//                }
//            }
//        });
//    }


}
