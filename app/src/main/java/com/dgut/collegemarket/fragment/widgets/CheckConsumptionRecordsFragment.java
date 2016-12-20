package com.dgut.collegemarket.fragment.widgets;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dgut.collegemarket.R;


public class CheckConsumptionAndSalesRecordsFragment extends Fragment {

    View consumptionRecord;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_records_1, null);

        consumptionRecord = view.findViewById(R.id.sales_records);


        return view;
    }


}
