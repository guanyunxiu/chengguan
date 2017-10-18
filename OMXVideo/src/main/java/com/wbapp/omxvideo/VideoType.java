package com.wbapp.omxvideo;

public class VideoType {
    public static final int AVC = 0;
    public static final int HEVC = 1;
    
    private static final String[] mimeTypes = {
        "video/avc",
        "video/hevc"
    };
    
    public static final String getMimeType(int videoType) {
        if (videoType < 0 || videoType > HEVC) {
            return null;
        }
        else {
            return mimeTypes[videoType];
        }
    }
}
