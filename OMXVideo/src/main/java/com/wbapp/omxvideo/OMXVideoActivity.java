package com.wbapp.omxvideo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.serenegiant.usb.IFrameCallback;
import com.serenegiant.widget.UVCCameraTextureView;
import com.wbapp.suspend.DesktopLayout;
import com.wbapp.suspend.ShowSuspendWindows;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButtonsController;
import android.widget.ZoomButtonsController.OnZoomListener;
import android.widget.ZoomControls;

public class OMXVideoActivity extends Activity implements
        SurfaceHolder.Callback {

    private int enc_mode; // 0:OMX, 1:MediaCodec, 2:soft
    final int ENC_OMX = 0;
    final int ENC_HARD = 1;
    final int ENC_SOFT = 2;

    final int CAM_BACK = 0;
    final int CAM_FRONT = 1;
    final int CAM_FRONT2 = 3;
    final int CAM_EXT = 2;
    private int currentCamId = CAM_BACK;

    private int video_enc_type;

    // 2 AvcEncoders, [0] is for transmitting, [1] is for saving.
    private AvcEncoder[] avcEncoders = new AvcEncoder[2];
    private boolean swap_uv = true;
    private boolean soft_swap = false; //swap UV in soft encode mode.
    private boolean c_420sp_420p = false;

    private GPS gps;
    private StateTask stateTask;
    private PreviewOSDTask previewOSDTask;

    UVCCamManager uvcManager;

    SurfaceView surface_camera;
    UVCCameraTextureView uvccam_view;
    TextView uvccam_info;
    private View container;
    private MenuItem recMenuItem;
    private MenuItem saveMenuItem;
    private MenuItem markMenuItem;
    private TextView infoText;
    private TextView previewOsdTimeText;
    private TextView previewOsdGpsText;
    private TextView previewOsdNameText;
    private ImageView recImageView;
    private ImageView saveImageView;
    private ImageView switchImageView;
    private TextView saveDurationView;
    private Animation recsaveAnimation;

    String server_addr;
    int cmd_port;
    int data_port;
    int transmode;
    int trans_width = 640;
    int trans_height = 480;
    int save_width = 1920;
    int save_height = 1080;
    int bitrate;
    int fps;
    int gop;
    int save_bitrate;
    String flashlight;
    int pic_width = 1280;
    int pic_height = 720;
    String pic_suffix;
    String pic_flashlight;
    int pic_continuous = 0;
    String ftp_user;
    String ftp_password;
    int ftp_port;
    String save_path;
    int save_filesize;
    int save_duration;
    public boolean save_audio_only;
    public int audio_type;
    boolean dual_encoding;
    boolean rotate;
    boolean needScale;
    int router_mode;
    boolean use_uvccam;

    int suspend_window;

    int osd_mask;
    String osd_devname;
    boolean osd_devname_refreshed;

    private String gpsString;

    Camera camera;
    boolean preview = false;
    boolean previewPicture = false;
    boolean recording = false;
    boolean saving = false;
    boolean savingAudio = false;
    boolean important = false;
    boolean disabled = false;

    Audio audio;
    Sound sound;

    int zoom;
    int maxZoom;
    int zoomStep;
    boolean canZoom;
    boolean canSmoothZoom;

    ZoomButtonsController zoomer;

    Object encodeLock = new Object();
    Object audioLock = new Object();

    private void msleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void msgBox(String msg) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(msg);
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public void msgBoxAndExit(String msg) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(msg);
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                exit();
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public static final OMXVideoJNI n = OMXVideoJNI.getInstance();
    public static OMXVideoActivity instance = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // disable sleep
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // full screen
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);

        instance = this;
        loadPreference();

        surface_camera = (SurfaceView) this.findViewById(R.id.surface_camera);
        SurfaceHolder holder = surface_camera.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(this);

        uvccam_view = (UVCCameraTextureView) findViewById(R.id.uvccam_view);
        uvccam_view.setAspectRatio(640 / 480.f);
        uvccam_view.setSurfaceTextureListener(mUVCCamSurfaceTextureListener);
        uvccam_info = (TextView) findViewById(R.id.uvccam_info);

        previewOsdTimeText = (TextView) this.findViewById(R.id.preview_osd_time);
        previewOsdGpsText = (TextView) this.findViewById(R.id.preview_osd_gps);
        previewOsdNameText = (TextView) this.findViewById(R.id.preview_osd_name);
        infoText = (TextView) this.findViewById(R.id.info_text);
        recImageView = (ImageView) this.findViewById(R.id.recImageView);
        saveImageView = (ImageView) this.findViewById(R.id.saveImageView);
        switchImageView = (ImageView) this.findViewById(R.id.switchImageView);
        saveDurationView = (TextView) this.findViewById(R.id.save_duration_view);

        container = findViewById(R.id.container);

        recsaveAnimation = new AlphaAnimation(0.0f, 1.0f);
        recsaveAnimation.setDuration(1000); //You can manage the blinking time with this parameter
        recsaveAnimation.setStartOffset(20);
        recsaveAnimation.setRepeatMode(Animation.REVERSE);
        recsaveAnimation.setRepeatCount(Animation.INFINITE);

        zoomer = new ZoomButtonsController(findViewById(R.id.zoom_view));
        zoomer.setAutoDismissed(true);
        zoomer.setFocusable(true);

        Log.i("", android.os.Build.BRAND + ", " + android.os.Build.MANUFACTURER
                + ", " + android.os.Build.PRODUCT + ", "
                + android.os.Build.HARDWARE + ", " + android.os.Build.DEVICE
                + ", " + android.os.Build.MODEL + ", "
                + android.os.Build.VERSION.RELEASE);
        String brand = android.os.Build.BRAND;
        String manufact = android.os.Build.MANUFACTURER;
        String hardware = android.os.Build.HARDWARE;
        String model = android.os.Build.MODEL;

        n.init(this);
        n.setDeviceInfo(brand, manufact, hardware, model);

        if ((hardware.equalsIgnoreCase("smdk4x12")
                && !android.os.Build.VERSION.RELEASE.startsWith("4.3")) ||
                hardware.equalsIgnoreCase("sc8830")) {
            swap_uv = false;
        }

        String needConvertHW[] = {"mt6592", "mt6595", "mt8752", "mt6735", "mt6795"};

        for (String hw : needConvertHW)
            if (hardware.equalsIgnoreCase(hw)) {
                c_420sp_420p = true;
                break;
            }

        gps = new GPS(this);
        gps.start();

        sound = new Sound(this);

        stateTask = new StateTask();
        // stateTask.execute();
        stateTask.executeOnExecutor(ThreadPoolService.getThreadPoolExecutor());

        previewOSDTask = new PreviewOSDTask();
        previewOSDTask.executeOnExecutor(ThreadPoolService.getThreadPoolExecutor());

        registerReceiver(batteryLowReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_LOW));

        if (suspend_window == 1) {
            ShowSuspendWindows suspendWindows = new ShowSuspendWindows(this);
            suspendWindows.start();
        }

        //注册广播
        registerReceiver(ClientActivity.mHomeKeyEventReceiver, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    private BroadcastReceiver batteryLowReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            //int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            //int temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            //int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            n.addVideoLog(13, "");
        }
    };

    void showHideViews() {
        if (use_uvccam) {
            surface_camera.setVisibility(View.INVISIBLE);
            //switchImageView.setVisibility(View.INVISIBLE);
            uvccam_view.setVisibility(View.VISIBLE);
            uvccam_info.setVisibility(View.VISIBLE);
        } else {
            surface_camera.setVisibility(View.VISIBLE);
            //switchImageView.setVisibility(View.VISIBLE);
            uvccam_view.setVisibility(View.INVISIBLE);
            uvccam_info.setVisibility(View.INVISIBLE);
        }
    }

    class StateTask extends AsyncTask<Void, String, Void> {
        final String[] stateMsg = {"初始化", "正在拨号", "拨号成功", "正在解析服务器地址",
                "正在连接服务器", "正在验证服务器", "服务器已经连接", "正在传输视频", "正在休眠"};

        @Override
        protected Void doInBackground(Void... params) {
            while (!isCancelled()) {
                int state = n.getState();
                if (state == 7 && !recording)
                    publishProgress("视频传输停止");
                else if (0 <= state && state < stateMsg.length)
                    publishProgress(stateMsg[state]);
                msleep(5000);
            }
            return null;
        }

        protected void onProgressUpdate(String... params) {
            infoText.setText(params[0]);
        }
    }

    class PreviewOSDTask extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String defaultNameString = String.format("视频%d", n.getDevId());

            while (!isCancelled()) {
                String timeString = getCurrentTimeString();
                publishProgress("time", timeString);

                if (gpsString != null && !gpsString.isEmpty()) {
                    publishProgress("gps", gpsString);
                }

                if (osd_devname_refreshed) {
                    osd_devname_refreshed = false;
                    String nameString;
                    if (osd_devname != null && !osd_devname.isEmpty()) {
                        nameString = osd_devname;
                    } else {
                        nameString = defaultNameString;
                    }
                    publishProgress("name", nameString);
                }

                msleep(1000);
            }
            return null;
        }

        private String getCurrentTimeString() {
            Calendar cal = Calendar.getInstance();
            return String.format("%d-%02d-%02d %02d:%02d:%02d",
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
        }

        protected void onProgressUpdate(String... params) {
            if (params[0].equals("time")) {
                previewOsdTimeText.setText(params[1]);
            } else if (params[0].equals("gps")) {
                previewOsdGpsText.setText(params[1]);
            } else if (params[0].equals("name")) {
                previewOsdNameText.setText(params[1]);
            }
        }
    }

    public void setGpsString(String str) {
        gpsString = str;
    }

    public void onSurfaceViewClick(View v) {
        openOptionsMenu();
        //if (zoomer != null)
        //    zoomer.setVisible(true);
        doFocus();
    }

    void doFocus() {
        if (camera != null && preview) {
            try {
                camera.autoFocus(new AutoFocusCallback() {
                    public void onAutoFocus(boolean success, Camera camera) {
                        Log.i("", "auto focus "
                                + (success ? "success" : "failed"));
                        camera.cancelAutoFocus();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadPreference() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        try {
            String val;
            val = sp.getString("server_addr",
                    getString(R.string.def_server_addr));
            server_addr = val;
            val = sp.getString("cmd_port", getString(R.string.def_cmd_port));
            cmd_port = Integer.valueOf(val);
            val = sp.getString("data_port", getString(R.string.def_data_port));
            data_port = Integer.valueOf(val);
            val = sp.getString("transmode", getString(R.string.def_transmode));
            transmode = val.equals("UDP") ? 0 : 1;

            val = sp.getString("video_enc_type", "h264");
            if (val.equals("h264"))
                video_enc_type = VideoType.AVC;
            else if (val.equals("h265"))
                video_enc_type = VideoType.HEVC;

            val = sp.getString("size", getString(R.string.def_size));
            String[] wh = val.split("x");
            if (wh.length > 1) {
                trans_width = Integer.valueOf(wh[0]);
                trans_height = Integer.valueOf(wh[1]);
            }
            Log.i("", "size: " + trans_width + "x" + trans_height);
            val = sp.getString("bitrate", getString(R.string.def_bitrate));
            bitrate = Integer.valueOf(val);
            val = sp.getString("fps", getString(R.string.def_fps));
            fps = Integer.valueOf(val);
            val = sp.getString("gop", getString(R.string.def_gop));
            gop = Integer.valueOf(val);

            val = sp.getString("save_bitrate",
                    getString(R.string.def_save_bitrate));
            save_bitrate = Integer.valueOf(val);

            flashlight = sp.getString("flashlight",
                    getString(R.string.def_flashlight));

            pic_suffix = "";
            val = sp.getString("pic_size", getString(R.string.def_pic_size));
            String[] ss = val.split("x");
            if (ss.length > 1) {
                pic_width = Integer.valueOf(ss[0]);
                pic_height = Integer.valueOf(ss[1]);
                if (ss.length > 2)
                    pic_suffix = ss[2];
            }
            pic_flashlight = sp.getString("pic_flashlight",
                    getString(R.string.def_flashlight));
            val = sp.getString("pic_continuous", "0");
            pic_continuous = Integer.parseInt(val);

            ftp_user = sp.getString("ftp_user",
                    getString(R.string.def_ftp_user));
            ftp_password = sp.getString("ftp_password",
                    getString(R.string.def_ftp_password));
            val = sp.getString("ftp_port", getString(R.string.def_ftp_port));
            ftp_port = Integer.parseInt(val);

            val = sp.getString("save_dir", getString(R.string.def_save_dir));
            save_path = "/mnt/sdcard/" + val;
            val = sp.getString("save_filesize",
                    getString(R.string.def_save_filesize));
            save_filesize = Integer.parseInt(val);

            val = sp.getString("save_duration", "5");
            save_duration = Integer.parseInt(val);

            save_audio_only = sp.getBoolean("save_audio_only", false);

            val = sp.getString("audio_type", getString(R.string.def_audio_type));
            if (val.equalsIgnoreCase("AMR"))
                audio_type = Audio.AMR;
            else if (val.equalsIgnoreCase("AAC"))
                audio_type = Audio.AAC;
            else
                audio_type = Audio.AMR;

            boolean osd_time = sp.getBoolean("osd_time", true);
            boolean osd_name = sp.getBoolean("osd_name", true);
            osd_mask = (osd_time ? 1 : 0) | (osd_name ? 2 : 0) | /*gps*/4;
            osd_devname = sp.getString("osd_devname", getString(R.string.def_osd_devname));
            if (osd_devname == null || osd_devname.isEmpty())
                osd_devname = getString(R.string.def_osd_devname);
            // notify the preview osd to update.
            osd_devname_refreshed = true;

            dual_encoding = sp.getBoolean("dual_encoding", false);
            enc_mode = sp.getBoolean("hard_enc", true) ? ENC_HARD : ENC_SOFT;
            rotate = sp.getBoolean("rotate", false);

            if (dual_encoding) {
                val = sp.getString("save_size", getString(R.string.def_save_size));
                String[] savesize = val.split("x");
                if (savesize.length > 1) {
                    save_width = Integer.valueOf(savesize[0]);
                    save_height = Integer.valueOf(savesize[1]);
                }
                Log.i("", "save size: " + save_width + "x" + save_height);
                needScale = trans_width != save_width || trans_height != save_height;
            } else {
                save_width = trans_width;
                save_height = trans_height;
                needScale = false;
            }

            val = sp.getString("router_mode", "0");
            router_mode = Integer.parseInt(val);

            val = sp.getString("suspend_window", "0");
            suspend_window = Integer.parseInt(val);

            use_uvccam = false;
            val = sp.getString("cam_choice", "back");
            if (val.equals("back"))
                currentCamId = CAM_BACK;
            else if (val.equals("front"))
                currentCamId = CAM_FRONT;
            else if (val.equals("front2"))
                currentCamId = CAM_FRONT2;
            else if (val.equals("external")) {
                currentCamId = CAM_EXT;
                use_uvccam = true;
            }

        } catch (NumberFormatException ex) {
            msgBox("设置参数错误");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem recItem = menu.add(R.string.start_rec);
        recItem.setIcon(android.R.drawable.ic_menu_upload);
        recItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                onRecClick(null);
                return true;
            }
        });
        recMenuItem = recItem;

        MenuItem saveItem = menu.add(R.string.save);
        saveItem.setIcon(android.R.drawable.ic_menu_save);
        saveItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                onSaveClick(null);
                return true;
            }
        });
        saveMenuItem = saveItem;

        MenuItem shotItem = menu.add(R.string.shot_pic);
        shotItem.setIcon(android.R.drawable.ic_menu_camera);
        shotItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                onShotClick(null);
                return true;
            }
        });
        
        /*MenuItem setItem = menu.add("设置");
        setItem.setIcon(android.R.drawable.ic_menu_preferences);
        setItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                onSettingsClick(null);
                return true;
            }
        }); */

        /*MenuItem savedItem = menu.add("回放");
        savedItem.setIcon(android.R.drawable.ic_menu_rotate);
        savedItem
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        final Activity activity = OMXVideoActivity.this;
                        final EditText pswdEditText = new EditText(activity);
                        pswdEditText.setHint("Password");
                        pswdEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        new AlertDialog.Builder(activity)
                            .setTitle("输入密码").setIcon(android.R.drawable.ic_dialog_info)
                            .setView(pswdEditText)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent replayIntent = new Intent(
                                            getApplicationContext(),
                                            ReplayActivity.class);
                                    if (pswdEditText.getText().toString().equals("888888")) {
                                        //stopPreview();
                                        replayIntent.putExtra("saveDir", save_path);
                                        startActivity(replayIntent);
                                    }
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                        
                        return true;
                    }
                }); */

        MenuItem lightItem = menu.add("关灯");
        lightItem.setIcon(android.R.drawable.ic_menu_view);
        lightItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                enableDisableView(container, false);
                disabled = true;
                TextView tip = (TextView) findViewById(R.id.light_off_tip);
                tip.setVisibility(View.VISIBLE);
                /*if (recording) {
                    // try 'hard' turn off (root)
                    if (n.turnLightOff()) {
                        return true;
                    }
                }*/
                // 'soft' turn off, reliable
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.screenBrightness = 0.01f;
                getWindow().setAttributes(lp);
                return true;
            }
        });


      /*  MenuItem clientItem = menu.add("监控");
        clientItem.setIcon(android.R.drawable.ic_menu_agenda);
        clientItem
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        onClientClick(null);
                        return true;
                    }
                });*/

        MenuItem checkWorkItem = menu.add("考勤");
        checkWorkItem.setIcon(android.R.drawable.ic_dialog_alert);
        checkWorkItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });

        MenuItem alarmItem = menu.add("报警");
        alarmItem.setIcon(android.R.drawable.ic_dialog_alert);
        alarmItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                doAlarm();
                return true;
            }
        });

        markMenuItem = menu.add("标重点文件");
        markMenuItem.setIcon(android.R.drawable.ic_menu_compass);
        markMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (saving)
                    toggleFileImportant();
                return true;
            }
        });

        MenuItem quitItem = menu.add("退出");
        quitItem.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        quitItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                exit();
                quit();
                return true;
            }
        });

        // set UI properly for the first creation time.
        if (recording)
            startRecUI();
        else
            stopRecUI();
        if (saving)
            startSaveUI();
        else
            stopSaveUI();

        return true;
    }

    public void onSettingsClick(View view) {
        Intent prefsIntent = new Intent(getApplicationContext(),
                VideoSettingsActivity.class);
        //if (recording)
        //    stopRecord();
        //stopPreview();
        startActivity(prefsIntent);
    }

    public void onClientClick(View view) {
        Intent clientIntent = new Intent(
                getApplicationContext(), ClientActivity.class);
        if (recording)
            stopRecord();
        stopPreview();
        startActivity(clientIntent);
    }

    private void toggleFileImportant() {
        if (important) {
            n.unsetFileImportant();
            important = false;
            markMenuItem.setTitle("标重点文件");
        } else {
            n.setFileImportant();
            important = true;
            markMenuItem.setTitle("取消重点文件");
        }
    }

    private void resetImportant() {
        important = false;
        if (markMenuItem != null)
            markMenuItem.setTitle("标重点文件");
    }

    // recursive..
    private void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;

            for (int idx = 0; idx < group.getChildCount(); idx++) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }

    void initZoom() {
        if (!camera.getParameters().isZoomSupported()) {
            Log.w("", "cannot zoom");
            canZoom = false;
            zoomer.setZoomInEnabled(false);
            zoomer.setZoomOutEnabled(false);
            return;
        }
        if (camera.getParameters().isSmoothZoomSupported()) {
            canSmoothZoom = true;
        } else {
            canSmoothZoom = false;
            Log.w("", "cannot smooth zoom");
        }
        // zoomer.getChildAt(0).getBackground().setAlpha(180);
        // zoomer.getChildAt(1).getBackground().setAlpha(180);
        zoom = camera.getParameters().getZoom();
        maxZoom = camera.getParameters().getMaxZoom();
        canZoom = true;
        Log.i("", "current zoom " + zoom + ". max zoom " + maxZoom);
        zoomStep = maxZoom / 6;
        if (zoomStep == 0)
            zoomStep = 1;
        zoomer.setOnZoomListener(new ZoomListener());
        updateZoomer();
    }

    void updateZoomer() {
        zoomer.setZoomInEnabled(zoom < maxZoom);
        zoomer.setZoomOutEnabled(zoom > 0);
    }

    class ZoomListener implements OnZoomListener {
        public void onVisibilityChanged(boolean visible) {
        }

        public void onZoom(boolean zoomIn) {
            if (!(camera != null && preview && canZoom))
                return;
            if (zoomIn) {
                if (zoom < maxZoom) {
                    zoom += zoomStep;
                    if (zoom > maxZoom)
                        zoom = maxZoom;
                }
            } else {
                if (zoom > 0) {
                    zoom -= zoomStep;
                    if (zoom < 0)
                        zoom = 0;
                }
            }

            if (canSmoothZoom) {
                camera.startSmoothZoom(zoom);
            } else {
                Parameters params = camera.getParameters();
                params.setZoom(zoom);
                camera.setParameters(params);
            }

            updateZoomer();
        }
    }

    private boolean openAvcEncoder() {
        int w, h;
        boolean res = true;

        if (avcEncoders[0] == null) {
            avcEncoders[0] = new AvcEncoder();
            avcEncoders[0].setDataCallback(new AvcEncoder.DataCallback() {
                public void OnGetData(byte[] buf, int len, long timestamp) {
                    n.sendAvcData(0, buf, len, timestamp);
                }
            });
            if (rotate) {
                w = trans_height;
                h = trans_width;
            } else {
                w = trans_width;
                h = trans_height;
            }
            res = avcEncoders[0].open(video_enc_type, w, h, bitrate, fps, gop);
        }
        if (dual_encoding && !save_audio_only) {
            if (avcEncoders[1] == null) {
                avcEncoders[1] = new AvcEncoder();
                avcEncoders[1].setDataCallback(new AvcEncoder.DataCallback() {
                    public void OnGetData(byte[] buf, int len, long timestamp) {
                        n.sendAvcData(1, buf, len, timestamp);
                    }
                });
                if (rotate) {
                    w = save_height;
                    h = save_width;
                } else {
                    w = save_width;
                    h = save_height;
                }
                res &= avcEncoders[1].open(video_enc_type, w, h, save_bitrate, fps, gop);
            }
        }
        if (!res)
            Toast.makeText(this, "Failed to create codec", Toast.LENGTH_LONG).show();
        return res;
    }

    private void startAvcEncoder() {
        if (avcEncoders[0] != null)
            avcEncoders[0].start();
        if (avcEncoders[1] != null)
            avcEncoders[1].start();
    }

    private void closeAvcEncoder() {
        if (recording || saving)
            return;
        avcEncoders[0].close();
        avcEncoders[0] = null;
        if (avcEncoders[1] != null) {
            avcEncoders[1].close();
            avcEncoders[1] = null;
        }
    }

    // rec button
    public void onRecClick(View v) {
        if (!recording) {
            startRecord(true);
        } else {
            stopRecord();
            saveState();
        }
        sound.playStartStop();
    }

    private boolean doStartEnc() {
        int rot = 0;
        if (rotate) {
            if (currentCamId == CAM_EXT)
                rot = 1;
            else
                rot = 1 + currentCamId;
        }

        byte[] devnameBytes = null;
        try {
            devnameBytes = osd_devname.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // avoid overlapping in transmitting and saving.
        synchronized (encodeLock) {
            Log.i("n", "calling start");
            if (!n.start(trans_width, trans_height, bitrate, fps, gop,
                    save_width, save_height, save_bitrate,
                    dual_encoding, enc_mode, rot, osd_mask, soft_swap,
                    audio_type, devnameBytes))
                return false;
            Log.i("n", "started");

            if (enc_mode == ENC_HARD)
                startAvcEncoder();
        }
        return true;
    }

    private void startRecord(final boolean saveStateWhenSucceed) {
        if (audio == null)
            audio = new Audio(this, save_audio_only ? Audio.PCM : audio_type);

        if (enc_mode == ENC_HARD) {
            if (!openAvcEncoder())
                return;
            Log.i("", "avcEncoder created");
        }

        // put the lengthy work in background..
        // so we can update the UI immediately
        startRecUI();

        AsyncTask<Integer, Integer, Integer> task = new AsyncTask<Integer, Integer, Integer>() {
            @Override
            protected Integer doInBackground(Integer... params) {
                Log.i("n", "calling netConfig");
                if (!n.netConfig(server_addr, cmd_port, data_port, transmode,
                        router_mode))
                    return -1;
                Log.i("n", "netConfiged");
                n.startTransmit();

                if (!doStartEnc())
                    return -2;

                // start record succeeded.
                // make us stoppable in the future.
                recording = true;

                audio.startPlay();
                // run audio record in background
                startAudioRecord();

                return 0;
            }

            protected void onPostExecute(Integer result) {
                if (result == 0) {
                    if (saveStateWhenSucceed)
                        saveState();
                    return;
                } else if (result < 0) {
                    msgBox("录像失败！");
                }
                stopRecUI();
            }
        };

        // task.execute();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void stopRecord() {
        if (recording) {
            recording = false;
            stopRecUI();
            stopAudioRecord();
            n.stopTransmit();
            if (enc_mode == ENC_HARD) {
                closeAvcEncoder();
            }
        }
    }

    private void startRecUI() {
        if (recMenuItem != null) {
            recMenuItem.setTitle(R.string.stop_rec);
            //recButton.setBackgroundResource(R.drawable.rec_back);
        }
        recImageView.setVisibility(View.VISIBLE);
        recImageView.setAnimation(recsaveAnimation);
    }

    private void stopRecUI() {
        if (recMenuItem != null) {
            recMenuItem.setTitle(R.string.start_rec);
            //recButton.setBackgroundColor(getResources().getColor(
            //        android.R.color.transparent));
        }
        recImageView.setVisibility(View.INVISIBLE);
        recImageView.setAnimation(null);
    }

    void startAudioRecord() {
        synchronized (audioLock) {
            if (audio == null)
                audio = new Audio(this, save_audio_only ? Audio.PCM : audio_type);
            if (audio.getIsRecording())
                return;
            audio.startRecord();
        }
    }

    void stopAudioRecord() {
        // stop only if both transmitting and saving stopped
        if (audio.getIsRecording()) {
            if (!recording && !saving) {
                // once started, never stop record audio,
                // for realtime audio call.
                //audio.stopRecord();
            }
        }
    }

    private void startSaveAudio() {
        //TODO: resolve conflict with video save.
        if (saving) {
            stopSave();
            saveState();
        }

        savingAudio = true;

        if (!n.initSave(save_path, save_filesize, save_duration, true, audio.getSampleRate(),
                onNewFileListener)) {
            return;
        }
        n.startSave();

        startAudioRecord();
    }

    private void stopSaveAudio() {
        stopAudioRecord();

        n.stopSave();

        savingAudio = false;
    }

    private void toggleSaveAudio() {
        if (savingAudio)
            stopSaveAudio();
        else
            startSaveAudio();
        sound.playStartStop();
    }

    public void onSaveClick(View v) {
        if (!saving) {
            startSave(true);
        } else {
            stopSave();
            saveState();
        }
        sound.playStartStop();
    }

    void startSave(final boolean saveStateWhenSucceed) {
        if (savingAudio)
            stopSaveAudio();

        if (!saving) {
            if (enc_mode == ENC_HARD) {
                if (!openAvcEncoder())
                    return;
            }
            startSaveUI();

            if (audio == null)
                audio = new Audio(this, save_audio_only ? Audio.PCM : audio_type);

            // new SaveTask().execute();
            SaveTask task = new SaveTask(saveStateWhenSucceed);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    void stopSave() {
        if (saving) {
            stopSaveUI();
            saving = false;
            if (enc_mode == ENC_HARD) {
                closeAvcEncoder();
            }
            stopAudioRecord();
            n.stopSave();

            if (important)
                resetImportant();
        }
    }

    private void startSaveUI() {
        if (saveMenuItem != null) {
            saveMenuItem.setTitle(R.string.stop_save);
            //saveButton.setBackgroundResource(R.drawable.btn_pressed);
        }
        saveImageView.setVisibility(View.VISIBLE);
        saveImageView.setAnimation(recsaveAnimation);
        if (markMenuItem != null)
            markMenuItem.setEnabled(true);
        saveDurationView.setText("00:00 00:00");
        saveDurationView.setVisibility(View.VISIBLE);
    }

    private void stopSaveUI() {
        if (saveMenuItem != null) {
            saveMenuItem.setTitle(R.string.save);
            //saveButton
            //    .setBackgroundResource(android.R.drawable.menuitem_background);
        }
        saveImageView.setAnimation(null);
        saveImageView.setVisibility(View.INVISIBLE);
        if (markMenuItem != null)
            markMenuItem.setEnabled(false);
        saveDurationView.setVisibility(View.INVISIBLE);
    }

    class SaveTask extends AsyncTask<Void, Void, Integer> {
        boolean saveStateWhenSucceed;
        long startTime;

        public SaveTask(boolean saveStateWhenSucceed) {
            this.saveStateWhenSucceed = saveStateWhenSucceed;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            if (!n.initSave(save_path, save_filesize, save_duration, save_audio_only, audio.getSampleRate(),
                    onNewFileListener))
                return -1;
            n.startSave();

            if (!doStartEnc())
                return -2;

            saving = true;

            startAudioRecord();

            startTime = System.currentTimeMillis();

            // loop for the save duration view
            while (saving) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                }
                publishProgress();
            }

            return 0;
        }

        @Override
        protected void onProgressUpdate(Void... params) {
            long time = System.currentTimeMillis();
            int dura = (int) ((time - startTime) / 1000);
            int sec = dura % 60;
            int min = (dura / 60) % 60;
            int hour = dura / 60 / 60;
            String text = String.format("00:%02d %02d:%02d", hour, min, sec);
            saveDurationView.setText(text);
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0) {
                if (saveStateWhenSucceed)
                    saveState();
                return;
            } else if (result == -1) {
                msgBox("存储不足！");
            } else if (result < 0) {
                n.stopSave();
                msgBox("录像失败！");
            }
            stopSaveUI();
        }
    }

    ;

    private OMXVideoJNI.OnNewFileListener onNewFileListener = new OMXVideoJNI.OnNewFileListener() {
        @Override
        public void onNewFile() {
            Log.i("", "new save file");
            mHandler.post(mNewFileRunnable);
        }
    };

    private Handler mHandler = new Handler();

    private Runnable mNewFileRunnable = new Runnable() {
        @Override
        public void run() {
            if (important)
                resetImportant();
        }
    };

    private void openBackCamera() {
        if (camera != null) {
            try {
                camera.reconnect();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Log.i("", "camera reopened");
        } else {
            for (int j = 0; j < 3; j++) {
                try {
                    camera = Camera.open();
                } catch (Exception e) {
                    e.printStackTrace();
                    camera = null;
                    if (j == 2)
                        break;
                    msleep(5000);
                    continue;
                }
                break;
            }
            if (camera == null) {
                // we can end up here if the hang-up key
                // is pressed, crap..
                msgBox("打开摄像头失败");
                return;
            }
            Log.i("", "camera opened");
        }
    }

    final boolean debug = false;

    private void openFrontCamera(int currentcamid) {
        if (camera != null) {
            try {
                camera.reconnect();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Log.i("", "camera reopened front");
            return;
        }

        CameraInfo info = new CameraInfo();
        int i, n = Camera.getNumberOfCameras();
        for (i = 0; i < n; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == currentcamid) {
                for (int j = 0; j < 3; j++) {
                    try {
                        camera = Camera.open(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                        camera = null;
                        if (j == 2)
                            break;
                        msleep(5000);
                        continue;
                    }
                    break;
                }
            }
        }
        if (camera == null) {
            msgBox("无法打开前置摄像头");
        }
    }

    private void openCamera() {
        if (currentCamId == CAM_BACK)
            openBackCamera();
        else if (currentCamId == CAM_FRONT)
            openFrontCamera(1);
        else if (currentCamId == CAM_FRONT2)
            openFrontCamera(2);
    }

    private void closeCamera() {
        if (camera != null) {
            //camera.unclock();
            camera.release();
            camera = null;
        }
    }

    private void switchCamera() {
        if (recording || saving) {
            return;
        }

        stopPreview();
        closeCamera();

        if (currentCamId == CAM_BACK) {
            openFrontCamera(1);
            if (camera != null)
                currentCamId = CAM_FRONT;
        } else {
            openBackCamera();
            if (camera != null)
                currentCamId = CAM_BACK;
        }

        startPreview(surface_camera.getHolder());
    }

    public void onSwitchClick(View view) {
        switchCamera();
    }

    int frameSize = 0;
    int frameLen;
    final int bufCount = 3;
    byte[][] frameBufs = new byte[bufCount][];
    byte[] dstYuvBuf;

    int preview_width, preview_height;
    int scale_width, scale_height;
    int scaleFrameLen;
    byte[] scaleYuvBuf;

    int[] setupPreviewFps(List<int[]> ranges) {
        int fps1000 = fps * 1000;
        int[] f = null;
        for (int[] r : ranges) {
            Log.i("", "fps range " + r[0] + " - " + r[1]);
            if (fps1000 >= r[0] && fps1000 <= r[1]) {
                if (f == null || r[0] > f[0] || r[1] > f[1])
                    f = r;
            }
        }
        if (f != null) {
            Log.i("", "selected range " + f[0] + " - " + f[1]);
        }
        return f;
    }

    void setupPreviewSize(List<Size> supportedPreviewSizes) {
        preview_width = trans_width > save_width ? trans_width : save_width;
        preview_height = trans_width > save_width ? trans_height : save_height;

        if (supportedPreviewSizes != null) {
            boolean support = false;
            // the smallest supported size
            int swidth = 0, sheight = 0;
            Iterator<Size> it = supportedPreviewSizes.iterator();
            while (it.hasNext()) {
                Size s = it.next();
                Log.i("", String.format("supported preview size %dx%d",
                        s.width, s.height));
                if (s.width == preview_width && s.height == preview_height) {
                    // configured size is supported
                    support = true;
                } else if (VideoSettingsActivity.getHeight(s.width) == s.height) {
                    // this is a wanted size
                    if (s.width < swidth || swidth == 0) {
                        swidth = s.width;
                        sheight = s.height;
                    }
                }
            }

            if (!support) {
                if (swidth != 0) {
                    preview_width = swidth;
                    preview_height = sheight;
                } else
                    ;
            }
        }

        // adjust the trans/save size to the preview size.
        if (needScale) {
            if (trans_width > save_width) {
                trans_width = preview_width;
                trans_height = preview_height;
                scale_width = save_width;
                scale_height = save_height;
            } else {
                save_width = preview_width;
                save_height = preview_height;
                scale_width = trans_width;
                scale_height = trans_height;
            }
        } else {
            trans_width = preview_width;
            trans_height = preview_height;
            save_width = preview_width;
            save_height = preview_height;
        }
    }

    void setupFrameBuffers() {
        // actual frame length of NV21
        frameLen = preview_width * preview_height * 3 / 2;

        if (rotate || c_420sp_420p || needScale) {
            dstYuvBuf = new byte[preview_width * (preview_height + 16) * 3 / 2];
        }

        if (needScale) {
            scaleFrameLen = scale_width * scale_height * 3 / 2;
            scaleYuvBuf = new byte[scale_width * (scale_height + 16) * 3 / 2];
        }
    }

    private void swapUV(byte[] buf, int len) {
        int size = len / 3;
        for (int i = size * 2; i < len; i += 2) {
            byte t = buf[i];
            buf[i] = buf[i + 1];
            buf[i + 1] = t;
        }
    }

    private void startPreview(SurfaceHolder holder) {
        if (preview)
            return;
        if (camera == null)
            return;
        try {
            Log.i("", "start preview");
            Camera.Parameters params = camera.getParameters();

            List<String> modes = params.getSupportedFocusModes();
            if (modes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            else if (modes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

            String fmode;
            if (flashlight.equals("off"))
                fmode = Parameters.FLASH_MODE_OFF;
            else if (flashlight.equals("on"))
                fmode = Parameters.FLASH_MODE_TORCH;
            else
                fmode = Parameters.FLASH_MODE_OFF;
            params.setFlashMode(fmode);

            params.setPreviewFormat(ImageFormat.NV21);
            // params.setPreviewFormat(PixelFormat.YCbCr_420_SP);

            params.setPreviewFrameRate(fps);
            List<int[]> ranges = params.getSupportedPreviewFpsRange();
            int[] f = setupPreviewFps(ranges);
            if (f != null)
                params.setPreviewFpsRange(f[0], f[1]);

            List<Size> sizes = params.getSupportedPreviewSizes();
            VideoSettingsActivity.videoSizes = sizes;

            setupPreviewSize(sizes);

            params.setPreviewSize(preview_width, preview_height);

            // picture sizes
            List<Size> picSizes = params.getSupportedPictureSizes();
            VideoSettingsActivity.picSizes = picSizes;

            boolean picSupport = false;
            int tWidth = 0, tHeight = 0;
            Iterator<Size> picIt = picSizes.iterator();
            while (picIt.hasNext()) {
                Size s = picIt.next();
                //Log.i("", String.format("supported picture size %dx%d",
                //        s.width, s.height));
                if (s.width == pic_width && s.height == pic_height) {
                    // configured size is supported
                    picSupport = true;
                    break;
                } else if (s.width > pic_width && s.height > pic_height && tWidth == 0 && tHeight == 0) {
                    tWidth = s.width;
                    tHeight = s.height;
                }
            }
            if (!picSupport) {
                pic_width = tWidth;
                pic_height = tHeight;
                Log.i("", String.format("adapted pic size %dx%d", tWidth, tHeight));
            }

            camera.setParameters(params);

            // clear buffers
            camera.setPreviewCallbackWithBuffer(null);

            // 3 buffers
            // taking picture requires more than YUV420 format
            int size = preview_width * preview_height * 2;
            if (frameSize < size) {
                frameSize = size;
                for (int i = 0; i < bufCount; i++) {
                    frameBufs[i] = new byte[frameSize];
                }
            }
            for (int i = 0; i < bufCount; i++) {
                camera.addCallbackBuffer(frameBufs[i]);
            }

            setupFrameBuffers();

            camera.setPreviewCallbackWithBuffer(
                    // camera.setPreviewCallback(
                    new Camera.PreviewCallback() {
                        public void onPreviewFrame(byte[] data, Camera _camera) {
                            //Log.i("", "frame "+data.length);
                            encodeFrame(data);
                            _camera.addCallbackBuffer(data);
                        }
                    });
            if (holder != null) {
                if (suspend_window == 1)
                    ShowSuspendWindows.setShowStatus(false);
                camera.setPreviewDisplay(holder);
                //camera.setPreviewDisplay(DesktopLayout.surfaceView.getHolder());
            } else {
                //SurfaceTexture surfaceTexture=new SurfaceTexture(10);
                //camera.setPreviewTexture(surfaceTexture);
                if (suspend_window == 1) {
                    ShowSuspendWindows.setShowStatus(true);
                    camera.setPreviewDisplay(DesktopLayout.surfaceView.getHolder());
                } else {
                    SurfaceTexture surfaceTexture = new SurfaceTexture(10);
                    camera.setPreviewTexture(surfaceTexture);
                }
            }
            camera.startPreview();
            camera.cancelAutoFocus();
            preview = true;

            initZoom();
        } catch (Exception e) {
            msgBox("预览失败！");
            e.printStackTrace();
        }
    }

    void encodeFrame(byte[] data) {
        encodeFrame(data, 0);
    }

    void encodeFrame(ByteBuffer data) {
        encodeFrame(data, 1);
    }

    // dataType: 0 byte array, 1 ByteBuffer.
    // byte array for internal camera, ByteBuffer for uvc camera.
    void encodeFrame(Object data, int dataType) {
        if (recording || saving) {
            long stamp = System.currentTimeMillis();

            if (enc_mode == ENC_OMX || enc_mode == ENC_SOFT) {
                boolean res = false;
                if (dataType == 0)
                    res = n.sendFrame((byte[]) data, frameLen, stamp);
                else if (dataType == 1)
                    res = n.sendFrameByteBuffer((ByteBuffer) data, frameLen, stamp);
                if (!res) {
                    Log.e("", "send frame failed");
                    msgBox("录像中止！");
                    stopRecord();
                    stopSave();
                }
            } else if (enc_mode == ENC_HARD) {
                if (swap_uv) {
                    if (dataType == 0)
                        swapUV((byte[]) data, frameLen);
                }

                Object input0, input1;
                int inputType0, inputType1;
                int inputLen0 = 0, inputLen1 = 0;

                if (rotate || c_420sp_420p || osd_mask != 0 || needScale) {
                    if (dataType == 0)
                        inputLen0 = n.processYUV((byte[]) data, preview_width, preview_height,
                                dstYuvBuf, c_420sp_420p, scale_width, scale_height, scaleYuvBuf);
                    else if (dataType == 1)
                        inputLen0 = n.processYUVByteBuffer((ByteBuffer) data, preview_width, preview_height,
                                dstYuvBuf, c_420sp_420p, scale_width, scale_height, scaleYuvBuf);

                    if (inputLen0 == 0)
                        inputLen0 = frameLen;

                    if (dstYuvBuf != null) {
                        input0 = dstYuvBuf;
                        inputType0 = 0;
                    } else {
                        input0 = data;
                        inputType0 = dataType;
                    }

                    if (needScale) {
                        input1 = scaleYuvBuf;
                        inputType1 = 0;
                        inputLen1 = scaleFrameLen;
                    } else {
                        input1 = input0;
                        inputType1 = inputType0;
                        inputLen1 = inputLen0;
                    }
                } else {
                    input0 = data;
                    inputType0 = dataType;
                    inputLen0 = frameLen;
                    input1 = input0;
                    inputType1 = inputType0;
                    inputLen1 = inputLen0;
                }

                if (input0 != input1) {
                    // we assumed input0's size > input1's size.
                    // if that's not true, swap them.
                    if (trans_width < save_width) {
                        Object t = input0;
                        input0 = input1;
                        input1 = t;
                        int i = inputType0;
                        inputType0 = inputType1;
                        inputType1 = i;
                        i = inputLen0;
                        inputLen0 = inputLen1;
                        inputLen1 = i;
                    }
                }

                boolean enable[] = new boolean[2];
                boolean enable_save_video = saving && !save_audio_only;
                if (dual_encoding) {
                    enable[0] = recording;
                    enable[1] = enable_save_video;
                } else {
                    enable[0] = recording || enable_save_video;
                    enable[1] = false;
                }

                if (enable[0]) {
                    if (inputType0 == 0)
                        avcEncoders[0].offerEncoder((byte[]) input0, inputLen0, stamp);
                    else if (inputType0 == 1)
                        avcEncoders[0].offerEncoder((ByteBuffer) input0, inputLen0, stamp);
                }
                if (enable[1]) {
                    if (inputType1 == 0)
                        avcEncoders[1].offerEncoder((byte[]) input1, inputLen1, stamp);
                    else if (inputType1 == 1)
                        avcEncoders[1].offerEncoder((ByteBuffer) input1, inputLen1, stamp);
                }
            }
        }
    }

    private void stopPreview() {
        if (!preview)
            return;
        if (camera != null) {
            Log.i("", "stop preview");
            camera.setPreviewCallbackWithBuffer(null); // clear buffers
            camera.setPreviewCallback(null); // stop callback
            camera.stopPreview();
            try {
                camera.setPreviewDisplay(null);
                camera.setPreviewTexture(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        preview = false;
    }

    void restartPreview(SurfaceHolder holder) {
        if (!use_uvccam) {
            // close uvccam
            stopPreview();
            openCamera();
            startPreview(holder);
        }
    }

    void backgroundPreview() {
        if (use_uvccam) {

        } else {
            stopPreview();
            startPreview(null);
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int sw, int sh) {
        // camera.release();
        loadPreference();
        popState();
        showHideViews();

        restartPreview(holder);

        // start wb network once settings changed
        n.netConfig(server_addr, cmd_port, data_port, transmode, router_mode);

        // always start audio recv and play
        if (audio == null)
            audio = new Audio(this, save_audio_only ? Audio.PCM : audio_type);
        audio.startPlay();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("", "surface destroy");
        // app switched to background, now we use
        // SurfaceTexture to do the preview..
        backgroundPreview();
    }

    void uvcPreview() {
        if (use_uvccam) {
            stopPreview();
            closeCamera();
            // open uvccam
            if (uvcManager == null) {
                uvcManager = new UVCCamManager(this);
                uvcManager.register();
            }
            startUVCCamPreview();
        }
    }

    private Surface mSurface;

    int startUVCCamPreview() {
        if (uvcManager == null) {
            uvccam_info.setText("USB错误");
            return -1;
        }
        if (uvcManager.updateDevices() == 0) {
            uvccam_info.setText("未找到外置摄像头");
            return -2;
        }

        setupPreviewSize(null);

        setupFrameBuffers();

        soft_swap = true;

        final SurfaceTexture st = uvccam_view.getSurfaceTexture();
        if (mSurface != null) {
            mSurface.release();
        }
        mSurface = new Surface(st);
        uvcManager.startPreview(mSurface, mIFrameCallback);
        //uvccam_info.setText(String.format("%dx%d", width, height));
        return 0;
    }

    private final IFrameCallback mIFrameCallback = new IFrameCallback() {
        @Override
        public void onFrame(final ByteBuffer frame) {
            // it's important that we CAN'T add log here, otherwise the uvc
            // camera will fail to call the preview callback. This is so strange
            // behavior that should be commented.
            //Log.i("uvc", "onFrame "+frame.array().length);
            encodeFrame(frame);
        }
    };

    SurfaceTextureListener mUVCCamSurfaceTextureListener = new SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(final SurfaceTexture surface, final int width, final int height) {
            Log.i("uvc", "Surface texture now avaialble.");
            loadPreference();
            showHideViews();
            uvcPreview();
        }

        @Override
        public void onSurfaceTextureSizeChanged(final SurfaceTexture surface, final int width, final int height) {
            Log.i("uvc", "Resized surface texture: " + width + '/' + height);
            //uvccam_info.setText("SurfaceTexture size changed");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(final SurfaceTexture surface) {
            Log.i("uvc", "Destroyed surface texture");
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(final SurfaceTexture surface) {
            //uvccam_info.setText("SurfaceTexture updated");
        }
    };

    //private FTP ftp = null;

    public void onShotClick(View v) {
        if (camera == null)
            return;
        //if (ftp == null)
        //    ftp = new FTP(this);
        takePicture();
    }

    private void takePicture() {
        // check if the previous take picture ended.
        if (previewPicture)
            return;
        // callback with buffer is not compatible with
        // taking picture!..
        stopPreview();
        startPreviewForPicture();
        try {
            camera.takePicture(null, null, null, mJpegCallback);
            sound.playShutter();
        } catch (Exception ex) {
            ex.printStackTrace();
            msgBox("拍照失败");
            return;
        }
    }

    private void startPreviewForPicture() {
        previewPicture = true;

        Camera.Parameters params = camera.getParameters();

        String fmode;
        if (pic_flashlight.equals("auto"))
            fmode = Parameters.FLASH_MODE_AUTO;
        else if (pic_flashlight.equals("off"))
            fmode = Parameters.FLASH_MODE_OFF;
        else if (pic_flashlight.equals("on"))
            fmode = Parameters.FLASH_MODE_ON;
        else
            fmode = Parameters.FLASH_MODE_AUTO;
        params.setFlashMode(fmode);

        params.setPictureFormat(ImageFormat.JPEG);
        params.setPictureSize(pic_width, pic_height);
        try {
            camera.setParameters(params);
            camera.setPreviewDisplay(surface_camera.getHolder());
            camera.startPreview();
        } catch (Exception ex) {
            ex.printStackTrace();
            msgBox("拍照预览失败");
        }
    }

    private PictureCallback mJpegCallback = new PictureCallback() {
        int picsTaken = 0;

        public void onPictureTaken(byte[] data, Camera _camera) {
            Log.i("", "taken picture " + data.length);

            int factor = 1;
            if (pic_suffix.equals("8M"))
                factor = 2;
            else if (pic_suffix.equals("16M"))
                factor = 4;
            else if (pic_suffix.equals("24M"))
                factor = 6;
            else if (pic_suffix.equals("32M"))
                factor = 8;

            Date currentTime = new Date();

            Bitmap bmp = getBitmap(data, factor, currentTime);

            if (bmp == null) {
                msgBox("照片处理失败！");
            } else {
                // save it
                String path = getNewPicFile(currentTime, pic_suffix, picsTaken + 1);
                if (path != null && bmp != null)
                    do {
                        try {
                            FileOutputStream stream = new FileOutputStream(path);
                            bmp.compress(CompressFormat.JPEG, 90, stream);
                            stream.flush();
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            msgBox("照片保存失败！");
                            break;
                        }
                        //ftp.setParams(server_addr, ftp_port, ftp_user, ftp_password);
                        //ftp.startUpload(path);
                    } while (false);
                n.addVideoLog(9, path);
            }
            // as documented, the preview is stopped after taking picture.
            // must call startPreview() on the camera.
            if (pic_continuous > 0) {
                if (++picsTaken < pic_continuous) {
                    _camera.startPreview();
                    _camera.takePicture(null, null, null, mJpegCallback);
                    sound.playShutter();
                    return;
                } else
                    picsTaken = 0;
            }

            startPreview(surface_camera.getHolder());
            previewPicture = false;
        }
    };

    private Bitmap getBitmap(byte[] data, int scaleFactor, Date currentTime) {
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        if (osd_mask != 0) {
            bmp = drawOSD(bmp, currentTime);
        }

        if (scaleFactor != 1) {
            double scale = Math.sqrt(scaleFactor);
            int width = (int) (bmp.getWidth() * scale);
            int height = (int) (bmp.getHeight() * scale);
            bmp = scaleImage(bmp, width, height);
        }

        return bmp;
    }

    private Bitmap drawOSD(Bitmap bmp, Date currentTime) {
        float scale = 4.0f;//getResources().getDisplayMetrics().density;

        Bitmap.Config bitmapConfig = bmp.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are immutable,
        // so we need to convert it to mutable one
        Bitmap copy = bmp.copy(bitmapConfig, true);
        if (copy != null) {
            bmp.recycle();
            bmp = copy;
        } else
            return bmp;

        Canvas canvas = new Canvas(bmp);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #D3D3D3
        paint.setColor(Color.rgb(0xd3, 0xd3, 0xd3));
        // text size in pixels
        paint.setTextSize((int) (14 * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.BLACK);

        if ((osd_mask & 1) != 0) {
            // OSD_TIME
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            String dateString = formatter.format(currentTime);
            // draw text to the Canvas top-left
            Rect bounds = new Rect();
            paint.getTextBounds(dateString, 0, dateString.length(), bounds);
            int x = 20;
            int y = 20 + bounds.height();
            canvas.drawText(dateString, x, y, paint);
        }

        if ((osd_mask & 2) != 0) {
            // OSD_DEVNAME
            String str = osd_devname;
            // draw text to the Canvas bottom-right
            Rect bounds = new Rect();
            paint.getTextBounds(str, 0, str.length(), bounds);
            int x = bmp.getWidth() - bounds.width() - 20;
            int y = bmp.getHeight() - 20;
            canvas.drawText(str, x, y, paint);
        }

        if ((osd_mask & 4) != 0) {
            // OSD_GPS
            String str = gpsString != null ? gpsString : "";
            // draw text to the Canvas top-right
            Rect bounds = new Rect();
            paint.getTextBounds(str, 0, str.length(), bounds);
            int x = bmp.getWidth() - bounds.width() - 20;
            int y = 20 + bounds.height();
            canvas.drawText(str, x, y, paint);
        }

        return bmp;
    }

    private Bitmap scaleImage(Bitmap bmp, int width, int height) {
        Bitmap scaled = null;
        try {
            scaled = Bitmap.createScaledBitmap(bmp, width, height, true);
        } catch (Exception ex) {
            ex.printStackTrace();
            scaled = null;
        }
        if (scaled == null) {
            Toast.makeText(this, "out of memory", Toast.LENGTH_SHORT).show();
            return bmp;
        }
        if (scaled != bmp)
            bmp.recycle();
        return scaled;
    }

    private String getNewPicFile(Date currentTime, String suffix, int number) {
        // mkdir
        String dir = "/mnt/sdcard/PICS";
        File df = new File(dir);
        if (!df.exists()) {
            if (!df.mkdir()) {
                msgBox("打开SD卡失败！");
                return null;
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTime);
        String subdir = String.format("%d-%d-%d",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        File subdirFile = new File(String.format("%s/%s", dir, subdir));
        if (!subdirFile.exists())
            subdirFile.mkdir();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        int devId = n.getDevId();
        String path = String.format("%s/%s/%d_%s_%02d.jpg", dir, subdir, devId, dateString, number);
        return path;
    }

    Toast alarmToast;

    private void doAlarm() {
        n.sendAlarm();
        if (alarmToast == null)
            alarmToast = Toast.makeText(this, "已报警", Toast.LENGTH_SHORT);
        alarmToast.show();
        sound.playAlarm();
    }

    @Override
    public void onBackPressed() {
        if (disabled)
            return;
        /*AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setMessage("确定退出？");
        dlg.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                exit();
                quit();
            }
        });
        dlg.setNegativeButton("取消", null);
        dlg.setCancelable(true);
        dlg.create().show();*/
        // launch start activity
        Intent startIntent = new Intent(getApplicationContext(),
                StartActivity.class);
        startActivity(startIntent);
    }

    long pressTime = 0;
    boolean calling = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (disabled) {
            if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK)
                do {
                    // tap the key twice in a short time
                    // then we resume
                    long now = System.currentTimeMillis();
                    if (now - pressTime > 300) {
                        pressTime = now;
                        break;
                    } else {
                        pressTime = 0;
                    }
                    // turn light on
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    // force turn on..
                    if (lp.screenBrightness < 0.88f)
                        lp.screenBrightness = 0.89f;
                    else
                        lp.screenBrightness = 0.87f;
                    getWindow().setAttributes(lp);
                    /*
                     * PowerManager pm =
                     * (PowerManager)getSystemService(Context.POWER_SERVICE);
                     * WakeLock mWakeLock =
                     * pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP,
                     * "omxvideo"); mWakeLock.acquire(); mWakeLock.release();
                     */

                    enableDisableView(container, true);
                    disabled = false;

                    TextView tip = (TextView) findViewById(R.id.light_off_tip);
                    tip.setVisibility(View.INVISIBLE);
                } while (false);
            return true;
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                // track for SOS long press
                event.startTracking();
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Log.i("", "volume key down " + event.getRepeatCount());
                if (event.getRepeatCount() == 0) {
                    startCall();
                }
                break;
            case KeyEvent.KEYCODE_CAMERA:
                onShotClick(null);
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                onRecClick(null);
                break;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                onSaveClick(null);
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                toggleSaveAudio();
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                // just return true for the track
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Log.i("", "volume key up " + event.getRepeatCount());
                stopCall();
                break;
            default:
                return super.onKeyUp(keyCode, event);
        }
        return true;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                doAlarm();
                break;
            default:
                return super.onKeyLongPress(keyCode, event);
        }
        return true;
    }

    void startCall() {
        if (calling)
            return;
        calling = true;

        startAudioRecord();

        n.startCall();
        CallTask task = new CallTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        TextView view = (TextView) findViewById(R.id.call_info);
        view.setText("正在连接");
        view.setVisibility(View.VISIBLE);
    }

    void stopCall() {
        if (calling) {
            n.stopCall();
            TextView view = (TextView) findViewById(R.id.call_info);
            view.setVisibility(View.INVISIBLE);
            calling = false;
        }
    }

    class CallTask extends AsyncTask<Void, Integer, Void> {
        final int CALL_CALLING = 0;
        final int CALL_OK = 1;
        final int CALL_END = 2;
        final int CALL_FAIL_NETWORK = -1;
        final int CALL_FAIL_RIGHT = -2;
        final int CALL_FAIL_OTHER = -3;
        TextView view;

        @Override
        protected Void doInBackground(Void... params) {
            view = (TextView) findViewById(R.id.call_info);
            int state, oldState = CALL_CALLING;
            while (!isCancelled()) {
                state = n.getCallState();
                if (state != oldState) {
                    publishProgress(state);
                    oldState = state;
                }
                msleep(50);
            }
            return null;
        }

        protected void onProgressUpdate(Integer... params) {
            if (calling) {
                switch (params[0]) {
                    case CALL_CALLING:
                        view.setText("正在连接");
                        break;
                    case CALL_OK:
                        view.setText("开始通话");
                        break;
                    case CALL_END:
                        view.setText("通话结束");
                        break;
                    case CALL_FAIL_NETWORK:
                        view.setText("通话失败：网络错误");
                        break;
                    case CALL_FAIL_RIGHT:
                        view.setText("通话失败：权限不足");
                        break;
                    case CALL_FAIL_OTHER:
                        view.setText("通话失败");
                        break;
                    default:
                        Log.i("", "invalid call state " + params[0]);
                        view.setText("错误");
                        break;
                }
            }
        }
    }

    void saveState() {
        SharedPreferences states = getSharedPreferences("state",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = states.edit();
        ed.putBoolean("record", recording);
        ed.putBoolean("saving", saving);
        ed.commit();
    }

    void popState() {
        SharedPreferences states = getSharedPreferences("state",
                Context.MODE_PRIVATE);
        boolean state_saving = states.getBoolean("saving", false);
        boolean state_record = states.getBoolean("record", false);
        if (state_record) {
            startRecord(false);
        }
        if (state_saving) {
            startSave(false);
        }
    }

    protected void onPause() {
        Log.i("", "pause");
        //leave();
        if (uvcManager != null) {
            uvcManager.unregister();
        }
        super.onPause();
    }

    protected void onResume() {
        Log.i("", "resume");
        //popState();
        if (uvcManager != null) {
            uvcManager.register();
        }
        super.onResume();
    }

    protected void onDestroy() {
        Log.i("", "destroy");
        //if (ftp != null)
        //    ftp.close();
        gps.stop();
        stateTask.cancel(false);
        previewOSDTask.cancel(false);
        if (uvcManager != null) {
            uvcManager.destroy();
            uvcManager = null;
        }
        // ensure the camera is released.
        exit();
        n.exit();
        super.onDestroy();
    }

    public void exit() {
        leave();
        finish();
    }

    private void quit() {
        System.exit(0);
    }

    private void leave() {
        Log.i("", "leaving OMXVideoActivity");
        // called when pausing, exiting..
        saveState();

        stopSave();
        stopRecord();
        stopPreview();
        if (camera != null) {
            // camera.unlock();
            camera.release();
            camera = null;
        }

        if (audio != null) {
            audio.stopPlay();
            audio.stopRecord();
        }

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setSpeakerphoneOn(false);

        try {
            unregisterReceiver(batteryLowReceiver);
        } catch (Exception ex) {
            // in case the receiver is not registered..
        }
    }
}
