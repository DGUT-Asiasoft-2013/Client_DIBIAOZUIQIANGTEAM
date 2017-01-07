package com.dgut.collegemarket.activity.myprofile.userInfo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.fragment.InputCell.SimpleTextInputCellFragment;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateUserNameActivity extends AppCompatActivity {

    SimpleTextInputCellFragment fragUserName = new SimpleTextInputCellFragment();
    Button btnUpdate;

    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_name);

        fragUserName = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_update_username);
        btnUpdate = (Button) findViewById(R.id.btn_update_name);
        iv_back = (ImageView) findViewById(R.id.iv_back);

        fragUserName.setHintText("快来改一个名字吧！");

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
        OkHttpClient client = Server.getSharedClient();
        MultipartBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("username", fragUserName.getText())
                .build();
        final Request request = Server.requestBuilderWithApi("user/update/username")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UpdateUserNameActivity.this, "网络连接失败，请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            User user = new ObjectMapper().readValue(result, User.class);
                            UserInfo myInfo=JMessageClient.getMyInfo();
                            myInfo.setNickname(fragUserName.getText());
                            JMessageClient.updateMyInfo(UserInfo.Field.nickname, myInfo, new BasicCallback() {
                                @Override
                                public void gotResult(int i, String s) {
                                    if (i == 0) {
                                        Log.i("UpdateUserInfoActivity", "updateNickName," + " responseCode = " + i + "; desc = " + s);
                                        Toast.makeText(UpdateUserNameActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                        finish();
                                        overridePendingTransition(R.anim.none, R.anim.slide_out_left);
                                    } else {
                                        Log.i("UpdateUserInfoActivity", "updateNickName," + " responseCode = " + i + "; desc = " + s);
                                    }
                                }
                            });



                        } catch (IOException e) {
                            Toast.makeText(UpdateUserNameActivity.this, "服务器异常", Toast.LENGTH_SHORT).show();
                        }
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
