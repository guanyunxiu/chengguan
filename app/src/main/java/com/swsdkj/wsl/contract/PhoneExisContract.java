package com.swsdkj.wsl.contract;

import android.content.Context;

/**
 * Created by Administrator on 2017\5\31 0031.
 */
public interface PhoneExisContract {
    interface Model {

    }

    interface View {
        void onSuccess(int code);
        void onFail();

    }

    interface Presenter {
        void onSubmit(Context context, String phone);
    }
}
