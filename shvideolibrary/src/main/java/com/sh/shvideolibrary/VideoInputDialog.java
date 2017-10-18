package com.sh.shvideolibrary;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by zhush on 2016/11/11
 * E-mail zhush@jerei.com
 * PS  小视频输入控件
 */

public class VideoInputDialog extends DialogFragment {

    private static final String TAG = "VideoInputDialog";
    private Camera mCamera;
    private CameraPreview mPreview;
    private ProgressBar mProgressRight,mProgressLeft;
    private MediaRecorder mMediaRecorder;
    private Timer mTimer;
    private final int MAX_TIME = 1000;
    private int mTimeCount;
    private long time;
    private boolean isRecording = false;
    private static String fileName;
    private VideoCall videoCall;

    public static int Q480 = CamcorderProfile.QUALITY_480P;
    public static int Q720 = CamcorderProfile.QUALITY_720P;
    public static int Q1080 = CamcorderProfile.QUALITY_1080P;
    public static int Q21600 = CamcorderProfile.QUALITY_2160P;
    private int quality =CamcorderProfile.QUALITY_480P;

    Context mContext;

    private static boolean flash = false;
    private static boolean cameraFront = false;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            mProgressRight.setProgress(mTimeCount);
            mProgressLeft.setProgress(mTimeCount);
        }
    };
    private Runnable sendVideo = new Runnable() {
        @Override
        public void run() {
            recordStop();
        }
    };
    private ImageView buttonFlash;
    private ImageView button_ChangeCamera;

    public void setVideoCall(VideoCall videoCall) {
        this.videoCall = videoCall;
    }

    public static VideoInputDialog newInstance(VideoCall videoCall,int quality,Context context) {
        VideoInputDialog dialog = new VideoInputDialog();
        dialog.setVideoCall(videoCall);
        dialog.setQuality(quality);
        dialog.setmContext(context);
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.maskDialog);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_video_input, container, false);
        //打开相机
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(getActivity(), mCamera);

        FrameLayout preview = (FrameLayout) v.findViewById(R.id.camera_preview);
        mProgressRight = (ProgressBar) v.findViewById(R.id.progress_right);
        mProgressLeft = (ProgressBar) v.findViewById(R.id.progress_left);
        buttonFlash = ((ImageView) v.findViewById(R.id.buttonFlash));
        buttonFlash.setOnClickListener(flashListener);
        button_ChangeCamera = ((ImageView) v.findViewById(R.id.button_ChangeCamera));
        button_ChangeCamera.setOnClickListener(switchCameraListener);
        mProgressRight.setMax(MAX_TIME);
        mProgressLeft.setMax(MAX_TIME);
        mProgressLeft.setRotation(180);
        ImageButton record = (ImageButton) v.findViewById(R.id.btn_record);
        record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //按下 开始录像
                        if (!isRecording) {
                            if (prepareVideoRecorder()) {
                                time = Calendar.getInstance().getTimeInMillis(); //倒计时
                                mMediaRecorder.start();
                                isRecording = true;
                                mTimer = new Timer();
                                mTimer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        mTimeCount++;
                                        Log.i("mTimeCount",mTimeCount+"****");
                                        mainHandler.post(updateProgress);
                                        if (mTimeCount == MAX_TIME) {
                                            mainHandler.post(sendVideo);
                                        }
                                    }
                                    }, 0, 10);
                            } else {
                                releaseMediaRecorder();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        //抬起 停止录像
                        recordStop();
                        break;
                }
                return true;
            }
        });
        preview.addView(mPreview);
        return v;
    }
    //闪光灯
    View.OnClickListener flashListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isRecording && !cameraFront) {
                if (flash) {
                    flash = false;
                    buttonFlash.setImageResource(R.mipmap.ic_flash_off_white);
                    setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                } else {
                    flash = true;
                    buttonFlash.setImageResource(R.mipmap.ic_flash_on_white);
                    setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                }
            }
        }
    };
    //闪光灯
    public void setFlashMode(String mode) {
        try {
            if (mContext.getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)
                    && mCamera != null
                    && !cameraFront) {

                mPreview.setFlashMode(mode);
                mPreview.refreshCamera(mCamera);

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, R.string.changing_flashLight_mode,
                    Toast.LENGTH_SHORT).show();
        }
    }


    //切换前置后置摄像头
    View.OnClickListener switchCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isRecording) {
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    releaseCamera();
                    chooseCamera();
                } else {
                    //只有一个摄像头不允许切换
                    Toast.makeText(mContext, R.string.only_have_one_camera
                            , Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    //选择摄像头
    public void chooseCamera() {
        if (cameraFront) {
            //当前是前置摄像头
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview
                mCamera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);

            }
        } else {
            //当前为后置摄像头
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview
                mCamera = Camera.open(cameraId);
                if (flash) {
                    flash = false;
                    buttonFlash.setImageResource(R.mipmap.ic_flash_off_white);
                    mPreview.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                }
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);

            }
        }
    }

    /**
     * 找前置摄像头,没有则返回-1
     *
     * @return cameraId
     */
    private int findFrontFacingCamera() {
        int cameraId = -1;
        //获取摄像头个数
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    /**
     * 找后置摄像头,没有则返回-1
     *
     * @return cameraId
     */
    private int findBackFacingCamera() {
        int cameraId = -1;
        //获取摄像头个数
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }
    @Override
    public void onPause() {
        super.onPause();
        recordStop();
        releaseMediaRecorder();
        releaseCamera();
    }

    /**
     * 停止录制
     */
    private void recordStop(){
        if (isRecording) {
            isRecording = false;
            if (isLongEnough()){
                mMediaRecorder.stop();
            }
            releaseMediaRecorder();
            mCamera.lock();
            if (mTimer != null) mTimer.cancel();
            mTimeCount = 0;
            mainHandler.post(updateProgress);

        }
    }


    /**
     *
     * @param ft
     * @param videoCall  录制视频回调
     * @param quality 分辨率
     * @param context
     */
    public static void show(FragmentManager ft,VideoCall videoCall,int quality,Context context){

        DialogFragment newFragment = VideoInputDialog.newInstance(videoCall,quality, context);
        newFragment.show(ft, "VideoInputDialog");
    }



    /** A safe way to get an instance of the Camera object. */
    private static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }



    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
            if (isLongEnough()){
                videoCall.videoPathCall(fileName);
                videoCall.videoFinish(true);
            }else{
                Toast.makeText(getContext(), getString(R.string.chat_video_too_short), Toast.LENGTH_SHORT).show();
                videoCall.videoFinish(false);
            }
            dismiss();
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
    //初始化 mMediaRecorder 用于录像
    private boolean prepareVideoRecorder(){

        if (mCamera==null)
            return  false;
        mMediaRecorder = new MediaRecorder();
        /**
         * 解锁camera
         * 设置输出格式为mpeg_4（mp4），此格式音频编码格式必须为AAC否则网页无法播放
         */
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        //声音
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        //视频
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // 第3步:设置输出格式和编码格式(针对低于API Level 8版本)
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        //设置分辨率为480P
//        mMediaRecorder.setProfile(CamcorderProfile.get(quality));
        // mMediaRecorder.setVideoSize(800, 480);// 视频尺寸
       // mMediaRecorder.setVideoFrameRate(30);// 视频帧频率
        mMediaRecorder.setVideoSize(640,480);
        mMediaRecorder.setVideoFrameRate(30);
        //音频编码格式对应应为AAC
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //视频编码格式对应应为H264
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //路径
        mMediaRecorder.setOutputFile(getOutputMediaFile().toString());


        mMediaRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
        try {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (cameraFront) {
                    mMediaRecorder.setOrientationHint(270);
                } else {
                    mMediaRecorder.setOrientationHint(90);
                }
            }

            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }



    /** Create a File for saving an image or video */
    public static File getOutputMediaFile(){

//        return  new File(getContext().getExternalCacheDir().getAbsolutePath() + "/" + fileName);
//        PackageManager pm = mContext.getPackageManager();
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        if (!dir.exists()){
            dir.mkdir();
        }
         fileName = dir+ "/video_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";
        Log.i("filePath",fileName);
        return  new File(fileName);
    }

    /**
     * 判断录制时间
     * @return
     */
    private boolean isLongEnough(){
        return Calendar.getInstance().getTimeInMillis() - time > 3000;
    }

    /**
     * Created by zhush on 2016/11/11
     * E-mail zhush@jerei.com
     * PS  录制视频回调
     */

    public static interface VideoCall{
        public void videoPathCall(String path);
        void  videoFinish(boolean isFinish);
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }
}
