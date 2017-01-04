package com.dgut.collegemarket.activity.orders;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Goods;
import com.dgut.collegemarket.api.entity.Orders;
import com.dgut.collegemarket.api.entity.Page;
import com.dgut.collegemarket.app.MsgType;
import com.dgut.collegemarket.fragment.pages.orders.OrdersDetailFrament;
import com.dgut.collegemarket.fragment.pages.orders.OrdersProgressFragment;
import com.dgut.collegemarket.util.MD5;
import com.dgut.collegemarket.view.layout.VRefreshLayout;
import com.dgut.collegemarket.view.widgets.JDHeaderView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.LocationContent;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.NotificationClickEvent;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import im.sdk.debug.activity.createmessage.ShowDownloadVoiceInfoActivity;
import im.sdk.debug.activity.createmessage.ShowMessageActivity;
import im.sdk.debug.activity.imagecontent.ShowDownloadPathActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class OrdersContentActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private final List fragmentList = new ArrayList<>();
    private RelativeLayout ly_page1;
    private TextView tv_page1;
    private RelativeLayout ly_page2;
    private TextView tv_page2;
    private ViewPager vp_scroll;
    public Orders orders;
    public int orders_id;
    CommonFragementPagerAdapter commonFragementPagerAdapter;
    OrdersDetailFrament ordersDetailFrament = new OrdersDetailFrament();
    OrdersProgressFragment ordersProgressFragment = new OrdersProgressFragment();
    VRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orders_id = getIntent().getIntExtra("orders_id", 0);
        setContentView(R.layout.activity_orders_content);

        ly_page1 = (RelativeLayout) findViewById(R.id.ly_page1);
        tv_page1 = (TextView) findViewById(R.id.tv_page1);
        ly_page2 = (RelativeLayout) findViewById(R.id.ly_page2);
        tv_page2 = (TextView) findViewById(R.id.tv_page2);
        vp_scroll = (ViewPager) findViewById(R.id.vp_scroll);

        ly_page1.setOnClickListener(this);
        ly_page2.setOnClickListener(this);
        initHeaderView();
        fragmentList.add(ordersProgressFragment);
        fragmentList.add(ordersDetailFrament);
        commonFragementPagerAdapter = new CommonFragementPagerAdapter(getFragmentManager());
        vp_scroll.setAdapter(commonFragementPagerAdapter);
        vp_scroll.addOnPageChangeListener(OrdersContentActivity.this);
        JMessageClient.registerEventReceiver(this,5);
    }

    private void initHeaderView() {
        mRefreshLayout = (VRefreshLayout) findViewById(R.id.refresh_layout);
        if (mRefreshLayout != null) {

            mRefreshLayout.setBackgroundColor(Color.DKGRAY);
            mRefreshLayout.setAutoRefreshDuration(400);
            mRefreshLayout.setRatioOfHeaderHeightToReach(1.5f);
            mRefreshLayout.addOnRefreshListener(new VRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshOrders();
                }
            });
        }

        mRefreshLayout.setHeaderView(mRefreshLayout.getDefaultHeaderView());
        mRefreshLayout.setBackgroundColor(Color.WHITE);

    }

    public void initFragementPagerView() {
        ordersDetailFrament.setOrder(orders);
        ordersProgressFragment.setOrder(orders);


        commonFragementPagerAdapter.notifyDataSetChanged();

    }

    private void refreshOrders() {
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("orders_id", String.valueOf(orders_id));

        final Request request = Server.requestBuilderWithApi("orders/getOrders")
                .post(multipartBuilder.build())
                .build();


        Server.getSharedClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.refreshComplete();
                        Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String result = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mRefreshLayout.refreshComplete();

                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            orders = mapper.readValue(result,
                                    Orders.class);
                            initFragementPagerView();


                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), "数据解析失败", Toast.LENGTH_SHORT).show();
                            System
                                    .out.println(result + "result");
                        }
                    }
                });

            }
        });


    }

    public void onEvent(MessageEvent event) {
        System.out.println("MessageEvent OrdersContentActivity");
        final Message msg = event.getMessage();
        switch (msg.getContentType()) {
            case text:
                //处理文字消息
                TextContent textContent = (TextContent) msg.getContent();
                Map stringExtras = textContent.getStringExtras();
                System.out.println("msg_type:" + stringExtras.get("msg_type"));
                if (stringExtras.get("msg_type").equals(MsgType.MSG_ORDERS)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRefreshLayout.autoRefresh();
                        }
                    });
                }
                break;
            default:
                break;
        }
    }
    public void onEvent(NotificationClickEvent event) {
       finish();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ly_page1:
                vp_scroll.setCurrentItem(0);
                break;
            case R.id.ly_page2:
                vp_scroll.setCurrentItem(1);
                break;
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 0) {
            ly_page1.setBackgroundResource(R.drawable.rectangle_left_select);
            tv_page1.setTextColor(Color.parseColor("#ffffff"));
            ly_page2.setBackgroundResource(R.drawable.rectangle_right);
            tv_page2.setTextColor(Color.parseColor("#3c9aff"));

        } else {
            ly_page1.setBackgroundResource(R.drawable.rectangle_left);
            tv_page1.setTextColor(Color.parseColor("#3c9aff"));
            ly_page2.setBackgroundResource(R.drawable.rectangle_right_select);
            tv_page2.setTextColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class CommonFragementPagerAdapter extends FragmentPagerAdapter {
        public CommonFragementPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return getCount() > position ? (Fragment) fragmentList.get(position) : null;
        }

        @Override
        public int getCount() {
            return fragmentList == null ? 0 : fragmentList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return super.POSITION_NONE;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private Handler scaleHandler = new Handler();
    private Runnable scaleRunnable = new Runnable() {

        @Override
        public void run() {
            mRefreshLayout.autoRefresh();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getWindow().getDecorView().post(new Runnable() {

            @Override
            public void run() {
                scaleHandler.post(scaleRunnable);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
    }


}
