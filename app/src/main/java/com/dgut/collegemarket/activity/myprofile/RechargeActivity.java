package com.dgut.collegemarket.activity.myprofile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.adapter.RechargeListAdapter;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Records;
import com.dgut.collegemarket.fragment.InputCell.SimpleTextInputCellFragment;
import com.dgut.collegemarket.util.AnimationEffec;
import com.dgut.collegemarket.util.CommonUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

//充值界面—实现充值功能
public class RechargeActivity extends Activity {

    String cause;
    ImageView imageview_turnback;
    LinearLayout ll_2, ll_3, ll_4, ll_5, ll_6;
    TextView tv_1, tv_2, tv_3, tv_4, tv_5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_recharge);

        imageview_turnback = (ImageView) findViewById(R.id.imageview_turnback);
        imageview_turnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_1 = (TextView) findViewById(R.id.tv_1);
        ll_2 = (LinearLayout) findViewById(R.id.ll_2);
        ll_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cause = ("支付宝(账号158****7205)充值");

                Intent itnt = new Intent(RechargeActivity.this, ContentRechargeActivity.class);
                itnt.putExtra("cause", cause);
                startActivity(itnt);
                finish();
            }
        });

        tv_2 = (TextView) findViewById(R.id.tv_2);
        ll_3 = (LinearLayout) findViewById(R.id.ll_3);
        ll_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cause = ("微信(账号158****7205)充值");

                Intent itnt = new Intent(RechargeActivity.this, ContentRechargeActivity.class);
                itnt.putExtra("cause", cause);
                startActivity(itnt);
                finish();
            }
        });

        tv_3 = (TextView) findViewById(R.id.tv_3);
        ll_4 = (LinearLayout) findViewById(R.id.ll_4);
        ll_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cause = ("白金卡(尾号6660)充值");

                Intent itnt = new Intent(RechargeActivity.this, ContentRechargeActivity.class);
                itnt.putExtra("cause", cause);
                startActivity(itnt);
                finish();
            }
        });

        tv_4 = (TextView) findViewById(R.id.tv_4);
        ll_5 = (LinearLayout) findViewById(R.id.ll_5);
        ll_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cause = ("黑金卡(尾号8880)充值");

                Intent itnt = new Intent(RechargeActivity.this, ContentRechargeActivity.class);
                itnt.putExtra("cause", cause);
                startActivity(itnt);
                finish();
            }
        });

        tv_5 = (TextView) findViewById(R.id.tv_5);
        ll_6 = (LinearLayout) findViewById(R.id.ll_6);
        ll_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cause = ("钻石卡(尾号9990)充值");

                Intent itnt = new Intent(RechargeActivity.this, ContentRechargeActivity.class);
                itnt.putExtra("cause", cause);
                startActivity(itnt);
                finish();
            }
        });


    }


//    void recharge() {
//
//        cause = because[adapter.getSelectItem()];
//
//        if (!editText.getText().toString().equals("")) {
//            AnimationEffec.setTransAniToRight(button, 0, 700, 0, 0, 1500);
//            String coin = editText.getText().toString();
//
//
//            MultipartBody body = new MultipartBody.Builder()
//                    .addFormDataPart("coin", coin)
//                    .addFormDataPart("cause", cause)
//                    .build();
//
//            Request request = Server.requestBuilderWithApi("rec/records/recharge")
//                    .post(body)
//                    .build();
//
//            Server.getSharedClient().newCall(request).enqueue(new Callback() {
//                @Override
//                public void onResponse(Call arg0, Response arg1) throws IOException {
//
//                    final String responseBody = arg1.body().string();
//
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//
//                            RechargeActivity.this.onSucceed(responseBody);
//                        }
//                    });
//                }
//
//                @Override
//                public void onFailure(Call arg0, final IOException arg1) {
//
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//
//                            RechargeActivity.this.onFailure(arg1);
//                        }
//                    });
//                }
//            });
//        } else {
//            Toast.makeText(RechargeActivity.this, "请输入金额", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    void onSucceed(String text) {
//
//        new AlertDialog.Builder(this).setMessage(text)
//                .setMessage("充值成功！")
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        finish();
//                        overridePendingTransition(R.anim.none, R.anim.slide_out_bottom);
//                    }
//                }).show();
//    }
//
//    void onFailure(Exception e) {
//
//        new AlertDialog.Builder(this)
//                .setMessage("网络异常" + e.getMessage())
//                .show();
//    }


    @Override
    protected void onResume() {
        super.onResume();

        Toast.makeText(RechargeActivity.this, "请选择充值方式", Toast.LENGTH_SHORT).show();

    }
}
