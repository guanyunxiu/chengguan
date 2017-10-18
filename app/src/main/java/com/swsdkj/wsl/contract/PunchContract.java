package com.swsdkj.wsl.contract;

import android.content.Context;

/**
 * 作者： 关云秀 on 2017/5/17.
 * 描述：
 */
public class PunchContract {
public interface View{
    void onSuccess();
    void onFail(int flag);
}

public interface Presenter{
    void onSubmit(Context context, String userid, String address);
}

public interface Model{
}


}