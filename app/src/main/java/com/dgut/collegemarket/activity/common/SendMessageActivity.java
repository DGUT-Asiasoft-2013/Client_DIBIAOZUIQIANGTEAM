package com.dgut.collegemarket.activity.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.adapter.ChatLVAdapter;
import com.dgut.collegemarket.adapter.FaceGVAdapter;
import com.dgut.collegemarket.adapter.FaceVPAdapter;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.util.CreateSigMsg;
import com.dgut.collegemarket.util.DateToString;
import com.dgut.collegemarket.view.message.ChatInfo;
import com.dgut.collegemarket.view.message.DropdownListView;
import com.dgut.collegemarket.view.message.DropdownListView.OnRefreshListenerHeader;
import com.dgut.collegemarket.view.message.MyEditText;


import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import im.sdk.debug.activity.createmessage.ShowDownloadVoiceInfoActivity;
import im.sdk.debug.activity.imagecontent.ShowDownloadPathActivity;


//私信界面—发送私信
public class SendMessageActivity extends Activity implements View.OnClickListener, OnRefreshListenerHeader {
    private static int RESULT_LOAD_IMAGE = 1;
    private String mPicturePath;


    UserInfo mUserInfo;
    String account;
    private TextView headerText;
    private ViewPager mViewPager;
    private LinearLayout mDotsLayout;
    private MyEditText input;
    private Button send;
    private DropdownListView mListView;
    private ChatLVAdapter mLvAdapter;

    private LinearLayout chat_face_container;
    private ImageView image_face, picture_face;//表情图标
    // 7列3行
    private int columns = 6;
    private int rows = 4;
    private List<View> views = new ArrayList<View>();
    private List<String> staticFacesList;
    private LinkedList<ChatInfo> infos = new LinkedList<ChatInfo>();
    private SimpleDateFormat sd;

    private String inputMessage = "";//模拟回复

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        account = (String) getIntent().getExtras().get("account");
        setContentView(R.layout.activity_send_message);
        JMessageClient.enterSingleConversation(account, null);

