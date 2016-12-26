package com.dgut.collegemarket.activity.myprofile;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.fragment.Records.FansRecordsFragment;
import com.dgut.collegemarket.fragment.Records.IdolsRecordsFragment;
import com.dgut.collegemarket.fragment.widgets.CheckIdolsAndFansRecordsFragment;
import com.dgut.collegemarket.fragment.widgets.CheckIdolsAndFansRecordsFragment.OnTabSelectedListener;
import com.dgut.collegemarket.fragment.widgets.CheckIdolsAndFansRecordsFragment.OnNewClickedListener;


//个人页面—我的订阅—点击事件
public class CheckIdolsAndFansRecordsActivity extends Activity {

    IdolsRecordsFragment contentIdolsRecords = new IdolsRecordsFragment();
    FansRecordsFragment contentFansRecords = new FansRecordsFragment();
    CheckIdolsAndFansRecordsFragment checkrecords_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_idols_and_fans_records);

        checkrecords_2 = (CheckIdolsAndFansRecordsFragment) getFragmentManager().findFragmentById(R.id.frag_tabbar_2);
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
                newFrag = contentIdolsRecords;
                break;
            case 1:
                newFrag = contentFansRecords;
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
