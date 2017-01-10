package com.dgut.collegemarket.activity.myprofile.userInfo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.fragment.InputCell.SimpleTextInputCellFragment;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateEmailActivity extends AppCompatActivity {

    SimpleTextInputCellFragment fragUserName = new SimpleTextInputCellFragment();
    Button btnUpdate;

    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        fragUserName = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_update_username);
        btnUpdate = (Button) findViewById(R.id.btn_update_name);
        iv_back = (ImageView) findViewById(R.id.iv_back);

        fragUserName.setHintText("请填写您的邮箱，注意格式噢！");

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.none, R.anim.slide_out_left);
            }
        });
    }

    public void update() {
        if(fragUserName.getText().equals("")){
            Toast.makeText(UpdateEmailActivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        Pattern p = Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$");
        Matcher m = p.matcher(fragUserName.getText());
        if(!m.matches()) {
            Toast.makeText(UpdateEmailActivity.this,"邮箱格式不正确,请重新输入",Toast.LENGTH_SHORT).show();
            return;
        }
        OkHttpClient client = Server.getSharedClient();
        MultipartBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("email", fragUserName.getText())
                .build();
        final Request request = Server.requestBuilderWithApi("user/update/email")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UpdateEmailActivity.this, "网络连接失败，请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                if(result.equals("")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UpdateEmailActivity.this, "当前用户已断开链接，请检查网络设置", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            Toast.makeText(UpdateEmailActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            finish();
                            overridePendingTransition(R.anim.none,R.anim.slide_out_left);
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.none, R.anim.slide_out_left);
        super.onBackPressed();
    }
}
