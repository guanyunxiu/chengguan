package com.wbapp.omxvideo;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoViewActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // disable sleep
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.video);
        view = (VideoView) findViewById(R.id.video_view);
        startPlay(getIntent().getExtras().getString("path"));
    }
    
    VideoView view;

    private void startPlay(String path) {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am.isWiredHeadsetOn() || am.isBluetoothA2dpOn() || am.isBluetoothScoOn()) {
            Log.i("video playback", "wired or bluetooth headset on");
        }
        else {
            Log.i("video playback", "turn speaker on");
            am.setSpeakerphoneOn(true);
        }
        
        view.setVideoPath(path);
        view.setMediaController(new MediaController(this));
        view.requestFocus();
        view.setKeepScreenOn(true);
        view.start();
    }
    
    protected void onDestroy() {
        view.stopPlayback();
        super.onDestroy();
    }
}
