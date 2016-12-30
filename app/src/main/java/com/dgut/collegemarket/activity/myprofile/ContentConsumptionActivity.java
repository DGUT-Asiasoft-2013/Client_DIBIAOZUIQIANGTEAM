package com.dgut.collegemarket.activity.myprofile;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.entity.Records;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.fragment.widgets.AvatarView;

import java.util.List;


//我的消费界面—显示数据库记录
public class ContentConsumptionActivity extends Activity {

    TextView money,cause,date;
    AvatarView imageView;
    private Records records;
    User user;
    List<Records> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_consumption);

        records = (Records) getIntent().getSerializableExtra("data");



        money = (TextView) findViewById(R.id.money);
        cause = (TextView) findViewById(R.id.cause);
        date = (TextView) findViewById(R.id.date);
        imageView = (AvatarView) findViewById(R.id.consumption_image);

        money.setText(" 我在北京时间： ");
        cause.setText(records.getCause() + records.getCoin() + " 元 ");

        String dateStr = DateFormat.format("yyyy-MM-dd hh:mm", records.getCreateDate()).toString();
        date.setText(dateStr);
        imageView.load(records.getUser().getAvatar());




    }
}
