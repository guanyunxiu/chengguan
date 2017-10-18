package com.swsdkj.wsl.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.swsdkj.wsl.R;
import com.swsdkj.wsl.base.BaseActivity;

import butterknife.BindView;
import photoview.PhotoViewAttacher;

/**
 * 作者： 关云秀 on 2017/2/15.
 * 描述：
 */
public class ImageSingActivity extends BaseActivity {
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.image_layout)
    FrameLayout imageLayout;
    private PhotoViewAttacher mAttacher;
    private Bitmap bitmap;
    @Override
    protected void setContentView() {
        setContentView(R.layout.image_detail_fragment);
        bitmap=getIntent().getParcelableExtra("bitmap");
    }

    @Override
    protected void init() {
        mAttacher = new PhotoViewAttacher(image);
        setimage(bitmap);
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                finish();
            }
        });
    }

    private void setimage(Bitmap bitmap) {
        image.setImageBitmap(bitmap);
    }
}
