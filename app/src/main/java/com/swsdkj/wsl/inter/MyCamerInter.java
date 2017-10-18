package com.swsdkj.wsl.inter;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/5/10 0010.
 */

public interface MyCamerInter {
    void captureSuccess(Bitmap bitmap);

    void recordSuccess(String url);

    void quit();



}
