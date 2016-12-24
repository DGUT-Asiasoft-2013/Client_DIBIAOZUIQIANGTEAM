package com.dgut.collegemarket.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.adapter.PostCommentAdapter;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Page;
import com.dgut.collegemarket.api.entity.Post;
import com.dgut.collegemarket.api.entity.PostComment;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        post = (Post) getIntent().getSerializableExtra("post");

        ivBack = (ImageView) findViewById(R.id.iv_back);
        lvComment = (ListView) findViewById(R.id.lv_article_comments_list);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        postCommentAdapter = new PostCommentAdapter(CommentActivity.this,postCommentList);
        lvComment.setAdapter(postCommentAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadComments();
    }

    public void loadComments(){
        OkHttpClient client = Server.getSharedClient();
        int id = post.getId();
        MultipartBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("postId",post.getId()+"")
                .build();
        Request request = Server.requestBuilderWithApi("post/postcomment/"+pageNum)
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
                        Toast.makeText(CommentActivity.this,"",Toast.LENGTH_SHORT).show();
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postCommentAdapter.notifyDataSetInvalidated();
                    }
                });
            }
        });
    }
}
