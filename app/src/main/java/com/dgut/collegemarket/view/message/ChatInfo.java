package com.dgut.collegemarket.view.message;

import cn.jpush.im.android.api.content.ImageContent;

public class ChatInfo  {


	public int iconFromResId;
	public String iconFromUrl;
	public String pictureFromUrl;
	public String content;
	public String time;
	public int fromOrTo;// 0 是收到的消息，1是发送的消息
	public int type;// 0 是文字，1是图片

	@Override
	public String toString() {
		return "ChatInfoEntity [iconFromResId=" + iconFromResId
				+ ", iconFromUrl=" + iconFromUrl + ", content=" + content
				+ ", time=" + time + ", fromOrTo=" + fromOrTo + "]";
	}
}
