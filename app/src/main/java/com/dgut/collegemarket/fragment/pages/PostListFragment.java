package com.dgut.collegemarket.fragment.pages;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.PostContentActivity;
import com.dgut.collegemarket.adapter.PostListAdapter;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Page;
import com.dgut.collegemarket.api.entity.Post;
import com.dgut.collegemarket.util.Densityutils;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class PostListFragment extends Fragment {

    private VRefreshLayout vRefreshLayout;
    View view;
    Activity activity;
    ListView lvPost;
    List<Post> postslist=new ArrayList<Post>();
    int pageNum=0;
    PostListAdapter postListAdapter;
    boolean firstBuilt=true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_page_post_list, null);
            activity= getActivity();
            lvPost = (ListView) view.findViewById(R.id.lv_post);

            vRefreshLayout  = (VRefreshLayout) view.findViewById(R.id.refresh_layout);
            if (vRefreshLayout != null) {
                vRefreshLayout.setBackgroundColor(Color.DKGRAY);
                vRefreshLayout.setAutoRefreshDuration(400);
                vRefreshLayout.setRatioOfHeaderHeightToReach(1.5f);
                vRefreshLayout.addOnRefreshListener(new VRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getPosts();
                    }
                });
            }
            vRefreshLayout.setHeaderView(vRefreshLayout.getDefaultHeaderView());
            vRefreshLayout.setBackgroundColor(Color.WHITE);

            postListAdapter  = new PostListAdapter(activity,postslist);
            lvPost.setAdapter(postListAdapter);
            lvPost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(activity,PostContentActivity.class);
                    intent.putExtra("post",postslist.get(i));
                    startActivity(intent);
                }
            });
        }
        return view;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        pageNum=0;
//        getPosts();
//    }

    private Handler scaleHandler = new Handler();
    private Runnable scaleRunnable = new Runnable() {

        @Override
        public void run() {
            if (firstBuilt) {
                vRefreshLayout.autoRefresh();
                firstBuilt=false;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        pageNum=0;
        activity.getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                scaleHandler.post(scaleRunnable);
            }
        });
    }
    public void getPosts(){
        OkHttpClient client = Server.getSharedClient();
        Request request = Server.requestBuilderWithApi("post/getposts/"+pageNum).build();
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("拼命获取帖子中");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        vRefreshLayout.refreshComplete();
                        Toast.makeText(activity,"联网失败，请检查网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Page<Post> postPage= new ObjectMapper().readValue(result,new TypeReference<Page<Post>>(){});
                if (postslist.size() == 0){
                    postslist.addAll(postPage.getContent());
                }else{
                    postslist.addAll(postPage.getContent());
                }
                pageNum = postPage.getNumber()+1;

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        vRefreshLayout.refreshComplete();
                        postListAdapter.notifyDataSetChanged();
                    }
                });

            }
        });
    }

    protected int dp2px(float dp) {
        return Densityutils.dp2px(activity, dp);
    }
}
