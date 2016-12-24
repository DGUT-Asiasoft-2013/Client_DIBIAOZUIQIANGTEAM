package com.dgut.collegemarket.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.fragment.InputCell.SimpleTextInputCellFragment;
import com.dgut.collegemarket.util.MD5;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ForgetPasswordActivity extends AppCompatActivity {

    SimpleTextInputCellFragment fragUserId = new SimpleTextInputCellFragment();
    SimpleTextInputCellFragment fragNewPassword = new SimpleTextInputCellFragment();
    SimpleTextInputCellFragment fragRepaterPassword = new SimpleTextInputCellFragment();

    Button btnUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        fragUserId = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_user_id);
        fragNewPassword = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_new_password);
        fragRepaterPassword = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_repater_password);
        btnUpdate = (Button) findViewById(R.id.btn_update);

        fragUserId.setHintText("用户账号");
        fragNewPassword.setHintText("新密码");
        fragRepaterPassword.setHintText("重复密码");


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!fragNewPassword.getText().toString().equals(fragRepaterPassword.getText().toString())) {
                    Toast.makeText(ForgetPasswordActivity.this,"前后密码不一致",Toast.LENGTH_SHORT).show();
                    return;
                }
                OkHttpClient okHttpClient = Server.getSharedClient();
                MultipartBody requestBody = new MultipartBody.Builder()
                        .addFormDataPart("account",fragUserId.getText().toString())
                        .addFormDataPart("newpassword",MD5.getMD5(fragNewPassword.getText().toString()))
                        .build();
                Request request = Server.requestBuilderWithApi("user/forget/password")
                        .post(requestBody)
                        .build();
                final ProgressDialog dialog = new ProgressDialog(ForgetPasswordActivity.this);
                dialog.setMessage("正在提交");
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();

                                Toast.makeText(ForgetPasswordActivity.this,"修改密码失败，请检查网络",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String result = response.body().string();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Log.e("debug",result);
                                Toast.makeText(ForgetPasswordActivity.this,result,Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

                    }
                });
            }
        });
    }
}
