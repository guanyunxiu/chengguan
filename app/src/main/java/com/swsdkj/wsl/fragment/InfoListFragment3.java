package com.swsdkj.wsl.fragment;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.maning.mndialoglibrary.MProgressDialog;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.activity.CollectListSelectActivity;
import com.swsdkj.wsl.activity.CollectSelectInfoActivity;
import com.swsdkj.wsl.activity.GaoDeMapActivity;
import com.swsdkj.wsl.activity.MainActivity;
import com.swsdkj.wsl.adapter.CollectItemAdapter;
import com.swsdkj.wsl.adapter.TypeAdapter;
import com.swsdkj.wsl.base.BaseFragment;
import com.swsdkj.wsl.bean.CollectTypeBean;
import com.swsdkj.wsl.bean.ProType;
import com.swsdkj.wsl.tool.CommonUtil;
import com.swsdkj.wsl.view.DividerDecoration;
import com.swsdkj.wsl.view.LoadStateManager;
import com.swsdkj.wsl.view.MultiStateView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 内容：
 * Created by 关云秀 on 2017\10\19 0019.
 */

public class InfoListFragment3 extends BaseFragment implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener,LocationSource,
        AMapLocationListener,GeocodeSearch.OnGeocodeSearchListener {

    @BindView(R.id.search_et)
    EditText searchEt;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.multiStateView)
    MultiStateView multiStateView;
    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.listview)
    RecyclerView listview;
    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;
    @BindView(R.id.right_lv)
    LinearLayout rightLv;
    @BindView(R.id.drawer_layout)
    public DrawerLayout drawerLayout;
    @BindView(R.id.location_tv)
    TextView locationTv;
    @BindView(R.id.sub_btn)
    Button subBtn;
    @BindView(R.id.shaixuan_tv)
    TextView shaixuanTv;
    CollectItemAdapter collectItemAdapter;
    MProgressDialog mMProgressDialog;
    private List<ProType> typeList;
    TypeAdapter adapter;

    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;
    MarkerOptions markerOption;
    LatLng latLng;
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private GeocodeSearch geocoderSearch;
    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_infolist3;
    }

    @Override
    protected void initViews() {
        //initTitle(false, "信息列表");
        setHasOptionsMenu(true);
        toolbar.inflateMenu(R.menu.menu_shaixuan);
        titleName.setText("信息列表");
        //注册EventBus,先订阅才能传值
        EventBus.getDefault().register(this);

        initContent();//初始化中间内容
        initRightMenu();//初始化右边菜单
        searchClick();
        initMap();
    }
    @Override
    protected void updateViews() {
        mLoadStateManager.setState(LoadStateManager.LoadState.Success);

    }
    /**
     * 初始化AMap对象
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        geocoderSearch = new GeocodeSearch(mContext);
        geocoderSearch.setOnGeocodeSearchListener(this);
    }
    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.mipmap.gps_point));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(STROKE_COLOR);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(2);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(FILL_COLOR);
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);
        // aMap.getUiSettings().setScrollGesturesEnabled(false);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setOnMarkerDragListener(new AMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {//长按拖动开始
//                ToastUtil.showLong("开始得到经纬度");
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                //拖动中
                //CommonUtil.showToast(mContext,marker.getPosition().latitude + "***"+marker.getPosition().longitude);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                //拖动结束
               // CommonUtil.showToast(mContext,marker.getPosition().latitude + "***"+marker.getPosition().longitude);
                LatLonPoint latLonPoint = new LatLonPoint(marker.getPosition().latitude, marker.getPosition().longitude);
                getAddress(latLonPoint);

            }
        });
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }

    /**
     * 初始化中间内容
     */
    public void initContent(){
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        collectItemAdapter = new CollectItemAdapter(getList());
        //noticeAdapter.setLoadingView(new CustomLoadMoreView(mContext));
        collectItemAdapter.setOnLoadMoreListener(this);
        collectItemAdapter.openLoadMore(5, true);
        collectItemAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
//        pullToRefreshAdapter.setPreLoadNumber(3);
        mRecyclerView.setAdapter(collectItemAdapter);
        collectItemAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                startActivity(new Intent(mContext, CollectSelectInfoActivity.class));
            }
        });
    }

    /**
     * 初始化右边菜单
     */
    public void initRightMenu(){
        getProType();
        listview.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new TypeAdapter(mContext, typeList);
        listview.setAdapter(adapter);
        listview.addItemDecoration(new DividerDecoration(mContext));
        //创建MyFragment对象

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, CollectTypeFragment.newInstance(0,1));
        fragmentTransaction.commit();
        setOnClick();
    }

    /***
     * 单击事件
     */
    public void setOnClick(){
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                drawerView.setClickable(true);

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        adapter.setTypeItem(new TypeAdapter.TypeItem() {
            @Override
            public void TypeItemImpl(int typeId) {
                adapter.selectTypeId = typeId;
                adapter.notifyDataSetChanged();
                CommonUtil.showToast(mContext,typeId+"***");
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                        .beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, CollectTypeFragment.newInstance(typeId,1));
                fragmentTransaction.commit();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent();
                switch (item.getItemId()){
                    case R.id.item_shaixuan:
                        drawerLayout.openDrawer(rightLv);
                        break;
                }
                return true;
            }
        });
        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, CollectListSelectActivity.class));
            }
        });
    }


    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoadMoreRequested() {

    }

    /**
     * 搜索操作
     */
    public void searchClick() {
        searchEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                // 修改回车键功能
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // 先隐藏键盘
                    CommonUtil.hideSoftInput(mContext, searchEt);
                    mMProgressDialog = CommonUtil.configDialogDefault(mContext);
                    mMProgressDialog.show("搜索中...");
                }
                return false;
            }
        });
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_shaixuan, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_shaixuan:
                drawerLayout.openDrawer(rightLv);
                break;
        }
        return true;
    }
    public List<CollectTypeBean> getList() {
        List<CollectTypeBean> collectTypeBeanList = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            CollectTypeBean collectTypeBean = new CollectTypeBean();
            collectTypeBean.setName("下水井盖");
            collectTypeBeanList.add(collectTypeBean);
        }
        return collectTypeBeanList;
    }
    public List<ProType> getProType(){
        typeList = new ArrayList<>();
        ProType proType = new ProType(1,"公共设施","");
        typeList.add(proType);
        ProType proType2 = new ProType(2,"道路交通","");
        typeList.add(proType2);
        ProType proType3 = new ProType(3,"市容环境","");
        typeList.add(proType3);
        ProType proType4 = new ProType(4,"园林绿化","");
        typeList.add(proType4);
        ProType proType5 = new ProType(5,"房屋土地","");
        typeList.add(proType5);
        ProType proType6 = new ProType(6,"其他设施","");
        typeList.add(proType6);
        return typeList;
    }
    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(String flag){
        String[] str = flag.split("：");
        if(str[0].equals("信息查询")){
           // groupPrecenter.onSelect();
            drawerLayout.closeDrawer(GravityCompat.END);
        }
    }




    /**
     * 方法必须重写
     */
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消注册
        mapView.onDestroy();
    }
    private void addMarkerToMap(double jingdu, double weidu) {
        latLng = new LatLng(jingdu, weidu);
        markerOption = new MarkerOptions();
        markerOption.position(latLng);
        Marker marker = aMap.addMarker(markerOption);
        marker.setDraggable(true);
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.mipmap.marker)))
        ;
        marker.showInfoWindow();
        marker.setRotateAngle(0);
    }
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {
                //mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                //定位成功回调信息，设置相关消息
                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(20));
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(aMapLocation);
                    //获取定位信息
                    StringBuffer buffer = new StringBuffer();
                    addMarkerToMap(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    buffer.append(aMapLocation.getCountry() + ""
                            + aMapLocation.getProvince() + ""
                            + aMapLocation.getCity() + ""
                            + aMapLocation.getProvince() + ""
                            + aMapLocation.getDistrict() + ""
                            + aMapLocation.getStreet() + ""
                            + aMapLocation.getStreetNum());
                    locationTv.setText(buffer.toString());
                   // android.widget.Toast.makeText(mContext, buffer.toString(), android.widget.Toast.LENGTH_LONG).show();
                    isFirstLoc = false;
                   /* mWeidu.setText(aMapLocation.getLatitude() + "");
                    mJingdu.setText(aMapLocation.getLongitude() + "");
                    Intent intent = new Intent(GaoDeMapActivity.this, MainActivity.class);
                    intent.putExtra("gaodeweidu", aMapLocation.getLatitude() + "");
                    intent.putExtra("gaodejingdu", aMapLocation.getLongitude() + "");
                    setResult(1, intent);*/
                }
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(mContext);
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
    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 0,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                String addressName = result.getRegeocodeAddress().getFormatAddress();
                //CommonUtil.showToast(mContext,addressName);
                locationTv.setText(addressName);
            } else {
                //ToastUtil.show(ReGeocoderActivity.this, R.string.no_result);
            }
        } else {
            // ToastUtil.showerror(this, rCode);
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}
