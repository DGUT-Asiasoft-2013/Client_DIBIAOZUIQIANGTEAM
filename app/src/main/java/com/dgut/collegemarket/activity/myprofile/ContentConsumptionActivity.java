package com.dgut.collegemarket.activity.myprofile;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Page;
import com.dgut.collegemarket.api.entity.Post;
import com.dgut.collegemarket.api.entity.Records;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.fragment.Records.ConsumptionRecordsFragment;
import com.dgut.collegemarket.fragment.widgets.AvatarView;
import com.dgut.collegemarket.util.MD5;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


//我的消费界面—显示数据库记录
public class ContentConsumptionActivity extends Activity {

    TextView money,cause,date;
    AvatarView imageView;
    private Records records;
    User user;
    List<Post> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_consumption);

        records = (Records) getIntent().getSerializableExtra("data");



        money = (TextView) findViewById(R.id.money);
        cause = (TextView) findViewById(R.id.cause);
        date = (TextView) findViewById(R.id.date);
        imageView = (AvatarView) findViewById(R.id.consumption_image);

        money.setText(records.getCoin()+"");
        cause.setText(records.getCause() + records.getCoin() + " 元 ");

        String dateStr = DateFormat.format("yyyy-MM-dd hh:mm", records.getCreateDate()).toString();
        date.setText(dateStr);
        imageView.load(records.getUser().getAvatar());

    }

}
