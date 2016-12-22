package com.dgut.collegemarket.fragment.pages;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dgut.collegemarket.R;


public class PostListFragment extends Fragment {
    View view;
    Activity activity;
    ListView lvPost;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_page_post_list, null);
            activity= getActivity();
            lvPost = (ListView) view.findViewById(R.id.lv_post);
        }
        return view;
    }

}
