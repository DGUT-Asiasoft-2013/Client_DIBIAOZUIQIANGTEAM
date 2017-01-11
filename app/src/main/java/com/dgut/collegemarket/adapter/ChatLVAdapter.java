package com.dgut.collegemarket.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dgut.collegemarket.R;
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

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;

@SuppressLint("NewApi")
public class ChatLVAdapter extends BaseAdapter {
    private Context mContext;
    private List<ChatInfo> list;
    /**
     * 弹出的更多选择框
     */
    private PopupWindow popupWindow;

    /**
     * 复制，删除
     */
    private TextView copy, delete;

    private LayoutInflater inflater;
    /**
     * 执行动画的时间
     */
    protected long mAnimationTime = 150;

    ImageView fromIcon, toIcon, fromPicture, toPicture;
    TextView fromContent, toContent, time;
    ViewGroup fromContainer, toContainer;
    Bitmap toBitmap, fromBitmap;

    public ChatLVAdapter(Context mContext, List<ChatInfo> list) {
        super();
        this.mContext = mContext;
        this.list = list;
        inflater = LayoutInflater.from(mContext);
        initPopWindow();
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.message_lv_item, null);

        } else {
            view = convertView;
        }

        fromContainer = (ViewGroup) view.findViewById(R.id.chart_from_container);
        toContainer = (ViewGroup) view.findViewById(R.id.chart_to_container);
        fromContent = (TextView) view.findViewById(R.id.chatfrom_content);
        toContent = (TextView) view.findViewById(R.id.chatto_content);
        time = (TextView) view.findViewById(R.id.chat_time);
        fromIcon = (ImageView) view.findViewById(R.id.chatfrom_icon);
        toIcon = (ImageView) view.findViewById(R.id.chatto_icon);
        fromPicture = (ImageView) view.findViewById(R.id.chatfrom_img);
        toPicture = (ImageView) view.findViewById(R.id.chatto_img);


