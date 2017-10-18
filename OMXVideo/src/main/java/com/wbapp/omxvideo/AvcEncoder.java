package com.wbapp.omxvideo;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import android.content.SharedPreferences;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.preference.PreferenceManager;
import android.util.Log;

public class AvcEncoder extends Thread {

    interface DataCallback {
        public void OnGetData(byte[] data, int len, long timestamp);
    }

    private MediaCodec mediaCodec;
    private int colorFormat;
    private byte[] outBuf;
    private boolean running;
    private long startMs;
    private DataCallback data_cb;
    
    public void setDataCallback(DataCallback cb) {
        data_cb = cb;
    }
    
    void probe(String mimeType) {
        // Find a code that supports the mime type
        int numCodecs = MediaCodecList.getCodecCount();
        MediaCodecInfo codecInfo = null;
        for (int i = 0; i < numCodecs && codecInfo == null; i++) {
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
            if (!info.isEncoder()) {
                continue;
            }
            String[] types = info.getSupportedTypes();
            boolean found = false;
            for (int j = 0; j < types.length && !found; j++) {
                if (types[j].equals(mimeType))
                    found = true;
            }
            if (!found)
                continue;
            codecInfo = info;
        }
        if (codecInfo == null) {
            Log.e("", "codec not found for " + mimeType);
            return;
        }
        else
            Log.d("", "Found " + codecInfo.getName() + " supporting " + mimeType);


        // Find a color profile that the codec supports
        colorFormat = 0;//MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
        for (int i = 0; i < capabilities.colorFormats.length; i++) {
            int format = capabilities.colorFormats[i];
            switch (format) {
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
                if (colorFormat < format)
                    colorFormat = format;
                Log.d("", "probed color format " + format);
                break;
            default:
                Log.d("", "Skipping unsupported color format " + format);
                break;
            }
        }
        Log.d("", "Using color format " + colorFormat);
    }

    public boolean open(int type, int width, int height, int bitrate, int fps, int gop) {
        String mimeType = VideoType.getMimeType(type);
        if (mimeType == null)
            return false;

        probe(mimeType);
        
        // work around for the encoder's alignment requirement
        int stride = (width&15)==0? width: (width&~15)+16;
        
        try {
            mediaCodec = MediaCodec.createEncoderByType(mimeType);
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(mimeType, stride, height);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate*1000);
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, fps);
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, gop<fps? 1:gop/fps);
            //mediaFormat.setInteger("stride", stride);

            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(OMXVideoActivity.instance);
            String val = sp.getString("compress_mode", "0");
            if (0 == val.compareTo("1"))   //vbr
                mediaFormat.setInteger("bitrate-mode", 0);       ////////////////////////////  这个是qcom版本

            mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mediaCodec.start();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            mediaCodec = null;
            return false;
        }
        outBuf = new byte[width*height];
        return true;
    }
    
    public void start() {
        if (running)
            return;
        // start the thread
        running = true;
        startMs = System.currentTimeMillis();
        super.start();
    }

    public void close() {
        try {
            if (running) {
                running = false;
                //do not block main thread
                //this.join();
            }
            if (mediaCodec != null) {
                mediaCodec.stop();
                mediaCodec.release();
            }
        } catch (Exception e){ 
            e.printStackTrace();
        }
    }
    
    public void run() {
        if (mediaCodec == null)
            return;
        ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex;
        try {
          while (running) {
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 1000);
            if (outputBufferIndex >= 0) {
                //Log.i("AvcEncoder", bufferInfo.size + " bytes, "+bufferInfo.flags);
                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                outputBuffer.get(outBuf, 0, bufferInfo.size);
                data_cb.OnGetData(outBuf, bufferInfo.size, bufferInfo.presentationTimeUs/1000);
                outputBuffer.clear();
                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
            }
          }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void _offerEncoder(byte[] input, ByteBuffer input2, int length, long timestamp) {
        if (!running || mediaCodec == null)
            return;
        //{
            ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
            int inputBufferIndex = mediaCodec.dequeueInputBuffer(0);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                //Log.i("", "input buffer size "+inputBuffer.capacity());
                inputBuffer.clear();
                if (input!=null)
                    inputBuffer.put(input, 0, length);
                else if (input2!=null)
                    inputBuffer.put(input2);
                // MediaCodec.queueInputBuffer() accepts pts in microsecond..
                // however, we will restore it to millisecond after dequeueOutputBuffer. Just make
                // it safe here.
                long pts = timestamp * 1000;
                mediaCodec.queueInputBuffer(inputBufferIndex, 0, length, pts, 0);
            }
            else
                Log.w("AvcEncoder", "out of input buffers");
        //}
    }
    
    public void offerEncoder(byte input[], int inputLen, long timestamp) {
        _offerEncoder(input, null, inputLen, timestamp);
    }

    public void offerEncoder(ByteBuffer input, int inputLen, long timestamp) {
        input.position(0);
        input.limit(inputLen);
        _offerEncoder(null, input, inputLen, timestamp);
    }
}
