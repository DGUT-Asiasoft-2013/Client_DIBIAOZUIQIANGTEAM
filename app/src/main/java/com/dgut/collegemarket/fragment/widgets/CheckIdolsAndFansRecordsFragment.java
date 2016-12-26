package com.dgut.collegemarket.fragment.widgets;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dgut.collegemarket.R;


public class CheckIdolsAndFansRecordsFragment extends Fragment {
    View idolsRecords, fansRecords, recordsButton;
    View[] tabs;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_idols_and_fans_records, null);

        idolsRecords = view.findViewById(R.id.idols_records);
        fansRecords = view.findViewById(R.id.fans_records);

        tabs = new View[] {
                idolsRecords, fansRecords,
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

    CheckIdolsAndFansRecordsFragment.OnTabSelectedListener onTabSelectedListener;

    public void setOnTabSelectedListener(CheckIdolsAndFansRecordsFragment.OnTabSelectedListener onTabSelectedListener) {
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

    CheckIdolsAndFansRecordsFragment.OnNewClickedListener onNewClickedListener;

    public void setOnNewClickedListener(CheckIdolsAndFansRecordsFragment.OnNewClickedListener listener){
        this.onNewClickedListener = listener;
    }

    void onNewClicked(){
        if(onNewClickedListener!=null)
            onNewClickedListener.onNewClicked();
    }

}