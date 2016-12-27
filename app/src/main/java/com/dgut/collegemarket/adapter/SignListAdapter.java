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
import com.dgut.collegemarket.api.entity.Post;
import com.dgut.collegemarket.api.entity.Sign;
import com.dgut.collegemarket.util.DateToString;
import com.dgut.collegemarket.util.JudgeLevel;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.List;

/**
 * Created by Administrator on 2016/12/21.
 */

public class SignListAdapter extends BaseAdapter {

    Context context;
    List<Sign> signs;

    public SignListAdapter(Context context, List<Sign> signs) {
        this.context = context;
        this.signs = signs;
    }

    @Override
    public int getCount() {
        return signs == null ? 0 : signs.size();
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

        View view=null;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.list_sign_item, null);
        } else {
            view = convertView;
        }
        TextView tvLevel = (TextView) view.findViewById(R.id.tv_sign_level);
        TextView tvName = (TextView) view.findViewById(R.id.tv_sign_name);
        TextView tvDate = (TextView) view.findViewById(R.id.tv_sign_date);
        TextView tvXp = (TextView) view.findViewById(R.id.tv_sign_xp);

        Sign sign = signs.get(i);
        tvLevel.setText("Lv"+ JudgeLevel.judege(sign.getUser().getXp()));
        tvName.setText(sign.getUser().getName());
        tvXp.setText(sign.getXp());
        tvDate.setText(DateToString.getStringDateYYMMDD(sign.getCreateDate()));
        return view;
    }

}
