package com.swsdkj.wsl.activity;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.swsdkj.wsl.R;
import com.swsdkj.wsl.base.BaseActivity;
import com.swsdkj.wsl.view.FullVideoView;

/**
 * Created by Administrator on 2017/5/11 0011.
 */

public class MyVideoView extends BaseActivity{
    private FullVideoView mVideoView;
    private String dataStr;
    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_mycamera;
    }

    @Override
    protected void initViews() {
        dataStr = getIntent().getStringExtra("datapath");

        Log.i("123", dataStr + "------------------本地传递视频-----------------------");

        mVideoView = (FullVideoView) this.findViewById(R.id.id_videoview);


        Uri uri = Uri.parse("android.resource://com.swsdkj.wsl.activity/"+dataStr);
        mVideoView.setVideoURI(uri);


        mVideoView.setMediaController(new MediaController(
                MyVideoView.this));// 显示视频播放控制栏
        mVideoView.requestFocus();
        // mVideoView.setSystemUiVisibility(View.INVISIBLE);//隐藏系统导航栏，sdk最低为11
        mVideoView.start();

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    protected void updateViews() {

    }

}
