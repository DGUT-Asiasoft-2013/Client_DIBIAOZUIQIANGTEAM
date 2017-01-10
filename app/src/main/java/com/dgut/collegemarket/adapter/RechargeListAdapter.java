package com.dgut.collegemarket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.Goods;
import com.dgut.collegemarket.util.DateToString;
import com.dgut.collegemarket.util.JudgeLevel;
import com.squareup.picasso.Picasso;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.Request;

/**
 * Created by Administrator on 2016/12/21.
 */

public class RechargeListAdapter extends BaseAdapter {

    private Context context;
    private String[] beans;
    ImageView recharge_back;
    // 用于记录每个RadioButton的状态，并保证只可选一个
    HashMap<String, Boolean> states = new HashMap<String, Boolean>();

    class ViewHolder {

        TextView tvName;
        RadioButton rb_state;
    }

    public RechargeListAdapter(Context context, String[] beans) {
        // TODO Auto-generated constructor stub
        this.beans = beans;
        this.context = context;


    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return beans.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return beans[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        // 页面
        ViewHolder holder;
        String bean = beans[position];
        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.list_recharge_item, null);


            holder = new ViewHolder();
            holder.tvName = (TextView) convertView
                    .findViewById(R.id.search_user_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.tvName.setText(bean);

        final RadioButton radio = (RadioButton) convertView.findViewById(R.id.radio_btn);


        holder.rb_state = radio;
        holder.rb_state.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // 重置，确保最多只有一项被选中
                for (String key : states.keySet()) {

                    states.put(key, false);

                }
                states.put(String.valueOf(position), radio.isChecked());
                RechargeListAdapter.this.notifyDataSetChanged();
            }
        });

        boolean res = false;
        if (states.get(String.valueOf(position)) == null
                || states.get(String.valueOf(position)) == false) {
            res = false;
            states.put(String.valueOf(position), false);
        } else
            res = true;

        holder.rb_state.setChecked(res);

        return convertView;
    }

    public int getSelectItem() {

        for (int i = 0; i < states.size(); i++) {
            if (states.get(String.valueOf(i))) {
                return i;
            }
        }

        return 0;
    }

}