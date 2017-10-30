package com.swsdkj.wsl.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.swsdkj.wsl.R;
import com.swsdkj.wsl.base.BaseActivity;
import com.swsdkj.wsl.base.BaseApplication;
import com.swsdkj.wsl.config.SharedConstants;
import com.swsdkj.wsl.contract.PhoneExisContract;
import com.swsdkj.wsl.presenter.PhoneExisPresenter;
import com.swsdkj.wsl.tool.CommonUtil;
import com.swsdkj.wsl.tool.ConnectRong;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/5/5 0005.
 */

public class WelComeActivity extends AppCompatActivity {
    private Context context;
    private PhoneExisPresenter phoneExisPresenter;
    TimerTask task=null;
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        startMainOrGuide();
    }

    //首次启动开启导航页
    private void startMainOrGuide() {

        new Handler().postDelayed(new Runnable() {
            public void run() {
              //  boolean flag = BaseApplication.mSharedPrefUtil.getBoolean(SharedConstants.LOGINFLAG,false);
              //  if(flag){
                 //   loginPresenter.onLogin(BaseApplication.mSharedPrefUtil.getString(SharedConstants.PHONE,""),BaseApplication.mSharedPrefUtil.getString(SharedConstants.PASSWORD,""));
               // }else{
                    Intent intent = new Intent(WelComeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    //overridePendingTransition(R.anim.hold, R.anim.zoom_in_exit);
              //  }
            }
        }, 2000);
    }
}
