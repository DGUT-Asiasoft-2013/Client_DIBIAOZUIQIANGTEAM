package com.dgut.collegemarket.activity.myprofile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.common.SendMessageActivity;
import com.dgut.collegemarket.api.entity.Records;
import com.dgut.collegemarket.api.entity.Subscriber;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.fragment.widgets.AvatarView;
import com.dgut.collegemarket.util.CommonUtils;

import java.util.List;

public class ContentFansActivity extends Activity {

    Button button;
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


        button = (Button)findViewById(R.id.send_messages);
        money = (TextView) findViewById(R.id.money);
        cause = (TextView) findViewById(R.id.cause);
        date = (TextView) findViewById(R.id.date);
        imageView = (AvatarView) findViewById(R.id.fans_image);

        money.setText(subscriber.getId().getSubscribers().getName() + " 在北京时间");
        cause.setText("成为我的粉丝");

        String dateStr = DateFormat.format("yyyy-MM-dd hh:mm", subscriber.getCreateDate()).toString();
        date.setText(dateStr);
        imageView.load(subscriber.getId().getSubscribers().getAvatar());


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.isFastDoubleClick()) {
                    return;
                } else {

                    startActivity(new Intent(ContentFansActivity.this, SendMessageActivity.class));
                }
            }
        });

    }

}
