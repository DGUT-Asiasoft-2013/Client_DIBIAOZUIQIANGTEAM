package com.dgut.collegemarket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.posts.CommentActivity;
import com.dgut.collegemarket.api.entity.PostComment;
import com.dgut.collegemarket.fragment.widgets.AvatarView;
import com.dgut.collegemarket.util.DateToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/23.
 */

public class PostCommentAdapter extends BaseAdapter{

    Context context;
    List<PostComment> postComments;
    CommentActivity commentActivity;

    public  PostCommentAdapter(Context context, List<PostComment> postComments,CommentActivity commentActivity){
        this.context = context;
        this.postComments = postComments;
        this.commentActivity = commentActivity;
    }
    @Override
    public int getCount() {
        return postComments == null? 0 : postComments.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View currentView, ViewGroup viewGroup) {
        View views = null;
        if(currentView == null){
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            views = inflater.inflate(R.layout.list_post_comment_item,null);
        }else{
            views = currentView;
        }

        AvatarView avatarView = (AvatarView) views.findViewById(R.id.av_comment_person_face);
        TextView tvName = (TextView) views.findViewById(R.id.tv_post_comment_person_name);
        TextView tvTime = (TextView) views.findViewById(R.id.tv_post_comment_time);
        TextView tvContent = (TextView) views.findViewById(R.id.tv_post_comment_content);
        final TextView tvAccept = (TextView) views.findViewById(R.id.tv_accept);
        ImageView ivAccept = (ImageView) views.findViewById(R.id.iv_accept);

        final PostComment postComment = postComments.get(i);
        avatarView.load(postComment.getCommentUser().getAvatar());
        tvName.setText(postComment.getCommentUser().getName());
        tvTime.setText(DateToString.getStringDate(postComment.getCreateDate()));
        tvContent.setText(postComment.getContent());
        if(postComment.getPost().issolve()){
            if(postComment.getPost().getAccepterId() == postComment.getId()){
                ivAccept.setVisibility(View.VISIBLE);
                tvAccept.setVisibility(View.GONE);
            }else {
                tvAccept.setVisibility(View.GONE);
                ivAccept.setVisibility(View.GONE);
            }
        }else{
            tvAccept.setText("采纳");
            tvAccept.setVisibility(View.VISIBLE);
        }
        tvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            commentActivity.Accept(postComment.getId());
            }
        });


        return views;
    }
}
