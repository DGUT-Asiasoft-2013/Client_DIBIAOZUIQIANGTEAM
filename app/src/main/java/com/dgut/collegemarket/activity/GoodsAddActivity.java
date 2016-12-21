package com.dgut.collegemarket.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Goods;
import com.dgut.collegemarket.fragment.InputCell.PictrueInputCellFragment;
import com.dgut.collegemarket.fragment.InputCell.SimpleTextInputCellFragment;
import com.dgut.collegemarket.util.AnimationEffec;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GoodsAddActivity extends Activity {

    ImageView truckImg;
    SimpleTextInputCellFragment titleFrag;
    SimpleTextInputCellFragment contentFrag;
    SimpleTextInputCellFragment priceFrag;
    SimpleTextInputCellFragment quantityFrag;
    PictrueInputCellFragment pictrueFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_add);
        truckImg = (ImageView) findViewById(R.id.truck);
        titleFrag = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_title);
        contentFrag = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_content);
        priceFrag = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_price);
        quantityFrag = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_quantity);
        pictrueFrag = (PictrueInputCellFragment) getFragmentManager().findFragmentById(R.id.fragment_pictrue);


        truckImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimationEffec.setTransAniToRight(truckImg, 0, 700, 0, 0, 1500);
                submit();
            }
        });
    }

    ProgressDialog progressDialog;

    private void submit() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("发货中");
        progressDialog.setCancelable(false);
        progressDialog.show();
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", titleFrag.getText())
                .addFormDataPart("content", contentFrag.getText())
                .addFormDataPart("quantity", quantityFrag.getText())
                .addFormDataPart("price", priceFrag.getText());
        if (pictrueFrag.getPngData() != null)
            multipartBuilder.addFormDataPart("albums", "albumsName"
                    , RequestBody
                            .create(
                                    MediaType.parse("image/png")
                                    , pictrueFrag.getPngData()));

        final Request request = Server.requestBuilderWithApi("goods/add")
                .post(multipartBuilder.build())
                .build();


        Server.getSharedClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(GoodsAddActivity.this).setMessage("服务器异常").show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressDialog.dismiss();
                final String result = response.body().string();
                ObjectMapper mapper = new ObjectMapper();
                final Goods goods = mapper.readValue(result, Goods.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(GoodsAddActivity.this)
                                .setMessage("商品编号：" + goods.getId() + " 上架成功，请留意购买信息")
                                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                        overridePendingTransition(R.anim.none, R.anim.slide_out_bottom);
                                    }
                                }).show();
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.none, R.anim.slide_out_bottom);


    }

    @Override
    protected void onResume() {
        super.onResume();
        titleFrag.setHintText("标题");
        contentFrag.setHintText("描述");
        quantityFrag.setHintText("数量");
        priceFrag.setHintText("价格");
        contentFrag.setLinesAndLength(5,200);
    }
}
