package com.swsdkj.wsl.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.activity.SignActivity;
import com.swsdkj.wsl.activity.WeatherSearchActivity;
import com.swsdkj.wsl.base.BaseApplication;
import com.swsdkj.wsl.config.MyConfig;
import com.swsdkj.wsl.config.SharedConstants;
import com.swsdkj.wsl.contract.PunchContract;
import com.swsdkj.wsl.presenter.PunchPresenterImpl;
import com.swsdkj.wsl.tool.CommonUtil;
import com.swsdkj.wsl.tool.DialogUtil;

/**
 * Created by Administrator on 2017/4/24 0024.
 */

public class MainFragment extends Fragment implements LocationSource,
        AMapLocationListener,PunchContract.View {
    private View layoutView;
    private MapView mapView;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private LinearLayout id_Sign,id_Sign2;
    private TextView addressTv,timeTV,time2TV,nameTV,weatherTv,videoTv;
    private PunchPresenterImpl punchPresenter;
    private String addressStr;
    private TextView dateTv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_main, null);
        punchPresenter = new PunchPresenterImpl(this);
        mapView = (MapView) layoutView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        id_Sign = (LinearLayout)layoutView.findViewById(R.id.id_Sign);
        id_Sign2 = (LinearLayout)layoutView.findViewById(R.id.id_Sign2);
        videoTv = (TextView)layoutView.findViewById(R.id.id_video_tv);
        dateTv = (TextView)layoutView.findViewById(R.id.id_date_tv);
        id_Sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSign();
            }
        });
        id_Sign2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String time = time2TV.getText().toString();
                confirm("当前时间为:"+time+",是否确定打卡");
            }
        });
        addressTv = (TextView)  layoutView.findViewById(R.id.id_address);
        timeTV = (TextView)layoutView.findViewById(R.id.id_time);
        time2TV = (TextView)layoutView.findViewById(R.id.id_time2);
        nameTV = (TextView)layoutView.findViewById(R.id.id_nameTV);
        weatherTv = (TextView)layoutView.findViewById(R.id.id_weather_tv);
        weatherTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WeatherSearchActivity.class);
                startActivity(intent);

                // RongIM.getInstance().startGroupChat(getActivity(), "qun0001", "fengjinchao");
            }
        });

        iniMap();
        initView();
        //startTime();
        initTime();
        return layoutView;
    }

    private void initView() {
        nameTV.setText(BaseApplication.mSharedPrefUtil.getString(SharedConstants.NAME,"")+"");
        dateTv.setText(CommonUtil.getTime3());
        addressStr = MyConfig.myAddress;
        if(!TextUtils.isEmpty(addressStr)) {
            addressTv.setText(addressStr + "");
        }
    }

    private void initTime() {
        //第二种mHandler.postDelayed(runnable, 1000);
        new Thread(new ThreadShow()).start();
    }
    public void confirm(String content){
        DialogUtil.alertDialog(getActivity(),content)
                .setPositiveButton(getString(R.string.ensure), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        punchPresenter.onSubmit(getActivity(), BaseApplication.mSharedPrefUtil.getString(SharedConstants.ID,""),MyConfig.myAddress);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
    }
    // handler类接收数据
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                timeTV.setText(CommonUtil.getTime1());
                time2TV.setText(CommonUtil.getTime1());
            }
        };
    };

    @Override
    public void onSuccess() {

        CommonUtil.showToast(getActivity(),"打卡成功");
    }
    @Override
    public void onFail(int flag) {
        if(flag == 6){
            CommonUtil.showToast(getActivity(), "今日已打卡完毕，不能再进行打卡");
        }else {
            CommonUtil.showToast(getActivity(), "打卡失败，请重新打卡");
        }
    }

    // 线程类
    class ThreadShow implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                    System.out.println("send...");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("thread error...");
                }
            }
        }
    }

    //签到打卡
    private void setSign(){
        Intent intent = new Intent(getActivity(), SignActivity.class);
        startActivity(intent);
    }

    private void iniMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        //CommonUtil.showToast(getActivity(),"************"+MyConfig.myAddress);

    }
    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {


        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.mipmap.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setZoomGesturesEnabled(false);
       // aMap.getUiSettings().setAllGesturesEnabled(false);
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        // aMap.setMyLocationType()
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }


    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(getActivity());
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }


}
