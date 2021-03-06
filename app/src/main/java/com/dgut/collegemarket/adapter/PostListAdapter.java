package com.dgut.collegemarket.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Goods;
import com.dgut.collegemarket.api.entity.Post;
import com.dgut.collegemarket.util.DateToString;
import com.dgut.collegemarket.util.JudgeLevel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator on 2016/12/21.
 */

public class PostListAdapter extends BaseAdapter {

    Context context;
    List<Post> mPost;

    public PostListAdapter(Context context, List<Post> mPost) {
        this.context = context;
        this.mPost = mPost;
    }

    @Override
    public int getCount() {
        return mPost == null ? 0 : mPost.size();
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        View view=null;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.list_post_item, null);

        } else {
            view = convertView;
        }
        ImageView imageAvatar = (ImageView) view.findViewById(R.id.image_avatar);
        TextView tvLevel = (TextView) view.findViewById(R.id.level);
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        TextView textTitle = (TextView) view.findViewById(R.id.text_post_title);
        ImageView ivContentImg = (ImageView) view.findViewById(R.id.iv_content_img);
        TextView textContent = (TextView) view.findViewById(R.id.text_post_content);
        TextView tvCreateTime = (TextView) view.findViewById(R.id.tv_createtime);
        TextView tvPrice = (TextView) view.findViewById(R.id.tv_price);
        ImageView imageAccepted  = (ImageView) view.findViewById(R.id.iv_accepted);

        Post post = mPost.get(i);

        tvLevel.setText("Lv"+JudgeLevel.judege(post.getPublishers().getXp()));
        tvName.setText(post.getPublishers().getName());
        textTitle.setText(post.getTitle());
        textContent.setText(post.getContent());
        tvCreateTime.setText(DateToString.getStringDateMMDD(post.getCreateDate()));
        tvPrice.setText("悬赏："+post.getReward()+"");
        String avatarUrl = Server.serverAddress + post.getPublishers().getAvatar();
        String albumsUrl = Server.serverAddress + post.getAlbums();
        Picasso.with(context).load(avatarUrl).fit().error(R.drawable.unknow_avatar) .into(imageAvatar)   ;
        Picasso.with(context).load(albumsUrl).resize(300,200).centerCrop().into(ivContentImg);
        if(post.issolve()){
            imageAccepted.setImageResource(R.drawable.accepted);
        }else{
            imageAccepted.setImageResource(R.drawable.unaccepted);
        }


        return view;
    }

}
