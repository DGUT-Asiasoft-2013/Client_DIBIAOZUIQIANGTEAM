package com.dgut.collegemarket.fragment.pages.orders;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.entity.Orders;
import com.dgut.collegemarket.util.DateToString;
import com.dgut.collegemarket.view.layout.UnderLineLinearLayout;


public class OrdersProgressFragment extends Fragment {

    private UnderLineLinearLayout mUnderLineLinearLayout;
    public View view;
    Orders orders;
    Activity activity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
       activity = getActivity();
        view = inflater.inflate(R.layout.fragment_orders_progress, null);
        initView();}
        return view;
    }

    public void initView() {
        mUnderLineLinearLayout = (UnderLineLinearLayout) view.findViewById(R.id.underline_layout);
        addItem();
    }


    public void addItem() {
        mUnderLineLinearLayout.removeAllViews();
            addFistItem();
    }

    private void addFistItem() {
        View v = LayoutInflater.from(activity).inflate(R.layout.fragment_orders_progress_item, mUnderLineLinearLayout, false);
        ((TextView) v.findViewById(R.id.tx_action)).setText("订单请求已发送");
        ((TextView) v.findViewById(R.id.tx_action_time)).setText(DateToString.getStringDate(orders.getCreateDate()));
        ((TextView) v.findViewById(R.id.tx_action_status)).setText("等待商家确认");
        mUnderLineLinearLayout.addView(v);

    }

    public void setOrder(Orders orders) {
        this.orders=orders;

    }
}