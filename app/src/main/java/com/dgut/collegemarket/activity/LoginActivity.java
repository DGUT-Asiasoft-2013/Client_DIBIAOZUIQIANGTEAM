package com.dgut.collegemarket.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.myprofile.userInfo.ForgetPasswordStep1Activity;
import com.dgut.collegemarket.adapter.EditTextAdapter;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.app.CurrentUserInfo;
import com.dgut.collegemarket.util.MD5;
import com.dgut.collegemarket.view.widgets.DropEditText;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import im.sdk.debug.utils.JPushUtil;
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


    Button login;
    TextView recover,register;
    ProgressDialog progressDialog;
    CheckBox cbRememberPassword;
    CheckBox cbAutoLogin;

    DropEditText account;
    EditText password;
    EditTextAdapter adapter;

    List<String> accountList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        account = (DropEditText) findViewById(R.id.drop_edit_text);
        password = (EditText) findViewById(R.id.et_pasword);

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
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), RegistersActivity.class);
                startActivity(intent);
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
        account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password.setText("");
                cbRememberPassword.setChecked(false);
                cbAutoLogin.setChecked(false);
                SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                for(String account:accountList){
                    if(account.equals(charSequence.toString())){
                        password.setText(preferences.getString(account+"password",""));
                        cbRememberPassword.setChecked(preferences.getBoolean(account+"remember",false));
                        cbAutoLogin.setChecked(preferences.getBoolean(account+"auto",false));
                        break;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        initUser();
    }

    /**
     * 初始化输入
     */
    public void initUser(){
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        Set<String> accountSet  = preferences.getStringSet("accountset",null);

        if(accountSet==null){
            return;
        }

        Iterator<String> iterator = accountSet.iterator();

        while(iterator.hasNext()){
            accountList.add(iterator.next());
        }
        adapter = new EditTextAdapter(this,accountList,LoginActivity.this);
        account.setAdapter(adapter);

        if(accountList!=null&&accountList.size()>0) {
            String passwords = preferences.getString(accountList.get(0) + "password", "");
            boolean auto = preferences.getBoolean(accountList.get(0) + "auto", false);
            boolean remember = preferences.getBoolean(accountList.get(0) + "remember", false);

            account.setText(accountList.get(0));
            account.setSelection(accountList.get(0).length());
            password.setText(passwords);

            cbAutoLogin.setChecked(auto);
            cbRememberPassword.setChecked(remember);
            if (auto) {
                loginHttpRequest();
            }
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

        Set<String> accountSet = preferences.getStringSet("accountset",null);
        if(accountSet==null){
            accountSet = new HashSet<String>();
        }
        accountSet.add(account);
        editor.putStringSet("accountset",accountSet);

        editor.putString(account+"password",password);
        editor.putBoolean(account+"auto",auto);
        editor.putBoolean(account+"remember",remember);
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
                .addFormDataPart("account", account.getText().toString())
                .addFormDataPart("passwordHash", MD5.getMD5(password.getText().toString()));//密码加密


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

                final String result = response.body().string();
                if(result.equals("")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {

                    final User user = new ObjectMapper().readValue(result, User.class);
                    CurrentUserInfo.user_id=user.getId();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /**=================     调用SDk登陆接口    =================*/
                            JMessageClient.login(account.getText().toString(), MD5.getMD5(password.getText().toString()), new BasicCallback() {
                                @Override
                                public void gotResult(int responseCode, String LoginDesc) {
                                    if (responseCode == 0) {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                        CurrentUserInfo.online = true;
                                        if(cbRememberPassword.isChecked()){
                                            if(cbAutoLogin.isChecked()){
                                                setUser(account.getText().toString(),password.getText().toString(),true,true);
                                            }else{
                                                setUser(account.getText().toString(),password.getText().toString(),true,false);
                                            }
                                        }
                                        //调用JPush API设置Alias
                                        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, account.getText().toString()));
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        Log.i("LoginActivity", "JMessageClient.login" + ", responseCode = " + responseCode + " ; LoginDesc = " + LoginDesc);

                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                                        Log.i("LoginActivity", "JMessageClient.login" + ", responseCode = " + responseCode + " ; LoginDesc = " + LoginDesc);
                                    }
                                }
                            });

                        }
                    });
                }
            }
        });
    }

    /**
     * #################    第一个界面,登陆或者是注册    #################
     */
    private void initData() {
        /**=================     获取个人信息不是null则直接进入Main界面    =================*/
        UserInfo myInfo = JMessageClient.getMyInfo();
        if (myInfo != null) {
            //调用JPush API设置Alias
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, myInfo.getUserName()));
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 判断输入
     * @return
     */
    private boolean isInputCorrect() {
        if (account.getText().equals("")) {
           Toast.makeText(LoginActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.getText().equals("")) {
            Toast.makeText(LoginActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    @Override
    protected void onResume() {
        JPushInterface.onResume(this);
        super.onResume();
        account.setHint("请输入账号");
        password.setHint("请输入密码");
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    public void deleteAccount(String accounts){
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        Set<String> accountSet  = preferences.getStringSet("accountset",null);
        accountSet.remove(accounts);
        SharedPreferences.Editor editor =  preferences.edit();
        editor.remove(accounts+"password");
        editor.remove(accounts+"remember");
        editor.remove(accounts+"auto");
        editor.commit();
        accountList.remove(accounts);
        if(accountList.size()>0){
            account.setText(accountList.get(0));
            adapter.notifyDataSetChanged();
        }else{
            account.setText("");
            account.dismiss();
        }

    }

    private static final String TAG = "JPush";
    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
                    break;

                case MSG_SET_TAGS:
                    Log.d(TAG, "Set tags in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), null, (Set<String>) msg.obj, mTagsCallback);
                    break;

                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };

    @Override
    protected void onPause() {
        JPushInterface.onPause(this);
        super.onPause();
    }


    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    if (JPushUtil.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }

            JPushUtil.showToast(logs, getApplicationContext());
        }

    };

    private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    if (JPushUtil.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }

            JPushUtil.showToast(logs, getApplicationContext());
        }

    };

}

