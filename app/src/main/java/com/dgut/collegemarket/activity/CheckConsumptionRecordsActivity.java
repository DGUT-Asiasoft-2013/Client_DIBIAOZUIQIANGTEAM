package com.dgut.collegemarket.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.fragment.Records.ConsumptionRecordsFragment;
import com.dgut.collegemarket.fragment.widgets.CheckConsumptionRecordsFragment;
import com.dgut.collegemarket.fragment.widgets.CheckConsumptionRecordsFragment.OnTabSelectedListener;
import com.dgut.collegemarket.fragment.widgets.CheckConsumptionRecordsFragment.OnNewClickedListener;


//个人页面—我的消费—点击事件
public class CheckConsumptionRecordsActivity extends Activity {

    ConsumptionRecordsFragment contentConsumptionRecords = new ConsumptionRecordsFragment();
    CheckConsumptionRecordsFragment checkrecords_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_consumption_records);

        checkrecords_1 = (CheckConsumptionRecordsFragment) getFragmentManager().findFragmentById(R.id.frag_tabbar_2);
        checkrecords_1.setOnTabSelectedListener(new OnTabSelectedListener() {

            @Override
            public void onTabSelected(int index) {
                changeContentFragment1(index);
            }
        });

        checkrecords_1.setOnNewClickedListener(new OnNewClickedListener() {

            @Override
            public void onNewClicked() {
                bringUpEditor1();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkrecords_1.getSelectedIndex() < 0) {
            checkrecords_1.setSelectedItem(0);
        }

    }

    void changeContentFragment1(int index) {
        Fragment newFrag = null;

        switch (index) {
            case 0:
                newFrag = contentConsumptionRecords;
                break;
            default:
                break;
        }

        if (newFrag == null) return;

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_1, newFrag)
                .commit();
    }

    void bringUpEditor1() {
//        Intent itnt = new Intent(this, NewContentActivity.class);
//        startActivity(itnt);
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.none);
    }

}