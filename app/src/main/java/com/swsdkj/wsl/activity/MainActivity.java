package com.swsdkj.wsl.activity;

import android.Manifest;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cretin.www.cretinautoupdatelibrary.utils.CretinAutoUpdateUtils;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.base.BaseActivity;
import com.swsdkj.wsl.base.BaseApplication;
import com.swsdkj.wsl.base.CheckPermissionsActivity;
import com.swsdkj.wsl.config.SharedConstants;
import com.swsdkj.wsl.fragment.ChatFragment;
import com.swsdkj.wsl.fragment.CollectInfoFragment;
import com.swsdkj.wsl.fragment.FootFragment;
import com.swsdkj.wsl.fragment.InfoListFragment;
import com.swsdkj.wsl.fragment.InfoListFragment2;
import com.swsdkj.wsl.fragment.InfoListFragment3;
import com.swsdkj.wsl.fragment.MainFragment;
import com.swsdkj.wsl.fragment.PersonInfoFragment;
import com.swsdkj.wsl.tool.ConnectRong;
import com.tbruyelle.rxpermissions.RxPermissions;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import rx.functions.Action1;

public class MainActivity extends BaseActivity {
    @BindView(R.id.fg_content)
    FrameLayout fgContent;
    @BindView(R.id.main01_rd)
    RadioButton main01Rd;
    @BindView(R.id.main02_rd)
    RadioButton main02Rd;
    @BindView(R.id.main03_rd)
    RadioButton main03Rd;
    private InfoListFragment3 infoListFragment;
    private CollectInfoFragment collectInfoFragment;
    private PersonInfoFragment personInfoFragment;
    private int pos ;
    private int clickFlag;
    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        ConnectRong.setConnect(MainActivity.this);
        CretinAutoUpdateUtils.getInstance(MainActivity.this).check();
        pos = getIntent().getIntExtra("pos",0);
        setSelection(1);
        getPermission();
    }
    @Override
    protected void updateViews() {
    }
    @OnClick({R.id.main01_rd,R.id.main02_rd,R.id.main03_rd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main01_rd:
                clickFlag = 0;
                setSelection(0);
                break;
            case R.id.main02_rd:
                clickFlag = 1;
                setSelection(1);
                break;
            case R.id.main03_rd:
                setSelection(2);
                break;
        }
    }
    //底部导航栏点击显示
    private int curSelection;
    public void setSelection(int index) {
        resetImg();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //设置刚进来为推荐页面
        int curSelection2 = curSelection;
        curSelection = index;
        switch (index) {
            //信息列表
            case 0:
                main01Rd.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.tab_01_true, 0, 0);
                main01Rd.setTextColor(getResources().getColor(R.color.yellow));
                if(infoListFragment == null){
                    infoListFragment = new InfoListFragment3();
                    ft.add(R.id.fg_content,infoListFragment);
                }
                showFragment(ft, infoListFragment);
                break;
            //信息搜集
            case 1:
                main02Rd.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.tab_02_true, 0, 0);
                main02Rd.setTextColor(getResources().getColor(R.color.yellow));
                if (collectInfoFragment == null) {
                    collectInfoFragment = new CollectInfoFragment();
                    ft.add(R.id.fg_content, collectInfoFragment);
                }
                showFragment(ft, collectInfoFragment);
                break;
            //个人资料
            case 2:
                main03Rd.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.tab_03_true, 0, 0);
                main03Rd.setTextColor(getResources().getColor(R.color.yellow));
                if (personInfoFragment == null) {
                    personInfoFragment = new PersonInfoFragment();
                    ft.add(R.id.fg_content, personInfoFragment);
                }
                showFragment(ft, personInfoFragment);
                break;
            default:
                curSelection = curSelection2;
                break;
        }
        ft.commit();
    }


    /**
     * 恢复默认图片
     */
    private void resetImg() {
        main01Rd.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.tab_01_false, 0, 0);
        main02Rd.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.tab_02_false, 0, 0);
        main03Rd.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.tab_03_false, 0, 0);
        main01Rd.setTextColor(getResources().getColor(R.color.white));
        main02Rd.setTextColor(getResources().getColor(R.color.white));
        main03Rd.setTextColor(getResources().getColor(R.color.white));
    }

    private void showFragment(FragmentTransaction ft, Fragment fragment) {
        hideFragments(ft);
        ft.show(fragment);
    }

    private void hideFragments(FragmentTransaction ft) {
        if (infoListFragment != null) {
            ft.hide(infoListFragment);
        }
        if (collectInfoFragment != null) {
            ft.hide(collectInfoFragment);
        }
        if (personInfoFragment != null) {
            ft.hide(personInfoFragment);
        }
    }

    private long exitTime = 0;
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出应该程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
                if (infoListFragment.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    //如果DrawerLayout 拦截点击 关闭Drawer
                    infoListFragment.drawerLayout.closeDrawer(GravityCompat.END);
                }
            } else {
                RongIM.getInstance().disconnect();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CretinAutoUpdateUtils.getInstance(this).destroy();
    }
    //判断权限
    private void getPermission() {
        new RxPermissions(this).request(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {

                        } else {

                        }
                    }
                });
    }

}
