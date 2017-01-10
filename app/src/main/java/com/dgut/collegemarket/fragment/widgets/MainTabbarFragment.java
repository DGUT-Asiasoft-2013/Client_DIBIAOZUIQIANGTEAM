package com.dgut.collegemarket.fragment.widgets;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.goods.GoodsAddActivity;
import com.dgut.collegemarket.activity.orders.OrdersContentActivity;
import com.dgut.collegemarket.activity.orders.OrdresCreateActivity;
import com.dgut.collegemarket.activity.posts.PostAddActivity;
import com.dgut.collegemarket.fragment.pages.OrderListFragment;
import com.dgut.collegemarket.util.AnimationEffec;


/**
 * Created by Administrator on 2016/12/6.
 */

public class MainTabbarFragment extends Fragment {

    View btnNew, tabFeeds, tabNotes, tabSearch, tabMe;
    View[] tabs;
    ImageView image;
Activity activity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_tabbar, null);
        activity  =getActivity();
        btnNew = view.findViewById(R.id.btn_new);
        tabFeeds = view.findViewById(R.id.tab_feeds);
        tabNotes = view.findViewById(R.id.tab_notes);
        tabSearch = view.findViewById(R.id.tab_search);
        tabMe = view.findViewById(R.id.tab_me);
        image = (ImageView) view.findViewById(R.id.img_add);

        tabs = new View[]{
                tabFeeds, tabNotes, tabSearch, tabMe
        };

        for (final View tab : tabs) {
            tab.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onTabClicked(tab);
                }
            });
        }
        return view;
    }

    public static interface OnTabSelectedListener {
        void onTabSelected(int index);
    }

    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        this.onTabSelectedListener = onTabSelectedListener;
    }

    OnTabSelectedListener onTabSelectedListener;

    public void setSelectTab(int index) {
        onTabClicked(tabs[index]);
    }

    View currentTab;

    void onTabClicked(View tab) {
        int selectedIndex = -1;
        if (currentTab == tab)
            return;
        currentTab = tab;
        for (int i = 0; i < tabs.length; i++) {
            View otherTab = tabs[i];
            if (otherTab == tab) {
                otherTab.setSelected(true);
                selectedIndex = i;

            } else {
                otherTab.setSelected(false);
            }
        }
        if (onTabSelectedListener != null && selectedIndex >= 0) {
            onTabSelectedListener.onTabSelected(selectedIndex);
        }
        if (selectedIndex == 0) {
            btnNew.setEnabled(true);
            AnimationEffec.setScaleAni(image, 0.8f, 1, 500);
            image.setImageResource(R.drawable.sale_pressed);
            btnNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, GoodsAddActivity.class);
                    startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.none);
                }
            });

        } else if (selectedIndex == 1) {
            btnNew.setEnabled(true);
            AnimationEffec.setScaleAni(image, 0.8f, 1, 500);
            image.setImageResource(R.drawable.post_pen);
            btnNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, PostAddActivity.class);
                    startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.none);
                }
            });

        } else if (selectedIndex == 2) {
            btnNew.setEnabled(true);
            AnimationEffec.setScaleAni(image, 0.8f, 1, 500);
            image.setImageResource(R.drawable.tab_orders_receiver);
            btnNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, OrdersContentActivity.class);
                    if (OrderListFragment.getOrdersLastOne() != null) {
                        intent.putExtra("orders_id", OrderListFragment.getOrdersLastOne().getId());
                        startActivity(intent);
                    }else{
                        Toast.makeText(activity,"暂时没有未完成订单",Toast.LENGTH_SHORT).show();
                    }

                }
            });


        } else {
            AnimationEffec.setLightExpendAni(image);
            image.setImageResource(R.drawable.question);
            btnNew.setEnabled(false);
        }

    }
}
