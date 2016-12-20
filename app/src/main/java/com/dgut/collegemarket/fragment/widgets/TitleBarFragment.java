package com.dgut.collegemarket.fragment.widgets;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgut.collegemarket.R;

/**
 * Created by Administrator on 2016/12/20.
 */

public class TitleBarFragment extends Fragment{

    TextView tvText;
    ImageView ivImg;
    Button btnTitle;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_titlebar,null);
        tvText = (TextView) view.findViewById(R.id.tv_title_text);
        ivImg = (ImageView) view.findViewById(R.id.iv_title_img);
        btnTitle = (Button) view.findViewById(R.id.btn_title);
        btnTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBtnClick();
            }
        });
        return view;
    }

    /**
     * 设置Title的内容
     * @param text
     */
    public void setTitleText(String text){
        tvText.setText(text);
    }

    /**
     * 设置按钮的内容
     * @param text
     */
    public void setTitleBtnText(String text){
        btnTitle.setText(text);
    }

    /**
     * 隐藏按钮
     */
    public void hideTitleBtn(){
        btnTitle.setVisibility(View.GONE);
    }
    interface OnTitleBtnClickListener{
        void onclick();
    }

    OnTitleBtnClickListener onTitleBtnClickListener ;

    public void setOnTitleClickListener( OnTitleBtnClickListener onTitleBtnClickListener){
        this.onTitleBtnClickListener = onTitleBtnClickListener;
    }

    /**
     * 按钮的监听器
     */
    private void onBtnClick(){
        onTitleBtnClickListener.onclick();
    }
}
