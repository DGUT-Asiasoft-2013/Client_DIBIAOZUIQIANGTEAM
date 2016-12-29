package com.dgut.collegemarket.fragment.pages;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.orders.OrdersContentActivity;
import com.dgut.collegemarket.adapter.GoodsListAdapter;
import com.dgut.collegemarket.adapter.OrdersListAdapter;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Goods;
import com.dgut.collegemarket.api.entity.Orders;
import com.dgut.collegemarket.api.entity.Page;
import com.dgut.collegemarket.util.MD5;
import com.dgut.collegemarket.view.layout.VRefreshLayout;
import com.dgut.collegemarket.view.widgets.JDHeaderView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;


public class OrderListFragment extends Fragment {
    View view;
    Activity activity;
     VRefreshLayout mRefreshLayout;
    int page = 0;
    int pageSize = 10;
    int NOT_MORE_PAGE = -1;
    View btnLoadMore;
    TextView textLoadMore;
    private ListView mListView;
    OrdersListAdapter adpter;
    private List<Orders> mOrders = new ArrayList<Orders>();
    Page<Orders> ordersPage;
    boolean firstBuilt=true;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_page_order_list, null);
            activity= getActivity();
            adpter = new OrdersListAdapter(activity, mOrders);
            mListView = (ListView) view.findViewById(R.id.listView);
            btnLoadMore = inflater.inflate(R.layout.list_foot, null);
            textLoadMore = (TextView) btnLoadMore.findViewById(R.id.loadmore);
            mListView.addFooterView(btnLoadMore);
            mListView.setAdapter(adpter);
            btnLoadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (page!=NOT_MORE_PAGE) {
                        refreshOrdersList();
                    }
                }
            });
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent=new Intent(activity, OrdersContentActivity.class);
                    intent.putExtra("orders",mOrders.get(i));
                    startActivity(intent);
                }
            });
            initHeaderView();
        }
        return view;
    }



    private void initHeaderView() {
        mRefreshLayout = (VRefreshLayout) view.findViewById(R.id.refresh_layout);
        if (mRefreshLayout != null) {
            mRefreshLayout.setBackgroundColor(Color.DKGRAY);
            mRefreshLayout.setAutoRefreshDuration(400);
            mRefreshLayout.setRatioOfHeaderHeightToReach(1.5f);
            mRefreshLayout.addOnRefreshListener(new VRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    page=0;
                    refreshOrdersList();
                }
            });
        }

        mRefreshLayout.setHeaderView(mRefreshLayout.getDefaultHeaderView());
        mRefreshLayout.setBackgroundColor(Color.parseColor("#ffc130"));
    }

    private void refreshOrdersList() {
        textLoadMore.setEnabled(false);
        textLoadMore.setText("加载中");


        final Request request = Server.requestBuilderWithApi("orders/my/all/" +  page++)
                .get()
                .build();


        Server.getSharedClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.refreshComplete();
                        textLoadMore.setEnabled(true);
                        textLoadMore.setText("网络异常");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String result = response.body().string();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mRefreshLayout.refreshComplete();

                        textLoadMore.setEnabled(true);
                        textLoadMore.setText("数据解析中");
                        try {

                            ObjectMapper mapper = new ObjectMapper();
                            ordersPage = mapper.readValue(result,
                                    new TypeReference<Page<Orders>>() {
                                    });
                            List<Orders> datas = ordersPage.getContent();


                            if (datas.size()<=pageSize&&page==1)
                            {   textLoadMore.setText("加载更多");
                                datas = ordersPage.getContent();
                                mOrders.clear();
                                mOrders.addAll(datas);
                                adpter.notifyDataSetInvalidated();
                            }
                            else if(ordersPage.getTotalPages()!=page){
                                textLoadMore.setText("加载更多");
                                mOrders.addAll(ordersPage.getContent());
                                adpter.notifyDataSetChanged();
                            }
                            else {
                                page=NOT_MORE_PAGE;
                                textLoadMore.setText("没有新内容");
                                mOrders.addAll(ordersPage.getContent());
                                adpter.notifyDataSetChanged();
                            }

                        } catch (IOException e) {
                            textLoadMore.setText("数据解析失败"+e.getLocalizedMessage());
                        }
                    }
                });

            }
        });


    }
    private Handler scaleHandler = new Handler();
    private Runnable scaleRunnable = new Runnable() {

        @Override
        public void run() {
            if (firstBuilt) {
                mRefreshLayout.autoRefresh();
                firstBuilt=false;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        activity.getWindow().getDecorView().post(new Runnable() {

            @Override
            public void run() {
                scaleHandler.post(scaleRunnable);
            }
        });
    }
}
