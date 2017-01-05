package com.dgut.collegemarket.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.activity.common.SendMessageActivity;
import com.dgut.collegemarket.activity.orders.OrdersContentActivity;
import com.dgut.collegemarket.app.CurrentUserInfo;
import com.dgut.collegemarket.app.MsgType;
import com.dgut.collegemarket.fragment.pages.GoodsListFragment;
import com.dgut.collegemarket.fragment.pages.MyProfileFragment;
import com.dgut.collegemarket.fragment.pages.PostListFragment;
import com.dgut.collegemarket.fragment.pages.OrderListFragment;
import com.dgut.collegemarket.fragment.widgets.MainTabbarFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.LocationContent;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.NotificationClickEvent;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import im.sdk.debug.activity.createmessage.CreateSigTextMessageActivity;
import im.sdk.debug.activity.createmessage.ShowCustomMessageActivity;
import im.sdk.debug.activity.createmessage.ShowDownloadVoiceInfoActivity;
import im.sdk.debug.activity.createmessage.ShowMessageActivity;
import im.sdk.debug.activity.imagecontent.ShowDownloadPathActivity;
import im.sdk.debug.activity.setting.ShowLogoutReasonActivity;


public class MainActivity extends Activity {
    public static final String SET_DOWNLOAD_PROGRESS = "set_download_progress";
    public static final String IS_DOWNLOAD_PROGRESS_EXISTS = "is_download_progress_exists";
    public static final String CUSTOM_MESSAGE_STRING = "custom_message_string";
    public static final String CUSTOM_FROM_NAME = "custom_from_name";
    public static final String DOWNLOAD_INFO = "download_info";
    public static final String DOWNLOAD_ORIGIN_IMAGE = "download_origin_image";
    public static final String DOWNLOAD_THUMBNAIL_IMAGE = "download_thumbnail_image";
    public static final String IS_UPLOAD = "is_upload";
    public static final String LOGOUT_REASON = "logout_reason";


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
        JMessageClient.registerEventReceiver(this,3);

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
        if (!CurrentUserInfo.online) {
            finish();
        }
        if (currentId < 0) {
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

    public void onEvent(NotificationClickEvent event) {
        System.out.println("NotificationClickEvent");
        Message msg = event.getMessage();

        final Intent notificationIntent = new Intent(getApplicationContext(), SendMessageActivity.class);
        MessageContent content = msg.getContent();
        switch (msg.getContentType()) {
            case text:

                Map stringExtras = content.getStringExtras();
                System.out.println("msg_type:"+stringExtras.get("msg_type"));
                if (stringExtras.get("msg_type").equals(MsgType.MSG_ORDERS)) {
                    Intent intent = new Intent(getApplicationContext(), OrdersContentActivity.class);
                    intent.putExtra("orders_id", Integer.valueOf((String) stringExtras.get("orders_id")));
                    startActivity(intent);
                }
                break;
            case image:

                notificationIntent.putExtra("user_id",msg.getFromUser().getUserName());
                startActivity(notificationIntent);
//                ImageContent imageContent = (ImageContent) content;
//                imageContent.downloadOriginImage(msg, new DownloadCompletionCallback() {
//                    @Override
//                    public void onComplete(int i, String s, File file) {
//                        if (i == 0) {
//                            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
//                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
//                            byte[] bitmapByte = bos.toByteArray();
//                            notificationIntent.setFlags(2);
//                            notificationIntent.putExtra("bitmap", bitmapByte);
//                            startActivity(notificationIntent);
//                        }
//                    }
//                });
//                final List<String> list = new ArrayList<String>();
//                msg.setOnContentDownloadProgressCallback(new ProgressUpdateCallback() {
//                    @Override
//                    public void onProgressUpdate(double v) {
//                        String progressStr = (int) (v * 100) + "%";
//                        list.add(progressStr);
//                        notificationIntent.putStringArrayListExtra(SET_DOWNLOAD_PROGRESS, (ArrayList<String>) list);
//                    }
//                });

//                boolean callbackExists = msg.isContentDownloadProgressCallbackExists();
//                notificationIntent.putExtra(IS_DOWNLOAD_PROGRESS_EXISTS, callbackExists + "");
                break;


            default:
                break;
        }
    }

    public void onEvent(MessageEvent event) {
        System.out.println("MessageEvent");
        final Message msg = event.getMessage();
        switch (msg.getContentType()) {
            case text:
                //处理文字消息
                TextContent textContent = (TextContent) msg.getContent();
                textContent.getText();



                break;
            case custom:
                final ConversationType targetType = event.getMessage().getTargetType();
                CustomContent customContent = (CustomContent) msg.getContent();
                final Map allStringValues = customContent.getAllStringValues();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (targetType.equals(ConversationType.single)) {
                            if (Integer.valueOf((String) allStringValues.get("orders_state")) == 1) {
                                UserInfo fromUser = msg.getFromUser();
                                Toast.makeText(getApplicationContext(), "您有一份来自" + fromUser.getUserName() + "的新订单！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                break;

            case image:

                final Intent intentImage = new Intent(getApplicationContext(), ShowDownloadPathActivity.class);
                final ImageContent imageContent = (ImageContent) msg.getContent();

                /**=================     下载图片信息中的缩略图    =================*/
                imageContent.downloadThumbnailImage(msg, new DownloadCompletionCallback() {
                    @Override
                    public void onComplete(int i, String s, File file) {
                        if (i == 0) {
                            Toast.makeText(getApplicationContext(), "下载缩略图成功", Toast.LENGTH_SHORT).show();
                            intentImage.putExtra(DOWNLOAD_THUMBNAIL_IMAGE, file.getPath());
                        } else {
                            Toast.makeText(getApplicationContext(), "下载原图失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                /**=================     下载图片消息中的原图    =================*/
                imageContent.downloadOriginImage(msg, new DownloadCompletionCallback() {
                    @Override
                    public void onComplete(int i, String s, File file) {
                        if (i == 0) {
                            Toast.makeText(getApplicationContext(), "下载原图成功", Toast.LENGTH_SHORT).show();
                            intentImage.putExtra(IS_UPLOAD, imageContent.isFileUploaded() + "");
                            intentImage.putExtra(DOWNLOAD_ORIGIN_IMAGE, file.getPath());
                            startActivity(intentImage);
                        } else {
                            Toast.makeText(getApplicationContext(), "下载原图失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            default:
                break;
        }
    }


    public void onEvent(LoginStateChangeEvent event) {
        LoginStateChangeEvent.Reason reason = event.getReason();
        UserInfo myInfo = event.getMyInfo();
        Intent intent = new Intent(getApplicationContext(), ShowLogoutReasonActivity.class);
        intent.putExtra(LOGOUT_REASON, "reason = " + reason + "\n" + "logout user name = " + myInfo.getUserName());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
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
