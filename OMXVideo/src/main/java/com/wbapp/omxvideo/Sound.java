package com.wbapp.omxvideo;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class Sound {
    
    private SoundPool soundPool;
    private int startStopSoundId;
    private int shutterSoundId;
    private int alarmSoundId;
    private boolean ready = false;

    public Sound(Context context) {
        soundPool = new SoundPool(5, AudioManager.STREAM_ALARM, 0);

        /** soundId for Later handling of sound pool **/
        startStopSoundId = soundPool.load(context, R.raw.startstop, 1);
        shutterSoundId = soundPool.load(context, R.raw.shutter, 1);
        alarmSoundId = soundPool.load(context, R.raw.alarm, 1);

        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener()
        {
           @Override
           public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
               ready = true;
           }
        });
    }

    private void playSoundId(int soundId) {
        if (ready)
            soundPool.play(soundId, 1, 1, 0, 0, 1);
    }

    public void playStartStop() {
        playSoundId(startStopSoundId);
    }

    public void playShutter()
    {
        playSoundId(shutterSoundId);
    }

    public void playAlarm()
    {
        playSoundId(alarmSoundId);
    }
}
