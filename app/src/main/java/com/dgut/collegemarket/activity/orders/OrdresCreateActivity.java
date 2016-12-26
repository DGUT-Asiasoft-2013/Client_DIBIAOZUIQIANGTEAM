package com.dgut.collegemarket.activity.orders;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.entity.Goods;

/**
 * 订单创建
 */
public class OrdresCreateActivity extends Activity {
    Goods goods;
    TextView nameText;
    ImageView avatarImg;
    TextView quantityText;
    TextView priceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordres_create);
        goods = (Goods) getIntent().getSerializableExtra("goods");

    }
}
