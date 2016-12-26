package com.dgut.collegemarket.activity.myprofile.userInfo;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.dgut.collegemarket.util.Util;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ForgetPasswordStep1Activity extends AppCompatActivity {

    SimpleTextInputCellFragment fragEmail = new SimpleTextInputCellFragment();
    SimpleTextInputCellFragment fragVerificationCode = new SimpleTextInputCellFragment();

    Button btnUpdate;
    Button btnGetVerificationCode;
    TextView exit;
    int verificationCode=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        fragEmail = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_user_id);
        fragVerificationCode = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_new_password);
        btnGetVerificationCode = (Button) findViewById(R.id.btn_get_verification_code);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        exit = (TextView) findViewById(R.id.tv_exit);
        fragEmail.setHintText("用户邮箱");
        fragVerificationCode.setHintText("验证码");

        exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.none,R.anim.slide_out_left);
            }
        });

        btnGetVerificationCode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                verificationCode = (int)(Math.random()*100);
                Toast.makeText(ForgetPasswordStep1Activity.this,"验证码："+verificationCode,Toast.LENGTH_SHORT).show();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fragEmail.getText().equals("")){
                    Toast.makeText(ForgetPasswordStep1Activity.this,"邮箱不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }else if(!Util.isEmail(fragEmail.getText())){
                    Toast.makeText(ForgetPasswordStep1Activity.this,"邮箱格式不正确",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(fragVerificationCode.getText().equals("验证码不能为空")){
                    Toast.makeText(ForgetPasswordStep1Activity.this,"",Toast.LENGTH_SHORT).show();
                    return;
                }else if(!(fragVerificationCode.getText().equals(verificationCode+""))){
                    Toast.makeText(ForgetPasswordStep1Activity.this,"验证码不正确",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(ForgetPasswordStep1Activity.this,ForgetPasswordStep2Activity.class);
                intent.putExtra("email",fragEmail.getText());
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.none);
            }
        });
    }
}
