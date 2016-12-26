package com.dgut.collegemarket.activity.myprofile.userInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class ForgetPasswordStep2Activity extends Activity {

    SimpleTextInputCellFragment fragNewpassword = new SimpleTextInputCellFragment();
    SimpleTextInputCellFragment fragRepaterpassword = new SimpleTextInputCellFragment();

    Button btnSubmit;
    TextView exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_step2);

        fragNewpassword = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_new_password);
        fragRepaterpassword = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_repater_password);
        btnSubmit = (Button) findViewById(R.id.btn_update);
        exit = (TextView) findViewById(R.id.tv_exit);

        fragNewpassword.setHintText("新密码");
        fragRepaterpassword.setHintText("重复密码");

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.none,R.anim.slide_out_left);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPasswrod = fragNewpassword.getText().toString();
                String repaterPassword = fragRepaterpassword.getText().toString();
                if(!newPasswrod.equals(repaterPassword)){
                    Toast.makeText(ForgetPasswordStep2Activity.this,"重复密码错误",Toast.LENGTH_SHORT).show();
                    return;
                }
                OkHttpClient okHttpClient = Server.getSharedClient();
                MultipartBody requestBody = new MultipartBody.Builder()
                        .addFormDataPart("email",getIntent().getStringExtra("email"))
                        .addFormDataPart("newpassword", MD5.getMD5(newPasswrod))
                        .build();
                Request request = Server.requestBuilderWithApi("user/forget/password")
                        .post(requestBody)
                        .build();
                final ProgressDialog dialog = new ProgressDialog(ForgetPasswordStep2Activity.this);
                dialog.setMessage("正在提交请求");
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
                                Toast.makeText(ForgetPasswordStep2Activity.this,"修改密码失败，请检查网络",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Toast.makeText(ForgetPasswordStep2Activity.this,"修改密码成功",Toast.LENGTH_SHORT).show();
                                finish();
                                overridePendingTransition(R.anim.none,R.anim.slide_out_left);
                            }
                        });

                    }
                });
            }
        });
    }
}
