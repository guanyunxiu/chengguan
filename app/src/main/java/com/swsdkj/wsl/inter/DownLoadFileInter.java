package com.swsdkj.wsl.inter;

import org.xutils.common.Callback;

import java.io.File;

/**
 * Created by Administrator on 2017/5/11 0011.
 */

public interface DownLoadFileInter {

    void onWaiting();

    void onStarted();

    void onLoading(long total, long current, boolean isDownloading);

    void onSuccess(File result);

    void onError(Throwable ex, boolean isOnCallback);

    void onCancelled(Callback.CancelledException cex);

    void onFinished();

}