//        if (position == 0) {
//            JMessageClient.getMyInfo().getAvatarBitmap(new GetAvatarBitmapCallback() {
//                @Override
//                public void gotResult(int i, String s, Bitmap bitmap) {
//                    fromBitmap = bitmap;
//                    fromIcon.setImageBitmap(bitmap);
//                }
//            });
//            JMessageClient.getUserInfo(list.get(position).account, new GetUserInfoCallback() {
//                @Override
//                public void gotResult(int i, String s, UserInfo userInfo) {
//                    userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
//                        @Override
//                        public void gotResult(int i, String s, Bitmap bitmap) {
//                            toBitmap = bitmap;
//                            toIcon.setImageBitmap(bitmap);
//                        }
//                    });
//                }
//            });
//        }else{
//            fromIcon.setImageBitmap(fromBitmap);
//            toIcon.setImageBitmap(toBitmap);
//        }


        if (list.get(position).fromOrTo == 0) {
            // 收到消息 from显示
            toContainer.setVisibility(View.GONE);
            fromContainer.setVisibility(View.VISIBLE);

            // 对内容做处理
            if (list.get(position).type == 0)//如果是文字
            {
                fromContent.setVisibility(View.VISIBLE);
                fromPicture.setVisibility(View.GONE);
                SpannableStringBuilder sb = handler(fromContent,
                        list.get(position).content);
                fromContent.setText(sb);

            }
            if (list.get(position).type == 1)//如果是图片
            {
                fromContent.setVisibility(View.GONE);
                fromPicture.setVisibility(View.VISIBLE);
                File file = new File(list.get(position).pictureFromUrl);

                Picasso.with(mContext).load(file).error(R.drawable.question).into(fromPicture);
            }
            time.setText(list.get(position).time);


        } else {
            // 发送消息 to显示
            toContainer.setVisibility(View.VISIBLE);
            fromContainer.setVisibility(View.GONE);

            // 对内容做处理
            if (list.get(position).type == 0)//如果是文字
            {
                toContent.setVisibility(View.VISIBLE);
                toPicture.setVisibility(View.GONE);
                SpannableStringBuilder sb = handler(toContent,
                        list.get(position).content);
                toContent.setText(sb);

            }
            if (list.get(position).type == 1 && list.get(position).pictureFromUrl != null)//如果是图片
            {
                toContent.setVisibility(View.GONE);
                toPicture.setVisibility(View.VISIBLE);
                File file = new File(list.get(position).pictureFromUrl);

                Picasso.with(mContext).load(file).error(R.drawable.question).into(toPicture);


            }
            time.setText(list.get(position).time);

        }
        fromContent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });
        toContent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });

        // 设置+按钮点击效果
        fromContent.setOnLongClickListener(new popAction(convertView,
                position, list.get(position).fromOrTo));
        toContent.setOnLongClickListener(new popAction(convertView,
                position, list.get(position).fromOrTo));
        return view;
    }

    private SpannableStringBuilder handler(final TextView gifTextView, String content) {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();
            try {
                String num = tempText.substring("#[face/png/f_static_".length(), tempText.length() - ".png]#".length());
                String gif = "face/gif/f" + num + ".gif";
                /**
                 * 如果open这里不抛异常说明存在gif，则显示对应的gif
                 * 否则说明gif找不到，则显示png
                 * */
                InputStream is = mContext.getAssets().open(gif);
                sb.setSpan(new AnimatedImageSpan(new AnimatedGifDrawable(is, new AnimatedGifDrawable.UpdateListener() {
                            @Override
                            public void update() {
                                gifTextView.postInvalidate();
                            }
                        })), m.start(), m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                is.close();
            } catch (Exception e) {
                String png = tempText.substring("#[".length(), tempText.length() - "]#".length());
                try {
                    sb.setSpan(new ImageSpan(mContext, BitmapFactory.decodeStream(mContext.getAssets().open(png))), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
        return sb;
    }


    /**
     * 屏蔽listitem的所有事件
     */
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    /**
     * 初始化弹出的pop
     */
    private void initPopWindow() {
        View popView = inflater.inflate(R.layout.message_item_copy_delete_menu,
                null);
        copy = (TextView) popView.findViewById(R.id.chat_copy_menu);
        delete = (TextView) popView.findViewById(R.id.chat_delete_menu);
        popupWindow = new PopupWindow(popView, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        // 设置popwindow出现和消失动画
        // popupWindow.setAnimationStyle(R.style.PopMenuAnimation);
    }

    /**
     * 显示popWindow
     */
    public void showPop(View parent, int x, int y, final View view,
                        final int position, final int fromOrTo) {
        // 设置popwindow显示位置
        popupWindow.showAtLocation(parent, 0, x, y);
        // 获取popwindow焦点
        popupWindow.setFocusable(true);
        // 设置popwindow如果点击外面区域，便关闭。
        popupWindow.setOutsideTouchable(true);
        // 为按钮绑定事件
        // 复制
        copy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                // 获取剪贴板管理服务
                ClipboardManager cm = (ClipboardManager) mContext
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本数据复制到剪贴板
                cm.setText(list.get(position).content);
            }
        });
        // 删除
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                if (fromOrTo == 0) {
                    // from
                    leftRemoveAnimation(view, position);
                } else if (fromOrTo == 1) {
                    // to
                    rightRemoveAnimation(view, position);
                }

                // list.remove(position);
                // notifyDataSetChanged();
            }
        });
        popupWindow.update();
        if (popupWindow.isShowing()) {

        }
    }

    /**
     * 每个ITEM中more按钮对应的点击动作
     */
    public class popAction implements OnLongClickListener {
        int position;
        View view;
        int fromOrTo;

        public popAction(View view, int position, int fromOrTo) {
            this.position = position;
            this.view = view;
            this.fromOrTo = fromOrTo;
        }

        @Override
        public boolean onLongClick(View v) {
            // TODO Auto-generated method stub
            int[] arrayOfInt = new int[2];
            // 获取点击按钮的坐标
            v.getLocationOnScreen(arrayOfInt);
            int x = arrayOfInt[0];
            int y = arrayOfInt[1];
            // System.out.println("x: " + x + " y:" + y + " w: " +
            // v.getMeasuredWidth() + " h: " + v.getMeasuredHeight() );
            showPop(v, x, y, view, position, fromOrTo);
            return true;
        }
    }

    /**
     * item删除动画
     */
    private void rightRemoveAnimation(final View view, final int position) {
        final Animation animation = (Animation) AnimationUtils.loadAnimation(
                mContext, R.anim.chatto_remove_anim);
        animation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                view.setAlpha(0);
                performDismiss(view, position);
                animation.cancel();
            }
        });

        view.startAnimation(animation);
    }

    /**
     * item删除动画
     */
    private void leftRemoveAnimation(final View view, final int position) {
        final Animation animation = (Animation) AnimationUtils.loadAnimation(mContext, R.anim.chatfrom_remove_anim);
        animation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                view.setAlpha(0);
                performDismiss(view, position);
                animation.cancel();
            }
        });

        view.startAnimation(animation);
    }

    /**
     * 在此方法中执行item删除之后，其他的item向上或者向下滚动的动画，并且将position回调到方法onDismiss()中
     *
     * @param dismissView
     * @param dismissPosition
     */
    private void performDismiss(final View dismissView,
                                final int dismissPosition) {
        final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();// 获取item的布局参数
        final int originalHeight = dismissView.getHeight();// item的高度

        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0)
                .setDuration(mAnimationTime);
        animator.start();

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                list.remove(dismissPosition);
                notifyDataSetChanged();
                // 这段代码很重要，因为我们并没有将item从ListView中移除，而是将item的高度设置为0
                // 所以我们在动画执行完毕之后将item设置回来
                ViewHelper.setAlpha(dismissView, 1f);
                ViewHelper.setTranslationX(dismissView, 0);
                ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
                lp.height = originalHeight;
                dismissView.setLayoutParams(lp);
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // 这段代码的效果是ListView删除某item之后，其他的item向上滑动的效果
                lp.height = (Integer) valueAnimator.getAnimatedValue();
                dismissView.setLayoutParams(lp);
            }
        });

    }

}
