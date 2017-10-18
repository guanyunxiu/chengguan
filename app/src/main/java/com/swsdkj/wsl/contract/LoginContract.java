package com.swsdkj.wsl.contract;

import android.content.Context;

import com.swsdkj.wsl.bean.User;

import org.xutils.http.RequestParams;

import java.util.Map;

/**
 * Created by Administrator on 2017/4/25 0025.
 */

public class LoginContract {

    
public interface View{
    void onLogoSuccess(User user);
    void onLogoFail();
    void onSendCodeSuccess();
    void onSendCodeFail();
}

public interface Presenter{
    void login(Context context, RequestParams params);

    void login(Context context, Map<String,String> map);

    void login(Context context, User user);

    void sendCode(Context context,String phone,String nameStr);
}

public interface Model{

}


}