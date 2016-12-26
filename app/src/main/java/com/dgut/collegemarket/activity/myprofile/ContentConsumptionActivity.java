package com.dgut.collegemarket.activity.myprofile;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.entity.Records;

import java.util.List;


//我的消费界面—显示数据库记录
public class ContentConsumptionActivity extends Activity {

    private Records records;
    List<Records> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_consumption);
        records = (Records) getIntent().getSerializableExtra("data");

//        ListView list = (ListView) findViewById(R.id.list);

        TextView money = (TextView) findViewById(R.id.money);
        TextView cause = (TextView) findViewById(R.id.cause);
        TextView date = (TextView) findViewById(R.id.date);

        money.setText(records.getCoin() + "");
        cause.setText(records.getCause());

        String dateStr = DateFormat.format("yyyy-MM-dd hh:mm", records.getCreateDate()).toString();
        date.setText(dateStr);

//            list.addHeaderView(headerView, null, false);

    }
}
