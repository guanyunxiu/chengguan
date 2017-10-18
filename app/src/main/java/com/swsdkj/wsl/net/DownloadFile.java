package com.swsdkj.wsl.net;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import com.swsdkj.wsl.inter.DownLoadFileInter;
import com.swsdkj.wsl.view.MyDialog;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

/**
 * Created by Administrator on 2017/5/11 0011.
 */

public class DownloadFile {

    public static void downloadFile(final Context context, final String url, String path,final DownLoadFileInter loadFileInter) {
        final MyDialog dialog = MyDialog.showDialog(context);
        dialog.show();

        RequestParams requestParams = new RequestParams(url);
        requestParams.setSaveFilePath(path);

        x.http().get(requestParams, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {
                loadFileInter.onWaiting();
            }

            @Override
            public void onStarted() {
                loadFileInter.onStarted();
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

                loadFileInter.onLoading(total,current,isDownloading);
               /* progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMessage("亲，努力下载中。。。");
                progressDialog.show();
                progressDialog.setMax((int) total);
                progressDialog.setProgress((int) current);*/
            }

            @Override
            public void onSuccess(File result) {

                loadFileInter.onSuccess(result);
                dialog.dismiss();

                /*  Toast.makeText(context, "下载成功", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();*/
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                loadFileInter.onError(ex,isOnCallback);
                dialog.dismiss();
              /*  ex.printStackTrace();
                Toast.makeText(MainActivity.this, "下载失败，请检查网络和SD卡", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();*/
            }

            @Override
            public void onCancelled(CancelledException cex) {
                loadFileInter.onCancelled(cex);
            }

            @Override
            public void onFinished() {
                loadFileInter.onFinished();
            }
        });
    }

    public static String getDownloadFilePath(){
        String path = "";

        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".mp4";

        File file = new File(appDir, fileName);
        path = file.getAbsolutePath();
        return  path;
    }
}
