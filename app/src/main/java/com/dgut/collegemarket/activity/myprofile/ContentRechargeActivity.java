package com.dgut.collegemarket.activity.myprofile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Records;
import com.dgut.collegemarket.util.AnimationEffec;
import com.dgut.collegemarket.util.CommonUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class ContentRechargeActivity extends Activity {

    Button btn_recharge;
    EditText et_money;
    String cause;
    ImageView iv_turnback;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_recharge);

        cause = getIntent().getStringExtra("cause");

        iv_turnback = (ImageView) findViewById(R.id.iv_turnback);
        iv_turnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent itnt = new Intent(ContentRechargeActivity.this, RechargeActivity.class);
                startActivity(itnt);
            }
        });


        text = (TextView) findViewById(R.id.text);
        text.setText(cause.toString());

        et_money = (EditText) findViewById(R.id.et_money);
        btn_recharge = (Button) findViewById(R.id.btn_recharge);
        btn_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_recharge.setEnabled(false);
                btn_recharge.setBackgroundColor(Color.parseColor("#ffb8b8b8"));
                btn_recharge.setTextColor(Color.BLACK);
                recharge();
            }
        });
    }

    void recharge() {


        if (!et_money.getText().toString().equals("")) {
            String coin = et_money.getText().toString();


            MultipartBody body = new MultipartBody.Builder()
                    .addFormDataPart("coin", coin)
                    .addFormDataPart("cause", cause)
                    .build();

            Request request = Server.requestBuilderWithApi("rec/records/recharge")
                    .post(body)
                    .build();

            Server.getSharedClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call arg0, Response arg1) throws IOException {

                    final String responseBody = arg1.body().string();

                    runOnUiThread(new Runnable() {
                        public void run() {

                            ContentRechargeActivity.this.onSucceed(responseBody);
                        }
                    });
                }

                @Override
                public void onFailure(Call arg0, final IOException arg1) {

                    runOnUiThread(new Runnable() {
                        public void run() {

                            ContentRechargeActivity.this.onFailure(arg1);
                        }
                    });
                }
            });
        } else {
            Toast.makeText(ContentRechargeActivity.this, "请输入金额", Toast.LENGTH_SHORT).show();
        }
    }

    void onSucceed(String text) {

        new AlertDialog.Builder(this).setMessage(text)

                .setMessage("充值成功！")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                        overridePendingTransition(R.anim.none, R.anim.slide_out_bottom);
                    }
                }).show();
    }

    void onFailure(Exception e) {

        new AlertDialog.Builder(this)
                .setMessage("网络异常" + e.getMessage())
                .show();
    }


    @Override
    protected void onResume() {
        super.onResume();

        Toast.makeText(ContentRechargeActivity.this, "请输入金额", Toast.LENGTH_SHORT).show();
    }
}
