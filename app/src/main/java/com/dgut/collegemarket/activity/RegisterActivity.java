package com.dgut.collegemarket.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.fragment.InputCell.PictrueInputCellFragment;
import com.dgut.collegemarket.fragment.InputCell.SimpleTextInputCellFragment;
import com.dgut.collegemarket.util.MD5;
import com.dgut.collegemarket.util.EmailUtil;


import java.io.IOException;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends Activity {

    SimpleTextInputCellFragment account;
    SimpleTextInputCellFragment password;
    SimpleTextInputCellFragment passwordRepeat;
    SimpleTextInputCellFragment name;
    SimpleTextInputCellFragment email;

    PictrueInputCellFragment pictrue;
    Button register;
    TextView exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        account = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_account);
        password = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_password);
        passwordRepeat = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_password2);
        pictrue = (PictrueInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_pictrue);
        name = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_name);
        email = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_email);
        register = (Button) findViewById(R.id.register);
        exit = (TextView) findViewById(R.id.tv_exit);

        account.setInputType(InputType.TYPE_CLASS_NUMBER);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.none,R.anim.slide_out_bottom);
            }
        });
    }

    /**
     * 提交注册信息
     */
    private void submit() {

//        if (!isInputCorrect()) {
//            return;
//        }
        final String accountS = account.getText();
        String passwordS = password.getText();
        String passwordRepeatS = passwordRepeat.getText();
        String emailS = email.getText();
        String nameS = name.getText();

        if (!passwordS.equals(passwordRepeatS)) {
            passwordRepeat.setEidtError("密码不一致");
            passwordRepeat.setClickable();
            return;
        }
       final String passwordMD5 = MD5.getMD5(passwordS);

        OkHttpClient client = new OkHttpClient();

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("account", accountS)
                .addFormDataPart("passwordHash", passwordMD5)
                .addFormDataPart("email", emailS)
                .addFormDataPart("name", nameS);
        if (pictrue.getPngData() != null)
            multipartBuilder.addFormDataPart("avatar", "avatarName"
                    , RequestBody
                            .create(
                                    MediaType.parse("image/png")
                                    , pictrue.getPngData()));

        final Request request = Server.requestBuilderWithApi("user/register")
                .post(multipartBuilder.build())
                .build();

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("注册中");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle("注册失败")
                                .setMessage("原因：" + e.getLocalizedMessage())
                                .setCancelable(true)
                                .setNegativeButton("好", null)
                                .show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (response.isSuccessful()) {
                    final String result = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!request.equals("")) {


/**=================     调用SDK注册接口    =================*/
                                JMessageClient.register(accountS, passwordMD5, new BasicCallback() {
                                    @Override
                                    public void gotResult(int responseCode, String registerDesc) {
                                        if (responseCode == 0) {
                                            progressDialog.dismiss();

                                            new AlertDialog.Builder(RegisterActivity.this)
                                                    .setTitle("注册成功")
                                                    .setCancelable(true)
                                                    .setNegativeButton("马上登陆", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            finish();
                                                        }
                                                    })
                                                    .show();
                                            Log.i("RegisterActivity", "JMessageClient.register " + ", responseCode = " + responseCode + " ; registerDesc = " + registerDesc);

                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                                            Log.i("RegisterActivity", "JMessageClient.register " + ", responseCode = " + responseCode + " ; registerDesc = " + registerDesc);
                                        }
                                    }
                                });

                            }
                            else {
                                progressDialog.dismiss();
                                new AlertDialog.Builder(RegisterActivity.this)
                                        .setTitle("注册失败")
                                        .setMessage("用户名已存在")
                                        .setCancelable(true)
                                        .setNegativeButton("重新注册", null)
                                        .show();
                            }
                        }
                    });

                }
            }
        });

    }

    /**
     * 判断用户输入
     * @return
     */
    private boolean isInputCorrect() {
        if (account.getText().equals("")) {
            account.setLayoutError("用户名不能为空");
            password.getText();//清除上一次密码为空的提示
            return false;
        } else if(!(account.getText().toString().length()>=8 && account.getText().toString().length()<=13)){
            account.setLayoutError("用户名输入格式不正确，请输入8-13个数字");
            return false;
        }

        if (password.getText().equals("")) {
            password.setLayoutError("密码不能为空");
            return false;
        }else if(!(password.getText().length()>=6 && password.getText().length()<=13)){
            password.setLayoutError("密码输入格式不正确，请输入6-13个字符");
            return false;
        }

        if (passwordRepeat.getText().equals("")) {
            passwordRepeat.setLayoutError("重复密码不能为空");
            return false;
        }else if(!(passwordRepeat.getText().length()>=6 && passwordRepeat.getText().length()<=13)){
            passwordRepeat.setLayoutError("重复密码输入格式不正确，请输入6-13个字符");
            return false;
        }

        if (email.getText().equals("")) {
            email.setLayoutError("邮箱地址不能为空");
            return false;
        }else if(!EmailUtil.isEmail(email.getText())){
            email.setLayoutError("邮箱格式不正确");
            return false;
        }

        if (name.getText().equals(email.getText())) {
            name.setLayoutError("昵称不能为空");
            return false;
        }else if (name.getText().length()>10){
            name.setLayoutError("昵称长度超出范围");
            return false;
        }

        return true;

    }

    /**
     * 返回
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onResume() {
        JPushInterface.onResume(this);
        super.onResume();

        account.setHintText("请输入账号(8-13位数字)");
        password.setHintText("请输入密码(6-13位字符)");
        password.setIsPassword(true);
        passwordRepeat.setHintText("请再输入一次密码(6-13位字符)");
        passwordRepeat.setIsPassword(true);
//        pictrue.setHintText("更改图片");
//        pictrue.setLableText("选择头像");
        email.setHintText("邮箱地址");
        email.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
        name.setHintText("昵称");
    }


    @Override
    protected void onPause() {
        JPushInterface.onPause(this);
        finish();
        super.onPause();
    }
}
