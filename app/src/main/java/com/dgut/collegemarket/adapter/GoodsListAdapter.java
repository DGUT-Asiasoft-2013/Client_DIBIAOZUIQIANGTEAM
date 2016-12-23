package com.dgut.collegemarket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Goods;
import com.dgut.collegemarket.util.DateToString;
import com.dgut.collegemarket.util.JudgeLevel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator on 2016/12/21.
 */

public class GoodsListAdapter extends BaseAdapter {

    Context context;
    List<Goods> mGoods;

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
        ImageView avatarImg = (ImageView) view.findViewById(R.id.image_avatar);
        ImageView albumsImg = (ImageView) view.findViewById(R.id.image_albums);
        titleText.setText(goods.getTitle());
        nameText.setText(goods.getPublishers().getName());
        levelText.setText("LV " + JudgeLevel.judege(goods.getPublishers().getXp()));
//        saleText.setText();
        contentText.setText(goods.getContent());
        quantityText.setText("数量 " + goods.getQuantity());
        priceText.setText("价格 " + goods.getPrice());
        timeText.setText(DateToString.getStringDate(goods.getCreateDate()));

//        String avatarUrl = Server.serverAddress_wuzeen + goods.getPublishers().getAvatar();
//        String albumsUrl = Server.serverAddress_wuzeen + goods.getAlbums();
        String avatarUrl = Server.serverAddress_shenjingrong + goods.getPublishers().getAvatar();
        String albumsUrl = Server.serverAddress_shenjingrong + goods.getAlbums();


        Picasso.with(context).load(avatarUrl).fit().error(R.drawable.unknow_avatar) .into(avatarImg)   ;


        Picasso.with(context).load(albumsUrl).resize(300,200).centerCrop().into(albumsImg);

        return view;
    }


}
