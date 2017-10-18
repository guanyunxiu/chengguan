package com.swsdkj.wsl.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.swsdkj.wsl.R;
import com.swsdkj.wsl.config.MyConfig;
import com.swsdkj.wsl.tool.StatusBarCompat;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/4/24 0024.
 */

public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        StatusBarCompat.compat(this, getResources().getColor(R.color.black));

        ButterKnife.bind(this);
        init();
    }

    protected abstract void setContentView();
    protected abstract void init();


    private int time1 = 0;
    private Timer timer1;
    TimerTask task1;
    public void setCountdown(final TextView view) {
        time1 = 180;
        timer1 = new Timer();

        task1 = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() { // UI thread
                    @Override
                    public void run() {
                        if (time1 <= 0) {
                            view.setClickable(true);//重新获得点击
                            view.setText(getResources().getString(R.string.sendcode));
                            task1.cancel();

                        } else {
                            view.setClickable(false);//重新获得点击
                            view.setText(time1 + getResources().getString(R.string.loginactivity_password_wait));

                        }
                        time1--;
                    }
                });
            }
        };
        timer1.schedule(task1, 0, 1000);
        time1 = 180;
    }
    /**
     * 为子类提供一个权限检查方法
     * @param permissions
     * @return
     */
    public boolean hasPermission(String... permissions){
        for(String permission : permissions){
            if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
    /**
     * 为子类提供一个权限请求方法
     * @param code
     * @param permissions
     */
    public void requestPermission(int code,String... permissions){
        ActivityCompat.requestPermissions(this,permissions,code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("code",requestCode+"***");
        switch (requestCode){
            case MyConfig.WRITE_EXTERNAL_CODE:
                if(this.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    doSDCardPermission();
                }
                break;
            case MyConfig.CALL_PHONE_CODE:
                doCallPhone();
                break;
            case MyConfig.CAMERA_CODE:
                if(this.checkCallingOrSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    doCamera();
                }
                break;
          /*  case Constants.RADIO_CODE:
                if(this.checkCallingOrSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
                    Log.i("code","分局共");
                    doRadio();
                }
                break;*/
        }
    }

    /**
     * 默认的写SD卡权限处理
     */
    public void doSDCardPermission(){
    }
    /**
     * 默认的打电话处理
     */
    public void doCallPhone(){
    }
    public void doCamera(){
    }
}
