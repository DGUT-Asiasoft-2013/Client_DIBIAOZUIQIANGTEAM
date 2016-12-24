package com.dgut.collegemarket.fragment.Records;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dgut.collegemarket.R;

//偶像列表
public class SubscriptionRecordsFragment extends Fragment {

    View view;
    ListView listView;
    View LoadMore;
    TextView textLoadMore;
    Activity activity;

    int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            activity = getActivity();
            view = inflater.inflate(R.layout.fragment_subscription_records, null);
            LoadMore = inflater.inflate(R.layout.widget_load_more_button, null);
            textLoadMore = (TextView) LoadMore.findViewById(R.id.load_more_text);

            listView = (ListView) view.findViewById(R.id.subscription_list);
            listView.addFooterView(LoadMore);

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

    void LoadMore() {
        LoadMore.setEnabled(false);
        textLoadMore.setText("加载更多");
    }

    void onItemClicked(int position) {

    }
}
