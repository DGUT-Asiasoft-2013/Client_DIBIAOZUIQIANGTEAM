package com.dgut.collegemarket.activity.orders;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.posts.CommentActivity;
import com.dgut.collegemarket.adapter.OrdersCommentAdapter;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Orders;
import com.dgut.collegemarket.api.entity.OrdersComment;
import com.dgut.collegemarket.api.entity.Page;
import com.dgut.collegemarket.view.layout.VRefreshLayout;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OrderCommentListActivity extends AppCompatActivity {

    ImageView ivBack;
    ListView lvOrderComment;
    OrdersCommentAdapter ordersCommentAdapter;
    List<OrdersComment> ordersCommentList = new ArrayList<OrdersComment>();

    TextView tvLoadMore;

    int pageNum = 0;
    int totalPageNum=0;
    int goodsId;

    View footView;
    VRefreshLayout vRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_comment_list);

        goodsId = getIntent().getIntExtra("goodsId",0);

        footView = LayoutInflater.from(OrderCommentListActivity.this).inflate(R.layout.list_post_comment_foot,null);
        tvLoadMore = (TextView) footView.findViewById(R.id.loadmore);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        lvOrderComment = (ListView) findViewById(R.id.lv_orders_comments_list);
        vRefreshLayout = (VRefreshLayout) findViewById(R.id.refresh_post_comment_layout);

        ordersCommentAdapter = new OrdersCommentAdapter(OrderCommentListActivity.this,ordersCommentList);
        lvOrderComment.addFooterView(footView);
        lvOrderComment.setAdapter(ordersCommentAdapter);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.none,R.anim.slide_out_left);
            }
        });
        tvLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pageNum<totalPageNum){
                    pageNum = pageNum+1;
                    getOrdersComments(pageNum);
                }
            }
        });

        if (vRefreshLayout != null) {
            vRefreshLayout.setBackgroundColor(Color.WHITE);
            vRefreshLayout.setAutoRefreshDuration(400);
            vRefreshLayout.setRatioOfHeaderHeightToReach(1.5f);
            vRefreshLayout.addOnRefreshListener(new VRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    ordersCommentList.clear();
                    pageNum=0;
                    getOrdersComments(pageNum);
                }
            });
        }
        getOrdersComments(pageNum);
    }

    void getOrdersComments(int page){
        OkHttpClient client = Server.getSharedClient();
        MultipartBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("goodsId",goodsId+"")
                .addFormDataPart("page",page+"")
                .build();
        Request request = Server.requestBuilderWithApi("orderscomment/getcomment")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vRefreshLayout.refreshComplete();
                        Toast.makeText(OrderCommentListActivity.this,"联网失败，请检查网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Page<OrdersComment> page = new ObjectMapper().readValue(result, new TypeReference<Page<OrdersComment>>() {});
                ordersCommentList.addAll(page.getContent());
                pageNum = page.getNumber();
                totalPageNum = page.getTotalPages();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vRefreshLayout.refreshComplete();
                        if(pageNum == totalPageNum){
                            tvLoadMore.setText("没有更多了");
                        }
                        ordersCommentAdapter.notifyDataSetInvalidated();
                    }
                });
            }
        });
    }
}
