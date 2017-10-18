package com.wbapp.omxvideo;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

public class Audio {
    public static final int AMR = 0;
    public static final int AAC = 1;
    public static final int PCM = 0xff;
    
    Activity activity;
    int audioType;

    int rate = 8000;
    int channel = AudioFormat.CHANNEL_IN_MONO;
    int bit = AudioFormat.ENCODING_PCM_16BIT;
    int audioBufferSize;

    private Thread recordThread;
    
    public Audio(Activity act, int type) {
        activity = act;
        audioType = type;
        if (type == AMR) {
            rate = 8000;
            audioBufferSize = 320;
        }
        else if (type == AAC) {
            rate = 48000;
            audioBufferSize = 2048;
        }
        else if (type == PCM) {
            rate = 48000;
            audioBufferSize = 2048;
        }
    }
    
    public int getAudioType() {
        return audioType;
    }
    
    public int getSampleRate() {
        return rate;
    }

    AudioRecord record = null;
    byte[] buffer;
    boolean isRecording;

    OMXVideoJNI n = OMXVideoJNI.getInstance();

    public void startRecord() {
        if (isRecording)
            return;
        if (record==null) {
            n.setSendAudioRate(rate);
            int minsize = AudioRecord.getMinBufferSize(rate, channel, bit);
            record = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                rate, channel, bit, minsize*2);
            Log.i("", "audio rec buf size "+minsize);
            buffer = new byte[audioBufferSize];
        }
        isRecording = true;
        recordThread = new RecordThread();
        recordThread.start();
    }
    
    class RecordThread extends Thread {
        public void run() {
            doRecord();
        }
    }
    
    public void doRecord() {
        try {
            record.startRecording();
            while (isRecording) {
                int len = record.read(buffer, 0, audioBufferSize);
                long stamp = System.currentTimeMillis();
                //Log.i("read audio", "record audio "+len);
                
                if (len == audioBufferSize)
                    n.sendAudio(buffer, len, stamp);
                else
                    Log.i("read audio", String.format("audio read too short %d/%d", len, audioBufferSize));
            }
            record.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void stopRecord() {
        isRecording = false;
        try {
            if (recordThread != null)
                recordThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public boolean getIsRecording() {
        return isRecording;
    }

    /*
    class RecordTask extends AsyncTask<Void, Integer, Void>{
        @Override
        protected Void doInBackground(Void... arg0) {
            doRecord();
            return null;
        }
    }*/

    AudioTrack track;
    byte[] playbuf;
    boolean isPlaying = false;

    public void startPlay() {
        if(isPlaying)
            return;
        if(track==null) {
            if (rate > 8000)
                n.setRecvAudioResample(rate);
            int chn = AudioFormat.CHANNEL_OUT_MONO;
            int size = AudioTrack.getMinBufferSize(rate, chn, bit);
            Log.i("", "audio play buf size "+size);
            track = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
                rate, chn, bit,
                size*2, AudioTrack.MODE_STREAM);
            playbuf = new byte[2048];

            AudioManager am = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
            if (am.isWiredHeadsetOn() || am.isBluetoothA2dpOn() || am.isBluetoothScoOn()) {
                Log.i("", "wired or bluetooth headset on");
            }
            else {
                Log.i("", "turn speaker on");
                am.setSpeakerphoneOn(true);
            }
        }
        isPlaying = true;
        new PlayTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    class PlayTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... arg0) {
            doPlay();
            return null;
        }
    }

    public void doPlay() {
        track.play();
        while(isPlaying) {
            int len = n.recvAudio(playbuf);
            if (len > 0)
                track.write(playbuf, 0, len);
        }
        track.stop();
        Log.i("", "audio play end");
    }

    public void stopPlay() {
        isPlaying = false;
    }
}
