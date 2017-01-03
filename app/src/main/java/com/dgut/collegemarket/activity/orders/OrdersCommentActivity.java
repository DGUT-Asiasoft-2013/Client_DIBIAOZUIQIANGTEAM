package com.dgut.collegemarket.activity.orders;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Orders;
import com.dgut.collegemarket.fragment.widgets.AvatarView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OrdersCommentActivity extends AppCompatActivity {

    Orders orders;

    ImageView back;
    EditText etComment;
    Button btnSubmit;
    AvatarView avFace;
    TextView tvName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_comment);

        orders = (Orders) getIntent().getSerializableExtra("orders");

        back = (ImageView) findViewById(R.id.iv_back);
        etComment = (EditText) findViewById(R.id.et_comment);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        avFace = (AvatarView) findViewById(R.id.av_user_face);
        tvName = (TextView) findViewById(R.id.tv_name);

        avFace.load(orders.getGoods().getPublishers().getAvatar());
        tvName.setText(orders.getGoods().getPublishers().getName());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.none,R.anim.slide_out_left);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = etComment.getText().toString();
                if(comment.equals("")){
                    Toast.makeText(OrdersCommentActivity.this,"评价内容不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                OkHttpClient client = Server.getSharedClient();
                MultipartBody requestbody = new MultipartBody.Builder()
                        .addFormDataPart("orders_id",""+orders.getId())
                        .addFormDataPart("comments",comment)
                        .build();
                Request request = Server.requestBuilderWithApi("orderscomment/addcomment")
                        .post(requestbody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(OrdersCommentActivity.this,"联网失败，请检查网络",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Log.e("debug",result);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(OrdersCommentActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
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
