package im.sdk.debug.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.dgut.collegemarket.app.MyApplication;

/**
 * Created by ${chenyn} on 16/3/14.
 *
 * @desc :获取内置的图片和mp3文件
 */
public class GetImageAndMusicUtil {
    public static File mFileDirectory;
    private File mFileMp3;
    Context mContext = MyApplication.getInstance();


    public static void getImage(Context mContext) {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Image/");
        if (!file.exists()) {
            file.mkdir();
        }

        try {
            mFileDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/Image/test.png");
            InputStream in = mContext.getAssets().open("ic_launcher.png");
            OutputStream out = null;
            out = new FileOutputStream(mFileDirectory);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveMp3() throws Exception {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/voice/");
        if (!file.exists()) {
            file.mkdir();
        }
        mFileMp3 = new File(Environment.getExternalStorageDirectory().getPath() + "/voice/test.mp3");
        InputStream in = mContext.getAssets().open("test.mp3");
        OutputStream out = new FileOutputStream(mFileMp3);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
