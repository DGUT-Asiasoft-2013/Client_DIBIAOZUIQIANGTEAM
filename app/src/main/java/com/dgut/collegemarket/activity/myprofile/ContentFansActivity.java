package com.dgut.collegemarket.activity.myprofile;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.entity.Records;
import com.dgut.collegemarket.api.entity.Subscriber;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.fragment.widgets.AvatarView;

import java.util.List;

public class ContentFansActivity extends Activity {

    TextView money,cause,date;
    AvatarView imageView;
    User user;
    private Subscriber subscriber;
    List<Subscriber> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_fans);

        subscriber = (Subscriber) getIntent().getSerializableExtra("data");

//        ListView list = (ListView) findViewById(R.id.list);

        money = (TextView) findViewById(R.id.money);
        cause = (TextView) findViewById(R.id.cause);
        date = (TextView) findViewById(R.id.date);
        imageView = (AvatarView) findViewById(R.id.fans_image);

        money.setText(subscriber.getId().getSubscribers().getName() + " 在北京时间");
        cause.setText("成为我的粉丝");

        String dateStr = DateFormat.format("yyyy-MM-dd hh:mm", subscriber.getCreateDate()).toString();
        date.setText(dateStr);
        imageView.load(subscriber.getId().getSubscribers().getAvatar());

//            list.addHeaderView(headerView, null, false);

    }

}
