
package com.dgut.collegemarket.fragment.pages;


import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.LoginActivity;
import com.dgut.collegemarket.activity.MainActivity;
import com.dgut.collegemarket.activity.goods.GoodsContentActivity;
import com.dgut.collegemarket.adapter.AdvertisementPagerAdapter;
import com.dgut.collegemarket.adapter.GoodsListAdapter;
import com.dgut.collegemarket.api.entity.Advertisement;
import com.dgut.collegemarket.api.entity.Page;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Goods;
import com.dgut.collegemarket.util.Densityutils;
import com.dgut.collegemarket.view.layout.VRefreshLayout;
import com.dgut.collegemarket.view.widgets.JDHeaderView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class GoodsListFragment extends Fragment {
    private VRefreshLayout mRefreshLayout;
    Activity activity;
    View view;
    ViewPager viewPager;
    private List<ImageView> mViews;
    private List<Goods> mGoods = new ArrayList<Goods>();
    private List<Advertisement> advertisementList = new ArrayList<>();

    Page<Goods> goodsPage;
    private ListView mListView;

    AdvertisementPagerAdapter advertisementPagerAdapter;
    private View mJDHeaderView;
    GoodsListAdapter adpter;
    int page = 0;
    int pageSize = 10;
    int NOT_MORE_PAGE = -1;

    GoodsListFragment fragment;
    View btnLoadMore;
    TextView textLoadMore;
   boolean firstBuilt=true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            activity = getActivity();
             fragment = this;
            view = inflater.inflate(R.layout.fragment_page_goods_list, null);
            initImageView(inflater);
            initHeaderView();

        }
        return view;
    }

    private void initImageView(LayoutInflater inflater) {
        viewPager = new ViewPager(activity);
        viewPager.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, dp2px(200)));
        mViews = new ArrayList<>();
        for(int i=0;i<3;i++){
            ImageView imageView = new ImageView(activity);
            imageView.setBackgroundResource(R.drawable.android1);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mViews.add(imageView);
        }
        advertisementPagerAdapter = new AdvertisementPagerAdapter(activity,mViews,advertisementList);
        viewPager.setAdapter(advertisementPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            //配合Adapter的currentItem字段进行设置。
            @Override
            public void onPageSelected(int arg0) {
                handler.sendMessage(Message.obtain(handler, ImageHandler.MSG_PAGE_CHANGED, arg0, 0));
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            //覆写该方法实现轮播效果的暂停和恢复
            @Override
            public void onPageScrollStateChanged(int arg0) {
                switch (arg0) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        handler.sendEmptyMessage(ImageHandler.MSG_KEEP_SILENT);
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);
                        break;
                    default:
                        break;
                }
            }
        });
        viewPager.setCurrentItem(Integer.MAX_VALUE/2);//默认在中间，使用户看不到边界

        getAdvertisements();

        adpter = new GoodsListAdapter(activity, mGoods);
        mListView = (ListView) view.findViewById(R.id.listView);
        mListView.addHeaderView(viewPager);
        btnLoadMore = inflater.inflate(R.layout.list_foot, null);
        textLoadMore = (TextView) btnLoadMore.findViewById(R.id.loadmore);
        mListView.addFooterView(btnLoadMore);
        mListView.setAdapter(adpter);
        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (page!=NOT_MORE_PAGE) {
                    refreshGoodsList();
                }
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent=new Intent(activity,GoodsContentActivity.class);
                if(mViews.size()==0)
                {
                    intent.putExtra("goods",mGoods.get(i));

                }else{
                    intent.putExtra("goods",mGoods.get(i-1));
                }

                startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_bottom,R.anim.none);
            }
        });
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
                    page=0;
                    refreshGoodsList();
                }
            });
        }

        mRefreshLayout.setHeaderView(mJDHeaderView);
        mRefreshLayout.setBackgroundColor(Color.WHITE);


    }

    private void refreshGoodsList() {
        textLoadMore.setEnabled(false);
        textLoadMore.setText("加载中");

        final Request request = Server.requestBuilderWithApi("goods/all/" +  page++)
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
                            goodsPage = mapper.readValue(result,
                                    new TypeReference<Page<Goods>>() {
                                    });
                            List<Goods> datas = goodsPage.getContent();


                            if (datas.size()<=pageSize&&page==1)
                            {   textLoadMore.setText("加载更多");
                                datas = goodsPage.getContent();
                                mGoods.clear();
                                mGoods.addAll(datas);
                                adpter.notifyDataSetInvalidated();
                            }
                            else if(goodsPage.getTotalPages()!=page){
                                textLoadMore.setText("加载更多");
                                mGoods.addAll(goodsPage.getContent());
                                adpter.notifyDataSetChanged();
                            }
                            else {
                                page=NOT_MORE_PAGE;
                                textLoadMore.setText("没有新内容");
                                mGoods.addAll(goodsPage.getContent());
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
            //开始轮播效果
            handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);
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

    private void getAdvertisements(){
        OkHttpClient client = Server.getSharedClient();
        Request request = Server.requestBuilderWithApi("goods/all/advertisement/0")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity,"联网失败，请重新检查网络!",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Page<Advertisement> page = new ObjectMapper().readValue(result, new TypeReference<Page<Advertisement>>() {});
                advertisementList.addAll(page.getContent());
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mViews.clear();
                        if(advertisementList.size()>0)
                        { for (int i =0; i < advertisementList.size(); i++) {
                            ImageView imageView = new ImageView(activity);
                            Picasso.with(activity).load(Server.serverAddress +advertisementList.get(i).getImgurl()).placeholder(R.drawable.unknow_avatar).error(R.drawable.unknow_avatar).into(imageView);
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            mViews.add(imageView);
                        }
                        advertisementPagerAdapter.notifyDataSetChanged();
                            mListView.addHeaderView(viewPager);
                        }
                        else{
                            mListView.removeHeaderView(viewPager);}

                    }
                });
            }
        });
    }

    protected int dp2px(float dp) {
        return Densityutils.dp2px(activity, dp);
    }
    //使用弱引用避免Handler泄露.这里的泛型参数可以不是Activity，也可以是Fragment等
    private WeakReference<GoodsListFragment> weakReference;
    private ImageHandler handler = new ImageHandler(new WeakReference<GoodsListFragment>(GoodsListFragment.this));
    private class ImageHandler extends Handler{

        /**
         * 请求更新显示的View。
         */
        protected static final int MSG_UPDATE_IMAGE  = 1;
        /**
         * 请求暂停轮播。
         */
        protected static final int MSG_KEEP_SILENT   = 2;
        /**
         * 请求恢复轮播。
         */
        protected static final int MSG_BREAK_SILENT  = 3;
        /**
         * 记录最新的页号，当用户手动滑动时需要记录新页号，否则会使轮播的页面出错。
         * 例如当前如果在第一页，本来准备播放的是第二页，而这时候用户滑动到了末页，
         * 则应该播放的是第一页，如果继续按照原来的第二页播放，则逻辑上有问题。
         */
        protected static final int MSG_PAGE_CHANGED  = 4;

        //轮播间隔时间
        protected static final long MSG_DELAY = 3000;


        private int currentItem = 0;

        protected ImageHandler(WeakReference<GoodsListFragment> wk){
            weakReference = wk;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (fragment==null){
                //Activity已经回收，无需再处理UI了
                return ;
            }
            //检查消息队列并移除未发送的消息，这主要是避免在复杂环境下消息出现重复等问题。
            if (fragment.handler.hasMessages(MSG_UPDATE_IMAGE)){
                fragment.handler.removeMessages(MSG_UPDATE_IMAGE);
            }
            switch (msg.what) {
                case MSG_UPDATE_IMAGE:
                    currentItem++;
                    fragment.viewPager.setCurrentItem(currentItem);
                    //准备下次播放
                    fragment.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_KEEP_SILENT:
                    //只要不发送消息就暂停了
                    break;
                case MSG_BREAK_SILENT:
                    fragment.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_PAGE_CHANGED:
                    //记录当前的页号，避免播放的时候页面显示不正确。
                    currentItem = msg.arg1;
                    break;
                default:
                    break;
            }
        }
    }
}