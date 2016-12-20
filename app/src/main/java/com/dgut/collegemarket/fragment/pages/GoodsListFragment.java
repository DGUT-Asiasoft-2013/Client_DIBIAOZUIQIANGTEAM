
package com.dgut.collegemarket.fragment.pages;


import com.dgut.collegemarket.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GoodsListFragment extends Fragment {

    Activity activity;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            activity = getActivity();
            view = inflater.inflate(R.layout.fragment_page_goods_list, null);


        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}