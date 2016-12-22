package com.dgut.collegemarket.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.fragment.pages.GoodsListFragment;
import com.dgut.collegemarket.fragment.pages.MyProfileFragment;
import com.dgut.collegemarket.fragment.pages.PostListFragment;
import com.dgut.collegemarket.fragment.pages.OrderListFragment;
import com.dgut.collegemarket.fragment.widgets.MainTabbarFragment;


public class MainActivity extends Activity {


    MainTabbarFragment tabbarFragment;
    GoodsListFragment contentFeedList;
    PostListFragment contentNoteList;
    OrderListFragment contentSearchPage;
    MyProfileFragment contentMyProfile;
    int currentId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabbarFragment = (MainTabbarFragment) getFragmentManager().findFragmentById(R.id.tabbar);
        tabbarFragment.setOnTabSelectedListener(new MainTabbarFragment.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int index) {
                changeContentPageFragment(index);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(currentId < 0) {
            tabbarFragment.setSelectTab(0);
        }

    }



    private void changeContentPageFragment(int index) {
        FragmentTransaction transition = getFragmentManager().beginTransaction();
        Fragment newFrag = null;
        switch (index) {
            case 0:
                currentId = 0;
                if (contentFeedList == null) {
                    contentFeedList = new GoodsListFragment();
                    transition.add(R.id.page_content, contentFeedList);

                }
                newFrag = contentFeedList;
                break;
            case 1:
                if (contentNoteList == null) {
                    contentNoteList = new PostListFragment();
                    transition.add(R.id.page_content, contentNoteList);
                }
                newFrag = contentNoteList;
                break;
            case 2:
                if (contentSearchPage == null) {
                    contentSearchPage = new OrderListFragment();
                    transition.add(R.id.page_content, contentSearchPage);
                }
                newFrag = contentSearchPage;
                break;
            case 3:
                if (contentMyProfile == null) {
                    contentMyProfile = new MyProfileFragment();
                    transition.add(R.id.page_content, contentMyProfile);
                }
                newFrag = contentMyProfile;
                break;

            default:
                break;
        }
        if (newFrag == null) return;

        hideFragment(transition);
        transition.show(newFrag).commit();

    }


    //隐藏所有的fragment
    private void hideFragment(FragmentTransaction transaction) {
        if (contentFeedList != null) {
            transaction.hide(contentFeedList);
        }
        if (contentNoteList != null) {
            transaction.hide(contentNoteList);
        }
        if (contentSearchPage != null) {
            transaction.hide(contentSearchPage);
        }
        if (contentMyProfile != null) {
            transaction.hide(contentMyProfile);
        }
    }
}
