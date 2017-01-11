package com.dgut.collegemarket.fragment.widgets;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.common.SendMessageActivity;
import com.dgut.collegemarket.activity.goods.GoodsContentActivity;
import com.dgut.collegemarket.activity.orders.OrdersCommentListActivity;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Goods;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.util.CommonUtils;
import com.dgut.collegemarket.util.JudgeLevel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class UserInfoInGoodsFragment extends Fragment {

    TextView levelText;
    TextView nameText;
    Button followBt;
    Button privateMsgBt;
    Button commentBt;
    User user;
    Goods goods;
    View view;
    Activity activity;
    ImageView avatar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_info_in_goods, null);
        activity = getActivity();
        user = GoodsContentActivity.publisher;
        goods = GoodsContentActivity.goods;

        levelText = (TextView) view.findViewById(R.id.tv_level);
        nameText = (TextView) view.findViewById(R.id.tv_name);
        followBt = (Button) view.findViewById(R.id.button_follow);
        privateMsgBt = (Button) view.findViewById(R.id.button_private_msg);
        commentBt = (Button) view.findViewById(R.id.button_comment_list);
        avatar = (ImageView) view.findViewById(R.id.img_avatar);


        return view;
    }

    void initView() {

        levelText.setText("LV " + JudgeLevel.judege(user.getXp()));
        Picasso.with(activity).load(Server.serverAddress + user.getAvatar()).resize(50, 50).centerInside().error(R.drawable.unknow_avatar).into(avatar);

        nameText.setText(user.getName());
        commentBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itnt = new Intent(activity, OrdersCommentListActivity.class);
                itnt.putExtra("goodsId", goods.getId());
                activity.startActivity(itnt);
            }
        });

        privateMsgBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = goods.getPublishers().getAccount().toString();
                Intent itnt = new Intent(getActivity(), SendMessageActivity.class);
                itnt.putExtra("account", account);
                startActivity(itnt);
            }
        });
        followBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.isFastDoubleClick()) {
                    return;
                } else {
                    waitForUpdate();
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

                   activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onReloadSubscribedResult(responseString);
                            onCheckSubscribedResult(responseString);
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                  activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onReloadSubscribedResult(false);
                            onCheckSubscribedResult(false);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call arg0, IOException e) {
                e.printStackTrace();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onReloadSubscribedResult(false);
                        onCheckSubscribedResult(false);
                    }
                });
            }
        });
    }

    void onCheckSubscribedResult(boolean result) {

        isSubscribed = result;
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

                activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

//                            onReloadSubscribedResult(count);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

//                            onReloadSubscribedResult(0);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call arg0, IOException e) {
                e.printStackTrace();
              activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

//                        onReloadSubscribedResult(0);
                    }
                });
            }
        });
    }

    void onReloadSubscribedResult(boolean count) {

        if (count) {
            followBt.setText("取消关注");
            followBt.setBackgroundColor(Color.parseColor("#ffb8b8b8"));
        } else {
            followBt.setText("+关注");
            followBt.setBackgroundColor(Color.parseColor("#3c9aff"));
        }
    }

    void waitForUpdate() {

        MultipartBody body = new MultipartBody.Builder()
                .addFormDataPart("subscribe", String.valueOf(!isSubscribed))
                .build();

        Request request = Server.requestBuilderWithApi("subscribe/" + user.getId())
                .post(body)
                .build();

        Server.getSharedClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
               activity.runOnUiThread(new Runnable() {
                    public void run() {

                        reload();
                    }
                });
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
               activity.runOnUiThread(new Runnable() {
                    public void run() {

                        reload();
                    }
                });
            }
        });
    }

    void reload() {

//        reloadSubscribed();
        checkSubscribed();
    }


    @Override
    public void onResume() {
        super.onResume();
        initView();

        reload();
    }
}
