package com.dgut.collegemarket.activity.posts;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Post;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PostContentActivity extends Activity {

    Post post;

    EditText etContent;
    Button btnComment;
    ImageView ivBack;
    ImageView ivContentImg;
    TextView tvTitle;
    TextView tvText;
    TextView tvLookComment;
    TextView tvLike;
    TextView tvLikeCount;
    boolean isLike = false;
    int countlikes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_content);
        post = (Post) getIntent().getSerializableExtra("post");
        Log.e("debug",post.getId()+"");

        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivContentImg = (ImageView) findViewById(R.id.iv_post_content_img);
        etContent = (EditText) findViewById(R.id.et_content);
        btnComment = (Button) findViewById(R.id.btn_comment_publish);
        tvTitle = (TextView) findViewById(R.id.tv_article_title);
        tvText = (TextView) findViewById(R.id.tv_article_text);
        tvLookComment = (TextView) findViewById(R.id.tv_lookcomments);
        tvLike = (TextView) findViewById(R.id.tv_likes);
        tvLikeCount = (TextView) findViewById(R.id.tv_likes_count);

        String albumsUrl = Server.serverAddress + post.getAlbums();
        Picasso.with(PostContentActivity.this).load(albumsUrl).resize(500,300).centerInside().into(ivContentImg);

        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnComment.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                postComment();
            }
        });

        tvLookComment.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                lookComments();
            }
        });

        tvLike.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(isLike){
                    deletelikes();
                }else{
                    likes();
                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        tvTitle.setText(post.getTitle());
        tvText.setText(post.getContent());
        checkLikes();
        countLikes();
    }

    /**
     * 发表评论
     */
    public void postComment() {
        if(etContent.getText().toString().equals("")){
            Toast.makeText(PostContentActivity.this,"评论不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        OkHttpClient client = Server.getSharedClient();

        MultipartBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("content", etContent.getText().toString())
                .build();

        Request request = Server.requestBuilderWithApi("post/"+post.getId()+ "/publish/postcomment")
                .post(requestBody)
                .build();

        final ProgressDialog dlg = new ProgressDialog(this);
        dlg.setCancelable(false);
        dlg.setCanceledOnTouchOutside(false);
        dlg.setMessage("正在发表");
        dlg.show();

        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call arg0, Response arg1) throws IOException {
                dlg.dismiss();
                final String result = arg1.body().string();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(PostContentActivity.this, "发表成功", Toast.LENGTH_LONG).show();
                        etContent.setText("");
                    }
                });

            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                dlg.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(PostContentActivity.this, "发表失败", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

    /**
     * 查看评论
     */
    public void lookComments() {
        Intent itnt = new Intent(PostContentActivity.this, CommentActivity.class);
        itnt.putExtra("post",post);
        startActivity(itnt);
    }

    /**
     * 点赞
     */
    public void likes() {
        OkHttpClient client = Server.getSharedClient();

        MultipartBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("postId", post.getId()+"")
                .build();

        Request request = Server.requestBuilderWithApi("likes/addlikes" )
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call arg0, Response arg1) throws IOException {
                String rs = arg1.body().string();
                final int res = Integer.valueOf(rs);
                runOnUiThread(new Runnable() {
                    public void run() {
                        tvLike.setText("取消点赞");
                        tvLikeCount.setText(res+"");
                        tvLike.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        tvLikeCount.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        isLike = true;
                    }
                });
            }

            public void onFailure(Call arg0, IOException arg1) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(PostContentActivity.this, "点赞失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 取消点赞
     */
    public void deletelikes() {
        OkHttpClient client = Server.getSharedClient();

        MultipartBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("postId", post.getId()+"")
                .build();

        Request request = Server.requestBuilderWithApi("likes/deletelikes" )
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call arg0, Response arg1) throws IOException {
                String rs = arg1.body().string();
                final int res = Integer.valueOf(rs);
                runOnUiThread(new Runnable() {
                    public void run() {
                        tvLike.setText("点赞");
                        tvLikeCount.setText(res+"");
                        tvLike.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        tvLikeCount.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        isLike = false;
                    }
                });
            }

            public void onFailure(Call arg0, IOException arg1) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(PostContentActivity.this, "取消点赞失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    /**
     * 检查是否点赞
     */
    public void checkLikes() {
        OkHttpClient client = Server.getSharedClient();
        MultipartBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("postId",post.getId()+"")
                .build();
        Request request = Server
                .requestBuilderWithApi("likes/judgelikes")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call arg0, Response arg1) throws IOException {
                String rs = arg1.body().string();
                boolean islike = Boolean.valueOf(rs);
                if (islike) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            isLike = true;
                            tvLike.setText("取消点赞");
                            tvLike.setTextColor(getResources().getColor(android.R.color.darker_gray));
                            tvLikeCount.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        }
                    });
                } else {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            isLike = false;
                            tvLike.setText("点赞");
                            tvLike.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                            tvLikeCount.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(PostContentActivity.this, "点赞检查出错", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 统计点赞人数
     *
     * @return
     */
    public void countLikes() {
        OkHttpClient client = Server.getSharedClient();
        MultipartBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("postId",post.getId()+"")
                .build();
        Request request = Server.requestBuilderWithApi("likes/countlikes" )
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call arg0, Response arg1) throws IOException {
                String rs = arg1.body().string();
                final int res = Integer.valueOf(rs);
                runOnUiThread(new Runnable() {
                    public void run() {
                        tvLikeCount.setText(res+"");
                    }
                });
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(PostContentActivity.this, "点赞数出错", Toast.LENGTH_SHORT).show();
                        tvLike.setClickable(true);
                    }
                });
            }
        });

    }


}
