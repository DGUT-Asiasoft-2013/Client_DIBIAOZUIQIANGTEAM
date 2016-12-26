package com.dgut.collegemarket.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.fragment.Records.MessagesRecordsFragment;
import com.dgut.collegemarket.fragment.Records.SubscriptionRecordsFragment;
import com.dgut.collegemarket.fragment.widgets.CheckSubscriptionAndMessagesRecordsFragment;
import com.dgut.collegemarket.fragment.widgets.CheckSubscriptionAndMessagesRecordsFragment.OnTabSelectedListener;
import com.dgut.collegemarket.fragment.widgets.CheckSubscriptionAndMessagesRecordsFragment.OnNewClickedListener;


//个人页面—我的订阅—点击事件
public class CheckSubscriptionAndMessagesRecordsActivity extends Activity {

    SubscriptionRecordsFragment contentSubscriptionRecords = new SubscriptionRecordsFragment();
    MessagesRecordsFragment contentMessagesRecords = new MessagesRecordsFragment();
    CheckSubscriptionAndMessagesRecordsFragment checkrecords_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_records_2);

        checkrecords_2 = (CheckSubscriptionAndMessagesRecordsFragment) getFragmentManager().findFragmentById(R.id.frag_tabbar_2);
        checkrecords_2.setOnTabSelectedListener(new OnTabSelectedListener() {

            @Override
            public void onTabSelected(int index) {
                changeContentFragment2(index);
            }
        });

        checkrecords_2.setOnNewClickedListener(new OnNewClickedListener() {

            @Override
            public void onNewClicked() {
                bringUpEditor2();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkrecords_2.getSelectedIndex() < 0) {
            checkrecords_2.setSelectedItem(0);
        }

    }

    void changeContentFragment2(int index) {
        Fragment newFrag = null;

        switch (index) {
            case 0:
                newFrag = contentSubscriptionRecords;
                break;
            case 1:
                newFrag = contentMessagesRecords;
                break;
            default:
                break;
        }

        if (newFrag == null) return;

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_2, newFrag)
                .commit();
    }

    void bringUpEditor2() {
//        Intent itnt = new Intent(this, NewContentActivity.class);
//        startActivity(itnt);
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.none);
    }
}
