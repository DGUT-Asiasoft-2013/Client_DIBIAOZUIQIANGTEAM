
package com.dgut.collegemarket.fragment.pages;


import com.dgut.collegemarket.R;
import com.dgut.collegemarket.adapter.GoodsListAdapter;
import com.dgut.collegemarket.api.entity.Page;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Goods;
import com.dgut.collegemarket.util.Densityutils;
import com.dgut.collegemarket.view.layout.VRefreshLayout;
import com.dgut.collegemarket.view.widgets.JDHeaderView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class GoodsListFragment extends Fragment {
    private VRefreshLayout mRefreshLayout;
    int pageSize = 10;
    int NOT_MORE_PAGE = -1;
    Activity activity;
    View view;
    private List<ImageView> mViews;
    private List<Goods> mGoods = new ArrayList<Goods>();
    private ListView mListView;
    private View mJDHeaderView;
    GoodsListAdapter adpter;
    int page = 0;
    Page<Goods> goodsPage;

    View btnLoadMore;
    TextView textLoadMore;
   boolean firstBuilt=true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            activity = getActivity();
            view = inflater.inflate(R.layout.fragment_page_goods_list, null);
            initImageView(inflater);
            initHeaderView();

        }
        return view;
    }

    private void initImageView(LayoutInflater inflater) {
        ViewPager viewPager = new ViewPager(activity);
        viewPager.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, dp2px(200)));
        mViews = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(activity);
            imageView.setImageResource(R.drawable.android1);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mViews.add(imageView);
        }
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mViews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = mViews.get(position);
                container.addView(imageView);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mViews.get(position));
            }
        });
        adpter = new GoodsListAdapter(activity, mGoods);
        mListView = (ListView) view.findViewById(R.id.listView);
        mListView.addHeaderView(viewPager);
        btnLoadMore = inflater.inflate(R.layout.list_foot, null);
        textLoadMore = (TextView) btnLoadMore.findViewById(R.id.loadmore);
        mListView.addFooterView(btnLoadMore);
        mListView.setAdapter(adpter);
    }

    private void initHeaderView() {
        mRefreshLayout = (VRefreshLayout) view.findViewById(R.id.refresh_layout);
        if (mRefreshLayout != null) {
            mJDHeaderView = new JDHeaderView(activity);
            mJDHeaderView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(64)));
            mRefreshLayout.setBackgroundColor(Color.DKGRAY);
            mRefreshLayout.setAutoRefreshDuration(400);
            mRefreshLayout.setRatioOfHeaderHeightToReach(1.5f);
            mRefreshLayout.addOnRefreshListener(new VRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshGoodsList();
                }
            });
        }

        mRefreshLayout.setHeaderView(mJDHeaderView);
        mRefreshLayout.setBackgroundColor(Color.WHITE);


    }

    private void refreshGoodsList() {


        final Request request = Server.requestBuilderWithApi("goods/all/" + page)
                .get()
                .build();


        Server.getSharedClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.refreshComplete();
                        new AlertDialog.Builder(activity).setMessage("服务器异常").show();
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

                        try {

                            ObjectMapper mapper = new ObjectMapper();
                            goodsPage = mapper.readValue(result,
                                    new TypeReference<Page<Goods>>() {
                                    });
                            List<Goods> datas = goodsPage.getContent();
                            mGoods.addAll(datas);

                            adpter.notifyDataSetChanged();
                        } catch (IOException e) {
                            e.printStackTrace();
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

    protected int dp2px(float dp) {
        return Densityutils.dp2px(activity, dp);
    }
}