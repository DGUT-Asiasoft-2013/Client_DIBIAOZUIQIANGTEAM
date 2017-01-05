package com.dgut.collegemarket.activity.myprofile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.dgut.collegemarket.R;


//我的私信界面—显示数据库记录—私信记录
public class CheckDirectMessagesActivity extends Activity {


    ImageView imageView_turnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_direct_messages);

        imageView_turnBack = (ImageView)findViewById(R.id.imageView_turnBack);
        imageView_turnBack.setOnClickListener(new View.OnClickListener() {
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
