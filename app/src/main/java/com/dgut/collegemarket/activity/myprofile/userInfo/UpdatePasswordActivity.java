package com.dgut.collegemarket.activity.myprofile.userInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.LoginActivity;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.fragment.InputCell.SimpleTextInputCellFragment;
import com.dgut.collegemarket.util.MD5;

import java.io.IOException;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdatePasswordActivity extends Activity {



    Button btnUpdate;
    ImageView ivback;
    EditText etPassword;
    EditText etRepatedPassword;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getIntent().getSerializableExtra("user");
        setContentView(R.layout.activity_update_password);
        init();

    }

    void init() {
        btnUpdate = (Button) findViewById(R.id.btn_next);
        ivback = (ImageView) findViewById(R.id.iv_back);
        etPassword = (EditText) findViewById(R.id.et_mobile);
        etRepatedPassword = (EditText) findViewById(R.id.et_repated_password);
        btnUpdate.setText("确认");
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePassword();
            }
        });
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.none, R.anim.slide_out_left);
            }
        });
    }

    void updatePassword() {


                final String password = etPassword.getText().toString();
                if (!password.equals(etRepatedPassword.getText().toString())) {
                    Toast.makeText(UpdatePasswordActivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                OkHttpClient okHttpClient = Server.getSharedClient();
                MultipartBody requestBody = new MultipartBody.Builder()
                        .addFormDataPart("newpassword", MD5.getMD5(etPassword.getText().toString()))
                        .build();
                Request request = Server.requestBuilderWithApi("user/update/password")
                        .post(requestBody)
                        .build();
                final ProgressDialog dialog = new ProgressDialog(UpdatePasswordActivity.this);
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
                                Toast.makeText(UpdatePasswordActivity.this, "修改密码失败，请检查网络", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        JMessageClient.updateUserPassword(user.getPasswordHash(), MD5.getMD5(etPassword.getText().toString()), new BasicCallback() {
                            @Override
                            public void gotResult(final int i, String s) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        if (i == 0) {
                                            Toast.makeText(UpdatePasswordActivity.this, "修改密码成功,请重新登录", Toast.LENGTH_SHORT).show();
                                            JMessageClient.logout();
                                            JPushInterface.setAlias(UpdatePasswordActivity.this,"",null);
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    }
                                });
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
