package com.wbapp.omxvideo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class FTP {
    private Activity activity;

    Toast connectFailToast;
    Toast loginFailToast;
    Toast uploadFailToast;
    Toast successToast;
    
    public FTP(Activity act) {
        this.activity = act;
        connectFailToast = Toast.makeText(activity, "连接Ftp失败！", Toast.LENGTH_SHORT);
        loginFailToast = Toast.makeText(activity, "登录Ftp失败！", Toast.LENGTH_SHORT);
        uploadFailToast = Toast.makeText(activity, "上传失败！", Toast.LENGTH_SHORT);
        successToast = Toast.makeText(activity, "拍照上传成功", Toast.LENGTH_SHORT);
    }
    
    public void close() {
        disconnect();
    }
    
    private String host;
    private int port;
    private String user;
    private String password;

    public void setParams(String server, int port, String usr, String pass) {
        if (host!=null && host.compareTo(server)==0 &&
            this.port == port &&
            user!=null && user.compareTo(usr)==0 &&
            password!=null && password.compareTo(pass) ==0 )
        {
            // no param changed
            return;
        }
        disconnect(); //require re-connect
        this.host = server;
        this.port = port;
        this.user = usr;
        this.password = pass;
    }

    public void startUpload(String path) {
        UploadTask task = new UploadTask();
        task//.execute(path);
            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
    }

    private class UploadTask extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            if (!connect())
                return -1;
            if (!login()) {
                disconnect();
                return -2;
            }
            if (!upload(params[0])) {
                disconnect();
                return -3;
            }
            return 0;
        }

        protected void onPostExecute(Integer result) {
            switch (result) {
            case -1:
                connectFailToast.show();
                break;
            case -2:
                loginFailToast.show();
                break;
            case -3:
                uploadFailToast.show();
                break;
            case 0:
                successToast.show();
                break;
            }
        }
    };
    
    FTPClient client;
    
    private boolean connect() {
        if (client==null)
            client = new FTPClient();
        if(client.isConnected())
            return true;
        try {
            client.connect(host, port);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    private void disconnect() {
        if(client==null)
            return;
        if(client.isConnected()) {
            try {
                client.disconnect(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        client = null;
    }
    
    private boolean login() {
        if(client.isAuthenticated())
            return true;
        try {
            client.login(user, password);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    private boolean upload(String path) {
        Log.i("", "upload "+path);
        File file = new File(path);
        try {
            client.upload(file);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
