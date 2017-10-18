package com.swsdkj.wsl.presenter;

import android.content.Context;

import com.swsdkj.wsl.contract.PhoneExisContract;
import com.swsdkj.wsl.net.BaseUrl;
import com.swsdkj.wsl.net.util.BaseResponse;
import com.swsdkj.wsl.net.util.BaseSubscriber;
import com.swsdkj.wsl.net.util.ExceptionHandle;
import com.swsdkj.wsl.net.util.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017\5\31 0031.
 */
public class PhoneExisPresenter implements PhoneExisContract.Presenter {
    PhoneExisContract.View view;
    public PhoneExisPresenter( PhoneExisContract.View view){
        this.view = view;
    }

    @Override
    public void onSubmit(Context context, String phone) {
        Map<String, String> map = new HashMap<>();
        map.put("phone",phone);
        RetrofitClient.getInstance(context).createBaseApi().get(BaseUrl.userft,map, new BaseSubscriber<BaseResponse>(context) {
            @Override
            public void onNext(BaseResponse baseResponse) {
                    view.onSuccess(baseResponse.getCode());
            }
            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {
                view.onFail();
            }
        });
    }
}
