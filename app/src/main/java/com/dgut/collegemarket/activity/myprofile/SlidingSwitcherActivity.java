package com.dgut.collegemarket.activity.myprofile;

import android.app.Activity;
import android.os.Bundle;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.view.viewswitcher.SlidingSwitcherView;

public class SlidingSwitcherActivity extends Activity {

    SlidingSwitcherView slidingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_switcher);

        slidingLayout = (SlidingSwitcherView)findViewById(R.id.slidingLayout);
        slidingLayout.startAutoPlay();

    }
}
