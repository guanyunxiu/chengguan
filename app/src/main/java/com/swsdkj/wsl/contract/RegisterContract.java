package com.swsdkj.wsl.contract;

import android.content.Context;

import com.swsdkj.wsl.bean.User;

import org.xutils.http.RequestParams;

import java.util.Map;

/**
 * Created by Administrator on 2017/5/9 0009.
 */

public class RegisterContract {

    public interface View{
        void onRegisterSuccess(User user);
        void onRegisterFail(int code);

    }

    public interface Presenter{

        void register(Context context,RequestParams params);

        void register(Context context,String phone,String name,String password,String idcard,String photo);

    }

    public interface Model{

    }

}
