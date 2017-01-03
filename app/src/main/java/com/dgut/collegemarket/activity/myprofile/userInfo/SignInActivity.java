package com.dgut.collegemarket.activity.myprofile.userInfo;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.adapter.SignListAdapter;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Page;
import com.dgut.collegemarket.api.entity.Sign;
import com.dgut.collegemarket.api.entity.SignMsg;
import com.dgut.collegemarket.api.entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    TextView exit;
    TextView tvCount;
    TextView tvRanking;
    List<Sign> signList = new ArrayList<Sign>();
    int pageNums = 0;

    SignListAdapter signListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        lvSign = (ListView) findViewById(R.id.lv_sign);
        btnSign = (Button) findViewById(R.id.btn_sign);
        exit = (TextView) findViewById(R.id.tv_exit);
        tvCount = (TextView) findViewById(R.id.textView18);
        tvRanking = (TextView) findViewById(R.id.textView14);
        signListAdapter = new SignListAdapter(SignInActivity.this,signList);

        lvSign.setAdapter(signListAdapter);
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSign.setClickable(false);
                int xp = new Random().nextInt(3)+1;
                addSign(xp);
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.none,R.anim.slide_out_left);
            }
        });
        getAllSigns(pageNums);
        btnSign.setClickable(false);
    }

    /**
     * 签到
     * @param xp
     */
    public void addSign(int xp){
        OkHttpClient client = Server.getSharedClient();
        MultipartBody requestBody = new MultipartBody.Builder()
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
                        btnSign.setClickable(true);
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
                        btnSign.setText("已签到");
                        btnSign.setClickable(false);
                        pageNums = 0;
                        signList.clear();
                        getAllSigns(pageNums);
                    }
                });
            }
        });
    }

    /**
     * 获取当天签到的人信息
     */
    public void getAllSigns(int pageNum){
        OkHttpClient client = Server.getSharedClient();
        Request request = Server.requestBuilderWithApi("sign/getallsigns/"+pageNum).build();
        client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SignInActivity.this,"网络连接失败，请检查网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                SignMsg sm = new ObjectMapper().readValue(result,SignMsg.class);
                signList.addAll(sm.getPage().getContent());
                pageNums = sm.getPage().getNumber();
                final int count = sm.getCount();
                final int ranking = sm.getRanking();
                runOnUiThread(new Runnable() {
                    public void run() {
//                        Toast.makeText(SignInActivity.this,"成功获取列表",Toast.LENGTH_SHORT).show();
                        tvCount.setText(count+"");
                        tvRanking.setText(ranking+"");
                        signListAdapter.notifyDataSetInvalidated();
                        if(ranking!=0){;
                            btnSign.setText("已签到");
                            btnSign.setClickable(false);
                        }else{
                            btnSign.setClickable(true);
                        }
                    }
                });
            }
        });
    }
}
