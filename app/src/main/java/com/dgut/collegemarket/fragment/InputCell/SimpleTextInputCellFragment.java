package com.dgut.collegemarket.fragment.InputCell;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dgut.collegemarket.R;

/**
 * Created by Administrator on 2016/12/5.
 */

public class SimpleTextInputCellFragment extends BaseInputCelllFragment {

    TextInputEditText edit;
    TextInputLayout textInputLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inputcell_simpletext, container, false);
        textInputLayout = (TextInputLayout) view.findViewById(R.id.TextWrapper);
        edit = (TextInputEditText) view.findViewById(R.id.edit);

        return view;
    }

    /**
     * 获取焦点
     */
    public void setClickable(){
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        edit.findFocus();
    }
    /**
     * 设置错误提示
     * @param error
     */
    public void setEidtError(String error) {
        edit.setError(error);

    }

    /**
     * 设置错误提示
     * @param error
     */
    public void setLayoutError(String error) {

        textInputLayout.setError(error);

    }

    /**
     * 设置背景颜色
     * @param color
     */
    public void setBackground(int color) {
        textInputLayout.setBackgroundColor(color);

    }

    /**
     * 设置行数和长度
     * @param num
     * @param length
     */
    public void setLinesAndLength(int num,int length) {
        edit.setLines(num);

        edit .setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});

    }

    /**
     * 设置内容
     * @param labletext
     */
    @Override
    public void setLableText(String labletext) {
        edit.setText(labletext);
    }

    /**
     * 设置输入提示
     * @param hintText
     */
    public void setHintText(String hintText) {
        textInputLayout.setHint(hintText);
//        edit.setTextColor(Color.parseColor("#FFFFFF"));
    }

    /**
     * 获取输入内容
     * @return
     */
    @Override
    public String getText() {
        textInputLayout.setErrorEnabled(false);
        return edit.getText().toString();
    }

    /**
     * 设置密码输入格式
     * @param isPassword
     */
    public void setIsPassword(boolean isPassword) {
        if (isPassword) {
            edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {

            edit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        }
    }

    /**
     * 设置输入格式
     * @param inputType
     */
    public void setInputType(int inputType){
        edit.setInputType(inputType);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
