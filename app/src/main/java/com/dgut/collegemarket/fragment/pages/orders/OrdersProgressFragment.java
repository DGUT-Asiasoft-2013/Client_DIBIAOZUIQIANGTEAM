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
import android.widget.Switch;
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
        leftBtn = (Button) view.findViewById(R.id.btn_left);
        rightBtn = (Button) view.findViewById(R.id.btn_right);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        if (CurrentUserInfo.user_id == orders.getGoods().getPublishers().getId()) {
            leftBtn.setText("私信");
           switch (orders.getState())
           {
               case 1:
                   rightBtn.setText("接单");
                   break;
               case 2:
                   rightBtn.setText("发货");
                   break;
               case 3:
                   rightBtn.setText("待确认");
                   break;
               case 4:
                   rightBtn.setText("待评价");
                   break;
               case 5:
                   rightBtn.setText("查看评价");
                   break;
               case 6:
                   rightBtn.setText("同意退款");
                   leftBtn.setText("拒绝退款");
                   break;
               case 7:
                   rightBtn.setText("已退款");
                   break;
               case 8:
                   rightBtn.setText("已拒绝");
                   break;
           }
        } else {
            leftBtn.setText("取消订单");
            switch (orders.getState())
            {
                case 1:
                    rightBtn.setText("等待接单");
                    break;
                case 2:
                    rightBtn.setText("已接单");
                    break;
                case 3:
                    rightBtn.setText("确认收货");
                    break;
                case 4:
                    leftBtn.setText("私信");
                    rightBtn.setText("去评价");
                    break;
                case 5:
                    leftBtn.setText("私信");
                    rightBtn.setText("查看评价");
                    break;
                case 6:
                    leftBtn.setText("私信");
                    rightBtn.setText("等待退款");
                    break;
                case 7:
                    leftBtn.setText("私信");
                    rightBtn.setText("退款成功");
                    break;
                case 8:
                    leftBtn.setText("私信");
                    rightBtn.setText("联系客服");
                    break;
            }

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
            rightBtn.setEnabled(true);
        } else if (rightBtn.getText().toString().equals("发货")) {
            rightBtn.setEnabled(true);
            rightBtn.setText("待确认");
        }
        else if (rightBtn.getText().toString().equals("确认收货")) {
            rightBtn.setEnabled(true);
            rightBtn.setText("去评价");
            leftBtn.setText("私信");
        }
        else if (rightBtn.getText().toString().equals("同意退款")) {
            leftBtn.setText("私信");
            rightBtn.setEnabled(true);
            rightBtn.setText("已退款");
        }
        else if (leftBtn.getText().toString().equals("拒绝退款")) {
            leftBtn.setText("私信");
            leftBtn.setEnabled(true);
            rightBtn.setText("已拒绝");
        }
        else if (leftBtn.getText().toString().equals("取消订单")) {
            leftBtn.setText("私信");
            leftBtn.setEnabled(true);
            rightBtn.setText("等待退款");
        }
    }

    public void setOrder(Orders orders) {
        this.orders = orders;

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_left:
                if (leftBtn.getText().toString().equals("取消订单")) {
                    changeState("申请取消", "等待卖方答复", 6 + "");
                    leftBtn.setEnabled(false);

                }
                else if (leftBtn.getText().toString().equals("私信")) {
                    Toast.makeText(activity, "私信", Toast.LENGTH_SHORT).show();
                }
                else if (leftBtn.getText().toString().equals("拒绝退款")) {
                    leftBtn.setEnabled(false);
                    changeState("拒绝退款", "如有疑问请联系客服", 8 + "");
                }
                break;
            case R.id.btn_right:
                if (rightBtn.getText().toString().equals("接单")) {
                    changeState("已接单", "请卖方尽快发货", 2 + "");
                    rightBtn.setEnabled(false);
                } else if (rightBtn.getText().toString().equals("发货")) {
                    rightBtn.setEnabled(false);
                    changeState("已发货", "请保证联系方式有效", 3 + "");
                }
                else if (rightBtn.getText().toString().equals("确认收货")) {
                    rightBtn.setEnabled(false);
                    changeState("交易完成", "如有疑问请联系客服", 4 + "");
                }
                else if (rightBtn.getText().toString().equals("查看评价")) {
                    rightBtn.setEnabled(false);
                    Toast.makeText(activity, "查看评价", Toast.LENGTH_SHORT).show();
                }
                else if (rightBtn.getText().toString().equals("同意退款")) {
                    rightBtn.setEnabled(false);
                    changeState("同意退款", "金币已退回买方", 7 + "");
                }



                break;
        }


    }


    private void changeState(String content, String title, String state) {

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("content", content)
                .addFormDataPart("title", title)
                .addFormDataPart("orders_id", orders.getId() + "")
                .addFormDataPart("state", state);

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