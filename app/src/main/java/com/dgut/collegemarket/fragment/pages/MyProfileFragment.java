package com.dgut.collegemarket.fragment.pages;

import android.app.Activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.myprofile.CheckConsumptionRecordsActivity;
import com.dgut.collegemarket.activity.myprofile.CheckIdolsAndFansRecordsActivity;
import com.dgut.collegemarket.activity.myprofile.AboutVersionActivity;

import com.dgut.collegemarket.activity.myprofile.CheckDirectMessagesActivity;
import com.dgut.collegemarket.activity.myprofile.RechargeActivity;
import com.dgut.collegemarket.activity.myprofile.SearchActivity;
import com.dgut.collegemarket.activity.myprofile.userInfo.SignInActivity;
import com.dgut.collegemarket.activity.myprofile.userInfo.UserInfoActivity;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.activity.myprofile.AboutCollegeMarketActivity;
import com.dgut.collegemarket.fragment.widgets.AvatarView;
import com.dgut.collegemarket.util.JudgeLevel;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MyProfileFragment extends Fragment {
    View view;
    Activity activity;
    AvatarView av;
    TextView tvName, tvEmail, tvLevel, tvXp;
    TextView tvMoney;
    ProgressBar pbXp;
    LinearLayout linearLayout,linearLayout0;
    RelativeLayout relativeLayout,rlMe;

    User user=new User();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_page_my_profile, null);
            activity = getActivity();
            av = (AvatarView) view.findViewById(R.id.av_user);
            tvName = (TextView) view.findViewById(R.id.tv_user_name);
            tvEmail = (TextView) view.findViewById(R.id.tv_level);
            tvLevel = (TextView) view.findViewById(R.id.tv_level);
            tvXp = (TextView) view.findViewById(R.id.tv_xp);
            pbXp = (ProgressBar) view.findViewById(R.id.pb_xp);
            tvMoney = (TextView) view.findViewById(R.id.tv_money);

            linearLayout0 = (LinearLayout) view.findViewById(R.id.linearLayout0);
            linearLayout0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent itnt = new Intent(getActivity(), SignInActivity.class);
                    startActivity(itnt);
                }
            });
            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout1);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), CheckConsumptionRecordsActivity.class));
                }
            });

            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout2);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), CheckIdolsAndFansRecordsActivity.class));
                }
            });

            relativeLayout = (RelativeLayout) view.findViewById(R.id.chong_money);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goRecharge();
                }
            });

            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout_search);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), SearchActivity.class));
                }
            });

            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout_messages);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), CheckDirectMessagesActivity.class));
                }
            });

            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout_about);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), AboutCollegeMarketActivity.class));
                }
            });

            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout_version);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), AboutVersionActivity.class));
                }
            });

            rlMe = (RelativeLayout) view.findViewById(R.id.linear_me);
            rlMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            });
        }
        return view;
    }

    //充值
    void goRecharge() {
        startActivity(new Intent(getActivity(), RechargeActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        getUser();
    }

    /**
     * 获取当前用户信息
     */
    public void getUser() {
        OkHttpClient client = Server.getSharedClient();
        Request request = Server.requestBuilderWithApi("user/me").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "获取用户信息失败，请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                user = new ObjectMapper().readValue(result, User.class);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        av.load(user);
                        tvName.setText(user.getName());
                        tvLevel.setText("Lv:" + JudgeLevel.judege(user.getXp()));
                        tvXp.setText(user.getXp() + "/" + JudgeLevel.juderMax(user.getXp()));
                        pbXp.setMax(JudgeLevel.juderMax(user.getXp()));
                        pbXp.setProgress(user.getXp());
                        if (user.getCoin() <= 1000000.0) {
                            tvMoney.setText(user.getCoin() + "");
                        } else {
                            tvMoney.setText("显示异常");
                        }
                    }
                });
            }
        });
    }
}
