package com.dgut.collegemarket.fragment.Records;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.myprofile.ContentConsumptionActivity;
import com.dgut.collegemarket.api.entity.Page;
import com.dgut.collegemarket.api.entity.Records;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.fragment.widgets.AvatarView;
import com.dgut.collegemarket.view.layout.VRefreshLayout;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

//消费列表
public class ConsumptionRecordsFragment extends Fragment {

    View view;
    ListView listView;

    View LoadMore;
    TextView textLoadMore;
    List<Records> data;
    Activity activity;
    VRefreshLayout mRefreshLayout;
    int NOT_MORE_PAGE = -1;
    int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {

            activity = getActivity();

            view = inflater.inflate(R.layout.fragment_records_consumption, null);
            LoadMore = inflater.inflate(R.layout.widget_load_more_button, null);
            textLoadMore = (TextView) LoadMore.findViewById(R.id.load_more_text);

            listView = (ListView) view.findViewById(R.id.consumption_list);
            listView.addFooterView(LoadMore);
            listView.setAdapter(listAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    onItemClicked(position);
                }
            });

            LoadMore.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (page != NOT_MORE_PAGE) {
                        LoadMore();
                    }
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
                    reload();
                }
            });
        }

        mRefreshLayout.setHeaderView(mRefreshLayout.getDefaultHeaderView());
        mRefreshLayout.setBackgroundColor(Color.parseColor("#ffc130"));
    }

    BaseAdapter listAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = null;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                view = inflater.inflate(R.layout.widget_records_item, null);
            } else {
                view = convertView;
            }

            AvatarView avatarView = (AvatarView)view.findViewById(R.id.consumption_image);
            TextView textCoin = (TextView) view.findViewById(R.id.money);
            TextView textCause = (TextView) view.findViewById(R.id.cause);
            TextView textDate = (TextView) view.findViewById(R.id.date);
            Records records = data.get(position);

            textCoin.setText(" 我在北京时间： ");
            textCause.setText( records.getCause()  + records.getCoin() + " 元 ");

            String dateStr = DateFormat.format("yyyy-MM-dd hh:mm", records.getCreateDate()).toString();
            textDate.setText(dateStr);
            avatarView.load(records.getUser().getAvatar());
            return view;
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        reload();
    }

    void reload() {
        Request request = Server.requestBuilderWithApi("rec/records")
                .get()
                .build();

        Server.getSharedClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                try {
                    final Page<Records> data = new ObjectMapper()
                            .readValue(arg1.body().string(),
                                    new TypeReference<Page<Records>>() {
                                    });
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            mRefreshLayout.refreshComplete();
                            ConsumptionRecordsFragment.this.page = data.getNumber();
                            ConsumptionRecordsFragment.this.data = data.getContent();
                            listAdapter.notifyDataSetInvalidated();
                        }
                    });
                } catch (final Exception e) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            mRefreshLayout.refreshComplete();
                            new AlertDialog.Builder(activity)
                                    .setMessage(e.getMessage())
                                    .show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call arg0, final IOException e) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        mRefreshLayout.refreshComplete();
                        new AlertDialog.Builder(activity)
                                .setMessage("服务器异常")
                                .show();
                    }
                });
            }
        });
    }

    void LoadMore() {
        LoadMore.setEnabled(false);
        textLoadMore.setText("加载中...");
        Request request = Server.requestBuilderWithApi("rec/records/" + (page + 1))
                .get()
                .build();
        Server.getSharedClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                activity.runOnUiThread(new Runnable() {
                    public void run() {

                        mRefreshLayout.refreshComplete();
                    }
                });

                try {
                    Page<Records> records = new ObjectMapper().readValue(arg1.body().string(), new TypeReference<Page<Records>>() {
                    });
                    if (records.getNumber() > page) {

                        if (data == null) {
                            data = records.getContent();
                        } else {
                            data.addAll(records.getContent());
                        }
                        page = records.getNumber();

                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                LoadMore.setEnabled(true);
                                textLoadMore.setText("加载完成");
                                listAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (Exception ex) {
                    textLoadMore.setText("数据解析失败"+ex.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        mRefreshLayout.refreshComplete();
                        LoadMore.setEnabled(true);
                        textLoadMore.setText("网络异常");
                    }
                });
            }
        });
    }

    void onItemClicked(int position) {

        Records records = data.get(position);

        Intent itnt = new Intent(activity, ContentConsumptionActivity.class);
        itnt.putExtra("data", records);
        startActivity(itnt);
    }
}