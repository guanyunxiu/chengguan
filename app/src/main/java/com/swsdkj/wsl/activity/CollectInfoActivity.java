package com.swsdkj.wsl.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sh.shvideolibrary.VideoInputDialog;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.adapter.InfoAdapter;
import com.swsdkj.wsl.adapter.PhotoAdapter2;
import com.swsdkj.wsl.adapter.VideoAdapter;
import com.swsdkj.wsl.base.BaseActivity;
import com.swsdkj.wsl.bean.InfoBean;
import com.swsdkj.wsl.bean.PhotoBean;
import com.swsdkj.wsl.config.MyConfig;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 内容：
 * Created by 关云秀 on 2017\10\19 0019.
 */

public class CollectInfoActivity extends BaseActivity implements VideoInputDialog.VideoCall {
    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.title01_tv)
    TextView title01Tv;
    @BindView(R.id.title02_tv)
    TextView title02Tv;
    @BindView(R.id.info_rlv)
    RecyclerView infoRlv;
    @BindView(R.id.photo_img)
    ImageView photoImg;
    @BindView(R.id.photo_rlv)
    RecyclerView photoRlv;
    @BindView(R.id.video_rlv)
    RecyclerView videoRlv;
    InfoAdapter infoAdapter;
    PhotoAdapter2 photoAdapter;
    VideoAdapter videoAdapter;
    @BindView(R.id.video_img)
    ImageView videoImg;
    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.address_tv)
    TextView addressTv;
    // 记录文件保存位置
    private String mFilePath;
    private FileInputStream is = null;
    public final int TAKE_PHOTO = 2;
    private List<PhotoBean> photoBeanList = new ArrayList<>();
    private List<PhotoBean> videoBeanList = new ArrayList<>();

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_collect_info;
    }

    @Override
    protected void initViews() {
        initTitle(true, "采集信息表");
        addressTv.setText("经度："+MyConfig.Lng+"     纬度："+MyConfig.Lat);
        initAdapter();//初始化Adapter
    }
    public void initAdapter(){
        infoRlv.setLayoutManager(new LinearLayoutManager(context));
        infoAdapter = new InfoAdapter(getList(), context);
        infoAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        infoRlv.setAdapter(infoAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        photoRlv.setLayoutManager(linearLayoutManager);
        photoAdapter = new PhotoAdapter2(photoBeanList, context);
        photoAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        photoRlv.setAdapter(photoAdapter);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        videoRlv.setLayoutManager(linearLayoutManager2);
        videoAdapter = new VideoAdapter(videoBeanList, context);
        videoAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        videoRlv.setAdapter(videoAdapter);
        photoAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                ArrayList<String> urls = new ArrayList<>();
                for (PhotoBean imageBean : photoBeanList) {
                    urls.add(imageBean.getAddress());
                }
                Intent intent = new Intent(context, ImagePagerActivity.class);
                // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, i);
                context.startActivity(intent);
            }
        });
        videoAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                PhotoBean photoBean = videoBeanList.get(i);
                Intent intent = new Intent(context, VideoSurface.class);
                intent.putExtra("datapath",photoBean.getAddress());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void updateViews() {

    }

    public List<InfoBean> getList() {
        List<InfoBean> list = new ArrayList<>();
        InfoBean infoBean = new InfoBean();
        infoBean.setName("部件名称");
        infoBean.setContent("上水井盖");
        infoBean.setItemType(2);
        list.add(infoBean);
        InfoBean infoBean2 = new InfoBean();
        infoBean2.setName("所属单位");
        infoBean2.setItemType(1);
        list.add(infoBean2);
        InfoBean infoBean3 = new InfoBean();
        infoBean3.setName("状态");
        infoBean3.setItemType(0);
        list.add(infoBean3);
        InfoBean infoBean4 = new InfoBean();
        infoBean4.setName("养护单位");
        infoBean4.setItemType(1);
        list.add(infoBean4);
        InfoBean infoBean5 = new InfoBean();
        infoBean5.setName("部件名称");
        infoBean5.setContent("上水井盖");
        infoBean5.setItemType(2);
        list.add(infoBean5);
        return list;
    }

  /*  @OnClick(R.id.photo_img)
    public void onClick() {
        final String[] stringItems = {"拍照", "录视频"};
        final ActionSheetDialog dialog = new ActionSheetDialog(context, stringItems, null);
        dialog.isTitleShow(false).show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {//拍照
                    openForntCamera();
                } else {//录视频
                    VideoInputDialog.show(getSupportFragmentManager(), CollectInfoActivity.this, VideoInputDialog.Q720, context);
                }
                dialog.dismiss();
            }
        });
    }*/

    //打开前置摄像头
    private void openForntCamera() {
        String state = Environment.getExternalStorageState(); //拿到sdcard是否可用的状态码
        if (state.equals(Environment.MEDIA_MOUNTED)) {   //如果可用
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
        } else {
            Toast.makeText(context, "sdcard不可用", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO://前置摄像头
                    PhotoBean photoBean = new PhotoBean();
                    photoBean.setItemType(0);
                    photoBean.setAddress(mFilePath);
                    photoBeanList.add(photoBean);
                    photoAdapter.setNewData(photoBeanList);

                    break;
            }
        }
    }

    @Override
    public void videoPathCall(String path) {
        PhotoBean photoBean = new PhotoBean();
        photoBean.setItemType(1);
        photoBean.setAddress(path);
        videoBeanList.add(photoBean);
        videoAdapter.setNewData(videoBeanList);
    }

    @Override
    public void videoFinish(boolean isFinish) {

    }
    @OnClick({R.id.photo_img, R.id.video_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.photo_img:
                openForntCamera();
                break;
            case R.id.video_img:
                VideoInputDialog.show(getSupportFragmentManager(), CollectInfoActivity.this, VideoInputDialog.Q720, context);
                break;
        }
    }
}
