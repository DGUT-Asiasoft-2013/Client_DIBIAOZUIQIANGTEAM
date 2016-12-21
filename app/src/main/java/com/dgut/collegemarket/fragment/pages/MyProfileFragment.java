package com.dgut.collegemarket.fragment.pages;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.dgut.collegemarket.R;
import com.dgut.collegemarket.entity.User;
import com.dgut.collegemarket.fragment.widgets.AvatarView;
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
    TextView tvName,tvEmail;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_page_my_profile, null);
            activity= getActivity();
            av = (AvatarView) view.findViewById(R.id.av_user);
            tvName = (TextView) view.findViewById(R.id.tv_user_name);
            tvEmail = (TextView) view.findViewById(R.id.tv_level);
            User user = (User) activity.getIntent().getSerializableExtra("user");
            av.load(user);
            tvName.setText(user.getName());
//            tvEmail.setText(user.getXp());
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
