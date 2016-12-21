package com.dgut.collegemarket.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dgut.collegemarket.api.entity.Goods;

import java.util.List;

/**
 * Created by Administrator on 2016/12/21.
 */

public class GoodsListAdapter extends BaseAdapter{

    Context  context;
    List<Goods> mGoods;
    public GoodsListAdapter(Context  context, List<Goods> mGoods){
        this.context=context;
        this.mGoods=mGoods;
    }

    @Override
    public int getCount() {
        return mGoods==null?0:mGoods.size();
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
    public View getView(int i, View view, ViewGroup viewGroup) {







        return null;
    }
}
