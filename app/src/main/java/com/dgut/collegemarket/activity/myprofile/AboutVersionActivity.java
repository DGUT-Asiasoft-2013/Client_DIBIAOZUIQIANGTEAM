package com.dgut.collegemarket.activity.myprofile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.dgut.collegemarket.R;


//个人页面—版本—点击事件
public class AboutVersionActivity extends Activity {


    ImageView imageview_turnback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_version);

        imageview_turnback = (ImageView)findViewById(R.id.imageview_turnback);
        imageview_turnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


}
