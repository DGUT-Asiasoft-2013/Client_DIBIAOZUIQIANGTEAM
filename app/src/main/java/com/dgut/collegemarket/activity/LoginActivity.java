package com.dgut.collegemarket.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;


import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.myprofile.userInfo.ForgetPasswordStep1Activity;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.app.CurrentUserInfo;
import com.dgut.collegemarket.fragment.InputCell.SimpleTextInputCellFragment;
import com.dgut.collegemarket.util.MD5;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 登录界面 实现账号密码的POST请求 得到返回的用户信息
 */
public class LoginActivity extends FragmentActivity {

    SimpleTextInputCellFragment account;
    SimpleTextInputCellFragment password;
    Button login;
    TextView recover,register;
    ProgressDialog progressDialog;
    CheckBox cbRememberPassword;
    CheckBox cbAutoLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        account = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_account);
        password = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_password);

        login = (Button) findViewById(R.id.username_sign_in_button);
        recover = (TextView) findViewById(R.id.password_recover);

        register = (TextView) findViewById(R.id.register);
        cbAutoLogin = (CheckBox) findViewById(R.id.cb_auto_login);
        cbRememberPassword = (CheckBox) findViewById(R.id.cb_remember_password);

        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInputCorrect()) {
                    return;
                }

                loginHttpRequest();
            }
        });
        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                overridePendingTransition(R.anim.slide_in_bottom,R.anim.none);
            }
        });
        recover.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordStep1Activity.class));
                overridePendingTransition(R.anim.slide_in_left,R.anim.none);
            }
        });
        cbRememberPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    cbAutoLogin.setChecked(false);
                }
            }
        });
        cbAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    cbRememberPassword.setChecked(true);
                }
            }
        });
        initUser();
    }

    /**
     * 初始化输入
     */
    public void initUser(){
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String accounts = preferences.getString("account","");
        String passwords = preferences.getString("password","");
        boolean auto = preferences.getBoolean("auto",false);
        boolean remember = preferences.getBoolean("remember",false);
        account.setLableText(accounts);
        password.setLableText(passwords);
        cbAutoLogin.setChecked(auto);
        cbRememberPassword.setChecked(remember);
        if(auto){
            loginHttpRequest();
        }
    }

    /**
     * 设置User参数
     * @param account
     * @param password
     */
    public void setUser(String account,String password,boolean remember,boolean auto){
        SharedPreferences preferences = getSharedPreferences("user",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("account",account);
        editor.putString("password",password);
        editor.putBoolean("auto",auto);
        editor.putBoolean("remember",remember);
        editor.commit();
    }

    /**
     * 登录
     */
    private void loginHttpRequest() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("登录中");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient client = Server.getSharedClient();

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("account", account.getText())
                .addFormDataPart("passwordHash", MD5.getMD5(password.getText()));//密码加密


        Request request = Server.requestBuilderWithApi("user/login")
                .post(multipartBuilder.build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,"网络连接失败，请检查网络设置",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                progressDialog.dismiss();
                final String result = response.body().string();
                if(result.equals("")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    final User user = new ObjectMapper().readValue(result, User.class);
                    CurrentUserInfo.user_id=user.getId();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            CurrentUserInfo.online = true;
                            if(cbRememberPassword.isChecked()){
                                if(cbAutoLogin.isChecked()){
                                    setUser(account.getText(),password.getText(),true,true);
                                }else{
                                    setUser(account.getText(),password.getText(),true,false);
                                }
                            }
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });
    }


    /**
     * 判断输入
     * @return
     */
    private boolean isInputCorrect() {
        if (account.getText().equals("")) {
            account.setLayoutError("用户名不能为空");
            password.getText();//清除上一次密码为空的提示
            return false;
        }
        if (password.getText().equals("")) {
            password.setLayoutError("密码不能为空");
            return false;
        }
        return true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        account.setHintText("请输入账号");
        password.setHintText("请输入密码");
        password.setIsPassword(true);
    }
}

