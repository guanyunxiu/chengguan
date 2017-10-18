package com.swsdkj.wsl.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;

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

public class WelComeActivity extends BaseActivity implements PhoneExisContract.View{
    private Context context;
    private PhoneExisPresenter phoneExisPresenter;
    @Override
    protected void setContentView() {
      /*  setContentView(R.layout.acitivty_welcome);*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        context = this;
        phoneExisPresenter = new PhoneExisPresenter(this);
    }

    TimerTask task=null;
    Timer timer = new Timer();
    @Override
    protected void init() {

        task = new TimerTask(){
            @Override
            public void run() {
                runOnUiThread(new Runnable() { // UI thread
                    @Override
                    public void run() {

                        Log.i("123",BaseApplication.mSharedPrefUtil.getString(SharedConstants.NAME,"")+"--------登录----------LoginActivity-------------------------");

                        if (BaseApplication.mSharedPrefUtil.getString(SharedConstants.PHONE,"").equals("")){


                            Intent intent = new Intent(context,LoginActivity.class);
                            startActivity(intent);
                            finish();

                            Log.i("123","--------登录----------LoginActivity-------------------------");

                        }else {
                            phoneExisPresenter.onSubmit(context,BaseApplication.mSharedPrefUtil.getString(SharedConstants.PHONE,""));
                          /*  Intent intent = new Intent(context,MainActivity.class);
                            startActivity(intent);
                            finish();*/

                            Log.i("123","--------登录-----------MainActivity------------------------");
                        }
                    }
                });
            }};
        timer.schedule(task, 2000);
    }

    @Override
    public void onSuccess(int code) {
        if(code == 7){
            Intent intent = new Intent(context,MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            CommonUtil.showToast(context,"用户不存在，请重新注册");
            Intent intent = new Intent(context,RegisterActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onFail() {

    }
}
