package com.dgut.collegemarket.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.fragment.Records.DirectMessagesRecordsFragment;
import com.dgut.collegemarket.fragment.widgets.CheckDirectMessagesFragment;
import com.dgut.collegemarket.fragment.widgets.CheckDirectMessagesFragment.OnNewClickedListener;
import com.dgut.collegemarket.fragment.widgets.CheckDirectMessagesFragment.OnTabSelectedListener;


//我的私信界面—显示数据库记录—私信记录
public class CheckDirectMessagesActivity extends Activity {

    DirectMessagesRecordsFragment contentDirectMessages = new DirectMessagesRecordsFragment();
    CheckDirectMessagesFragment checkrecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_direct_messages);

        checkrecords = (CheckDirectMessagesFragment) getFragmentManager().findFragmentById(R.id.frag_tabbar);
        checkrecords.setOnTabSelectedListener(new OnTabSelectedListener() {

            @Override
            public void onTabSelected(int index) {
                changeContentFragment1(index);
            }
        });

        checkrecords.setOnNewClickedListener(new OnNewClickedListener() {

            @Override
            public void onNewClicked() {
                bringUpEditor1();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkrecords.getSelectedIndex() < 0) {
            checkrecords.setSelectedItem(0);
        }

    }

    void changeContentFragment1(int index) {
        Fragment newFrag = null;

        switch (index) {
            case 0:
                newFrag = contentDirectMessages;
                break;
            default:
                break;
        }

        if (newFrag == null) return;

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content, newFrag)
                .commit();
    }

    void bringUpEditor1() {
//        Intent itnt = new Intent(this, NewContentActivity.class);
//        startActivity(itnt);
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.none);
    }
}
