package com.dgut.collegemarket.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Goods;
import com.dgut.collegemarket.util.DateToString;
import com.dgut.collegemarket.util.JudgeLevel;
import com.dgut.collegemarket.util.PxtDipTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * Created by Administrator on 2016/12/21.
 */

public class GoodsListAdapter extends BaseAdapter {
    ImageView albumsImg;
    Context context;
    List<Goods> mGoods;
    int height, width;

    public GoodsListAdapter(Context context, List<Goods> mGoods) {
        this.context = context;
        this.mGoods = mGoods;
    }

    @Override
    public int getCount() {
        return mGoods == null ? 0 : mGoods.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        View view = null;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.list_goods_item, null);
        } else {
            view = convertView;
        }
        Goods goods = mGoods.get(i);
        TextView titleText = (TextView) view.findViewById(R.id.text_title);
        TextView nameText = (TextView) view.findViewById(R.id.name);
        TextView levelText = (TextView) view.findViewById(R.id.level);
        TextView saleText = (TextView) view.findViewById(R.id.sale);
        TextView contentText = (TextView) view.findViewById(R.id.text_content);
        TextView quantityText = (TextView) view.findViewById(R.id.quantity);
        TextView priceText = (TextView) view.findViewById(R.id.price);
        TextView timeText = (TextView) view.findViewById(R.id.createtime);
        final ImageView avatarImg = (ImageView) view.findViewById(R.id.image_avatar);
        albumsImg = (ImageView) view.findViewById(R.id.image_albums);
        titleText.setText(goods.getTitle());
        nameText.setText(goods.getPublishers().getName());
        levelText.setText("LV " + JudgeLevel.judege(goods.getPublishers().getXp()));
//        saleText.setText();
        contentText.setText(goods.getContent());
        quantityText.setText("数量 " + goods.getQuantity());
        priceText.setText("价格 " + goods.getPrice());
        timeText.setText(DateToString.getStringDateMMDD(goods.getCreateDate()));

        String avatarUrl = Server.serverAddress + goods.getPublishers().getAvatar();
        final String albumsUrl = Server.serverAddress + goods.getAlbums();

        Picasso.with(context).load(avatarUrl).fit().error(R.drawable.unknow_avatar).into(avatarImg);


        if (width == 0 || height == 0) {
            ViewTreeObserver viewTreeObserver = albumsImg.getViewTreeObserver();
            viewTreeObserver
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @SuppressWarnings("deprecation")
                        @Override
                        public void onGlobalLayout() {
                            albumsImg.getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);
                            width = PxtDipTransform.px2dip(context, albumsImg.getWidth());
                            height = PxtDipTransform.px2dip(context, albumsImg.getHeight());
                            System.out.println("width:"+width+"   height:"+height);
                            Picasso.with(context).load(albumsUrl).resize(width, height).centerCrop().into(albumsImg);
                        }
                    });
        } else {
            Picasso.with(context).load(albumsUrl).resize(width, height).centerCrop().into(albumsImg);
        }
        return view;
    }


}
