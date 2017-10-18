package com.swsdkj.wsl.contract;

import android.content.Context;

import com.swsdkj.wsl.bean.SignBean;

import org.xutils.http.RequestParams;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/25 0025.
 */

public class SignYesContract {
    
    
public interface View{
    void onSuccess(List<SignBean> signBeanList);
    void onFail();

}

public interface Presenter{
    void selsignby(Context context, RequestParams params);
}

public interface Model{
}


}