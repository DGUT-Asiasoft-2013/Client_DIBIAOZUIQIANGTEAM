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
import com.dgut.collegemarket.api.entity.OrdersComment;
import com.dgut.collegemarket.api.entity.PostComment;
import com.dgut.collegemarket.app.CurrentUserInfo;
import com.dgut.collegemarket.fragment.widgets.AvatarView;
import com.dgut.collegemarket.util.DateToString;

import java.util.List;

/**
 * Created by 景荣 on 2016/12/23.
 */

public class OrdersCommentAdapter extends BaseAdapter{

    Context context;
    List<OrdersComment> ordersCommentsList;

    public OrdersCommentAdapter(Context context, List<OrdersComment> ordersCommentsList){
        this.context = context;
        this.ordersCommentsList = ordersCommentsList;

    }
    @Override
    public int getCount() {
        return ordersCommentsList == null? 0 : ordersCommentsList.size();
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
            views = inflater.inflate(R.layout.list_orders_comment_item,null);
        }else{
            views = currentView;
        }

        AvatarView avatarView = (AvatarView) views.findViewById(R.id.av_comment_person_face);
        TextView tvName = (TextView) views.findViewById(R.id.tv_post_comment_person_name);
        TextView tvTime = (TextView) views.findViewById(R.id.tv_post_comment_time);
        TextView tvContent = (TextView) views.findViewById(R.id.tv_post_comment_content);


        OrdersComment ordersComments = ordersCommentsList.get(i);
        avatarView.load(ordersComments.getOrders().getBuyer().getAvatar());
        tvName.setText(ordersComments.getOrders().getBuyer().getName());
        tvTime.setText(DateToString.getStringDate(ordersComments.getCreateDate()));
        tvContent.setText(ordersComments.getComments());

        return views;
    }
}
