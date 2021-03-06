package com.dgut.collegemarket.fragment.pages.regist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.RegisterActivity;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.fragment.InputCell.PictrueInputCellFragment;
import com.dgut.collegemarket.util.MD5;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/1/5.
 */

public class RegisterThirdFragment extends Fragment{

    View view;
    Activity activity;

    String phone = "";

    Button btnNext;
    ImageView ivback;
    EditText etPassword;
    EditText etRepatedPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_regist_third,null);
            activity = getActivity();
            SharedPreferences sharedPreferences = activity.getSharedPreferences("regist", Context.MODE_PRIVATE);
            phone = sharedPreferences.getString("phone","");
            init();
        }
        return view;
    }

    void init(){
        btnNext = (Button) view.findViewById(R.id.btn_next);
        ivback = (ImageView) view.findViewById(R.id.iv_back);
        etPassword = (EditText) view.findViewById(R.id.et_mobile);
        etRepatedPassword = (EditText) view.findViewById(R.id.et_repated_password);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regist();
            }
        });
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
    }

    public void regist(){
        final String password = etPassword.getText().toString();
        if(!password.equals(etRepatedPassword.getText().toString())){
            Toast.makeText(activity,"密码不一致",Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = Server.getSharedClient();
        MultipartBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("account",phone)
                .addFormDataPart("passwordHash", MD5.getMD5(password))
                .build();
        final Request request = Server.requestBuilderWithApi("user/register")
                .post(requestBody)
                .build();

        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("注册中");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(activity,"联网失败，请检查网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!request.equals("")) {
                            try {
                                User user = new ObjectMapper().readValue(result,User.class);

/**=================     调用SDK注册接口    =================*/
                            JMessageClient.register(phone, MD5.getMD5(password), new BasicCallback() {
                                @Override
                                public void gotResult(int responseCode, String registerDesc) {
                                    if (responseCode == 0) {
                                        progressDialog.dismiss();
                                        try {
                                            User user = new ObjectMapper().readValue(result,User.class);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        goNext();
                                        Log.i("RegisterActivity", "JMessageClient.register " + ", responseCode = " + responseCode + " ; registerDesc = " + registerDesc);

                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(activity, "注册失败", Toast.LENGTH_SHORT).show();
                                        Log.i("RegisterActivity", "JMessageClient.register " + ", responseCode = " + responseCode + " ; registerDesc = " + registerDesc);
                                    }
                                }
                            });
                            } catch (IOException e) {
                                progressDialog.dismiss();
                                Toast.makeText(activity, "注册失败", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                        else {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(activity)
                                    .setTitle("注册失败")
                                    .setMessage("用户名已存在")
                                    .setCancelable(true)
                                    .setNegativeButton("重新注册", null)
                                    .show();
                        }
                    }
                });

            }

        });
    }

    public static interface OnGoNextListener{
        void onGoNext();
    }

    OnGoNextListener onGoNextListener;

    public void setOnGoNextListener(OnGoNextListener onGoNextListener) {
        this.onGoNextListener = onGoNextListener;
    }

    void goNext(){
        if(onGoNextListener!=null){
            onGoNextListener.onGoNext();
        }
    }

    public static interface OnGoBackListener{
        void onGoBack();
    }
    OnGoBackListener onGoBackListener;
    public void setOnGoBackListener(OnGoBackListener onGoBackListener){
        this.onGoBackListener = onGoBackListener;
    }
    public void goBack(){
        if(onGoBackListener!=null){
            onGoBackListener.onGoBack();
        }
    }

}
