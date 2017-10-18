package com.swsdkj.wsl.presenter;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.swsdkj.wsl.bean.ResponseEntity;
import com.swsdkj.wsl.bean.ResultMsg;
import com.swsdkj.wsl.bean.User;
import com.swsdkj.wsl.contract.SignNoContract;
import com.swsdkj.wsl.fragment.SignNoFragment;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2017/5/8 0008.
 */

public class SignNoPresenterImpl implements SignNoContract.Presenter{
    private SignNoContract.View view;
    public SignNoPresenterImpl(SignNoContract.View view){
        this.view=view;
    }

    @Override
    public void getUsers(Context context, RequestParams params) {

        Callback.Cancelable cancelable
                = x.http().get(params, new Callback.CommonCallback<ResponseEntity>() {
            @Override
            public void onSuccess(ResponseEntity result) {
                String json = result.getResult().toString();

                ResultMsg resultMsg = new Gson().fromJson(json,new TypeToken<ResultMsg>() {
                }.getType());

                if (resultMsg.getCode()==0){
                    List<User> users = new Gson().fromJson(new Gson().toJson(resultMsg.getList()),new TypeToken<List<User>>() {
                    }.getType());
                    view.onLogoSuccess(users);
                }else {
                    view.onLogoFail();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                view.onLogoFail();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                view.onLogoFail();
            }

            @Override
            public void onFinished() {

            }
        });

    }

}
