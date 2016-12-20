package com.dgut.collegemarket.fragment.pages;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dgut.collegemarket.R;


public class OrderListFragment extends Fragment {
    View view;
    Activity activity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_page_order_list, null);
            activity= getActivity();
        }
        return view;
    }

}