        initStaticFaces();
        initViews();
        loadConversation();
    }

    private void loadConversation() {
        Conversation conversation = getConversation();
        if (conversation == null) return;
        getAllMessage(conversation);

    }

    @SuppressLint("SimpleDateFormat")
    private void initViews() {
        mListView = (DropdownListView) findViewById(R.id.message_chat_listview);

        headerText = (TextView) findViewById(R.id.header);
        JMessageClient.getUserInfo(account, new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, final UserInfo userInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        headerText.setText("与 " + userInfo.getNickname() + " 对话中");
                    }
                });
            }
        });

        sd = new SimpleDateFormat("MM-dd HH:mm");

        mLvAdapter = new ChatLVAdapter(this, infos);
        mListView.setAdapter(mLvAdapter);
        //表情图标
        image_face = (ImageView) findViewById(R.id.image_face);
        //表情布局
        chat_face_container = (LinearLayout) findViewById(R.id.chat_face_container);
        mViewPager = (ViewPager) findViewById(R.id.face_viewpager);
        mViewPager.setOnPageChangeListener(new PageChange());
        //表情下小圆点
        mDotsLayout = (LinearLayout) findViewById(R.id.face_dots_container);
        input = (MyEditText) findViewById(R.id.input_sms);
        input.setOnClickListener(this);
        send = (Button) findViewById(R.id.send_sms);
        InitViewPager();
        //表情按钮
        image_face.setOnClickListener(this);
        // 发送
        send.setOnClickListener(this);

        picture_face = (ImageView) findViewById(R.id.picture_input);
        picture_face.setOnClickListener(this);

        mListView.setOnRefreshListenerHead(this);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    if (chat_face_container.getVisibility() == View.VISIBLE) {
                        chat_face_container.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });
    }


    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.input_sms://输入框
                if (chat_face_container.getVisibility() == View.VISIBLE) {
                    chat_face_container.setVisibility(View.GONE);
                }
                break;
            case R.id.image_face://表情
                hideSoftInputView();//隐藏软键盘
                if (chat_face_container.getVisibility() == View.GONE) {
                    chat_face_container.setVisibility(View.VISIBLE);
                } else {
                    chat_face_container.setVisibility(View.GONE);
                }
                break;
            case R.id.picture_input://图片
                hideSoftInputView();//隐藏软键盘
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
                break;
            case R.id.send_sms://发送

                inputMessage = input.getText().toString();
                if (!inputMessage.equals("")) {

                    infos.add(getChatInfoTo(inputMessage, 0));
                    mLvAdapter.notifyDataSetChanged();
                    mListView.smoothScrollToPosition(mLvAdapter.getCount() - 1);//移动到尾部


                    CreateSigMsg.context = this;
                    CreateSigMsg.CreateSigTextMsg(account, JMessageClient.getMyInfo().getNickname(), inputMessage, null);

                    input.setText("");
                } else {
                    Toast.makeText(SendMessageActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mPicturePath = cursor.getString(columnIndex);

            cursor.close();


            File file = new File(mPicturePath);


            try {
                ImageContent imageContent = new ImageContent(file);
                infos.add(getChatInfoTo(imageContent.getLocalThumbnailPath(), 1));
                mLvAdapter.notifyDataSetChanged();
                mListView.setSelection(ListView.FOCUS_DOWN);//刷新到底部
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            try {
                Message imageMessage = JMessageClient.createSingleImageMessage(account, null, file);
                imageMessage.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
//                            Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                            System.out.println("发送成功");

                        } else {
//                            Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                JMessageClient.sendMessage(imageMessage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    public void onEvent(MessageEvent event) {

        final Message msg = event.getMessage();
        final ChatInfo chatInfo = new ChatInfo();
        MessageContent content = msg.getContent();
        if (content.getContentType() == ContentType.text) {
            TextContent stringExtra = (TextContent) content;
            chatInfo.content = stringExtra.getText();
            chatInfo.type = 0;
        }
        if (content.getContentType() == ContentType.image) {
            final ImageContent imageContent = (ImageContent) msg.getContent();
            imageContent.downloadThumbnailImage(msg, new DownloadCompletionCallback() {
                @Override
                public void onComplete(int i, String s, File file) {
                    if (i == 0) {
                        chatInfo.pictureFromUrl = file.getPath();
                        System.out.println("图片:" + chatInfo.pictureFromUrl);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mLvAdapter.notifyDataSetChanged();
                                mListView.setSelection(ListView.FOCUS_DOWN);//刷新到底部
                            }
                        });
                    }
                }
            });
            chatInfo.type = 1;
        }
        chatInfo.time = sd.format(new Date());
        if (msg.getDirect() == MessageDirect.send) {
            chatInfo.fromOrTo = 1;
        } else {
            chatInfo.fromOrTo = 0;
        }
        infos.add(chatInfo);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLvAdapter.notifyDataSetChanged();
                mListView.smoothScrollToPosition(mLvAdapter.getCount() - 1);//移动到尾部
            }
        });

    }

    /*
     * 初始表情 *
     */
    private void InitViewPager() {
        // 获取页数
        for (int i = 0; i < getPagerCount(); i++) {
            views.add(viewPagerItem(i));
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(16, 16);
            mDotsLayout.addView(dotsItem(i), params);
        }
        FaceVPAdapter mVpAdapter = new FaceVPAdapter(views);
        mViewPager.setAdapter(mVpAdapter);
        mDotsLayout.getChildAt(0).setSelected(true);
    }

    private View viewPagerItem(int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.message_face_gridview, null);//表情布局
        GridView gridview = (GridView) layout.findViewById(R.id.chart_face_gv);
        /**
         * 注：因为每一页末尾都有一个删除图标，所以每一页的实际表情columns *　rows　－　1; 空出最后一个位置给删除图标
         * */
        List<String> subList = new ArrayList<String>();
        subList.addAll(staticFacesList
                .subList(position * (columns * rows - 1),
                        (columns * rows - 1) * (position + 1) > staticFacesList
                                .size() ? staticFacesList.size() : (columns
                                * rows - 1)
                                * (position + 1)));
        /**
         * 末尾添加删除图标
         * */
        subList.add("emotion_del_normal.png");
        FaceGVAdapter mGvAdapter = new FaceGVAdapter(subList, this);
        gridview.setAdapter(mGvAdapter);
        gridview.setNumColumns(columns);
        // 单击表情执行的操作
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String png = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();
                    if (!png.contains("emotion_del_normal")) {// 如果不是删除图标
                        insert(getFace(png));
                    } else {
                        delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return gridview;
    }

    private SpannableStringBuilder getFace(String png) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        try {
            /**
             * 经过测试，虽然这里tempText被替换为png显示，但是但我单击发送按钮时，获取到輸入框的内容是tempText的值而不是png
             * 所以这里对这个tempText值做特殊处理
             * 格式：#[face/png/f_static_000.png]#，以方便判斷當前圖片是哪一個
             * */
            String tempText = "#[" + png + "]#";
            sb.append(tempText);
            sb.setSpan(
                    new ImageSpan(SendMessageActivity.this, BitmapFactory
                            .decodeStream(getAssets().open(png))), sb.length()
                            - tempText.length(), sb.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb;
    }

    /**
     * 向输入框里添加表情
     */
    private void insert(CharSequence text) {
        int iCursorStart = Selection.getSelectionStart((input.getText()));
        int iCursorEnd = Selection.getSelectionEnd((input.getText()));
        if (iCursorStart != iCursorEnd) {
            ((Editable) input.getText()).replace(iCursorStart, iCursorEnd, "");
        }
        int iCursor = Selection.getSelectionEnd((input.getText()));
        ((Editable) input.getText()).insert(iCursor, text);
    }

    /**
     * 删除图标执行事件
     * 注：如果删除的是表情，在删除时实际删除的是tempText即图片占位的字符串，所以必需一次性删除掉tempText，才能将图片删除
     */
    private void delete() {
        if (input.getText().length() != 0) {
            int iCursorEnd = Selection.getSelectionEnd(input.getText());
            int iCursorStart = Selection.getSelectionStart(input.getText());
            if (iCursorEnd > 0) {
                if (iCursorEnd == iCursorStart) {
                    if (isDeletePng(iCursorEnd)) {
                        String st = "#[face/png/f_static_000.png]#";
                        ((Editable) input.getText()).delete(
                                iCursorEnd - st.length(), iCursorEnd);
                    } else {
                        ((Editable) input.getText()).delete(iCursorEnd - 1,
                                iCursorEnd);
                    }
                } else {
                    ((Editable) input.getText()).delete(iCursorStart,
                            iCursorEnd);
                }
            }
        }
    }

    /**
     * 判断即将删除的字符串是否是图片占位字符串tempText 如果是：则讲删除整个tempText
     **/
    private boolean isDeletePng(int cursor) {
        String st = "#[face/png/f_static_000.png]#";
        String content = input.getText().toString().substring(0, cursor);
        if (content.length() >= st.length()) {
            String checkStr = content.substring(content.length() - st.length(),
                    content.length());
            String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(checkStr);
            return m.matches();
        }
        return false;
    }

    private ImageView dotsItem(int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.message_dot_image, null);
        ImageView iv = (ImageView) layout.findViewById(R.id.face_dot);
        iv.setId(position);
        return iv;
    }

    /**
     * 根据表情数量以及GridView设置的行数和列数计算Pager数量
     *
     * @return
     */
    private int getPagerCount() {
        int count = staticFacesList.size();
        return count % (columns * rows - 1) == 0 ? count / (columns * rows - 1)
                : count / (columns * rows - 1) + 1;
    }

    /**
     * 初始化表情列表staticFacesList
     */
    private void initStaticFaces() {
        try {
            staticFacesList = new ArrayList<String>();
            String[] faces = getAssets().list("face/png");
            //将Assets中的表情名称转为字符串一一添加进staticFacesList
            for (int i = 0; i < faces.length; i++) {
                staticFacesList.add(faces[i]);
            }
            //去掉删除图片
            staticFacesList.remove("emotion_del_normal.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 表情页改变时，dots效果也要跟着改变
     */
    class PageChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
                mDotsLayout.getChildAt(i).setSelected(false);
            }
            mDotsLayout.getChildAt(arg0).setSelected(true);
        }

    }

    @Override
    protected void onResume() {
        JMessageClient.registerEventReceiver(this, 5);
        super.onResume();
    }

    @Override
    protected void onPause() {
        JMessageClient.unRegisterEventReceiver(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        JMessageClient.unRegisterEventReceiver(this);
        JMessageClient.exitConversation();
        super.onDestroy();
    }

    /**
     * 发送的信息
     *
     * @param message
     * @return
     */
    private ChatInfo getChatInfoTo(String message, int type) {
        ChatInfo info = new ChatInfo();
        info.type = type;
        if (type == 0) {
            info.content = message;
        } else {
            info.pictureFromUrl = message;
        }
        info.fromOrTo = 1;
        info.time = sd.format(new Date());
        return info;
    }

    /**
     * 接收的信息
     *
     * @param message
     * @return
     */
    private ChatInfo getChatInfoFrom(String message) {
        ChatInfo info = new ChatInfo();
        info.content = message;
        info.fromOrTo = 0;
        info.time = sd.format(new Date());
        return info;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    mListView.onRefreshCompleteHeader();
                    break;
            }
        }
    };


    @Override
    public void onRefresh() {
        new Thread() {
            @Override
            public void run() {
                loadConversation();
                android.os.Message msg = mHandler.obtainMessage(0);
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private Conversation getConversation() {


        Conversation conversation;
        if (!TextUtils.isEmpty(account)) {
            conversation = JMessageClient.getSingleConversation(account, null);
            if (conversation != null) {
                mUserInfo = (UserInfo) conversation.getTargetInfo();
            } else {
//                Toast.makeText(SendMessageActivity.this, "会话不存在", Toast.LENGTH_SHORT).show();
                return null;
            }
        } else {
//            Toast.makeText(SendMessageActivity.this, "需要userName", Toast.LENGTH_SHORT).show();
            return null;
        }
        return conversation;
    }

    private void getAllMessage(Conversation conversation) {
        List<cn.jpush.im.android.api.model.Message> allMessage = conversation.getAllMessage();
        infos.clear();
        for (cn.jpush.im.android.api.model.Message msg : allMessage) {
            final ChatInfo chatInfo = new ChatInfo();
            MessageContent content = msg.getContent();
            if (content.getContentType() == ContentType.text) {
                TextContent stringExtra = (TextContent) content;
                chatInfo.content = stringExtra.getText();
                chatInfo.type = 0;
            }
            if (content.getContentType() == ContentType.image) {
                final ImageContent imageContent = (ImageContent) msg.getContent();
                imageContent.downloadThumbnailImage(msg, new DownloadCompletionCallback() {
                    @Override
                    public void onComplete(int i, String s, File file) {
                        if (i == 0) {
                            chatInfo.pictureFromUrl = file.getPath();
                            System.out.println("图片:" + chatInfo.pictureFromUrl);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mLvAdapter.notifyDataSetChanged();
                                    mListView.smoothScrollToPosition(mLvAdapter.getCount() - 1);//移动到尾部
                                }
                            });
                        }
                    }
                });
                chatInfo.type = 1;
            }

            chatInfo.time = DateToString.timeStamp2Date(String.valueOf(msg.getCreateTime()),"MM-dd HH:mm");
            if (msg.getDirect() == MessageDirect.send) {
                chatInfo.fromOrTo = 1;
            } else {
                chatInfo.fromOrTo = 0;
            }
            infos.add(chatInfo);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLvAdapter.notifyDataSetChanged();
                mListView.smoothScrollToPosition(mLvAdapter.getCount() - 1);//移动到尾部
            }
        });


    }
}




