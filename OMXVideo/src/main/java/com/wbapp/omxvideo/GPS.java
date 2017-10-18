package com.wbapp.omxvideo;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class GPS implements Runnable {
    
    OMXVideoActivity activity;
    Thread thread;
    boolean cancelThread = false;
    LocationManager lm;
    
    Handler mHandler = new Handler();
    
    Runnable locationUpdater = new Runnable() {
        @Override
        public void run() {
            requestLocationUpdate();
        }
    };
    
    public GPS(Activity act) {
        activity = (OMXVideoActivity)act;
    }
    
    public void start() {
        requestLocationUpdate();
        thread = new Thread(this);
        thread.start();
    }
    
    public void stop() {
        cancelThread = true;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void requestLocationUpdate() {
        if (lm == null)
            lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0.1f, locationListenerGps);
        }
        else
            Log.e("", "gps provider not enabled");
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0.1f, locationListenerGps);
        }
        else
            Log.e("", "network location provider not enabled");
    }
    
    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        SimpleDateFormat tfmt = new SimpleDateFormat("HHmmss");
        SimpleDateFormat dfmt = new SimpleDateFormat("ddMMyy");
        Date date = new Date();
        // 查找到服务信息    位置数据标准类
        Criteria criteria = new Criteria();
        //查询精度:高
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 是否查询海拔:是
        criteria.setAltitudeRequired(true);
        //是否查询方位角:是
        criteria.setBearingRequired(true);
        //是否允许付费
        criteria.setCostAllowed(true);
        // 电量要求:低
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        //是否查询速度:是
        criteria.setSpeedRequired(true);
        String provider = null;
        while(!cancelThread) {
            provider = lm.getBestProvider(criteria, true);
            Log.i("", "location provider "+provider);
            if(provider!=null)
                break;
            else
                sleep(5000);
        }

        while(!cancelThread) {
            mHandler.post(locationUpdater);
            Location location = lm.getLastKnownLocation(provider);
            if(location!=null) {
                float bear = location.getBearing();   //偏离正北方的度数
                float latitude = (float)location.getLatitude();      //维度
                float longitude= (float)location.getLongitude();     //经度
                float speed = location.getSpeed();    //速度
                long time = location.getTime() - (long)(8*60*60*1000);  //时间
                date.setTime(time);
                String ts = tfmt.format(date);
                String ds = dfmt.format(date);
                float lat = convertDegree(latitude);
                float lon = convertDegree(longitude);

               String gpsdata = String.format(
                       "$GPRMC,%s.00,A,%f,N,%f,E,%.03f,%.02f,%s,,,A*57",
                       ts, lat, lon, speed, bear, ds);
                gpsdata = gpsdata + "\r\n";
                Log.i("", String.format("location: %s", gpsdata));
                OMXVideoJNI.getInstance().sendGPSData(gpsdata, gpsdata.length());
                
                String gpsString = String.format("%f %f", lat, lon);
                activity.setGpsString(gpsString);
            }
            else {
                Log.e("", "cannot get location info");
            }

            sleep(5000);
        }
    }
    
    private float convertDegree(float deg) {
        float d = (float)Math.floor(deg);
        float m = (deg-d)*60;
        return d*100+m;
    }
    
    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.i("", "location changed");
        }
        public void onStatusChanged(String provider, int status, Bundle extra) {
            Log.i("", "status changed. provider="+provider);
        }
        public void onProviderDisabled(String provider) {
            Log.i("", provider + " provider disabled");
        }
        public void onProviderEnabled(String provider) {
            Log.i("", provider + " provider enabled");
        }
    };
}
