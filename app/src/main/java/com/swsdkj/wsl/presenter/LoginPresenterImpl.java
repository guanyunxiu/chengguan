package com.swsdkj.wsl.presenter;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.swsdkj.wsl.base.BaseApplication;
import com.swsdkj.wsl.bean.User;
import com.swsdkj.wsl.config.SharedConstants;
import com.swsdkj.wsl.contract.LoginContract;
import com.swsdkj.wsl.net.BaseUrl;
import com.swsdkj.wsl.net.FailMsg;
import com.swsdkj.wsl.net.util.BaseResponse;
import com.swsdkj.wsl.net.util.BaseSubscriber;
import com.swsdkj.wsl.net.util.ExceptionHandle;
import com.swsdkj.wsl.net.util.RetrofitClient;
import com.swsdkj.wsl.tool.CommonUtil;

import org.xutils.http.RequestParams;

import java.util.HashMap;
import java.util.Map;

/**
* Created by Administrator on 2017/04/25
*/

public class LoginPresenterImpl implements LoginContract.Presenter{
    LoginContract.View view;
    public LoginPresenterImpl(LoginContract.View view){
        this.view=view;
    }

    @Override
    public void login(Context context, RequestParams params) {

    }

    @Override
    public void login(final Context context, Map<String, String> map) {
        RetrofitClient.getInstance(context).createBaseApi().post(BaseUrl.goto1,map, new BaseSubscriber<BaseResponse>(context) {
            @Override
            public void onNext(BaseResponse baseResponse) {
                if (FailMsg.showMsg(context,baseResponse.getCode())){
                    Gson gson = new Gson();
                    Log.i("gson",baseResponse.getUser().toString());
                    String s = new Gson().toJson(baseResponse.getUser());
                    User user = gson.fromJson(s,User.class);
                    CommonUtil.saveUser(user);
                    view.onLogoSuccess(user);
                }else {
                    view.onLogoFail();
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {
                view.onLogoFail();
            }
        });
    }

    @Override
    public void login(Context context, User user) {

    }

    @Override
    public void sendCode(final Context context, String phone,String nameStr) {
        Map<String,String> map = new HashMap<>();
        map.put("name",nameStr);
        map.put("phone",phone);

        RetrofitClient.getInstance(context).createBaseApi().post(BaseUrl.sendCode,map, new BaseSubscriber<BaseResponse>(context) {
            @Override
            public void onNext(BaseResponse baseResponse) {
                Log.i("12",baseResponse.toString()+"---------发送验证吗--------------------");
                if (baseResponse.getCode()==0){
                    BaseApplication.mSharedPrefUtil.putString(SharedConstants.SESSIONID,baseResponse.getSessionId());
                    BaseApplication.mSharedPrefUtil.commit();
                    view.onSendCodeSuccess();
                }else {
                    CommonUtil.showToast(context,"错误码"+baseResponse.getCode());
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {
                view.onSendCodeFail();
            }
        });
    }

}