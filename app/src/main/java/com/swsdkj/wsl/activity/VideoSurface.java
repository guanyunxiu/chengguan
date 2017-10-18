package com.swsdkj.wsl.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


import com.swsdkj.wsl.R;
import com.swsdkj.wsl.net.DownloadFile;
import com.swsdkj.wsl.view.MyDialog;

import java.io.IOException;

/**
 * 作者： 关云秀 on 2017/3/21.
 * 描述：
 */
public class VideoSurface  extends Activity implements MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener,MediaPlayer.OnInfoListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener,MediaPlayer.OnVideoSizeChangedListener,SurfaceHolder.Callback{
    private Display currDisplay;
    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private MediaPlayer player;
    private int vWidth,vHeight;
    private ProgressBar progressBar;
    //private boolean readyToPlay = false;
    //private String dataPath;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.video_surface);

        String urlPath = getIntent().getStringExtra("datapath");
        surfaceView = (SurfaceView)this.findViewById(R.id.video_surface);
        progressBar = (ProgressBar)this.findViewById(R.id.loading);
        progressBar.setVisibility(View.VISIBLE);
        //给SurfaceView添加CallBack监听
        holder = surfaceView.getHolder();
        holder.addCallback(this);
        //为了可以播放视频或者使用Camera预览，我们需要指定其Buffer类型
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        //下面开始实例化MediaPlayer对象
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnInfoListener(this);
        player.setOnPreparedListener(this);
        player.setOnSeekCompleteListener(this);
        player.setOnVideoSizeChangedListener(this);
        Log.v("surface", "Begin:::"+urlPath);

        //然后指定需要播放文件的路径，初始化MediaPlayer
        //   String dataPath = Environment.getExternalStorageDirectory().getPath()+"/Test_Movie.m4v";


        initPlay(urlPath);
    }

    private void initPlay(String dataPath) {
        try {
            player.setDataSource(dataPath);
            Log.v("surface", "Next:::");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //然后，我们取得当前Display对象
        currDisplay = this.getWindowManager().getDefaultDisplay();
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // 当Surface尺寸等参数改变时触发
        Log.v("surface", "Surface Change:::");
        //dialog.dismiss();
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 当SurfaceView中的Surface被创建的时候被调用
        //在这里我们指定MediaPlayer在当前的Surface中进行播放
        player.setDisplay(holder);
        //在指定了MediaPlayer播放的容器后，我们就可以使用prepare或者prepareAsync来准备播放了
        player.prepareAsync();


    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        Log.v("surface", "Surface Destory:::");
    }
    @Override
    public void onVideoSizeChanged(MediaPlayer arg0, int arg1, int arg2) {
        // 当video大小改变时触发
        // 这个方法在设置player的source后至少触发一次
        Log.v("surface", "Video Size Change");

    }
    @Override
    public void onSeekComplete(MediaPlayer arg0) {
        // seek操作完成时触发
        Log.v("surface", "Seek Completion");
    }
    @Override
    public void onPrepared(MediaPlayer player) {
        // 当prepare完成后，该方法触发，在这里我们播放视频

        //首先取得video的宽和高
        vWidth = player.getVideoWidth();
        vHeight = player.getVideoHeight();

        if(vWidth > currDisplay.getWidth()){
            /*//如果video的宽或者高超出了当前屏幕的大小，则要进行缩放
            float wRatio = (float)vWidth/(float)currDisplay.getWidth();
            float hRatio = (float)vHeight/(float)currDisplay.getHeight();
            //选择大的一个进行缩放
            float ratio = Math.max(wRatio, hRatio);
            vWidth = (int)Math.ceil((float)vWidth/ratio);
            vHeight = (int)Math.ceil((float)vHeight/ratio);*/
            //变成横屏了
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        //设置surfaceView的布局参数
        // surfaceView.setLayoutParams(new LinearLayout.LayoutParams(vWidth, vHeight));
        progressBar.setVisibility(View.GONE);
        //然后开始播放视频
        player.start();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //变成横屏了
            //  setVideoParams(mMediaPlayer, true);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //变成竖屏了
            // setVideoParams(mMediaPlayer, false);
        }
    }
    @Override
    public boolean onInfo(MediaPlayer player, int whatInfo, int extra) {
        // 当一些特定信息出现或者警告时触发
        switch(whatInfo){
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                break;
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                break;
        }
        return false;
    }
    @Override
    public boolean onError(MediaPlayer player, int whatError, int extra) {
        Log.v("Play Error:::", "onError called");
        switch (whatError) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.v("Play Error:::", "MEDIA_ERROR_SERVER_DIED");
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.v("Play Error:::", "MEDIA_ERROR_UNKNOWN");
                break;
            default:
                break;
        }
        return false;
    }
    @Override
    public void onCompletion(MediaPlayer player) {
        // 当MediaPlayer播放完成后触发
        Log.v("surface", "Play Over:::");
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }
}
