package com.dgut.collegemarket.fragment.Records;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
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
import com.dgut.collegemarket.activity.ContentConsumptionActivity;
import com.dgut.collegemarket.api.entity.Page;
import com.dgut.collegemarket.api.entity.Records;
import com.dgut.collegemarket.api.Server;
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

    int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            activity = getActivity();
            view = inflater.inflate(R.layout.fragment_consumption_records, null);
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
                    LoadMore();
                }
            });
        }

        return view;
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

            TextView textCoin = (TextView) view.findViewById(R.id.money);
            TextView textCause = (TextView) view.findViewById(R.id.cause);
            TextView textDate = (TextView) view.findViewById(R.id.date);
            Records records = data.get(position);

            textCoin.setText(records.getCoin() + "元");
            textCause.setText(records.getCause());

            String dateStr = DateFormat.format("yyyy-MM-dd hh:mm", records.getCreateDate()).toString();
            textDate.setText(dateStr);
            return view;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    void reload() {
        Request request = Server.requestBuilderWithApi("record/records")
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
                            ConsumptionRecordsFragment.this.page = data.getNumber();
                            ConsumptionRecordsFragment.this.data = data.getContent();
                            listAdapter.notifyDataSetInvalidated();
                        }
                    });
                } catch (final Exception e) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
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
        textLoadMore.setText("加载更多");
        Request request = Server.requestBuilderWithApi("record/records/" + (page+1))
                .get()
                .build();
        Server.getSharedClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        LoadMore.setEnabled(true);
                        textLoadMore.setText("成功");
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
                                listAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        LoadMore.setEnabled(true);
                        textLoadMore.setText("失败");
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