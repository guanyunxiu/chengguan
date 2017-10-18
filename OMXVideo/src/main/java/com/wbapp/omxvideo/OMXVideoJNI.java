package com.wbapp.omxvideo;

import java.nio.ByteBuffer;

import android.app.Activity;

public class OMXVideoJNI {
    static {
        System.loadLibrary("omxvideo_jni");
    }
    
    private static OMXVideoJNI instance;
    public static OMXVideoJNI getInstance() {
        if (instance==null)
            instance = new OMXVideoJNI();
        return instance;
    }

    public native boolean init(Activity activity);
    
    public native void exit();

    public native int getDevId();

    public native void setDeviceInfo(String brand, String manufact, String hardware, String model);

    public native boolean netConfig(String addr, int cmd_port, int data_port, int trans_mode, int router_mode);
    
    public native boolean start(int width, int height, int bitrate, int fps, int gop,
            int save_width, int save_height, int save_bitrate,
            boolean dual_encoding, int enc_mode, int rotate, int osd_mask, boolean soft_swap,
            int audio_type, byte[] devnameBytes);
    
    public native boolean stop();
    
    public native void startTransmit();
    public native void stopTransmit();
    
    public interface OnNewFileListener {
        void onNewFile();
    }
    
    public native boolean initSave(String dir, int size, int duration, boolean saveAudioOnly, int audioSampleRate,
            OnNewFileListener listener);

    public native void startSave();
    public native void stopSave();
    
    public native void setFileImportant();
    public native void unsetFileImportant();

    public native boolean sendFrame(byte[] frame, int len, long timestamp);
    
    public native boolean sendFrameByteBuffer(ByteBuffer buffer, int len, long timestamp);
    
    public native boolean sendAvcData(int ch, byte[] data, int len, long timestamp);

    public native boolean sendAudio(byte[] buf, int len, long timestamp);

    public native boolean sendGPSData(String data, int len);
    
    public native boolean sendAlarm();

    public native int getState();
    
    public native void setSendAudioRate(int rate);
    
    public native void setRecvAudioResample(int rate);

    public native int recvAudio(byte[] buf);

    public native boolean turnLightOff();

    public native int processYUV(byte[] src, int width, int height, byte[] dst, boolean nv12_2_i420,
            int scale_width, int scale_height, byte[] scale_buf);
    
    public native int processYUVByteBuffer(ByteBuffer src, int width, int height, byte[] dst, boolean nv12_2_i420,
            int scale_width, int scale_height, byte[] scale_buf);

    public native void startCall();
    
    public native void stopCall();

    public native int getCallState();

    /* log types should conform to the jni side:
        VLOG_SYS_START = 1,
        VLOG_SYS_STOP = 2,
        VLOG_START_SAVE = 3,
        VLOG_STOP_SAVE = 4,
        VLOG_START_AUDIOREC = 5,
        VLOG_STOP_AUDIOREC = 6,
        VLOG_START_TRANS = 7,
        VLOG_STOP_TRANS = 8,
        VLOG_PICSHOT = 9,
        VLOG_ALARM = 10,
        VLOG_START_TALK = 11,
        VLOG_STOP_TALK = 12,
        VLOG_ALERT_LOWBAT = 13,
        VLOG_ALERT_SPACEFULL = 14,
    */
    public native void addVideoLog(int type, String arg);

    // this is currently not used.
    public native void addPicOsd(byte[] data, int width, int height, int osd_mask,
            byte[] devnameBytes);
}
