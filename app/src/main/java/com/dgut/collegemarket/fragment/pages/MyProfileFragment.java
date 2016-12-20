package com.dgut.collegemarket.fragment.pages;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.CheckConsumptionAndSalesRecordsActivity;
import com.dgut.collegemarket.activity.CheckSubscriptionAndMessagesRecordsActivity;
import com.dgut.collegemarket.fragment.widgets.CheckSubscriptionAndMessagesRecordsFragment;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MyProfileFragment extends Fragment {
    View view;
    Activity activity;
    RelativeLayout relativeLayout1;
    RelativeLayout relativeLayout2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_page_my_profile, null);
            activity= getActivity();

            relativeLayout1 = (RelativeLayout)view.findViewById(R.id.relativeLayout1);
            relativeLayout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goCheckCASActivity();
                }
            });

            relativeLayout2 = (RelativeLayout)view.findViewById(R.id.relativeLayout2);
            relativeLayout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goCheckSAMActivity();
                }
            });
        }
        return view;
    }

    void goCheckCASActivity() {
        Intent itnt1 = new Intent(getActivity(), CheckConsumptionAndSalesRecordsActivity.class);
        startActivity(itnt1);
    }

    void goCheckSAMActivity() {
        Intent itnt2 = new Intent(getActivity(), CheckSubscriptionAndMessagesRecordsActivity.class);
        startActivity(itnt2);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
