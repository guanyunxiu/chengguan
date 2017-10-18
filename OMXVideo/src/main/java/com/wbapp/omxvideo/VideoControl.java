package com.wbapp.omxvideo;

public class VideoControl {
    public native void control(int ctl);
    
    final int CTL_UP = 1;
    final int CTL_DOWN = 2;
    final int CTL_LEFT = 3;
    final int CTL_RIGHT = 4;
    final int CTL_ZOOMIN = 5;
    final int CTL_ZOOMOUT = 6;
    final int CTL_STOP = 0xff;
}
