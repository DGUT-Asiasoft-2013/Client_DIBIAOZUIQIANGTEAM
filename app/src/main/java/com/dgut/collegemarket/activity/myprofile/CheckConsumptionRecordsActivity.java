package com.dgut.collegemarket.activity.myprofile;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.fragment.Records.ConsumptionRecordsFragment;


//个人页面—我的消费—点击事件
public class CheckConsumptionRecordsActivity extends Activity {


    ImageView imageview_turnback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_consumption_records);

        imageview_turnback = (ImageView)findViewById(R.id.imageview_turnback);
        imageview_turnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}