package com.wbapp.omxvideo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

public class AvcDecoder extends Thread {
    int videoType;
    int spsCode;
    int iCode;
    
    private MediaCodec mediaCodec;
    boolean running;
    
    public void init() {
        listCodecs();
    }

    void listCodecs() {
        int count = MediaCodecList.getCodecCount();
        try {
            FileWriter fw = new FileWriter("/sdcard/omxvideo_codecs.txt");
            for (int i=0; i<count; i++) {
                MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
                /*if (!codecInfo.isEncoder())
                    continue;

                String[] types = codecInfo.getSupportedTypes();
                for (int j = 0; j < types.length; j++) {
                    if (types[j].equalsIgnoreCase(mimeType)) {
                        return codecInfo;
                    }
                }*/
                fw.write(codecInfo.getName()+","+(codecInfo.isEncoder()?"E":"D")+"\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean open(int videoType, int width, int height, Surface surface) {
        // work around for the encoder's alignment requirement
        //int stride = (width&15)==0? width: (width&~15)+16;
        this.videoType = videoType;
        String mimeType = VideoType.getMimeType(videoType);
        if (videoType == VideoType.AVC) {
            spsCode = 0x67;
            iCode = 0x65;
        }
        else if (videoType == VideoType.HEVC) {
            spsCode = 0x40;
            iCode = 0x26;
        }
        else // not supported
            return false;
        try {
            mediaCodec = MediaCodec.createDecoderByType(mimeType);
            //mediaCodec = MediaCodec.createByCodecName("OMX.Intel.hw_vd.h265");
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(mimeType, width, height);
            //mediaFormat.setInteger("stride", stride);
            mediaCodec.configure(mediaFormat, surface, null, 0);
            mediaCodec.start();
        }
        catch (Exception e) {
            e.printStackTrace();
            mediaCodec = null;
        }
        if (mediaCodec == null)
            return false;
        return true;
    }
    
    public void start() {
        if (running)
            return;
        // start the thread
        running = true;
        super.start();
    }
    
    public void close() {
        try {
            if (running) {
                running = false;
                this.join();
            }
            if (mediaCodec != null) {
                mediaCodec.stop();
                mediaCodec.release();
            }
        } catch (Exception e){ 
            e.printStackTrace();
        }
    }
    
    private int getCodecConfigLength(byte[] buf, int off, int len) {
        int length = 0;
        for (int i=4; i<len; i++) {
            if (buf[off+i]==0 && buf[off+i+1]==0 && buf[off+i+2]==0 &&
                    buf[off+i+3]==1 && buf[off+i+4] == iCode) {
                length = i;
                break;
            }
        }
        return length;
    }
    
    public void decodeVideo(byte[] input, int offset, int len, long stamp) {
        //Log.v("AvcDecoder",
        //        String.format("%d, input video %d, %02x %02x %02x ..",
        //                stamp, len, input[offset+3], input[offset+4], input[offset+5]));
        int flags = 0;
        if (input[offset+4] == spsCode) {
            int configLength = getCodecConfigLength(input, offset, len);
            Log.i("AvcDecoder", "codec config length "+configLength+" / "+len);
            flags = MediaCodec.BUFFER_FLAG_CODEC_CONFIG;
            inputVideo(input, offset, configLength, stamp, flags);
            offset += configLength;
            len -= configLength;
            flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
            inputVideo(input, offset, len, stamp, flags);
        }
        else
            inputVideo(input, offset, len, stamp, flags);
    }
    
    private void inputVideo(byte[] input, int offset, int len, long stamp, int flags) {
        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
        // wait infinitely
        int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(input, offset, len);
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, len, stamp, flags);
        }
        else
            Log.w("AvcDecoder", "out of input buffers");
    }
    
    public void run() {
        ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        while (running) {
            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000);
            if (outputBufferIndex >= 0) {
                //Log.v("AvcDecoder", "buffer index "+outputBufferIndex+", "+ bufferInfo.size + " bytes, "+bufferInfo.flags);
                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                // render the buffer
                mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
            }
            else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                outputBuffers = mediaCodec.getOutputBuffers();
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // Subsequent data will conform to new format.
                MediaFormat format = mediaCodec.getOutputFormat();
          }
        }
    }
}
