package com.swsdkj.wsl.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.swsdkj.wsl.base.BaseApplication;
import com.swsdkj.wsl.bean.ResponseEntity;
import com.swsdkj.wsl.bean.ResultMsg;
import com.swsdkj.wsl.bean.SignBean;
import com.swsdkj.wsl.bean.User;
import com.swsdkj.wsl.config.MyConfig;
import com.swsdkj.wsl.config.SharedConstants;
import com.swsdkj.wsl.contract.RegisterContract;
import com.swsdkj.wsl.net.BaseUrl;
import com.swsdkj.wsl.net.FailMsg;
import com.swsdkj.wsl.net.util.BaseResponse;
import com.swsdkj.wsl.net.util.BaseSubscriber;
import com.swsdkj.wsl.net.util.ExceptionHandle;
import com.swsdkj.wsl.net.util.RetrofitClient;
import com.swsdkj.wsl.tool.CommonUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/9 0009.
 */

public class RegisterPreseterImpl implements RegisterContract.Presenter{
    RegisterContract.View view;
    public RegisterPreseterImpl(RegisterContract.View view){
        this.view=view;
    }

    @Override
    public void register(Context context, RequestParams params) {
        Log.i("123",new Gson().toJson(params)+"------------ 传入参数------------------------");

        /*params.addHeader("Content-type","application/json;charset=UTF-8");*/
        Callback.Cancelable cancelable
                = x.http().post(params, new Callback.CommonCallback<ResponseEntity>() {
            @Override
            public void onSuccess(ResponseEntity result) {
                String json = result.getResult().toString();
                Log.i("123",json+"---------------------注册--------1-------");
                ResultMsg resultMsg = new Gson().fromJson(json,new TypeToken<ResultMsg>() {
                }.getType());

                if (resultMsg.getCode()==0){
                    Log.i("123",new Gson().toJson(resultMsg.getUser())+"---------------------注册----2-----------");
                    User user = new Gson().fromJson(new Gson().toJson(resultMsg.getUser()),new TypeToken<User>() {
                    }.getType());

                    CommonUtil.saveUser(user);
                    view.onRegisterSuccess(user);
                }else {
                    view.onRegisterFail(resultMsg.getCode());
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //view.onSignFail();
                Log.i("123",ex.toString()+"---------------------注册----3-----------");
                view.onRegisterFail(-1);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                //view.onSignFail();
                Log.i("123",cex.toString()+"---------------------注册---4-----------");
                view.onRegisterFail(-1);
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    public void register(final Context context, String phone,String name,String password,String idcard,String photo) {

        RequestParams params=new RequestParams(BaseUrl.register2);

        params.setMultipart(true);//设置表单传送
        params.setCancelFast(true);//设置可以立即被停止i
        if (!TextUtils.isEmpty(photo)){
            params.addBodyParameter("file", new File(photo),"multipart/form-data");
        }

        params.addBodyParameter("name",  name);
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("password", password);
        params.addBodyParameter("idcard", idcard);

        Callback.Cancelable cancelable
                = x.http().post(params, new Callback.CommonCallback<ResponseEntity>() {
            @Override
            public void onSuccess(ResponseEntity result) {
                String json = result.getResult().toString();
                Log.i("123",json+"---------------------注册--------1-------");
                ResultMsg resultMsg = new Gson().fromJson(json,new TypeToken<ResultMsg>() {
                }.getType());

                if (resultMsg.getCode()==0){
                    Log.i("123",new Gson().toJson(resultMsg.getObj())+"---------------------注册----2-----------");
                    User user = new Gson().fromJson(new Gson().toJson(resultMsg.getObj()),new TypeToken<User>() {
                    }.getType());

                    CommonUtil.saveUser(user);
                    view.onRegisterSuccess(user);
                }else {
                    view.onRegisterFail(resultMsg.getCode());
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("123",ex.toString()+"----------------------上传图片-------2-------------------");
                view.onRegisterFail(-1);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i("123",cex.toString()+"----------------------上传图片-------3-------------------");
                view.onRegisterFail(-1);
            }

            @Override
            public void onFinished() {
                Log.i("123","----------------------上传完成-------3-------------------");
            }
        });
    }
}
