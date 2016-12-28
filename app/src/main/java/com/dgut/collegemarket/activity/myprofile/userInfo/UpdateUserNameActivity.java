package com.dgut.collegemarket.activity.myprofile.userInfo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.fragment.InputCell.SimpleTextInputCellFragment;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateUserNameActivity extends AppCompatActivity {

    SimpleTextInputCellFragment fragUserName = new SimpleTextInputCellFragment();
    Button btnUpdate;
    TextView exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_name);

        fragUserName = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_update_username);
        btnUpdate = (Button) findViewById(R.id.btn_update_name);
        exit = (TextView) findViewById(R.id.tv_exit);

        fragUserName.setHintText("快来改一个名字吧！");

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.none,R.anim.slide_out_left);
            }
        });
    }

    public void update(){
        OkHttpClient client = Server.getSharedClient();
        MultipartBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("username",fragUserName.getText())
                .build();
        Request request = Server.requestBuilderWithApi("user/update/username")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UpdateUserNameActivity.this,"网络连接失败，请检查网络",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(UpdateUserNameActivity.this,"用户已断开连接，请重新登录",Toast.LENGTH_SHORT).show();
                            finish();
                            overridePendingTransition(R.anim.none,R.anim.slide_out_left);
                        }
                    });
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UpdateUserNameActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(R.anim.none,R.anim.slide_out_left);
                    }
                });
            }
        });
    }
}
