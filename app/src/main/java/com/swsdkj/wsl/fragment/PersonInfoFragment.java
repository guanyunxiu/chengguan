package com.swsdkj.wsl.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.kevin.crop.UCrop;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.activity.CropActivity;
import com.swsdkj.wsl.base.BaseFragment;
import com.swsdkj.wsl.tool.PhotoUtil;
import com.swsdkj.wsl.tool.PhotoUtil2;
import com.swsdkj.wsl.view.DialogWithYesOrNoUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.rong.imkit.emoticon.AndroidEmoji;

/**
 * 内容：
 * Created by 关云秀 on 2017\10\19 0019.
 */

public class PersonInfoFragment extends BaseFragment {
    @BindView(R.id.photo_img)
    ImageView photoImg;
    @BindView(R.id.name_tv)
    TextView nameTv;
    @BindView(R.id.phone_tv)
    TextView phoneTv;
    @BindView(R.id.position_tv)
    TextView positionTv;
    @BindView(R.id.nums_tv)
    TextView numsTv;
    @BindView(R.id.idcard_tv)
    TextView idcardTv;
    @BindView(R.id.sub_btn)
    Button subBtn;
    File currentFile,outFile;
    /** 选择头像相册选取 */
    private static final int REQUESTCODE_PICK = 1;
    /** 选择头像拍照选取 */
    private static final int PHOTO_REQUEST_TAKEPHOTO = 3;
    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_person_info;
    }

    @Override
    protected void initViews() {
        initTitle(true, "个人信息");
    }
    @Override
    protected void updateViews() {

    }
    @OnClick({R.id.photo_lv, R.id.name_lv, R.id.phone_tv, R.id.position_lv, R.id.nums_lv, R.id.idcard_lv, R.id.sub_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.photo_lv:
                selPhoto();
                break;
            case R.id.name_lv:
                DialogWithYesOrNoUtils.getInstance().showEditDialog(mContext,"请输入姓名", getString(R.string.confirm), new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void executeEvent() {
                    }
                    @Override
                    public void executeEditEvent(String editText) {
                        if (TextUtils.isEmpty(editText)) {
                            return;
                        }
                        nameTv.setText(editText);
                    }
                    @Override
                    public void updatePassword(String oldPassword, String newPassword) {

                    }
                });
                break;
            case R.id.phone_tv:
                break;
            case R.id.position_lv:
                DialogWithYesOrNoUtils.getInstance().showEditDialog(mContext,"请输入职位", getString(R.string.confirm), new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void executeEvent() {
                    }
                    @Override
                    public void executeEditEvent(String editText) {
                        if (TextUtils.isEmpty(editText)) {
                            return;
                        }
                        positionTv.setText(editText);
                    }
                    @Override
                    public void updatePassword(String oldPassword, String newPassword) {

                    }
                });
                break;
            case R.id.nums_lv:
                DialogWithYesOrNoUtils.getInstance().showEditDialog(mContext,"请输入工作年限", getString(R.string.confirm), new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void executeEvent() {
                    }
                    @Override
                    public void executeEditEvent(String editText) {
                        if (TextUtils.isEmpty(editText)) {
                            return;
                        }
                        numsTv.setText(editText);
                    }
                    @Override
                    public void updatePassword(String oldPassword, String newPassword) {

                    }
                });
                break;
            case R.id.idcard_lv:
                DialogWithYesOrNoUtils.getInstance().showEditDialog(mContext,"请输入身份证号", getString(R.string.confirm), new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void executeEvent() {
                    }
                    @Override
                    public void executeEditEvent(String editText) {
                        if (TextUtils.isEmpty(editText)) {
                            return;
                        }
                        idcardTv.setText(editText);
                    }
                    @Override
                    public void updatePassword(String oldPassword, String newPassword) {

                    }
                });
                break;
            case R.id.sub_btn:
                break;
        }
    }
    private void selPhoto() {
        final String[] stringItems = {"拍照", "从相册选择",};
        final ActionSheetDialog dialog = new ActionSheetDialog(mContext, stringItems, null);
        dialog.isTitleShow(false).show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){//拍照
                    doCamera();
                }else if(position == 1){//从相册选择
                    doSDCardPermission();
                }
                dialog.dismiss();
            }
        });
    }
    /**
     * 头像选择
     */
    public void doCamera() {
        outFile = openCamera();
    }
    public void doSDCardPermission() {
        openPic();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  进行判断是那个操作跳转回来的，如果是裁剪跳转回来的这块就要把图片现实到View上，其他两种的话都把数据带入裁剪界面
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                //相册
                case REQUESTCODE_PICK:
                    if (data == null || data.getData() == null) {
                        return;
                    }
                    Uri uri;
                    uri = data.getData();
                    startCropActivity(uri);
                    break;
                case UCrop.REQUEST_CROP:    // 裁剪图片结果
                    if (data != null) {
                        Bitmap bitmap = getBitmap(data);
                        Log.i("bitmap", data + "**");
                        photoImg.setImageBitmap(bitmap);
                        currentFile = PhotoUtil2.saveBitmapFile(bitmap);
                    }
                    break;
                //拍照
                case PHOTO_REQUEST_TAKEPHOTO:
                    startCropActivity(Uri.fromFile(outFile));
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    /**
     * 打开相册
     */
    public void openPic() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(pickIntent, REQUESTCODE_PICK);
    }
    public Bitmap getBitmap(Intent data){
        final Uri resultUri = UCrop.getOutput(data);
        Bitmap bitmap = null;
        if (null != resultUri) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), resultUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            bitmap = new CropActivity().getCropBitmap();
        }
        return bitmap;
    }
    /**
     * 裁剪图片方法实现
     * @param uri
     */
    public void startCropActivity( Uri uri) {
        // 剪切后图像文件
        Uri mDestinationUri = Uri.fromFile(new File(mContext.getCacheDir(), "cropImage.jpeg"));
            UCrop.of(uri, mDestinationUri)
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(512, 512)
                    .withTargetActivity(CropActivity.class)
                    .start(mContext,PersonInfoFragment.this);
    }
    /**
     * 打开相机
     */
    public  File openCamera() {
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
            startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
        } else {
            Log.e("CAMERA", "请确认已经插入SD卡");
        }
        return outFile;
    }
}
