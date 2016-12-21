package com.dgut.collegemarket.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.entity.User;
import com.dgut.collegemarket.fragment.widgets.AvatarView;
import com.dgut.collegemarket.fragment.widgets.InfoListFragment;
import com.dgut.collegemarket.fragment.widgets.TitleBarFragment;

/**
 * Created by Administrator on 2016/12/21.
 */

public class UserInfoActivity extends Activity{

    User user;
    TextView tvTitle,tvExit;
    AvatarView userAvatar ;

    InfoListFragment fragmentUserName = new InfoListFragment();
    InfoListFragment fragmentUserEmail = new InfoListFragment();
    InfoListFragment fragmentUserXp= new InfoListFragment();
    InfoListFragment fragmentUsercoins = new InfoListFragment();
    InfoListFragment fragmentUserCreatedate = new InfoListFragment();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_userinfo);

        user = (User) getIntent().getSerializableExtra("user");
        tvTitle = (TextView) findViewById(R.id.tv_user_title);
        tvExit = (TextView) findViewById(R.id.tv_exit);
        userAvatar = (AvatarView) findViewById(R.id.av_user_avatar);
        fragmentUserName = (InfoListFragment) getFragmentManager().findFragmentById(R.id.fragment_user_name);
        fragmentUserEmail = (InfoListFragment) getFragmentManager().findFragmentById(R.id.fragment_user_email);
        fragmentUserXp = (InfoListFragment) getFragmentManager().findFragmentById(R.id.fragment_user_xp);
        fragmentUsercoins = (InfoListFragment) getFragmentManager().findFragmentById(R.id.fragment_user_coins);
        fragmentUserCreatedate = (InfoListFragment) getFragmentManager().findFragmentById(R.id.fragment_user_createdate);
        tvTitle.setText("个人信息");
        tvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        userAvatar.load(user);

    }

    @Override
    protected void onResume() {
        super.onResume();
        fragmentUserName.setTvUserAttribute("昵称");
        fragmentUserName.setTvUserContent(user.getName());
        fragmentUserEmail.setTvUserAttribute("邮箱");
        fragmentUserEmail.setTvUserContent(user.getEmail());
        fragmentUserXp.setTvUserAttribute("总经验");
        fragmentUserXp.setTvUserContent(user.getXp()+"");
        fragmentUsercoins.setTvUserAttribute("金币");
        fragmentUsercoins.setTvUserContent(user.getCoin()+"");
        fragmentUserCreatedate.setTvUserAttribute("创建时间");
        fragmentUserCreatedate.setTvUserContent(DateFormat.format("yyyy-MM-dd hh:ss",user.getCreateDate()).toString());
    }

}
