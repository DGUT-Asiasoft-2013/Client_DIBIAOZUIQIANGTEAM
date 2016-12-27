package com.dgut.collegemarket.activity.orders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.common.ContactListActivity;
import com.dgut.collegemarket.api.entity.Goods;

/**
 * 订单创建
 */
public class OrdresCreateActivity extends Activity {
    Goods goods;
    TextView nameText;
    TextView titleText;
    ImageView avatarImg;
    TextView quantityText;
    TextView priceText;
    TextView sumText1;
    TextView sumText2;
    RelativeLayout addressRL,timeRL,noteRL,payWayRL;

    int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordres_create);
        goods = (Goods) getIntent().getSerializableExtra("goods");
        num = getIntent().getIntExtra("quantity", 0);
        initView();
    }

    private void initView() {
        nameText = (TextView) findViewById(R.id.tv_name);
        avatarImg = (ImageView) findViewById(R.id.img_avatar);
        quantityText = (TextView) findViewById(R.id.tv_quantity);
        priceText = (TextView) findViewById(R.id.tv_price);
        sumText1 = (TextView) findViewById(R.id.tv_sum_1);
        sumText2 = (TextView) findViewById(R.id.tv_sum_2);
        titleText=(TextView) findViewById(R.id.tv_title);

        titleText.setText(goods.getTitle());
        nameText.setText(goods.getPublishers().getName());
        quantityText.setText("X" + num);
        priceText.setText(goods.getPrice() + "");
        sumText1.setText(num * goods.getPrice() + "元");
        sumText2.setText(num * goods.getPrice() + "");

        addressRL= (RelativeLayout) findViewById(R.id.rl_address);;
        timeRL= (RelativeLayout) findViewById(R.id.rl_time);;
        noteRL= (RelativeLayout) findViewById(R.id.rl_note);;
        payWayRL= (RelativeLayout) findViewById(R.id.rl_pay_way);;

        addressRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OrdresCreateActivity.this, ContactListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.none);
            }
        });

        timeRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        noteRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        payWayRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
