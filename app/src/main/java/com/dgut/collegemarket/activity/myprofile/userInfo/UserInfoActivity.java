package com.dgut.collegemarket.activity.myprofile.userInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dgut.collegemarket.R;
import com.dgut.collegemarket.api.Server;
import com.dgut.collegemarket.api.entity.User;
import com.dgut.collegemarket.fragment.widgets.AvatarView;
import com.dgut.collegemarket.fragment.widgets.InfoListFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/12/21.
 */

public class UserInfoActivity extends Activity{

    User user;
    TextView tvTitle,tvExit;
    AvatarView userAvatar ;
    RelativeLayout rlAvatar,rlUpdatePassword;

    InfoListFragment fragmentUserName = new InfoListFragment();
    InfoListFragment fragmentUserEmail = new InfoListFragment();
    InfoListFragment fragmentUserXp= new InfoListFragment();
    InfoListFragment fragmentUsercoins = new InfoListFragment();
    InfoListFragment fragmentUserCreatedate = new InfoListFragment();

    final int REQUESTCODE_CAMERA = 1;
    final int REQUESTCODE_ALBUM = 2;
    final int REQUESTCODE_CUTTING=3;

    Uri uri;
    String url;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_userinfo);
        user = (User) getIntent().getSerializableExtra("user");
        tvTitle = (TextView) findViewById(R.id.tv_user_title);
        tvExit = (TextView) findViewById(R.id.tv_exit);
        rlUpdatePassword = (RelativeLayout) findViewById(R.id.rl_change_password);
        userAvatar = (AvatarView) findViewById(R.id.av_user_avatar);
        rlAvatar = (RelativeLayout) findViewById(R.id.fragment_avatar);
        fragmentUserName = (InfoListFragment) getFragmentManager().findFragmentById(R.id.fragment_user_name);
        fragmentUserEmail = (InfoListFragment) getFragmentManager().findFragmentById(R.id.fragment_user_email);
        fragmentUserXp = (InfoListFragment) getFragmentManager().findFragmentById(R.id.fragment_user_xp);
        fragmentUsercoins = (InfoListFragment) getFragmentManager().findFragmentById(R.id.fragment_user_coins);
        fragmentUserCreatedate = (InfoListFragment) getFragmentManager().findFragmentById(R.id.fragment_user_createdate);


        tvTitle.setText("个人信息");
        tvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rlAvatar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onImageViewClicked();
            }
        });

        rlUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itnt = new Intent(UserInfoActivity.this,UpdatePasswordActivity.class);
                startActivity(itnt);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        userAvatar.load(user);
        fragmentUserName.setTvUserAttribute("昵称");
        fragmentUserName.setTvUserContent(user.getName());
        fragmentUserEmail.setTvUserAttribute("邮箱");
        fragmentUserEmail.setTvUserContent(user.getEmail());
        fragmentUserXp.setTvUserAttribute("总经验");
        fragmentUserXp.setTvUserContent(user.getXp()+"");
        fragmentUsercoins.setTvUserAttribute("金币");
        fragmentUsercoins.setTvUserContent(user.getCoin()+"");
        fragmentUserCreatedate.setTvUserAttribute("创建时间");
        fragmentUserCreatedate.setTvUserContent(DateFormat.format("yyyy-MM-dd hh:ss",user.getCreateDate()).toString());
    }

    void onImageViewClicked() {
        String[] items = {
                "拍照",
                "相册"
        };
        new AlertDialog.Builder(UserInfoActivity.this).setTitle("选择照片")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                takePhoto();
                                break;
                            case 1:
                                pickFromAlbum();
                                break;
                            default:
                        }
                    }
                }).setNegativeButton("取消", null).show();

    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        uri = Uri.fromFile(getNewFile());
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUESTCODE_CAMERA);
    }

    private void pickFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUESTCODE_ALBUM);
    }

    Bitmap bitmap;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == REQUESTCODE_CAMERA) {
            bitmap = (Bitmap) data.getExtras().get("data");
            updateAvatar();
//            startPhotoZoom(data.getData());
//            startPhotoZoom(uri);
        } else if (requestCode == REQUESTCODE_ALBUM) {
//            startPhotoZoom(data.getData());
            try {
                // 读取uri所在的图片
                bitmap = MediaStore.Images.Media.getBitmap(UserInfoActivity.this.getContentResolver(), data.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateAvatar();
        }else if(requestCode == REQUESTCODE_CUTTING){
//            try {
//                // 读取uri所在的图片
//                bitmap = MediaStore.Images.Media.getBitmap(UserInfoActivity.this.getContentResolver(), data.getData());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            updateAvatar();
        }
    }
    /**
     * 裁剪图片方法实现
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent();
        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", true);
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }


    public byte[] getPngData() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bitmap!=null)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        else {
            return null;
        }
        byte[] datas = baos.toByteArray();

        return datas;
    }

    public File getNewFile()  {
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return null;
        }
        String sdcardPath=Environment.getExternalStorageDirectory().getPath();
        File imgPath=new File(sdcardPath+"/img/");
        if(!imgPath.exists()){
            imgPath.mkdirs();
        }
        File userImgFile=new File(imgPath.getPath()+"/"+user.getId()+".png");
        if(!userImgFile.exists()){
            try {
                userImgFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return userImgFile;
    }
    /**
     * 上传头像
     */
    public void updateAvatar(){
        OkHttpClient client = Server.getSharedClient();
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if(getPngData()!=null){
            multipartBuilder.addFormDataPart("avatar", "avatarName"
                    , RequestBody
                            .create(
                                    MediaType.parse("image/png")
                                    , getPngData()));
        }
        final Request request = Server.requestBuilderWithApi("user/update/avatar")
                .post(multipartBuilder.build())
                .build();

        final ProgressDialog progressDialog = new ProgressDialog(UserInfoActivity.this);
        progressDialog.setMessage("上传中");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(UserInfoActivity.this)
                                .setTitle("上传失败")
                                .setMessage("原因：" + e.getLocalizedMessage())
                                .setCancelable(true)
                                .setNegativeButton("好", null)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    final String result = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UserInfoActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                            userAvatar.load(user);
                        }
                    });
                }
            }
        });
    }
}
