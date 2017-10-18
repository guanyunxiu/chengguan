package com.swsdkj.wsl.contract;

import android.content.Context;

import com.swsdkj.wsl.bean.User;

import org.xutils.http.RequestParams;

import java.util.List;

/**
 * Created by Administrator on 2017/5/8 0008.
 */

public class SignNoContract {

    public interface View{
        void onLogoSuccess(List<User> users);
        void onLogoFail();
    }

    public interface Presenter{
        void getUsers(Context context,RequestParams params);
    }

    public interface Model{

    }
}
