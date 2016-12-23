package com.dgut.collegemarket.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Goods;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.w3c.dom.Text;

public class GoodsContentActivity extends AppCompatActivity {
 ImageView albumsImg;
    ImageView avatarImg;
    Goods goods;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_goods_content);
        goods= (Goods) getIntent().getSerializableExtra("goods");

         toolbar = (Toolbar) findViewById(R.id.toolbar);

        albumsImg= (ImageView) findViewById(R.id.image_albums);
        avatarImg= (ImageView) findViewById(R.id.img_avatar);
        Picasso.with(this).load(Server.serverAddress_wuzeen+goods.getAlbums()).resize(500,300).centerInside().into(albumsImg);
        Picasso.with(this).load(Server.serverAddress_wuzeen+goods.getPublishers().getAvatar()).resize(50,50).centerInside().error(R.drawable.unknow_avatar).into(avatarImg);

        System.out.println(Server.serverAddress_wuzeen+goods.getAlbums());
        initView();



    }

    TextView titleText;
    TextView contentText;
    private void initView() {

//        titleText= (TextView) findViewById(R.id.text_title);
        contentText= (TextView) findViewById(R.id.text_content);
        contentText.setText(goods.getContent());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
      overridePendingTransition(R.anim.none,R.anim.slide_out_bottom);
    }



}
