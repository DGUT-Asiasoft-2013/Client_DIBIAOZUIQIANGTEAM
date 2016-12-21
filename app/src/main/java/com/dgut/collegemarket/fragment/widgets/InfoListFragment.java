package com.dgut.collegemarket.fragment.widgets;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dgut.collegemarket.R;

/**
 * Created by Administrator on 2016/12/21.
 */

public class InfoListFragment extends Fragment{

    TextView tvUserAttribute,tvUserContent;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_userinfo_list,null);
        tvUserAttribute = (TextView) view.findViewById(R.id.tv_user_attribute);
        tvUserContent = (TextView) view.findViewById(R.id.tv_user_content);
        return view;
    }

    public void setTvUserAttribute(String text){
        tvUserAttribute.setText(text);
    }

    public void setTvUserContent(String text){
        tvUserContent.setText(text);
    }
}
