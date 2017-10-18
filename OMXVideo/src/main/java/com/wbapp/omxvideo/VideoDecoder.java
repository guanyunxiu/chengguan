package com.wbapp.omxvideo;

public class VideoDecoder {
    class VideoFrame {
        public byte[] buffer;
        public int length;
        public long stamp;
    };
    
    public native int getVideoSize();
    // get next compressed video frame.
    public native int getVideoFrame(VideoFrame frame);
    
    // fill decoded picture into bitmap.
    public native void setBitmap(Object bmp);
    public native int updateBitmap();
    public native void releaseBitmap(Object bmp);
}
