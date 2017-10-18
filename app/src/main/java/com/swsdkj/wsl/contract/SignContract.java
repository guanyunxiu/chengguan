package com.swsdkj.wsl.contract;

import android.content.Context;

import com.swsdkj.wsl.bean.User;

import java.io.File;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/5 0005.
 */

public class SignContract {

    public interface View{
        void onSignSuccess();
        void onSignFail();
    }

    public interface Presenter{
        void sign(Context context,String fileStr1,String fileStr2,File face1,File face2,String remarks);
    }

    public interface Model{
    }

}
