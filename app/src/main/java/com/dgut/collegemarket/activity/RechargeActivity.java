package com.dgut.collegemarket.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.entity.Records;
import com.dgut.collegemarket.api.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class RechargeActivity extends AppCompatActivity {

    private Spinner spinner;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    EditText editText;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        editText = (EditText) findViewById(R.id.edit1);
        button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recharge();
            }
        });

        spinner = (Spinner) findViewById(R.id.spinner);

        //数据
        data_list = new ArrayList<String>();
        data_list.add("100");
        data_list.add("200");
        data_list.add("500");
        data_list.add("1000");

        //适配器
        arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);
    }

    void recharge() {
        if (!editText.equals("")) {
            String coin = editText.getText().toString();
            String cause = "充值";

            MultipartBody body = new MultipartBody.Builder()
                    .addFormDataPart("coin", coin)
                    .addFormDataPart("cause", cause)
                    .build();

            Request request = Server.requestBuilderWithApi("record/records/recharge")
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
            Toast.makeText(RechargeActivity.this, "金额为空！", Toast.LENGTH_LONG).show();
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
        new AlertDialog.Builder(this).setMessage(e.getMessage()).show();
    }

}
