package com.dgut.collegemarket.activity.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Message;
import com.dgut.collegemarket.fragment.InputCell.PictrueHDInputCellFragment;
import com.dgut.collegemarket.fragment.InputCell.SimpleTextInputCellFragment;
import com.dgut.collegemarket.util.AnimationEffec;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


//私信界面—发送私信
public class SendMessageActivity extends Activity {

    ImageView sendButton;
    SimpleTextInputCellFragment fragmentTitle = new SimpleTextInputCellFragment();
    SimpleTextInputCellFragment fragmentContent = new SimpleTextInputCellFragment();
    PictrueHDInputCellFragment fragmentPictrue = new PictrueHDInputCellFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        fragmentTitle = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.message_name);
        fragmentContent = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.message_text);
        fragmentPictrue = (PictrueHDInputCellFragment) getFragmentManager().findFragmentById(R.id.message_picture);
        sendButton = (ImageView) findViewById(R.id.truck);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fragmentTitle.getText().equals("")) {
                    fragmentTitle.setHintText("用户不能为空");
                } else if (fragmentContent.getText().equals("")) {
                    fragmentContent.setHintText("内容不能为空");
                } else {
                    AnimationEffec.setTransAniToRight(sendButton, 0, 700, 0, 0, 1500);
                    sendMessage();
                }
            }
        });

    }

    ProgressDialog progressDialog;

    void sendMessage() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("发送中");
        progressDialog.setCancelable(false);
        progressDialog.show();

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("receiver", fragmentTitle.getText())
                .addFormDataPart("content", fragmentContent.getText());

        if (fragmentPictrue.getPngData() != null)
            multipartBuilder.addFormDataPart("picture", "pictureName"
                    , RequestBody
                            .create(
                                    MediaType.parse("image/png")
                                    , fragmentPictrue.getPngData()));

        final Request request = Server.requestBuilderWithApi("message/send")
                .post(multipartBuilder.build())
                .build();


        Server.getSharedClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(SendMessageActivity.this).setMessage("服务器异常").show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressDialog.dismiss();
                final String result = response.body().string();
                ObjectMapper mapper = new ObjectMapper();
                final Message message = mapper.readValue(result, Message.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(SendMessageActivity.this)
                                .setMessage("成功发送给" + message.getReceiver())
                                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                        overridePendingTransition(R.anim.none, R.anim.slide_out_bottom);
                                    }
                                }).show();
                    }
                });
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.none, R.anim.slide_out_bottom);


    }

    @Override
    protected void onResume() {
        super.onResume();

        fragmentTitle.setHintText("发给");
        fragmentContent.setHintText("内容");
        fragmentContent.setLinesAndLength(10, 400);
        fragmentPictrue.setHintText("添加照片");
    }


}
