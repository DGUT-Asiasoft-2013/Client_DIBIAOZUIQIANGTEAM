package com.dgut.collegemarket.fragment.InputCell;

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

    public void setEidtError(String error) {
        edit.setError(error);

    }

    public void setLayoutError(String error) {

        textInputLayout.setError(error);

    }
    public void setBackground(int color) {
        textInputLayout.setBackgroundColor(color);

    }

    public void setLinesAndLength(int num,int length) {
        edit.setLines(num);

        edit .setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});

    }

    @Override
    public void setLableText(String labletext) {
        edit.setText(labletext);
    }

    public void setHintText(String hintText) {
        textInputLayout.setHint(hintText);
//        edit.setTextColor(Color.parseColor("#FFFFFF"));
    }

    @Override
    public String getText() {
        textInputLayout.setErrorEnabled(false);
        return edit.getText().toString();
    }


    public void setIsPassword(boolean isPassword) {
        if (isPassword) {
            edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {

            edit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        }


    }


    @Override
    public void onResume() {
        super.onResume();

    }


}
