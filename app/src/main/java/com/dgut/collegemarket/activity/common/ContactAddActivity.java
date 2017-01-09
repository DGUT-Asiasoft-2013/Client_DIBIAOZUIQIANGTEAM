package com.dgut.collegemarket.activity.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.LoginActivity;
import com.dgut.collegemarket.activity.MainActivity;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Contact;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.util.MD5;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ContactAddActivity extends Activity {

    EditText nameEt, phoneEt, schoolEt, susheEt;
    ImageView submitBtn;
   ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_add);
        nameEt = (EditText) findViewById(R.id.et_name);
        phoneEt = (EditText) findViewById(R.id.et_phone);
        schoolEt = (EditText) findViewById(R.id.et_school);
        susheEt = (EditText) findViewById(R.id.et_sushe);
        submitBtn = (ImageView) findViewById(R.id.btn_checkmark);
        backBtn= (ImageView) findViewById(R.id.iv_add_contact_back);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.none, R.anim.slide_out_left);
            }
        });
    }

    ProgressDialog progressDialog;

    private void submit() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("保存中");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient client = Server.getSharedClient();

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", nameEt.getText().toString())
                .addFormDataPart("phone", phoneEt.getText().toString())
                .addFormDataPart("school", schoolEt.getText().toString())
                .addFormDataPart("sushe", susheEt.getText().toString());

        Request request = Server.requestBuilderWithApi("contact/add")
                .post(multipartBuilder.build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ContactAddActivity.this, "网络连接失败，请检查网络设置", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                progressDialog.dismiss();
                final String result = response.body().string();

                if (result.equals("")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ContactAddActivity.this, "服务器异常", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    final Contact contact = new ObjectMapper().readValue(result, Contact.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            overridePendingTransition(R.anim.none, R.anim.slide_out_left);
                        }
                    });
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.none, R.anim.slide_out_left);
    }

}
