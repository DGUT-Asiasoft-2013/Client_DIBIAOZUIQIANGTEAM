package com.dgut.collegemarket.fragment.widgets;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.goods.GoodsContentActivity;
import com.dgut.collegemarket.activity.orders.OrderCommentListActivity;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Goods;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.util.JudgeLevel;
import com.squareup.picasso.Picasso;

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
        avatar= (ImageView) view.findViewById(R.id.img_avatar);



        return view;
    }

    void initView() {

        levelText.setText("LV " + JudgeLevel.judege(user.getXp()));
        Picasso.with(activity).load(Server.serverAddress + user.getAvatar()).resize(50, 50).centerInside().error(R.drawable.unknow_avatar).into(avatar);

        nameText.setText(user.getName());
        commentBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itnt = new Intent(activity, OrderCommentListActivity.class);
                itnt.putExtra("goodsId",goods.getId());
                activity.startActivity(itnt);
            }
        });

        privateMsgBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waitForUpdate();
            }
        });
        followBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waitForUpdate();
            }
        });
    }

    void waitForUpdate() {
        new AlertDialog.Builder(activity)
                .setTitle("敬请期待")
                .setMessage("后续功能将在下个版本添加").show();
    }


    @Override
    public void onResume() {
        super.onResume();
        initView();
    }
}
