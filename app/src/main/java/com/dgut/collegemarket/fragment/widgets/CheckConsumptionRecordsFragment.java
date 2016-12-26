package com.dgut.collegemarket.fragment.widgets;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dgut.collegemarket.R;


public class CheckConsumptionRecordsFragment extends Fragment {
    View consumptionRecords;
    View[] tabs;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_consumption_records, null);

        consumptionRecords = view.findViewById(R.id.consumption_check);

        tabs = new View[] {
                consumptionRecords,
        };

        for(final View tab : tabs){
            tab.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onTabClicked(tab);
                }
            });
        }


        return view;
    }

    public static interface OnTabSelectedListener {
        void onTabSelected(int index);
    }

    CheckConsumptionRecordsFragment.OnTabSelectedListener onTabSelectedListener;

    public void setOnTabSelectedListener(CheckConsumptionRecordsFragment.OnTabSelectedListener onTabSelectedListener) {
        this.onTabSelectedListener = onTabSelectedListener;
    }

    public void setSelectedItem(int index){
        if(index>=0 && index<tabs.length){
            onTabClicked(tabs[index]);
        }
    }

    public int getSelectedIndex(){
        for(int i=0; i<tabs.length; i++){
            if(tabs[i].isSelected()) return i;
        }

        return -1;
    }

    void onTabClicked(View tab){
        int selectedIndex = -1;

        for(int i=0; i<tabs.length; i++){
            View otherTab = tabs[i];
            if(otherTab == tab){
                otherTab.setSelected(true);
                selectedIndex = i;
            }else{
                otherTab.setSelected(false);
            }
        }

        if(onTabSelectedListener!=null && selectedIndex>=0){
            onTabSelectedListener.onTabSelected(selectedIndex);
        }
    }

    public static interface OnNewClickedListener{
        void onNewClicked();
    }

    CheckConsumptionRecordsFragment.OnNewClickedListener onNewClickedListener;

    public void setOnNewClickedListener(CheckConsumptionRecordsFragment.OnNewClickedListener listener){
        this.onNewClickedListener = listener;
    }

    void onNewClicked(){
        if(onNewClickedListener!=null)
            onNewClickedListener.onNewClicked();
    }

}