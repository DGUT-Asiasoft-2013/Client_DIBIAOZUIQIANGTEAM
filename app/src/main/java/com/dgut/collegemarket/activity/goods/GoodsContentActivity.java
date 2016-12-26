package com.dgut.collegemarket.activity.goods;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Goods;
import com.dgut.collegemarket.api.entity.User;
import com.squareup.picasso.Picasso;

public class GoodsContentActivity extends AppCompatActivity {
    ImageView albumsImg;
    ImageView avatarImg;
     Goods goods;
    Toolbar toolbar;
    public  static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        goods = (Goods) getIntent().getSerializableExtra("goods");
        user=goods.getPublishers();
        setContentView(R.layout.activity_goods_content);
    
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        albumsImg = (ImageView) findViewById(R.id.image_albums);
        avatarImg = (ImageView) findViewById(R.id.img_avatar);
        setPopupView();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_buy);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPopupWindow.showAsDropDown(view);

            }
        });

        Picasso.with(this).load(Server.serverAddress_wuzeen + goods.getAlbums()).resize(500, 300).centerInside().into(albumsImg);
        Picasso.with(this).load(Server.serverAddress_wuzeen + goods.getPublishers().getAvatar()).resize(50, 50).centerInside().error(R.drawable.unknow_avatar).into(avatarImg);

        System.out.println(Server.serverAddress_wuzeen + goods.getAlbums());
        initView();


    }


    TextView titleText;
    TextView contentText;


    private void initView() {

        titleText = (TextView) findViewById(R.id.text_title);
        contentText = (TextView) findViewById(R.id.text_content);
        titleText.setText(goods.getTitle());

        contentText.setText(goods.getContent());
    }


    TextView quantityText;
    TextView priceText;
    TextView preQuantityText;
    TextView buyQuantityText;
    PopupWindow mPopupWindow;
    Button settlementBt;
    TextView addBt;
    TextView deleteBt;

    private void setPopupView() {
        View popupView = getLayoutInflater().inflate(R.layout.layout_popupwindow, null);

        mPopupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.mystyle);
        mPopupWindow.getContentView().setFocusableInTouchMode(true);
        mPopupWindow.getContentView().setFocusable(true);
        mPopupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mPopupWindow != null && mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                    }
                    return true;
                }
                return false;
            }
        });

        quantityText = (TextView) popupView.findViewById(R.id.text_quantity);
        priceText = (TextView) popupView.findViewById(R.id.text_price);
        preQuantityText = (TextView) popupView.findViewById(R.id.text_quantity_buy_pre);
        buyQuantityText = (TextView) popupView.findViewById(R.id.text_quantity_buy);
        settlementBt = (Button) popupView.findViewById(R.id.button_settlement);
        addBt = (TextView) popupView.findViewById(R.id.bt_add);
        deleteBt = (TextView) popupView.findViewById(R.id.bt_delete);

        quantityText.setText(goods.getQuantity()+"");
        priceText.setText(goods.getPrice() + "");

        addBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.valueOf(preQuantityText.getText().toString());
                if (num < goods.getQuantity())
                    num++;
                preQuantityText.setText(num + "");
                buyQuantityText.setText(num + "");
                settlementBt.setText("结算（" + num * goods.getPrice() + "元）");
            }
        });

        deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.valueOf(preQuantityText.getText().toString());
                if (num > 0)
                    num--;
                preQuantityText.setText(num + "");
                buyQuantityText.setText(num + "");
                settlementBt.setText("结算（" + num * goods.getPrice() + "元）");
            }
        });
        settlementBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(GoodsContentActivity.this)
                        .setTitle("敬请期待")
                        .setMessage("后续功能将在下个版本添加").show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.none, R.anim.slide_out_bottom);
    }


}
