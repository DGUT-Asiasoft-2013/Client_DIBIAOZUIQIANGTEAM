package im.sdk.debug.activity.conversation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dgut.collegemarket.R;


/**
 * Created by ${chenyn} on 16/3/30.
 *
 * @desc :会话相关主界面
 */
public class ConversationActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

    }

    private void initView() {
        setContentView(R.layout.im_activity_conversation);
        Button bt_getInfo = (Button) findViewById(R.id.bt_get_conversation_info);
        Button bt_setInfo = (Button) findViewById(R.id.bt_get_info);
        Button bt_enterConversation = (Button) findViewById(R.id.bt_enter_conversation);
        Button bt_deleteConversation = (Button) findViewById(R.id.bt_delete_conversation);

        bt_getInfo.setOnClickListener(this);
        bt_setInfo.setOnClickListener(this);
        bt_enterConversation.setOnClickListener(this);
        bt_deleteConversation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_get_conversation_info://获取会话的各种属性
                Intent intentSet = new Intent(getApplicationContext(), GetConversationInfoActivity.class);
                startActivity(intentSet);
                break;
            case R.id.bt_get_info://排序message
                Intent intentGet = new Intent(getApplicationContext(), OrderMessageActivity.class);
                startActivity(intentGet);
                break;
            case R.id.bt_enter_conversation://进入会话不展示通知
                Intent intentEnterSingle = new Intent(getApplicationContext(), IsShowNotifySigActivity.class);
                startActivity(intentEnterSingle);
                break;
            case R.id.bt_delete_conversation://删除会话
                Intent intentDelete = new Intent(getApplicationContext(), DeleteConversationActivity.class);
                startActivity(intentDelete);
                break;
            default:
                break;
        }
    }
}

