package com.dgut.collegemarket.activity.orders;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.common.ContactListActivity;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Contact;
import com.dgut.collegemarket.api.entity.Goods;
import com.dgut.collegemarket.api.entity.Orders;
import com.dgut.collegemarket.app.MsgType;
import com.dgut.collegemarket.util.CreateSigMsg;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 订单创建
 */
public class OrdresCreateActivity extends Activity {
    private static final int RESULT_GEICONTACT = 100;
    private static final int RESULT_GETNOTE = 101;
    Goods goods;
    Contact contact;
    TextView contactNameText;
    TextView titleText;
    ImageView avatarImg,imageView;
    TextView quantityText;
    TextView priceText;
    TextView sumText1;
    TextView sumText2;
    TextView paywayText;
    TextView noteText;
    RelativeLayout addressRL, addressResultRL, noteRL, payWayRL;
    TextView nameText, phoneText, schoolText, susheText;
    int num = 0;
    PopupWindow mPopupWindow;
    Button createBtn;
    boolean isPayOnline=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordres_create);
        goods = (Goods) getIntent().getSerializableExtra("goods");
        num = getIntent().getIntExtra("quantity", 0);
        initView();
    }

    private void initView() {
        imageView = (ImageView)findViewById(R.id.iv_add_contact_back);
        nameText = (TextView) findViewById(R.id.tv_publishers_name);
        avatarImg = (ImageView) findViewById(R.id.img_avatar);
        quantityText = (TextView) findViewById(R.id.tv_quantity);
        priceText = (TextView) findViewById(R.id.tv_price);
        sumText1 = (TextView) findViewById(R.id.tv_sum_1);
        sumText2 = (TextView) findViewById(R.id.tv_sum_2);
        titleText = (TextView) findViewById(R.id.tv_title);
        paywayText = (TextView) findViewById(R.id.tv_pay_way);
        noteText = (TextView) findViewById(R.id.tv_note);
        createBtn = (Button) findViewById(R.id.btn_orders_create);

        titleText.setText(goods.getTitle());
        nameText.setText(goods.getPublishers().getName());
        System.out.println(nameText.getText().toString()+" :"+goods.getPublishers().getName());
        quantityText.setText("X" + num);
        priceText.setText(goods.getPrice() + "");
        sumText1.setText(num * goods.getPrice() + "元");
        sumText2.setText(num * goods.getPrice() + "");

        Picasso.with(this).load(Server.serverAddress + goods.getPublishers().getAvatar()).resize(30, 30).centerInside().error(R.drawable.unknow_avatar).into(avatarImg);


        addressRL = (RelativeLayout) findViewById(R.id.rl_address);
        ;
        addressResultRL = (RelativeLayout) findViewById(R.id.rl_address_result);
        ;
        noteRL = (RelativeLayout) findViewById(R.id.rl_note);
        ;
        payWayRL = (RelativeLayout) findViewById(R.id.rl_pay_way);
        ;

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addressRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrdresCreateActivity.this, ContactListActivity.class);
                startActivityForResult(intent, RESULT_GEICONTACT);
                overridePendingTransition(R.anim.slide_in_left, R.anim.none);
            }
        });


        noteRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrdresCreateActivity.this, NoteEditActivity.class);
                startActivityForResult(intent, RESULT_GETNOTE);
                overridePendingTransition(R.anim.slide_in_left, R.anim.none);
            }
        });

        payWayRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPaywayPopupView();
                mPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, -200);
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOrder();
            }


        });
    }

    ProgressDialog progressDialog;

    private void createOrder() {
        if(contact==null) {
            Toast.makeText(this,"请选择收货地址",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("提交中");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient client = Server.getSharedClient();



        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("goods_id", goods.getId()+"")
                .addFormDataPart("contact_id", contact.getId()+"")
                .addFormDataPart("price", priceText.getText().toString())
                .addFormDataPart("quantity", String.valueOf(num))
                .addFormDataPart("note", noteText.getText().toString())
                .addFormDataPart("isPayOnline", String.valueOf(isPayOnline))
 .addFormDataPart("state", String.valueOf(1));
        Request request = Server.requestBuilderWithApi("orders/add")
                .post(multipartBuilder.build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(OrdresCreateActivity.this, "网络连接失败，请检查网络设置", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                progressDialog.dismiss();
                final String result = response.body().string();

                if (result.equals("")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OrdresCreateActivity.this, "服务器异常", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    System.out.println("Orders:"+result);
                    final Orders orders = new ObjectMapper().readValue(result, Orders.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OrdresCreateActivity.this, "订单创建成功" , Toast.LENGTH_SHORT).show();
                            //发送新订单消息
                            Map<String, String> valuesMap  = new HashMap<>();
                            valuesMap.put("msg_type", MsgType.MSG_ORDERS);
                            valuesMap.put("orders_state", "1");
                            valuesMap.put("orders_id", orders.getId()+"");
                            CreateSigMsg.context=getApplicationContext();
//                            CreateSigMsg.CreateSigCustomMsg(orders.getGoods().getPublishers().getAccount(),valuesMap);
                            CreateSigMsg.CreateSigTextMsg(orders.getGoods().getPublishers().getAccount(),
                                    "订单消息","您有一份来自"+orders.getBuyer().getName()+"的新订单",valuesMap);
                            Intent intent=new Intent(OrdresCreateActivity.this,OrdersContentActivity.class);
                            intent.putExtra("orders_id",orders.getId());
                            setResult(RESULT_OK);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });


    }


    private void setPaywayPopupView() {
        View popupView = getLayoutInflater().inflate(R.layout.dialog_pay_way, null);

        mPopupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.mystyle);
        mPopupWindow.getContentView().setFocusableInTouchMode(true);
        mPopupWindow.getContentView().setFocusable(true);
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);

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
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });

        TextView payOnline = (TextView) popupView.findViewById(R.id.tv_pay_online);
        TextView payOutline = (TextView) popupView.findViewById(R.id.tv_pay_outline);
        payOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paywayText.setText("在线支付");
                mPopupWindow.dismiss();
                isPayOnline=true;
            }
        });
        payOutline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paywayText.setText("货到付款");
                isPayOnline=false;
                mPopupWindow.dismiss();
            }
        });

    }

    private void setContactView() {
        addressResultRL.setVisibility(View.VISIBLE);
        contactNameText = (TextView) findViewById(R.id.tv_name);
        phoneText = (TextView) findViewById(R.id.tv_phone);
        schoolText = (TextView) findViewById(R.id.tv_school);
        susheText = (TextView) findViewById(R.id.tv_sushe);

        contactNameText.setText(contact.getName());
        phoneText.setText(contact.getPhone());
        schoolText.setText(contact.getSchool());
        susheText.setText(contact.getSushe());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_GEICONTACT:
                    contact = (Contact) data.getExtras().get("contact");
                    setContactView();
                    break;
                case RESULT_GETNOTE:
                    noteText.setVisibility(View.VISIBLE);
                    noteText.setText(data.getExtras().getString("content"));
                    break;
                default:
                    break;
            }

        }


    }
}
