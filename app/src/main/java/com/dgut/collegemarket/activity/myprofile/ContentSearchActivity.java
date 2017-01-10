package com.dgut.collegemarket.activity.myprofile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import com.dgut.collegemarket.api.entity.Post;
import com.dgut.collegemarket.api.entity.Subscriber;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.fragment.widgets.AvatarView;
import com.dgut.collegemarket.util.CommonUtils;
import com.dgut.collegemarket.util.JudgeLevel;
import com.dgut.collegemarket.view.viewswitcher.SlidingSwitcherView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

//查找人界面点击事件—实现他人主页—实现点击私信—订阅功能
public class ContentSearchActivity extends Activity {

    TextView text, title, content;
    Subscriber subscriber;
    User user;
    ImageView imageview_turnback;
    Button subscribeButton;
    SlidingSwitcherView slidingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_search);
        user = (User) getIntent().getSerializableExtra("user");

        slidingLayout = (SlidingSwitcherView) findViewById(R.id.slidingLayout);
        slidingLayout.startAutoPlay();
        text = (TextView) findViewById(R.id.text);
        title = (TextView) findViewById(R.id.text_title);
        content = (TextView) findViewById(R.id.text_content);
        TextView contentName = (TextView) findViewById(R.id.name);
        TextView contentLv = (TextView) findViewById(R.id.level);
        TextView textDate = (TextView) findViewById(R.id.date);
        AvatarView avatar = (AvatarView) findViewById(R.id.image_avatar);

        avatar.load(user);
        contentLv.setText("Lv:" + JudgeLevel.judege(user.getXp()));
        contentName.setText(user.getName().toString());
        text.setText( user.getName().toString());
        String dateStr = DateFormat.format("yyyy-MM-dd hh:mm", user.getCreateDate()).toString();
        textDate.setText(dateStr);

        Button messageButton = (Button) findViewById(R.id.messages_button);
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.isFastDoubleClick()) {
                    return;
                } else {
                    Intent itnt = new Intent(ContentSearchActivity.this, SendMessageActivity.class);
                    String account = user.getAccount().toString();
                    itnt.putExtra("account", account);
                    startActivity(itnt);
                }
            }
        });

        imageview_turnback = (ImageView) findViewById(R.id.imageview_turnback);
        imageview_turnback.setOnClickListener(new View.OnClickListener() {
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

        Request request = Server.requestBuilderWithApi("subscribe/isSubscribed/" + user.getId()).get().build();

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
        subscribeButton.setTextColor(result ? Color.BLACK : Color.WHITE);
    }

    void reloadSubscribed() {

        Request request = Server.requestBuilderWithApi("subscribe/" + user.getId())
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
            subscribeButton.setBackgroundColor(Color.parseColor("#ffb8b8b8"));
        } else {
            subscribeButton.setText("+关注");
            subscribeButton.setBackgroundColor(Color.parseColor("#3c9aff"));
        }
    }

    void toggleSubscribed() {

        MultipartBody body = new MultipartBody.Builder()
                .addFormDataPart("subscribe", String.valueOf(!isSubscribed))
                .build();

        Request request = Server.requestBuilderWithApi("subscribe/" + user.getId())
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

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("publishers_id", user.getId() + "");


        Request request = Server.requestBuilderWithApi("post/findByUserId")
                .post(multipartBuilder.build())
                .build();

        Server.getSharedClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                String result = arg1.body().string();
                if (!result.equals(""))
                    try {
                        final Post post = new ObjectMapper()
                                .readValue(result, Post.class);
                        ContentSearchActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                if (post.equals("")) {
                                    title.setText("");
                                    content.setText("");
                                } else {
                                    title.setText(post.getTitle());
                                    content.setText(post.getContent());

                                }

                            }
                        });
                    } catch (final Exception e) {
                        ContentSearchActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                new AlertDialog.Builder(ContentSearchActivity.this)
                                        .setMessage(e.getMessage())
                                        .show();
                            }
                        });
                    }
            }

            @Override
            public void onFailure(Call arg0, final IOException e) {
                ContentSearchActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        new AlertDialog.Builder(ContentSearchActivity.this)
                                .setMessage("服务器异常")
                                .show();
                    }
                });
            }
        });
    }
}
