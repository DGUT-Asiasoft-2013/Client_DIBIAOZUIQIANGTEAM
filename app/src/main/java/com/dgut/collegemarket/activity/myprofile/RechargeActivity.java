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

    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    EditText editText;
    TextView textView9;
    ImageView button, imageView_turnBack, recharge_back;
    ListView list;
    ListAdapter listAdapter;

    private ListView listView;
    private RechargeListAdapter adapter;
    private String[] beans = new String[]{"支付宝转账", "微    信转账", "黑金卡支付", "白金卡支付", "钻石卡支付",
            "银联卡支付", "长城卡支付", "农行卡支付", "工商卡支付", "浦发卡支付"};

    private String[] because = new String[]{"用支付宝充值了", "用微信充值了", "用黑金卡充值了", "用白金卡充值了", "用钻石卡充值了",
            "用银联卡充值了", "用长城卡充值了", "用农行卡充值了", "用工商卡充值了", "用浦发卡充值了"};

    int i;
    String cause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_recharge);


        textView9 = (TextView) findViewById(R.id.textView9);
        textView9.setVisibility(View.GONE);

        editText = (EditText) findViewById(R.id.edit1);
        editText.setVisibility(View.GONE);

        imageView_turnBack = (ImageView) findViewById(R.id.imageView_turnBack);
        imageView_turnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        list = (ListView) findViewById(R.id.recharge_list);
        list.setAdapter(listAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                onItemClicked(position);
            }
        });

        initView();

        button = (ImageView) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CommonUtils.isFastDoubleClick()) {
                    Toast.makeText(RechargeActivity.this, "请勿重复点击", Toast.LENGTH_LONG).show();
                    editText.setText("");
                    return;
                } else {
                    recharge();
                }
            }
        });
    }


    private void initView() {
        // TODO Auto-generated method stub
        Log.i("htp", "beans.size:" + beans.length);
        listView = (ListView) findViewById(R.id.recharge_list);
        adapter = new RechargeListAdapter(RechargeActivity.this, beans);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    void onItemClicked(int position) {

    }


    void recharge() {

//        String coin = editText.getText().toString();
//
//        Intent itnt = new Intent(this, ContentRechargeActivity.class);
//        itnt.putExtra("coin", coin);
//        startActivity(itnt);


        textView9.setVisibility(View.VISIBLE);
        editText.setVisibility(View.VISIBLE);

        cause = because[adapter.getSelectItem()];

        if (!editText.getText().toString().equals("")) {
            AnimationEffec.setTransAniToRight(button, 0, 700, 0, 0, 1500);
            String coin = editText.getText().toString();


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

                            RechargeActivity.this.onSucceed(responseBody);
                        }
                    });
                }

                @Override
                public void onFailure(Call arg0, final IOException arg1) {

                    runOnUiThread(new Runnable() {
                        public void run() {

                            RechargeActivity.this.onFailure(arg1);
                        }
                    });
                }
            });
        } else {
            Toast.makeText(RechargeActivity.this, "请输入金额", Toast.LENGTH_SHORT).show();
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

        Toast.makeText(RechargeActivity.this, "请选择支付方式", Toast.LENGTH_SHORT).show();

    }
}
