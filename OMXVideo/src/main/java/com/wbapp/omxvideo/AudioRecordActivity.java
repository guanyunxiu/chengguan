package com.wbapp.omxvideo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ToggleButton;

public class AudioRecordActivity extends Activity {
    
    Audio audio;
    OMXVideoJNI n = new OMXVideoJNI();
    
    String save_path;
    int save_filesize;
    int save_duration;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.audiorecord);
        
        loadPreference();
    }
    
    private void loadPreference() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);

        String val = sp.getString("save_dir", getString(R.string.def_save_dir));
        save_path = "/mnt/sdcard/" + val;
        val = sp.getString("save_filesize",
                getString(R.string.def_save_filesize));
        save_filesize = Integer.parseInt(val);

        val = sp.getString("save_duration", "5");
        save_duration = Integer.parseInt(val);
    }
    
    @Override
    protected void onDestroy() {
        stopAudioRecord();
        super.onDestroy();
    }
    
    public void onMicOpenClick(View view) {
   /*     ToggleButton micOpen = (ToggleButton) findViewById(R.id.mic_open);
        if (micOpen.isChecked()) {
            startAudioRecord();
        }
        else {
            stopAudioRecord();
        }*/
    }
    
    void startAudioRecord() {
        if (audio == null)
            audio = new Audio(this, Audio.PCM);
        if (audio.getIsRecording())
            return;
        
        if (!n.initSave(save_path, save_filesize, save_duration, true, audio.getSampleRate(), null)) {
            return;
        }
        n.startSave();
        
        audio.startRecord();
    }

    void stopAudioRecord() {
        // stop only if both transmitting and saving stopped
        if (audio != null && audio.getIsRecording()) {
            audio.stopRecord();
        }

        n.stopSave();
    }
}
