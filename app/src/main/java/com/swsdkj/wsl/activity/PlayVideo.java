package com.swsdkj.wsl.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.wechatsmallvideoview.SurfaceVideoViewCreator;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.base.BaseActivity;

/**
 * 作者： 关云秀 on 2017/5/15.
 * 描述：
 */
public class PlayVideo extends BaseActivity {
    private SurfaceVideoViewCreator surfaceVideoViewCreator;
    private String path;
    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_playvideo;
    }

    @Override
    protected void initViews() {
        path = getIntent().getStringExtra("path");
        ActivityCompat.requestPermissions(
                PlayVideo.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1
        );

        surfaceVideoViewCreator =
                new SurfaceVideoViewCreator(this,(RelativeLayout)findViewById(R.id.activity_main)) {
                    @Override
                    protected Activity getActivity() {
                        return PlayVideo.this;     /** 当前的 Activity */
                    }

                    @Override
                    protected boolean setAutoPlay() {
                        Log.i("play","setAutoPlay");
                        return false;                 /** true 适合用于，已进入就自动播放的情况 */
                    }

                    @Override
                    protected int getSurfaceWidth() {
                        Log.i("play","getSurfaceWidth");
                        return 0;                     /** Video 的显示区域宽度，0 就是适配手机宽度 */
                    }
                    @Override
                    protected int geturfaceHeight() {
                        Log.i("play","geturfaceHeight");
                        WindowManager wm =  (WindowManager) getApplicationContext()
                                .getSystemService(Context.WINDOW_SERVICE);
                        int height = wm.getDefaultDisplay().getHeight();
                        return height;                   /** Video 的显示区域高度，dp 为单位 */
                    }
                    @Override
                    protected void setThumbImage(ImageView thumbImageView) {
                        Log.i("play","setThumbImage");
                        Glide.with(PlayVideo.this)
                                .load(path)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.all_darkbackground)
                                .dontAnimate()
                                .into(thumbImageView);
                    }

                    /** 这个是设置返回自己的缓存路径，
                     * 应对这种情况：
                     *     录制的时候是在另外的目录，播放的时候默认是在下载的目录，所以可以在这个方法处理返回缓存
                     * */
                    @Override
                    protected String getSecondVideoCachePath() {
                        Log.i("play","getSecondVideoCachePath");
                        return null;
                    }

                    @Override
                    protected String getVideoPath() {
                        Log.i("play","getVideoPath");
                        // return "http://121.42.183.149:8080/CWA/upload/4/sign/0543808f-fbe6-4846-bb97-a39eb5a23911.mp4";
                        return path;
                    }
                };
        surfaceVideoViewCreator.debugModel = true;
        surfaceVideoViewCreator.setUseCache(true);
    }

    @Override
    protected void updateViews() {

    }
    @Override
    protected void onPause() {
        super.onPause();
        surfaceVideoViewCreator.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        surfaceVideoViewCreator.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        surfaceVideoViewCreator.onDestroy();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        surfaceVideoViewCreator.onKeyEvent(event); /** 声音的大小调节 */
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    break;
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
