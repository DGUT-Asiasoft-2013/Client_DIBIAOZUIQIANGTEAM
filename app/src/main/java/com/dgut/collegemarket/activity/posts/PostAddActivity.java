package com.dgut.collegemarket.activity.posts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.fragment.InputCell.PictrueHDInputCellFragment;
import com.dgut.collegemarket.fragment.InputCell.PictrueInputCellFragment;
import com.dgut.collegemarket.fragment.InputCell.SimpleTextInputCellFragment;
import com.dgut.collegemarket.util.AnimationEffec;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostAddActivity extends Activity {

    ImageView back;
    EditText edTitle,edContent,edPrice;
    ImageView btnCheckmark;
    PictrueHDInputCellFragment fragmentPictrue = new PictrueHDInputCellFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_adds);



        edTitle = (EditText) findViewById(R.id.et_title);
        edContent = (EditText) findViewById(R.id.et_content);
        edPrice = (EditText) findViewById(R.id.et_price);
        btnCheckmark = (ImageView) findViewById(R.id.btn_checkmark);
        fragmentPictrue = (PictrueHDInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_pictrue);
        back = (ImageView) findViewById(R.id.iv_add_goods_back);


        btnCheckmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }



    /**
     * 发布帖子。。
     */
    public void submit(){
        if(edContent.getText().toString().equals("")||edPrice.getText().toString().equals("")||edTitle.getText().toString().equals("")){
            Toast.makeText(PostAddActivity.this,"输入不能有空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(fragmentPictrue.getPngData()==null){
            Toast.makeText(PostAddActivity.this,"请添加一张图片",Toast.LENGTH_SHORT).show();
            return;
        }
        OkHttpClient client = Server.getSharedClient();
        MultipartBody.Builder requestBody = new MultipartBody.Builder()
                .addFormDataPart("title",edTitle.getText().toString())
                .addFormDataPart("content",edContent.getText().toString())
                .addFormDataPart("reward",edPrice.getText().toString());

        if (fragmentPictrue.getPngData() != null)
            requestBody.addFormDataPart("albums", "albumsName"
                    , RequestBody
                            .create(
                                    MediaType.parse("image/png")
                                    , fragmentPictrue.getPngData()));
        Request request = Server.requestBuilderWithApi("post/addpost")
                .post(requestBody.build())
                .build();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("发贴中");
        progressDialog.setCancelable(false);
        progressDialog.show();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(PostAddActivity.this).setMessage("服务器异常").show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(PostAddActivity.this).setMessage("发帖成功").show();
                        finish();
                    }
                });
            }
        });
    }
}
