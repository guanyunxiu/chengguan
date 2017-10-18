package com.wbapp.omxvideo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class PlayActivity extends Activity implements OnTouchListener {

    SurfaceView view;
    ProgressBar progress;
    
    VideoDecoder decoder = new VideoDecoder();
    VideoControl controller = new VideoControl();
    AvcDecoder avcDecoder;
    
    static Audio audio = null;
    
    static final int DEC_SF = 0;
    static final int DEC_MEDIACODEC = 1;
    int decodeMode = DEC_MEDIACODEC;
    
    boolean running;
    
    Toast infoToast;
    String resName;
    String videoInfo;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // disable sleep
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.play);
        view = (SurfaceView) this.findViewById(R.id.play_video_view);
        view.getHolder().addCallback(new SurfaceViewCallback());
        view.setOnTouchListener(this);
        progress = (ProgressBar) findViewById(R.id.play_progress);
        infoToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        resName = "";

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            resName = extras.getString("resource_name");
            decodeMode = extras.getBoolean("hard_dec") ? DEC_MEDIACODEC : DEC_SF;
        }

        videoInfo = String.format("%s\n等待数据..", resName);
        initToast();
        
        if (decodeMode == DEC_MEDIACODEC) {
            avcDecoder = new AvcDecoder();
            avcDecoder.init();
        }
    }
    
    class SurfaceViewCallback implements SurfaceHolder.Callback {
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height) {
            //now it's safe to get view size
            startPlay();
        }
        public void surfaceCreated(SurfaceHolder holder) {
        }
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }
    
    private void startPlay() {
        running = true;
        new RenderTask()//.execute();
            .executeOnExecutor(ThreadPoolService.getVideoPlayExecutor());
        // create 1 audio task and do not stop,
        // otherwise we can end up dead-locked.
        if (audio==null) {
            audio = new Audio(this, OMXVideoActivity.instance.save_audio_only ?
                    Audio.PCM : OMXVideoActivity.instance.audio_type);
            audio.startPlay();
        }
    }
    
    class RenderTask extends AsyncTask<Void, Integer, Void> {
        Bitmap bmp;
        int size;
        int width, height;
        Rect src, dst;
        
        void getSize() {
            size = decoder.getVideoSize();
            if (size == 0)
                return;
            // width and height are packed into one int
            width = size&0xffff;
            height = (size>>16)&0xffff;
        }
        
        void setupBitmap() {
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            int viewWidth = view.getWidth();
            int viewHeight = view.getHeight();
            src = new Rect(0, 0, width - 1, height - 1);
            int dwidth = viewWidth;// viewHeight*width/height;
            int dheight = viewHeight;
            int dx = 0;// (viewWidth-dwidth)/2;
            int dy = 0;
            dst = new Rect(dx, dy, dx + dwidth - 1, dheight - 1);
        }
        
        void runSFDecode() {
            setupBitmap();
            
            SurfaceHolder holder = view.getHolder();

            decoder.setBitmap(bmp);
            Canvas canvas;
            Paint paint = null;//new Paint(Paint.FILTER_BITMAP_FLAG);
            boolean first = true;
            while (running) {
                int len = decoder.updateBitmap();
                if (len<0) //decoder stopped
                {
                    if (len == -2) {
                        //size changed
                        decoder.releaseBitmap(bmp);
                        getSize();
                        setupBitmap();
                        decoder.setBitmap(bmp);
                    }
                    else
                        break;
                }
                if (!running) //user quit while update bitmap
                    break;
                // we are ready to play first frame now.
                // hide the progress bar.
                if (first) {
                    publishProgress(0);
                    first = false;
                }
                //Log.d("", "frame "+len);
                canvas = holder.lockCanvas();
                if(canvas!=null) {
                    canvas.drawBitmap(bmp, src, dst, paint);
                    holder.unlockCanvasAndPost(canvas);
                }
                else
                    Log.e("render", "lock canvas fail");
            }

            decoder.releaseBitmap(bmp);
        }
        
        void runMediaDecode() {
            // allocate a buffer big enough to hold the compressed video frame.
            byte[] buffer = new byte[width*height];
            int length;
            VideoDecoder.VideoFrame frame = decoder.new VideoFrame();
            frame.buffer = buffer;
            
            int videoType = -1;
            while (running) {
                //wait for an I-frame
                //Log.i("", "about to getVideoFrame");
                length = decoder.getVideoFrame(frame);
                //Log.i("", "getVideoFrame returned");
                if (length <= 0) // end of stream
                    return;
                if (frame.buffer[4] == 0x67)
                    videoType = VideoType.AVC;
                else if (frame.buffer[4] == 0x40)
                    videoType = VideoType.HEVC;
                else {
                    Log.i("", String.format("not an I-frame (%x)", frame.buffer[4]));
                    continue;
                }
                Log.i("", "video type is " + (videoType == 0 ? "AVC" : "HEVC"));
                break;
            }
            
            // hide progress bar
            publishProgress(0);
            
            Surface surface = view.getHolder().getSurface();
            
            if (!avcDecoder.open(videoType, width, height, surface)) {
                publishProgress(1, videoType);
                return;
            }
            
            avcDecoder.start();
            
            while (running) {
                avcDecoder.decodeVideo(frame.buffer, 0, frame.length, frame.stamp);
                
                length = decoder.getVideoFrame(frame);
                if (length <= 0)
                    break;
            }
            
            avcDecoder.close();
        }
        
        @Override
        protected Void doInBackground(Void... arg0) {
            getSize();
            if (size == 0) // we stopped before a frame is received.
                return null;
            Log.i("", "video size "+width+"x"+height);
            videoInfo = String.format("%s\n视频尺寸: %dx%d", resName, width, height);
            
            if (decodeMode == DEC_SF) {
                runSFDecode();
            }
            else if (decodeMode == DEC_MEDIACODEC) {
                runMediaDecode();
            }
            Log.i("", "render task end");
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... args) {
            if (args[0] == 0)
                progress.setVisibility(View.GONE);
            else if (args[0] == 1) {
                int videoType = args[1];
                Toast.makeText(PlayActivity.this,
                        "Codec not found for " + (videoType == 0 ? "AVC" : "HEVC"),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
    
    protected void onDestroy() {
        running = false;
        super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item;

        item = menu.add("信息");
        item.setIcon(android.R.drawable.ic_menu_info_details);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem item) {
                infoToast.setText(videoInfo);
                infoToast.show();
                return true;
            }
        });
        
        return true;
    }
    
    
    GestureDetector gest;
    ScaleGestureDetector scale;

    enum TouchState {
      Idle, Scroll, Scale
    };
    TouchState touchState = TouchState.Idle;

    int scrollDir = 0; //0 left, 1 right, 2 up, 3 down
    int scrollCount = 0;
    
    int scaleDir = 0; //0 out, 1 in
    int scaleCount = 0;
    
    Toast showToast;
    ImageView leftImage, rightImage, upImage, downImage, zoominImage, zoomoutImage;

    private void initToast() {
        // use only 1 toast ensures us switch status quickly
        showToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        leftImage = new ImageView(this);
        leftImage.setImageResource(R.drawable.left);
        rightImage = new ImageView(this);
        rightImage.setImageResource(R.drawable.right);
        upImage = new ImageView(this);
        upImage.setImageResource(R.drawable.up);
        downImage = new ImageView(this);
        downImage.setImageResource(R.drawable.down);
        zoominImage = new ImageView(this);
        zoominImage.setImageResource(R.drawable.in);
        zoomoutImage = new ImageView(this);
        zoomoutImage.setImageResource(R.drawable.out);
    }
    
    private void showImage(ImageView image, int gravity) {
        // hide the wait toast for a while..
        showToast.setView(image);
        showToast.setGravity(gravity, 0, 0);
        showToast.show();
    }
    
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_UP) {
            Log.i("", "idle");
            touchState = TouchState.Idle;
            scrollCount = 0;
            scaleCount = 0;
            //source.control(FrameSource.CTL_STOP, 0);
        }
        if(gest==null)
            gest = new GestureDetector(this, new MyGestureListener());
        if(scale==null)
            scale = new ScaleGestureDetector(this, new MyScaleGestureListener());

        boolean r = gest.onTouchEvent(event);
        r &= scale.onTouchEvent(event);
        if(r)
            return true;
        
        return true;
    }
    
    class MyScaleGestureListener implements OnScaleGestureListener {
        public boolean onScale(ScaleGestureDetector s) {
            do {
                if(touchState!=TouchState.Idle && touchState!=TouchState.Scale)
                    break;
                int dir = s.getCurrentSpan()<s.getPreviousSpan()? 0:1;
                if(dir==1) {
                    if(scaleCount<4 && ++scaleCount<4)
                        break;
                }
                else {
                    if(scaleCount>-4 && --scaleCount>-4)
                        break;
                }
                if(touchState!=TouchState.Scale || scaleDir!=dir) {
                    Log.i("", "scale "+dir);
                    touchState = TouchState.Scale;
                    scaleDir = dir;
                    scaleCount=0;
                    if(dir==1) {
                        controller.control(controller.CTL_ZOOMIN);
                        showImage(zoominImage, Gravity.TOP|Gravity.RIGHT);
                    }
                    else {
                        controller.control(controller.CTL_ZOOMOUT);
                        showImage(zoomoutImage, Gravity.TOP|Gravity.RIGHT);
                    }
                }
            } while(false);
            return true;
        }
        public boolean onScaleBegin(ScaleGestureDetector s) {
            return true;
        }
        public void onScaleEnd(ScaleGestureDetector s) {
        }
    }
    
    class MyGestureListener extends SimpleOnGestureListener {
        public boolean onFling(MotionEvent e1, MotionEvent e2, float veloX,
                float veloY) {
            return true;
        }
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distX,
                float distY) {
            if(touchState!=TouchState.Scale) do {
                int dir;
                if(Math.abs(distX)>10){
                    dir = distX>0? 0:1;
                }
                else if(Math.abs(distY)>10) {
                    dir = distY>0? 2:3;
                }
                else
                    break;
                if(dir==1||dir==3) {
                    if(scrollCount<4 && ++scrollCount<4)
                        break;
                }
                else if(dir==0||dir==2) {
                    if(scrollCount>-4 && --scrollCount>-4)
                        break;
                }
                if(touchState!=TouchState.Scroll || scrollDir!=dir) {
                    Log.i("", "scroll "+dir);
                    touchState = TouchState.Scroll;
                    scrollDir = dir;
                    scrollCount = 0;
                    if(dir==0) {
                        controller.control(controller.CTL_LEFT);
                        showImage(leftImage, Gravity.LEFT);
                    }
                    else if(dir==1) {
                        controller.control(controller.CTL_RIGHT);
                        showImage(rightImage, Gravity.RIGHT);
                    }
                    else if(dir==2) {
                        controller.control(controller.CTL_UP);
                        showImage(upImage, Gravity.TOP);
                    }
                    else if(dir==3) {
                        controller.control(controller.CTL_DOWN);
                        showImage(downImage, Gravity.BOTTOM);
                    }
                }
            }
            while(false);
            return true;
        }
    }
}
