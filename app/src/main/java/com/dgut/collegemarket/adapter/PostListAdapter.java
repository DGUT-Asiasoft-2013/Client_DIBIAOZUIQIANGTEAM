package com.dgut.collegemarket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.entity.Goods;
import com.dgut.collegemarket.api.entity.Post;
import com.dgut.collegemarket.util.DateToString;

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

        View view = null;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.list_post_item, null);
        } else {
            view = convertView;
        }

        return view;
    }


}
