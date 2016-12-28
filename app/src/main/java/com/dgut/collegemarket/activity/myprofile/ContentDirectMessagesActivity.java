package com.dgut.collegemarket.activity.myprofile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.entity.Records;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.fragment.widgets.AvatarView;

import java.util.List;

public class ContentDirectMessagesActivity extends AppCompatActivity {

    TextView money,cause,date;
    AvatarView imageView;
    private Records records;
    User user;
    List<Records> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_direct_messages);

        records = (Records) getIntent().getSerializableExtra("data");

//        ListView list = (ListView) findViewById(R.id.list);

        money = (TextView) findViewById(R.id.money);
        cause = (TextView) findViewById(R.id.cause);
        date = (TextView) findViewById(R.id.date);
        imageView = (AvatarView) findViewById(R.id.messages_image);

        money.setText(" 我在北京时间： ");
        cause.setText(" 用某某宝 " + records.getCause() + " 了 " + records.getCoin() + " 元 ");

        String dateStr = DateFormat.format("yyyy-MM-dd hh:mm", records.getCreateDate()).toString();
        date.setText(dateStr);
        imageView.load(records.getUser().getAvatar());

//            list.addHeaderView(headerView, null, false);

    }

}
