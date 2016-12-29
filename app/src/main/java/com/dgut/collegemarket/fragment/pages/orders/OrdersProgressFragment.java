package com.dgut.collegemarket.fragment.pages.orders;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.LoginActivity;
import com.dgut.collegemarket.activity.MainActivity;
import com.dgut.collegemarket.activity.common.ContactListActivity;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Contact;
import com.dgut.collegemarket.api.entity.Orders;
import com.dgut.collegemarket.api.entity.OrdersProgress;
import com.dgut.collegemarket.api.entity.Page;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.app.CurrentUserInfo;
import com.dgut.collegemarket.util.DateToString;
import com.dgut.collegemarket.util.MD5;
import com.dgut.collegemarket.view.layout.UnderLineLinearLayout;
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


public class OrdersProgressFragment extends Fragment implements View.OnClickListener {

    private UnderLineLinearLayout mUnderLineLinearLayout;
    public View view;
    Orders orders;
    Activity activity;
    List<OrdersProgress> mProgress = new ArrayList<>();
    Page<OrdersProgress> progressPage;
    LinearLayout ll_handle;
    Button leftBtn, rightBtn;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            activity = getActivity();
            view = inflater.inflate(R.layout.fragment_orders_progress, null);
            initView();
        }
        return view;
    }

    public void initView() {
        ll_handle = (LinearLayout) view.findViewById(R.id.ll_handle);
        mUnderLineLinearLayout = (UnderLineLinearLayout) view.findViewById(R.id.underline_layout);
        mUnderLineLinearLayout.removeAllViews();
        if (CurrentUserInfo.user_id == orders.getGoods().getPublishers().getId()) {
            ll_handle.setVisibility(View.VISIBLE);
            leftBtn = (Button) view.findViewById(R.id.btn_left);
            rightBtn = (Button) view.findViewById(R.id.btn_right);
            leftBtn.setOnClickListener(this);
            rightBtn.setOnClickListener(this);
        } else {
            ll_handle.setVisibility(View.GONE);
        }


        loadOrdersProgress();

    }

    private void loadOrdersProgress() {

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("orders_id", orders.getId() + "");

        //处理进度最多10个
        final Request request = Server.requestBuilderWithApi("orders/progress/byOrdersId/" + 0)
                .post(multipartBuilder.build())
                .build();

        Server.getSharedClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "网络连接失败，请检查网络设置", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String result = response.body().string();
                ObjectMapper mapper = new ObjectMapper();
                progressPage = mapper.readValue(result,
                        new TypeReference<Page<OrdersProgress>>() {
                        });
                final List<OrdersProgress> datas = progressPage.getContent();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mProgress.clear();
                        mProgress.addAll(datas);
                        for (int i = 0; i < mProgress.size(); i++) {
                            addProgressItem(i);
                        }
                    }

                });

            }
        });


    }

    private void addProgressItem(int i) {
        View v = LayoutInflater.from(activity).inflate(R.layout.fragment_orders_progress_item, mUnderLineLinearLayout, false);
        ((TextView) v.findViewById(R.id.tx_action)).setText(mProgress.get(i).getContent());
        ((TextView) v.findViewById(R.id.tx_action_time)).setText(DateToString.getStringDate(mProgress.get(i).getCreateDate()));
        ((TextView) v.findViewById(R.id.tx_action_status)).setText(mProgress.get(i).getTitle());
        mUnderLineLinearLayout.addView(v);
    }

    private void addProgressItem(OrdersProgress progress) {
        View v = LayoutInflater.from(activity).inflate(R.layout.fragment_orders_progress_item, mUnderLineLinearLayout, false);
        ((TextView) v.findViewById(R.id.tx_action)).setText(progress.getContent());
        ((TextView) v.findViewById(R.id.tx_action_time)).setText(DateToString.getStringDate(progress.getCreateDate()));
        ((TextView) v.findViewById(R.id.tx_action_status)).setText(progress.getTitle());
        mUnderLineLinearLayout.addView(v);

        if (rightBtn.getText().toString().equals("接单")) {
            rightBtn.setText("发货");
        }
        else if(rightBtn.getText().toString().equals("发货"))
        {
            rightBtn.setText("确认收货");
        }
        else if(rightBtn.getText().toString().equals("确认收货"))
        {
            rightBtn.setText("待评价");
        }
        else
        {
            rightBtn.setText("查看评价");
        }
    }

    public void setOrder(Orders orders) {
        this.orders = orders;

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_left:
                break;
            case R.id.btn_right:
                if (rightBtn.getText().toString().equals("接单")) {
                    changeState("已接单","请卖方尽快发货");

                }
                else if(rightBtn.getText().toString().equals("发货"))
                {
                    changeState("已发货","请保证联系方式有效");
                }
                else if(rightBtn.getText().toString().equals("确认收货"))
                {
                    changeState("交易完成","如有疑问请联系客服");
                }
                else if(rightBtn.getText().toString().equals("待评价"))
                {
                    Toast.makeText(activity,"待评价",Toast.LENGTH_SHORT).show();
                }
                else
                {
                  Toast.makeText(activity,"跳往评价页面",Toast.LENGTH_SHORT).show();
                }

                break;
        }


    }


    private void changeState(String content,String title ) {

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("content", content)
                .addFormDataPart("title", title)
                .addFormDataPart("orders_id", orders.getId() + "");

        //处理进度最多10个
        final Request request = Server.requestBuilderWithApi("orders/progress/add")
                .post(multipartBuilder.build())
                .build();

        Server.getSharedClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "网络连接失败，请检查网络设置", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String result = response.body().string();
                ObjectMapper mapper = new ObjectMapper();
                final OrdersProgress progress = mapper.readValue(result,
                        OrdersProgress.class);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        addProgressItem(progress);
                    }

                });

            }
        });


    }
}