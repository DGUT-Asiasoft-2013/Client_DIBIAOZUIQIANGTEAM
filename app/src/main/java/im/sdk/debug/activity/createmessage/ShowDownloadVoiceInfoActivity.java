package im.sdk.debug.activity.createmessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.dgut.collegemarket.R;

import im.sdk.debug.activity.TypeActivity;

/**
 * Created by ${chenyn} on 16/4/10.
 *
 * @desc :
 * @parame :
 * @return :
 */
public class ShowDownloadVoiceInfoActivity extends Activity {
    private TextView mTv_showDownloadInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra(TypeActivity.DOWNLOAD_INFO);
        mTv_showDownloadInfo.append("down load info :" + "\n" + stringExtra);
    }

    private void initView() {
        setContentView(R.layout.im_activity_show_download_info);

        mTv_showDownloadInfo = (TextView) findViewById(R.id.tv_show_download_info);
    }
}
