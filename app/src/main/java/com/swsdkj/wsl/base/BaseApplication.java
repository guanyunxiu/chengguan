package com.swsdkj.wsl.base;

import android.app.Activity;
import android.app.Application;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.cretin.www.cretinautoupdatelibrary.utils.CretinAutoUpdateUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.bean.SealExtensionModule;
import com.swsdkj.wsl.bean.UpdateModel;
import com.swsdkj.wsl.config.MyConfig;
import com.swsdkj.wsl.config.SharedConstants;
import com.swsdkj.wsl.tool.FontsOverride;
import com.swsdkj.wsl.tool.SharedPrefUtil;

import org.xutils.x;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

/**
 * Created by Administrator on 2017/4/24 0024.
 */

public class BaseApplication extends Application {
    public static SharedPrefUtil mSharedPrefUtil;
    private static BaseApplication mApplication;
    public static List<Activity> activityList = new LinkedList<Activity>();
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/PingFang Bold.ttf");
        mApplication = this;
        mSharedPrefUtil=new SharedPrefUtil(this, SharedConstants.sharersinfor);
        x.Ext.init(this);
        RongIM.init(this);

        setMyExtensionModule();
        iniMap();
        initImageLoader();
        codeUpdate();
        MultiDex.install(this);
    }

    public void removeActivity(Activity act, int index) {
        if (activityList != null && !activityList.isEmpty()) {
            activityList.remove(act);
        }
    }

    public void addActivity(Activity act) {
        if (activityList != null) {
            activityList.add(act);
        }
    }
    public void setMyExtensionModule() {
        List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();
        IExtensionModule defaultModule = null;
        if (moduleList != null) {
            for (IExtensionModule module : moduleList) {
                if (module instanceof DefaultExtensionModule) {
                    defaultModule = module;
                    break;
                }
            }
            if (defaultModule != null) {
                RongExtensionManager.getInstance().unregisterExtensionModule(defaultModule);
                RongExtensionManager.getInstance().registerExtensionModule(new SealExtensionModule());
            }
        }
    }
    private void codeUpdate(){
        Log.i("icon",R.mipmap.ic_launcher+"***");
        CretinAutoUpdateUtils.Builder builder = new CretinAutoUpdateUtils.Builder()
                .setBaseUrl("http://www.saiwensida.com:8080/SDK/updatesdk.action")
                .setIgnoreThisVersion(false)
                .setShowType(CretinAutoUpdateUtils.Builder.TYPE_DIALOG)
                .setIconRes(R.mipmap.ic_launcher)
                .showLog(true)
                //设置下载时展示的应用吗
                .setAppName(getResources().getString(R.string.app_name))
                .setRequestMethod(CretinAutoUpdateUtils.Builder.METHOD_GET)
                .setTransition(new UpdateModel())
                .build();
        CretinAutoUpdateUtils.init(builder);
    }
    private void initImageLoader() {
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);

    }
    public static BaseApplication getInstance(){
        return mApplication;
    }
    private AMapLocationClientOption mLocationOption;
    private AMapLocationClient mlocationClient;
    private void iniMap() {
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(mAMapLocationListener);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为3分钟
        mLocationOption.setInterval(2000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }
    //中国lbs太原市小店区140105
    private AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (location != null) {
                Double geoLat = location.getLatitude();
                Double geoLng = location.getLongitude();
                MyConfig.Lat  = geoLat;
                MyConfig.Lng = geoLng;
                String cityCode = location.getCityCode();
                String desc = location.getDistrict();

                location.getStreetNum();

                Log.i("aaaalocation",geoLat+"***"+geoLng);
                String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
                        + "\n精    度    :" + location.getAccuracy() + "米"
                        + "\n定位方式:" + location.getProvider() + "\n定位时间:"
                        + new Date(location.getTime()).toLocaleString() + "\n位置描述:" + desc + "\n省:"
                        + location.getProvince() + "\n市:" + location.getCity()
                         + "\n城市编码:"
                        +cityCode+ "\n区(县):" + location.getDistrict() + "\n区域编码:" + location
                        .getAdCode()+"\n国家:"+location.getCountry()+"\n详细地址："+location.getAddress());

                Log.i("123",str+"--------------高德定位----------------");
                MyConfig. myCity = location.getCity();
                MyConfig.myAddress=location.getAddress();
            }
        }
    };
}
