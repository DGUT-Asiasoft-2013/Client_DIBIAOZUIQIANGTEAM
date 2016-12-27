package com.dgut.collegemarket.activity.myprofile.userInfo;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.adapter.SignListAdapter;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Sign;
import com.dgut.collegemarket.api.entity.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SignInActivity extends Activity {

    ListView lvSign;
    Button btnSign;

    List<Sign> signList = new ArrayList<Sign>();
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        user = (User) getIntent().getSerializableExtra("user");

        lvSign = (ListView) findViewById(R.id.lv_sign);
        btnSign = (Button) findViewById(R.id.btn_sign);
        SignListAdapter signListAdapter = new SignListAdapter(SignInActivity.this,signList);

        lvSign.setAdapter(signListAdapter);
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int xp = new Random().nextInt(3);
                addSign(xp,user.getId());
            }
        });
    }

    public void addSign(int xp,int userId){
        OkHttpClient client = Server.getSharedClient();
        MultipartBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("userId",userId+"")
                .addFormDataPart("xp",xp+"")
                .build();
        final Request request = Server.requestBuilderWithApi("sign/addsign")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SignInActivity.this,"签到失败，请检查网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                if (result.equals("")){
                    Toast.makeText(SignInActivity.this,"服务器忙，请稍后再试",Toast.LENGTH_SHORT).show();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SignInActivity.this,"签到成功",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
