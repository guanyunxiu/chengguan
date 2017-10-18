package com.swsdkj.wsl.presenter;
import android.content.Context;

import com.google.gson.Gson;
import com.swsdkj.wsl.bean.User;
import com.swsdkj.wsl.contract.PunchContract;
import com.swsdkj.wsl.net.BaseUrl;
import com.swsdkj.wsl.net.util.BaseResponse;
import com.swsdkj.wsl.net.util.BaseSubscriber;
import com.swsdkj.wsl.net.util.ExceptionHandle;
import com.swsdkj.wsl.net.util.RetrofitClient;
import com.swsdkj.wsl.tool.CommonUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

/**
* Created by Administrator on 2017/05/17
*/

public class PunchPresenterImpl implements PunchContract.Presenter{
    PunchContract.View view;
    public PunchPresenterImpl(PunchContract.View view){
        this.view = view;
    }
    @Override
    public void onSubmit(Context context, String userid, String address) {
        Map<String, String> map = new HashMap<>();
        map.put("userid",userid);
        map.put("address",address);
        map.put("ft","0");
        RetrofitClient.getInstance(context).createBaseApi().post(BaseUrl.addsign2,map, new BaseSubscriber<BaseResponse>(context) {
            @Override
            public void onNext(BaseResponse baseResponse) {
                if (baseResponse.getCode()==0){
                    view.onSuccess();
                }else{
                    view.onFail(baseResponse.getCode());
                }
            }
            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {
                view.onFail(2);
            }
        });
    }
}