package com.dgut.collegemarket.activity.posts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.adapter.PostCommentAdapter;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Page;
import com.dgut.collegemarket.api.entity.Post;
import com.dgut.collegemarket.api.entity.PostComment;
import com.dgut.collegemarket.view.layout.VRefreshLayout;
import com.dgut.collegemarket.view.widgets.DefaultHeaderView;
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

public class CommentActivity extends Activity {

    ImageView ivBack;
    ListView lvComment;
    List<PostComment> postCommentList = new ArrayList<PostComment>();

    PostCommentAdapter postCommentAdapter;
    Post post;
    int pageNum = 0;
    int totalPageNum = 0;

    View footView;

    TextView tvLoadMore;

    VRefreshLayout vRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        footView = LayoutInflater.from(CommentActivity.this).inflate(R.layout.list_post_comment_foot,null);

        tvLoadMore = (TextView) footView.findViewById(R.id.loadmore);
        vRefreshLayout = (VRefreshLayout) findViewById(R.id.refresh_post_comment_layout);
        post = (Post) getIntent().getSerializableExtra("post");

        ivBack = (ImageView) findViewById(R.id.iv_back);
        lvComment = (ListView) findViewById(R.id.lv_article_comments_list);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pageNum<totalPageNum){
                    pageNum = pageNum+1;
                    loadComments(pageNum);
                }
            }
        });

        if (vRefreshLayout != null) {
            vRefreshLayout.setBackgroundColor(Color.DKGRAY);
            vRefreshLayout.setAutoRefreshDuration(400);
            vRefreshLayout.setRatioOfHeaderHeightToReach(1.5f);
            vRefreshLayout.addOnRefreshListener(new VRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    postCommentList.clear();
                    pageNum=0;
                    loadComments(pageNum);
                }
            });
        }

        vRefreshLayout.setHeaderView(new DefaultHeaderView(CommentActivity.this));
        vRefreshLayout.setBackgroundColor(Color.WHITE);

        postCommentAdapter = new PostCommentAdapter(CommentActivity.this,postCommentList,CommentActivity.this);
        lvComment.addFooterView(footView);
        lvComment.setAdapter(postCommentAdapter);
        loadComments(pageNum);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void loadComments(int pageNums){
        OkHttpClient client = Server.getSharedClient();
        int id = post.getId();
        MultipartBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("postId",post.getId()+"")
                .build();
        Request request = Server.requestBuilderWithApi("post/postcomment/"+pageNums)
                .post(requestBody)
                .build();
        final ProgressDialog dialog = new ProgressDialog(CommentActivity.this);
        dialog.setMessage("拼命加载评论中");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vRefreshLayout.refreshComplete();
                        Toast.makeText(CommentActivity.this,"联网失败，请检查网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                dialog.dismiss();
                String result = response.body().string();

                Page<PostComment> page = new ObjectMapper().readValue(result, new TypeReference<Page<PostComment>>(){});
                postCommentList.addAll(page.getContent());
                pageNum = page.getNumber();
                totalPageNum = page.getTotalPages();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vRefreshLayout.refreshComplete();
                        if(pageNum == totalPageNum){
                            tvLoadMore.setText("没有更多了");
                        }
                        postCommentAdapter.notifyDataSetInvalidated();
                    }
                });
            }
        });
    }

    /**
     * 采纳评论
     */
    public void Accept(int accepterId) {
        OkHttpClient client = Server.getSharedClient();
        MultipartBody requestBody  = new MultipartBody.Builder()
                .addFormDataPart("accepterId",accepterId+"")
                .addFormDataPart("postId",post.getId()+"")
                .build();
        Request request = Server.requestBuilderWithApi("post/postcomment/accept")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CommentActivity.this,"联网失败，请检查网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CommentActivity.this,"采纳成功",Toast.LENGTH_SHORT).show();
                        postCommentList.clear();
                        loadComments(0);
                    }
                });
            }
        });
    }
}
