package com.dgut.collegemarket.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.orders.OrderCommentListActivity;
import com.dgut.collegemarket.activity.orders.OrdersCommentActivity;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Orders;
import com.dgut.collegemarket.app.CurrentUserInfo;
import com.dgut.collegemarket.util.OrdersStateToText;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 泽恩 on 2016/12/21.
 */

public class OrdersListAdapter extends BaseAdapter {

    Context context;
    List<Orders> mOrders;

    ImageView avatarImg;
    TextView nameText, titleText, quantityText, sumText, stateText;
    Button leftBtn, rightBtn;

    public OrdersListAdapter(Context context, List<Orders> mOrders) {
        this.context = context;
        this.mOrders = mOrders;
    }

    @Override
    public int getCount() {
        return mOrders == null ? 0 : mOrders.size();
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
        final Orders orders = mOrders.get(i);
        View view = null;
        if (orders.getBuyer().getId() == CurrentUserInfo.user_id) //判断订单是不是当前用户购买的
        {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                view = inflater.inflate(R.layout.list_orders_buy_item, null);
            } else {
                view = convertView;
            }


            avatarImg = (ImageView) view.findViewById(R.id.img_avatar);
            nameText = (TextView) view.findViewById(R.id.tv_name);
            titleText = (TextView) view.findViewById(R.id.tv_title);
            quantityText = (TextView) view.findViewById(R.id.tv_quantity);
            sumText = (TextView) view.findViewById(R.id.tv_sum);
            stateText = (TextView) view.findViewById(R.id.tv_orders_state);
            leftBtn = (Button) view.findViewById(R.id.btn_orders_button_1);
            rightBtn = (Button) view.findViewById(R.id.btn_orders_button_2);

            String avatarUrl = Server.serverAddress + orders.getGoods().getPublishers().getAvatar();
            Picasso.with(context).load(avatarUrl).resize(30, 30).centerInside().into(avatarImg);
            nameText.setText(orders.getGoods().getPublishers().getName());
            titleText.setText(orders.getGoods().getTitle());
            quantityText.setText("X" + orders.getQuantity());
            sumText.setText(orders.getQuantity() * orders.getPrice() + "元");

            stateText.setText(OrdersStateToText.getTextForBuyer(orders.getState()));

            leftBtn.setText("私信");
            leftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            rightBtn.setVisibility(View.GONE);
            if (orders.getState() == 5) {
                rightBtn.setVisibility(View.VISIBLE);
                rightBtn.setText("查看评价");
                rightBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent itnt = new Intent(context,OrderCommentListActivity.class);
                        itnt.putExtra("goodsId",orders.getGoods().getId());
                        context.startActivity(itnt);
                    }
                });
            }
            if (orders.getState() == 4) {
                rightBtn.setVisibility(View.VISIBLE);
                rightBtn.setText("去评价");
                rightBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent itnt = new Intent(context,OrdersCommentActivity.class);
                        itnt.putExtra("orders",orders);
                        context.startActivity(itnt);

                    }
                });
            }

        } else//不是购买的，那就是收到的订单
        {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                view = inflater.inflate(R.layout.list_orders_buy_item, null);
            } else {
                view = convertView;
            }
            avatarImg = (ImageView) view.findViewById(R.id.img_avatar);
            nameText = (TextView) view.findViewById(R.id.tv_name);
            titleText = (TextView) view.findViewById(R.id.tv_title);
            quantityText = (TextView) view.findViewById(R.id.tv_quantity);
            sumText = (TextView) view.findViewById(R.id.tv_sum);
            stateText = (TextView) view.findViewById(R.id.tv_orders_state);
            leftBtn = (Button) view.findViewById(R.id.btn_orders_button_1);
            rightBtn = (Button) view.findViewById(R.id.btn_orders_button_2);

            String avatarUrl = Server.serverAddress + orders.getBuyer().getAvatar();
            Picasso.with(context).load(avatarUrl).resize(30, 30).centerInside().into(avatarImg);

            nameText.setText(orders.getBuyer().getName());
            titleText.setText(orders.getGoods().getTitle());
            quantityText.setText("X" + orders.getQuantity());
            sumText.setText(orders.getQuantity() * orders.getPrice() + "元");

            stateText.setText(OrdersStateToText.getTextForPublisers(orders.getState()));

            leftBtn.setText("私信");
            leftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            rightBtn.setVisibility(View.GONE);
            if (orders.getState() == 5) {
                rightBtn.setVisibility(View.VISIBLE);
                rightBtn.setText("查看评价");
                rightBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent itnt = new Intent(context,OrderCommentListActivity.class);
                        itnt.putExtra("goodsId",orders.getGoods().getId());
                        context.startActivity(itnt);
                    }
                });
            }

        }
        return view;
    }


}
