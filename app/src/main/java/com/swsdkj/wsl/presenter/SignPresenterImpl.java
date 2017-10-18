package com.swsdkj.wsl.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.swsdkj.wsl.base.BaseApplication;
import com.swsdkj.wsl.bean.ResponseEntity;
import com.swsdkj.wsl.config.MyConfig;
import com.swsdkj.wsl.config.SharedConstants;
import com.swsdkj.wsl.contract.SignContract;
import com.swsdkj.wsl.net.BaseUrl;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

/**
 * Created by Administrator on 2017/5/5 0005.
 */

public class SignPresenterImpl implements SignContract.Presenter {

    SignContract.View view;
    public SignPresenterImpl(SignContract.View view){
        this.view=view;
    }

    @Override
    public void sign(Context context, String fileStr1, String fileStr2,File face1,File face2,String remarks) {
        RequestParams params=new RequestParams(BaseUrl.addsign);

        params.setMultipart(true);//设置表单传送
        params.setCancelFast(true);//设置可以立即被停止i
        if (fileStr1!=null&&!fileStr1.equals("")){
            params.addBodyParameter("file1", new File(fileStr1),"multipart/form-data");
        }
        if (fileStr2!=null&&!fileStr2.equals("")){
            params.addBodyParameter("file2", new File(fileStr2),"multipart/form-data");
        }
        if(face1 != null){
            params.addBodyParameter("faces1", face1,"multipart/form-data");
        }
        if(face2 != null){
            params.addBodyParameter("faces2", face2,"multipart/form-data");
        }

        params.addBodyParameter("userid",  BaseApplication.mSharedPrefUtil.getString(SharedConstants.ID,""));
        params.addBodyParameter("category", "0");
        params.addBodyParameter("address", MyConfig.myAddress+"");
        if (remarks!=null&&!remarks.equals("")){
            params.addBodyParameter("context", remarks);
        }
        params.addBodyParameter("ft","1");



        Callback.Cancelable cancelable
                = x.http().post(params, new Callback.CommonCallback<ResponseEntity>() {
            @Override
            public void onSuccess(ResponseEntity result) {
                Log.i("123",result.toString()+"----------------------上传图片-------1-------------------");
                view.onSignSuccess();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("123",ex.toString()+"----------------------上传图片-------2-------------------");
                view.onSignFail();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i("123",cex.toString()+"----------------------上传图片-------3-------------------");
                view.onSignFail();
            }

            @Override
            public void onFinished() {
                Log.i("123","----------------------上传完成-------3-------------------");
            }
        });
    }

}
