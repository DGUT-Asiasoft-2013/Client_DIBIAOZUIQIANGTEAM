package com.dgut.collegemarket.fragment.InputCell;

import android.app.Fragment;

/**
 * Created by Administrator on 2016/12/5.
 */

public abstract class BaseInputCelllFragment extends Fragment{
    abstract public  void  setLableText(String labletext);
    abstract public  void  setHintText(String hinttext);
    abstract public  String  getText();
}
