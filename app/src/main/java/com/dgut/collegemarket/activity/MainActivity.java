package com.dgut.collegemarket.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.app.CurrentUserInfo;
import com.dgut.collegemarket.fragment.pages.GoodsListFragment;
import com.dgut.collegemarket.fragment.pages.MyProfileFragment;
import com.dgut.collegemarket.fragment.pages.PostListFragment;
import com.dgut.collegemarket.fragment.pages.OrderListFragment;
import com.dgut.collegemarket.fragment.widgets.MainTabbarFragment;

import java.util.Timer;
import java.util.TimerTask;


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
        if(!CurrentUserInfo.online){
            finish();
        }
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

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            exitBy2Click(); //调用双击退出函数
        }
        return false;
    }
    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
            System.exit(0);
        }
    }
}
