package com.dgut.collegemarket.fragment.pages;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.posts.PostContentActivity;
import com.dgut.collegemarket.adapter.PostListAdapter;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Page;
import com.dgut.collegemarket.api.entity.Post;
import com.dgut.collegemarket.view.layout.VRefreshLayout;
import com.dgut.collegemarket.view.widgets.DefaultHeaderView;
import com.dgut.collegemarket.view.widgets.JDHeaderView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class PostListFragment extends Fragment {
    View view;
    View footView;

    private PopupWindow popupWindow;

    Activity activity;

    ListView lvPost;
    List<Post> postslist=new ArrayList<Post>();

    int pageNum=0;
    int totalPageNum =0;

    PostListAdapter postListAdapter;

    private VRefreshLayout mRefreshLayout;

    TextView btnLoadmore;
    ImageView ivDownList;

    int solve=-1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_page_post_list, null);
            footView = inflater.inflate(R.layout.list_post_foot,null);

            activity= getActivity();

            btnLoadmore = (TextView) footView.findViewById(R.id.loadmore);

            ivDownList = (ImageView) view.findViewById(R.id.iv_down_list);
            lvPost = (ListView) view.findViewById(R.id.lv_post);
            mRefreshLayout = (VRefreshLayout) view.findViewById(R.id.refresh_layout);

            if (mRefreshLayout != null) {
                mRefreshLayout.setBackgroundColor(Color.DKGRAY);
                mRefreshLayout.setAutoRefreshDuration(400);
                mRefreshLayout.setRatioOfHeaderHeightToReach(1.5f);
                mRefreshLayout.addOnRefreshListener(new VRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        postslist.clear();
                        pageNum=0;
                        getPosts(pageNum);
                    }
                });
            }

            mRefreshLayout.setHeaderView(new DefaultHeaderView(activity));
            mRefreshLayout.setBackgroundColor(Color.WHITE);

            postListAdapter  = new PostListAdapter(activity,postslist);

            lvPost.addFooterView(footView);
            lvPost.setAdapter(postListAdapter);
            lvPost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(activity,PostContentActivity.class);
                    intent.putExtra("post",postslist.get(i));
                    startActivity(intent);
                }
            });

            btnLoadmore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(pageNum<totalPageNum){
                        pageNum = pageNum+1;
                        getPosts(pageNum);
                    }
                }
            });
            ivDownList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (popupWindow != null&&popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        return;
                    } else {
                        initmPopupWindowView();
                        popupWindow.showAsDropDown(v, 5, 10);
                    }
                }
            });
            getPosts(pageNum);
        }
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
                popupWindow = null;
            }
        }

    }

    /**
     * 获取帖子列表
     * @param pageNums
     */
    public void getPosts(int pageNums){
        OkHttpClient client = Server.getSharedClient();
        Request request = Server.requestBuilderWithApi("post/getposts/"+pageNums).build();
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("拼命获取帖子中");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.refreshComplete();
                        dialog.dismiss();
                        Toast.makeText(activity,"联网失败，请检查网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Page<Post> postPage= new ObjectMapper().readValue(result,new TypeReference<Page<Post>>(){});

                List<Post> acceptedList = new ArrayList<Post>();
                List<Post> unAcceptedList = new ArrayList<Post>();

                for(Post p:postPage.getContent()){
                    if(p.issolve()){
                        acceptedList.add(p);
                    }else{
                        unAcceptedList.add(p);
                    }
                }
                pageNum = postPage.getNumber();
                totalPageNum = postPage.getTotalPages();

                if(pageNum==0){
                    postslist.clear();
                }
                if(solve==0){
                    postslist.addAll(acceptedList);
                }else if(solve==1){
                    postslist.addAll(unAcceptedList);
                }else{
                    postslist.addAll(postPage.getContent());
                }



                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.refreshComplete();
                        dialog.dismiss();

                        postListAdapter.notifyDataSetChanged();
                        if(pageNum == totalPageNum){
                            btnLoadmore.setText("没有更多了");
                        }
                    }
                });

            }
        });
    }

    public void initmPopupWindowView() {

        // // 获取自定义布局文件pop.xml的视图
        View customView = LayoutInflater.from(activity).inflate(R.layout.popupwindow_post,
                null, false);
        // 创建PopupWindow实例,200,150分别是宽度和高度
        popupWindow = new PopupWindow(customView, 250, 300);
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        popupWindow.setAnimationStyle(R.style.AnimationFade);
        // 自定义view添加触摸事件
        customView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }

                return false;
            }
        });
        popupWindow.setOutsideTouchable(true);
        /** 在这里可以实现自定义视图的功能 */
        TextView tvAll= (TextView) customView.findViewById(R.id.tv_all);
        TextView tvAccepted = (TextView) customView.findViewById(R.id.tv_accepted);
        TextView tvUnAccepted = (TextView) customView.findViewById(R.id.tv_unaccepted);

        tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solve=-1;
                postslist.clear();
                getPosts(0);
                popupWindow.dismiss();
            }
        });
        tvAccepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solve=0;
                postslist.clear();
                getPosts(0);
                popupWindow.dismiss();
            }
        });
        tvUnAccepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solve=1;
                postslist.clear();
                getPosts(0);
                popupWindow.dismiss();
            }
        });
    }
}
