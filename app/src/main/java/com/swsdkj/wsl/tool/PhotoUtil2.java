package com.swsdkj.wsl.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.kevin.crop.UCrop;
import com.swsdkj.wsl.activity.CropActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * 内容：
 * Created by 关云秀 on 2017\10\20 0020.
 */

public class PhotoUtil2 {
    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public static void startCropActivity(Activity activity, Uri uri, int flag) {
        // 剪切后图像文件
        Uri mDestinationUri = Uri.fromFile(new File(activity.getCacheDir(), "cropImage.jpeg"));
        if(flag == 0) {
            UCrop.of(uri, mDestinationUri)
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(512, 512)
                    .withTargetActivity(CropActivity.class)
                    .start(activity);
        }else{
            UCrop.of(uri, mDestinationUri)
                    .withAspectRatio(4, 3)
                    .withMaxResultSize(512, 512)
                    .withTargetActivity(CropActivity.class)
                    .start(activity);
        }
    }
    /**
     * 打开相机
     */
    public static File openCamera(Activity activity, int PHOTO_REQUEST_TAKEPHOTO) {
        String state = Environment.getExternalStorageState();
        File outFile = null;
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!outDir.exists()) {
                outDir.mkdirs();
            }
            outFile = new File(outDir, System.currentTimeMillis() + ".jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outFile));
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            activity.startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
        } else {
            Log.e("CAMERA", "请确认已经插入SD卡");
        }
        return outFile;
    }
    /**
     * 打开相册
     */
    public static void openPic(Activity activity,int REQUESTCODE_PICK) {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        activity.startActivityForResult(pickIntent, REQUESTCODE_PICK);
    }
    public static Bitmap getBitmap(Intent data, Context mcontext){
        final Uri resultUri = UCrop.getOutput(data);
        Bitmap bitmap = null;
        if (null != resultUri) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(mcontext.getContentResolver(), resultUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
          /*  Bundle extras = data.getExtras();
            if (extras != null) {
                //可用于图像上传
                String filePath = extras.getString("filepath");
                Log.i("filePath",filePath+"***********");
            }*/
            bitmap = new CropActivity().getCropBitmap();
        }
        return bitmap;
    }
    //Bitmap对象保存味图片文件
    public static String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }
    public static File  saveBitmapFile(Bitmap bm) {
        String path =getSDPath()+"/crop/";
        File dirFile = new File(path);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File myCaptureFile = new File(path + Calendar.getInstance().getTimeInMillis()+"crop.jpg");

        try {
            BufferedOutputStream bos = null;
            bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return myCaptureFile;
    }
}
