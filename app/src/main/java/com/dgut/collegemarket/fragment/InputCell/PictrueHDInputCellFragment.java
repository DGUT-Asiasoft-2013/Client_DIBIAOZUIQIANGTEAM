package com.dgut.collegemarket.fragment.InputCell;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgut.collegemarket.R;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by Administrator on 2016/12/5.
 */

public class PictrueHDInputCellFragment extends Fragment {

    final int REQUESTCODE_CAMERA = 1;
    final int REQUESTCODE_ALBUM = 2;



    ImageView imageView;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inputcell_pictrue_hd, container, false);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImageViewClicked();
            }
        });

        return view;
    }

    void onImageViewClicked() {
        String[] items = {
                "拍照",
                "相册"
        };
        new AlertDialog.Builder(getActivity()).setTitle("添加图片")
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
    protected static final String SAVED_IMAGE_DIR_PATH = "/sdcard/DCIM/";
    String out_file_path;
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File dir = new File(SAVED_IMAGE_DIR_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        out_file_path = SAVED_IMAGE_DIR_PATH + System.currentTimeMillis() + ".jpg";
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(out_file_path)));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
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

             bitmap =  BitmapFactory.decodeFile(out_file_path);
            imageView.setImageBitmap(bitmap);



        } else if (requestCode == REQUESTCODE_ALBUM) {
            try {
                // 读取uri所在的图片
                 bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    public byte[] getPngData() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bitmap!=null)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        else {
            return null;
        }
        byte[] datas = baos.toByteArray();
        System.out.println("datas:"+datas.length);
        return datas;
    }


}
