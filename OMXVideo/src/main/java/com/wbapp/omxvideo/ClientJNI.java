package com.wbapp.omxvideo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Xml.Encoding;

public class ClientJNI {
    static {
        // ensure load lib omxvideo_jni
        OMXVideoJNI.getInstance();
        //System.loadLibrary("stub");
       System.loadLibrary("client_jni");
    }
    
    public native void setNetConfig(String addr, int port, String user, String pswd, int latency);
    
    public boolean load() {
        textbuf = new byte[256];
        /*textStream = new ByteArrayInputStream(textbuf);
        try {
            textReader = new InputStreamReader(textStream, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        textchars = new char[256];*/
        //set the text buffer for the callback,
        //avoiding making a new byte array from the
        //jni side on each callback, which is slow.
        return load(textbuf);
    }

    public native boolean load(byte[] textBuf);
    
    public interface ResourceCallback {
        public void onVisitResource(Resource r);
    }
    
    private ResourceCallback resCallback;
    private Resource resbuf;
    private byte[] textbuf;
    /*private ByteArrayInputStream textStream;
    private InputStreamReader textReader;
    private char[] textchars;*/
    
    public void setResourceCallback(ResourceCallback callback)
    {
        resCallback = callback;
        resbuf = new Resource();
    }
    
    // callback during load
    public void resourceCallback(int type, int id, int state, int textlen, int deep) {
        if(resCallback!=null) {
            resbuf.type = type;
            resbuf.id = id;
            resbuf.state = state;
            resbuf.deep = deep;
            /*try {
                textStream.reset();
                textReader.read(textchars, 0, textlen);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            resbuf.text = new String(textchars, 0, textlen);*/
            byte[] tmp = new byte[textlen];
            System.arraycopy(textbuf, 0, tmp, 0, textlen);
            try {
                resbuf.text = new String(tmp, "GBK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            resCallback.onVisitResource(resbuf);
        }
    }
    
    public native boolean startNetVideo(int id, boolean hardDec);

    public native boolean stopNetVideo();
    
    public native void setBitmap(Bitmap bmp);
    
    public native int updateBitmap();
}
