package im.sdk.debug.activity.createmessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.dgut.collegemarket.R;

import im.sdk.debug.activity.TypeActivity;

/**
 * Created by ${chenyn} on 16/4/7.
 *
 * @desc :
 */
public class ShowCustomMessageActivity extends Activity {

    private TextView mTv_getCustomMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent.getFlags() == 2) {
            String stringName = intent.getStringExtra(TypeActivity.CUSTOM_FROM_NAME);
            mTv_getCustomMap.append("信息来自 ：" + stringName + "\n");
            String stringCustom = intent.getStringExtra(TypeActivity.CUSTOM_MESSAGE_STRING);
            mTv_getCustomMap.append("CustomString消息 ：" + stringCustom + "\n");
        }
    }

    private void initView() {
        setContentView(R.layout.im_activity_get_custom_map);
        mTv_getCustomMap = (TextView) findViewById(R.id.tv_get_custom_map);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
