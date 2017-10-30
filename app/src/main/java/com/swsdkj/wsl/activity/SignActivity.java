package com.swsdkj.wsl.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sh.shvideolibrary.VideoInputDialog;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.base.BaseActivity;
import com.swsdkj.wsl.config.MyConfig;
import com.swsdkj.wsl.contract.SignContract;
import com.swsdkj.wsl.inter.MyCamerInter;
import com.swsdkj.wsl.presenter.SignPresenterImpl;
import com.swsdkj.wsl.tool.CommonUtil;
import com.swsdkj.wsl.tool.DialogUtil;
import com.swsdkj.wsl.tool.ImageUtil;
import com.swsdkj.wsl.view.MyDialog;
import com.yqritc.scalablevideoview.ScalableVideoView;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import zhangphil.iosdialog.widget.ActionSheetDialog;

/**
 * Created by Administrator on 2017/4/26 0026.
 */

public class SignActivity extends BaseActivity implements SignContract.View,VideoInputDialog.VideoCall {
    private SignPresenterImpl signPresenter;
    @BindView(R.id.id_photo_lv1)
    LinearLayout idPhotoLv1;
    @BindView(R.id.id_photo_lv2)
    LinearLayout idPhotoLv2;
    @BindView(R.id.id_video_sdv)
    ScalableVideoView idVideoSdv;
    @BindView(R.id.id_video_sdv2)
    ScalableVideoView idVideoSdv2;
    @BindView(R.id.id_photo)
    ImageView photoImg;//前置摄像头
    @BindView(R.id.id_photo1)
    ImageView photoImg1;//后置摄像头
    @BindView(R.id.id_submit)
    Button submitBut;
    @BindView(R.id.id_time)
    TextView timeTV;
    @BindView(R.id.id_address)
    TextView addressTV;
    @BindView(R.id.id_remarks)
    EditText remarksET;
    private Context context;
    public final int TAKE_PHOTO=2;
    private File face1,face2;
    MyDialog dialog;
    String file1 = null,file2 = null;
    public Activity mactivity;
    private int camFlag = 0;
    // 记录文件保存位置
    private String mFilePath;
    private FileInputStream is = null;
    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_sign;
    }

    @Override
    protected void initViews() {
        context = this;
        mactivity = this;
        signPresenter = new SignPresenterImpl(this);
        timeTV.setText(CommonUtil.getTime2());
        addressTV.setText(MyConfig.myAddress+"");
    }

    @Override
    protected void updateViews() {

    }

    @OnClick({R.id.id_photo_lv1,R.id.id_photo_lv2,R.id.id_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_photo_lv1:
                //前置摄像头
                camFlag = 0;
                setItem();
                break;
            case R.id.id_photo_lv2:
                //后置摄像头
                camFlag = 1;
                setItem();
                break;
            case R.id.id_submit:
                //签到
                setSign();
                break;
        }
    }

    /**
     * 选项选择n
     */
    public void setItem(){
        DialogUtil.alertDialog2(this)
                .addSheetItem(getResources().getString(R.string.find_item_photo),
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                openForntCamera();
                            }
                        })
                .addSheetItem(getResources().getString(R.string.find_item_video),
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                    VideoInputDialog.show(getSupportFragmentManager(),SignActivity.this, VideoInputDialog.Q720,mactivity);
                            }
                        }).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode){
                case TAKE_PHOTO://前置摄像头
                        try {
                            // 获取输入流
                            is = new FileInputStream(mFilePath);
                            // 把流解析成bitmap
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            // 设置图片
                            if(camFlag == 0) {
                                photoImg.setImageBitmap(bitmap);
                                if (mFilePath != null) {
                                    photoImg.setVisibility(View.VISIBLE);
                                    idVideoSdv.setVisibility(View.GONE);
                                    file1 = ImageUtil.compressImage(mFilePath, ImageUtil.getFilePath());
                                    // file1Str = file.getAbsolutePath();
                                }
                            }else if(camFlag == 1){
                                photoImg1.setImageBitmap(bitmap);
                                if (mFilePath != null) {
                                    photoImg1.setVisibility(View.VISIBLE);
                                    idVideoSdv2.setVisibility(View.GONE);
                                    file2 = ImageUtil.compressImage(mFilePath, ImageUtil.getFilePath());
                                    // file1Str = file.getAbsolutePath();
                                }
                            }
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } finally {
                            // 关闭流
                            try {
                                if(is != null) {
                                    is.close();
                                }
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    break;
            }
    }

    //打开前置摄像头
    private void openForntCamera(){
        String state = Environment.getExternalStorageState(); //拿到sdcard是否可用的状态码
        if (state.equals(Environment.MEDIA_MOUNTED)){   //如果可用
            // 获取SD卡路径
            String FilePath = Environment.getExternalStorageDirectory().getPath();
            // 文件名
             mFilePath = FilePath + "/" + System.currentTimeMillis() + ".jpg";
            // 指定拍照
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // 加载路径
            Uri uri = Uri.fromFile(new File(mFilePath));
            // 指定存储路径，这样就可以保存原图了
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            // 拍照返回图片
            startActivityForResult(intent, TAKE_PHOTO);

        }else {
            Toast.makeText(context,"sdcard不可用",Toast.LENGTH_SHORT).show();
        }
    }


    //签到
    private void setSign(){

        String remarksStr = remarksET.getText().toString();

        dialog = MyDialog.showDialog(context);

        if (CommonUtil.isFastDoubleClick()) {
            return;
        }else{
            dialog.show();

            signPresenter.sign(context,file1,file2,face1,face2,remarksStr);
        }


    }


    @Override
    public void onSignSuccess() {
        dialog.dismiss();
        Toast.makeText(context,"上传成功",Toast.LENGTH_SHORT).show();
        EventBus.getDefault().post("成功");
        finish();
    }

    @Override
    public void onSignFail() {

    }


    public File saveMyBitmap(Bitmap mBitmap){
        String uuid = UUID.randomUUID().toString();
        File f = new File("/sdcard/videoimg" + uuid+".jpg");
        try {
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 70, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    @Override
    public void videoPathCall(String path) {
        if(camFlag == 0){
            photoImg.setVisibility(View.GONE);
            idVideoSdv.setVisibility(View.VISIBLE);
            playVideo(path,idVideoSdv);
            file1 = path;
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            try{
                media.setDataSource(context, Uri.parse(file1));
            } catch (IllegalArgumentException ex) {
                // Assume this is a corrupt video file
            }
            Bitmap bitmap = media.getFrameAtTime();
            face1 = saveMyBitmap(bitmap);
        }else if(camFlag == 1){
            photoImg1.setVisibility(View.GONE);
            idVideoSdv2.setVisibility(View.VISIBLE);
            playVideo(path,idVideoSdv2);
            file2 = path;
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            try{
                media.setDataSource(context, Uri.parse(file2));
            } catch (IllegalArgumentException ex) {
                // Assume this is a corrupt video file
            }
            Bitmap bitmap = media.getFrameAtTime();
            face2 = saveMyBitmap(bitmap);
        }
    }

    @Override
    public void videoFinish(boolean isFinish) {

    }
    public void playVideo(String url,final ScalableVideoView idVideoSdv){
        try {
            idVideoSdv.setDataSource(url);
            idVideoSdv.setVolume(0, 0);
            idVideoSdv.setLooping(true);
            idVideoSdv.prepare(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    idVideoSdv.start();
                }
            });
        } catch (IOException ioe) {
            //ignore
        }
    }


}
