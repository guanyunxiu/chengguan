package com.wbapp.omxvideo;

import java.nio.ByteBuffer;
import java.util.List;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import com.serenegiant.usb.DeviceFilter;
import com.serenegiant.usb.IFrameCallback;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.USBMonitor.OnDeviceConnectListener;
import com.serenegiant.usb.USBMonitor.UsbControlBlock;
import com.serenegiant.usb.UVCCamera;

public class UVCCamManager {
    
    final boolean DEBUG = true;
    final String TAG = "UVCCamManager";
    
    final int PREVIEW_WIDTH  = 640;
    final int PREVIEW_HEIGHT = 480;
    
    Context context;
    USBMonitor mUSBMonitor;
    List<UsbDevice> devList;
    UVCCamera mUVCCamera;
    Surface previewSurface;
    IFrameCallback frameCallback;

    public UVCCamManager(Context context) {
        this.context = context;
        try{
            mUSBMonitor = new USBMonitor(context, mOnDeviceConnectListener);
        }
        catch(Exception e){
            e.printStackTrace();
            mUSBMonitor = null;
        }
    }
    
    public void destroy() {
        if(mUSBMonitor != null){
            mUSBMonitor.destroy();
            mUSBMonitor = null;
        }
    }
    
    public void register() {
        if (mUSBMonitor != null){
            mUSBMonitor.register();
        }
    }
    
    public void unregister() {
        if (mUSBMonitor != null){
            mUSBMonitor.unregister();
        }
    }

    public int updateDevices() {
        if (DEBUG) Log.i(TAG, "updateDevices:");
        if (mUSBMonitor == null)
            return 0;
        final List<DeviceFilter> filter = DeviceFilter.getDeviceFilters(context, R.xml.device_filter);
        devList = mUSBMonitor.getDeviceList(filter.get(0));
        if (devList == null)
            return 0;        
        return devList.size();
    }

    public void startPreview(Surface surface, IFrameCallback frameCallback) {
        if (devList == null || devList.size() == 0)
            return;
        previewSurface = surface;
        this.frameCallback = frameCallback;
        UsbDevice dev = devList.get(0);
        Log.i("", String.format("UVC Camera:(%x:%x)", dev.getVendorId(), dev.getProductId()));
        mUSBMonitor.requestPermission(dev);
    }
    
    private final OnDeviceConnectListener mOnDeviceConnectListener = new OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            Toast.makeText(context, "USB_DEVICE_ATTACHED", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnect(final UsbDevice device, final UsbControlBlock ctrlBlock, final boolean createNew) {
            if (DEBUG) Log.v(TAG, "onConnect:");
            Toast.makeText(context, "USB Connect", Toast.LENGTH_SHORT).show();
            openCamera(ctrlBlock);
            doStartPreview();
        }

        @Override
        public void onDisconnect(final UsbDevice device, final UsbControlBlock ctrlBlock) {
            if (DEBUG) Log.v(TAG, "onDisconnect:");
            Toast.makeText(context, "USB Disconnect", Toast.LENGTH_SHORT).show();
            closeCamera();
        }
        @Override
        public void onDettach(final UsbDevice device) {
            Toast.makeText(context, "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onCancel() {
        }
    };

    void openCamera(final UsbControlBlock ctrlBlock) {
        if (DEBUG) Log.v(TAG, "handleOpen:");
        closeCamera();
        mUVCCamera = new UVCCamera();
        mUVCCamera.open(ctrlBlock);
    }

    void closeCamera() {
        if (DEBUG) Log.v(TAG, "handleClose:");
        if (mUVCCamera != null) {
            mUVCCamera.close();
            mUVCCamera.destroy();
            mUVCCamera = null;
        }
    }
    
    void doStartPreview() {
        if (DEBUG) Log.v(TAG, "doStartPreview:");
        if (mUVCCamera == null) return;

        try {
            mUVCCamera.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT, UVCCamera.FRAME_FORMAT_MJPEG);
        } catch (final IllegalArgumentException e) {
            try {
                // fallback to YUV mode
                mUVCCamera.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT, UVCCamera.DEFAULT_PREVIEW_MODE);
            } catch (final IllegalArgumentException e1) {
                closeCamera();
                return;
            }
        }
        
        mUVCCamera.setPreviewDisplay(previewSurface);
        mUVCCamera.startPreview();
        mUVCCamera.setFrameCallback(frameCallback, UVCCamera.PIXEL_FORMAT_NV21);
    }
    
    void stopPreview() {
        if (mUVCCamera != null) {
            mUVCCamera.stopPreview();
            mUVCCamera.setFrameCallback(null, 0);
        }
    }
}
