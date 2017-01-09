package com.dgut.collegemarket.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.goods.GoodsContentActivity;
import com.dgut.collegemarket.api.entity.Advertisement;
import com.dgut.collegemarket.view.message.AnimatedGifDrawable;
import com.dgut.collegemarket.view.message.AnimatedImageSpan;
import com.dgut.collegemarket.view.message.ChatInfo;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("NewApi")
public class AdvertisementPagerAdapter extends PagerAdapter {
    private List<ImageView> mViews;
    Context context;
    List<Advertisement> advertisementList;

    public AdvertisementPagerAdapter(Context context,List<ImageView> mViews,List<Advertisement> advertisementList){
        this.context = context;
        this.mViews = mViews;
        this.advertisementList = advertisementList;
    }

    @Override
    public int getCount() {
//                return mViews.size();
        //设置成最大，使用户看不到边界
        return mViews.size()==0?0:Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {


        //对ViewPager页号求模取出View列表中要显示的项
        position %= mViews.size();
        if (position<0){
            position = mViews.size()+position;
        }
        final  int i = position;
        ImageView view = mViews.get(position);
        //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        ViewParent vp =view.getParent();
        if (vp!=null){
            ViewGroup parent = (ViewGroup)vp;
            parent.removeView(view);
        }
        container.addView(view);
        //add listeners here if necessary
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itnt = new Intent(context, GoodsContentActivity.class);
                itnt.putExtra("goods",advertisementList.get(i).getGoods());
                context.startActivity(itnt);
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//                container.removeView(mViews.get(position));
    }

}
