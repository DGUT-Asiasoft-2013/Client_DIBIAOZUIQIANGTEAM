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
import com.dgut.collegemarket.activity.LoginActivity;
import com.dgut.collegemarket.activity.RegisterSuccessActivity;
import com.dgut.collegemarket.activity.RegistersActivity;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.fragment.InputCell.SimpleTextInputCellFragment;
import com.dgut.collegemarket.fragment.pages.forgetpassword.ForgetPasswordFirstFragment;
import com.dgut.collegemarket.fragment.pages.forgetpassword.ForgetPasswordSecondFragment;
import com.dgut.collegemarket.fragment.pages.forgetpassword.ForgetPasswordThirdFragment;
import com.dgut.collegemarket.fragment.pages.regist.RegisterFirstFragment;
import com.dgut.collegemarket.fragment.pages.regist.RegisterSecondFragment;
import com.dgut.collegemarket.fragment.pages.regist.RegisterThirdFragment;
import com.dgut.collegemarket.util.MD5;
import com.dgut.collegemarket.util.Util;

import java.io.IOException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ForgetPasswordActivity extends AppCompatActivity {

    private String APPKEY = "1a8522e867d05";
    private String APPSECURITY = "119df776ef1a4fbf570645a33bf93103";

    ForgetPasswordFirstFragment forgetPasswordFirstFragment = new ForgetPasswordFirstFragment();
    ForgetPasswordSecondFragment forgetPasswordSecondFragment = new ForgetPasswordSecondFragment();
    ForgetPasswordThirdFragment forgetPasswordThirdFragment = new ForgetPasswordThirdFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        SMSSDK.initSDK(ForgetPasswordActivity.this,APPKEY,APPSECURITY);
        EventHandler eh=new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                goStep3();
                            }
                        });

                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        boolean smart = (Boolean)data;
                        if(smart) {
                            //通过智能验证
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ForgetPasswordActivity.this,"通过智能验证",Toast.LENGTH_SHORT).show();
                                }
                            });
                            goStep3();
                        } else {
                            //依然走短信验证

                        }
                    }
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ForgetPasswordActivity.this,"输入不正确",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调

        forgetPasswordFirstFragment.setOnGoNextListener(new ForgetPasswordFirstFragment.OnGoNextListener() {
            @Override
            public void onGoNext() {
                goStep2();
            }
        });
        forgetPasswordFirstFragment.setOnGoBackListener(new ForgetPasswordFirstFragment.OnGoBackListener() {
            @Override
            public void onGoBack() {
                backOut();
            }
        });
        forgetPasswordSecondFragment.setOnGoNextListener(new ForgetPasswordSecondFragment.OnGoNextListener() {
            @Override
            public void onGoNext() {
                goStep3();
            }
        });
        forgetPasswordSecondFragment.setOnGoBackListener(new ForgetPasswordSecondFragment.OnGoBackListener() {
            @Override
            public void onGoBack() {
                goBackStep1();
            }
        });
        forgetPasswordThirdFragment.setOnGoNextListener(new ForgetPasswordThirdFragment.OnGoNextListener(){
            @Override
            public void onGoNext() {
                goStep4();
            }
        });
        forgetPasswordThirdFragment.setOnGoBackListener(new ForgetPasswordThirdFragment.OnGoBackListener() {
            @Override
            public void onGoBack() {
                goBackStep2();
            }
        });
        getFragmentManager().beginTransaction().replace(R.id.container, forgetPasswordFirstFragment).commit();

    }
    void backOut(){
        finish();
        overridePendingTransition(R.anim.none,R.anim.slide_out_bottom);
    }

    void goBackStep1(){

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.slide_in_left,
                        R.animator.slide_out_right,
                        R.animator.slide_in_right,
                        R.animator.slide_out_left)
                .replace(R.id.container, forgetPasswordFirstFragment)
                .commit();
    }
    void goStep2(){

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.slide_in_right,
                        R.animator.slide_out_left,
                        R.animator.slide_in_left,
                        R.animator.slide_out_right)
                .replace(R.id.container, forgetPasswordSecondFragment)
                .addToBackStack(null)
                .commit();
    }
    void goBackStep2(){

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.slide_in_left,
                        R.animator.slide_out_right,
                        R.animator.slide_in_right,
                        R.animator.slide_out_left)
                .replace(R.id.container, forgetPasswordSecondFragment)
                .commit();
    }
    void goStep3(){

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.slide_in_right,
                        R.animator.slide_out_left,
                        R.animator.slide_in_left,
                        R.animator.slide_out_right)
                .replace(R.id.container, forgetPasswordThirdFragment)
                .addToBackStack(null)
                .commit();
    }
    void goStep4(){
        Intent itnt = new Intent(ForgetPasswordActivity.this,LoginActivity.class);
        startActivity(itnt);
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.none);
    }
}
