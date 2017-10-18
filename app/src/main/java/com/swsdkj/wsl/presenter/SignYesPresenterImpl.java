package com.swsdkj.wsl.presenter;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.swsdkj.wsl.bean.ResponseEntity;
import com.swsdkj.wsl.bean.ResultMsg;
import com.swsdkj.wsl.bean.SignBean;
import com.swsdkj.wsl.contract.SignYesContract;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
* Created by Administrator on 2017/04/25
*/

public class SignYesPresenterImpl implements SignYesContract.Presenter{
    SignYesContract.View view;
    public SignYesPresenterImpl(SignYesContract.View view){
        this.view=view;
    }

    @Override
    public void selsignby(final Context context, RequestParams params) {

        Callback.Cancelable cancelable
                = x.http().get(params, new Callback.CommonCallback<ResponseEntity>() {
            @Override
            public void onSuccess(ResponseEntity result) {
                String json = result.getResult().toString();
                ResultMsg resultMsg = new Gson().fromJson(json,new TypeToken<ResultMsg>() {
                }.getType());

                if (resultMsg.getCode()==0){
                    List<SignBean> signBeen2 = new Gson().fromJson(new Gson().toJson(resultMsg.getList()),new TypeToken<List<SignBean>>() {
                    }.getType());

                    view.onSuccess(signBeen2);
                }else {
                    view.onFail();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //view.onSignFail();
                view.onFail();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                //view.onSignFail();
                view.onFail();
            }

            @Override
            public void onFinished() {
            }
        });




    }
}