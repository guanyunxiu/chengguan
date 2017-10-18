package com.swsdkj.wsl.activity;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.swsdkj.wsl.fragment.FootFragment;
import com.swsdkj.wsl.fragment.MainFragment;
import com.swsdkj.wsl.tool.ConnectRong;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

public class MainActivity extends BaseActivity {
    @BindView(R.id.fg_content)
    FrameLayout fgContent;
    @BindView(R.id.id_scenicpot_rdo)
    RadioButton id_scenicpot_rdo;
    @BindView(R.id.id_mine_rdo)
    RadioButton id_mine_rdo;
    @BindView(R.id.id_chat_rdo)
    RadioButton id_chat_rdo;
    @BindView(R.id.message_num)
    TextView idMessageNum;
    private MainFragment mainFragment;
    private FootFragment footFragment;
    private int pos ;
    private int clickFlag;
    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
        ConnectRong.setConnect(MainActivity.this);
        CretinAutoUpdateUtils.getInstance(MainActivity.this).check();
    }

    @Override
    protected void init() {
        pos = getIntent().getIntExtra("pos",0);
        setSelection(pos);
        //查询消息
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RongIM.getInstance().setOnReceiveUnreadCountChangedListener(mCountListener, Conversation.ConversationType.GROUP);
            }
        }, 500);
    }

    @OnClick({R.id.id_scenicpot_rdo,R.id.id_mine_rdo,R.id.id_chat_rdo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_scenicpot_rdo:
                clickFlag = 0;
                setSelection(0);
                break;
            case R.id.id_mine_rdo:
                clickFlag = 1;
                setSelection(1);
                break;
            case R.id.id_chat_rdo:
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
            //首页
            case 0:
                id_scenicpot_rdo.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_main_on, 0, 0);
               /* if (homePageFragment == null) {
                    homePageFragment = new HomePageFragment();
                    ft.add(R.id.fg_content, homePageFragment);
                }*/
                if(mainFragment == null){
                    mainFragment = new MainFragment();
                    ft.add(R.id.fg_content,mainFragment);
                }
                showFragment(ft, mainFragment);
                break;
            //足记
            case 1:
                id_mine_rdo.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_foot_on, 0, 0);
                if (footFragment == null) {
                    footFragment = new FootFragment();
                    ft.add(R.id.fg_content, footFragment);
                }
                showFragment(ft, footFragment);
                break;
            //群聊
            case 2:
                RongIM.getInstance().startGroupChat(this, BaseApplication.mSharedPrefUtil.getString(SharedConstants.GROUPID,""),  BaseApplication.mSharedPrefUtil.getString(SharedConstants.GROUPNAME,""));
                if(clickFlag == 0){
                    id_scenicpot_rdo.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_main_on, 0, 0);
                }else if(clickFlag == 1) {
                    id_mine_rdo.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_foot_on, 0, 0);
                }
              /*  id_chat_rdo.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_my_on, 0, 0);
                if (footFragment == null) {
                    chatFragment = new ChatFragment();
                    ft.add(R.id.fg_content, chatFragment);
                }

                showFragment(ft, chatFragment);*/
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
        id_scenicpot_rdo.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_main, 0, 0);
        id_mine_rdo.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_foot, 0, 0);
    }

    private void showFragment(FragmentTransaction ft, Fragment fragment) {
        hideFragments(ft);
        ft.show(fragment);
    }

    private void hideFragments(FragmentTransaction ft) {
        if (mainFragment != null) {
            ft.hide(mainFragment);
        }
        if (footFragment != null) {
            ft.hide(footFragment);
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
              /*  *//** 单击按钮时启动服务 *//*
                Intent intent = new Intent(MainActivity.this,
                        LocationHttpService.class);
                *//** 退出Activity是，停止服务 *//*
                stopService(intent);*/
            } else {
                RongIM.getInstance().disconnect();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public RongIM.OnReceiveUnreadCountChangedListener mCountListener = new RongIM.OnReceiveUnreadCountChangedListener() {
        @Override
        public void onMessageIncreased(int count) {
            if (count == 0) {
                idMessageNum.setVisibility(View.GONE);
            } else if (count > 0 && count < 100) {
                idMessageNum.setVisibility(View.VISIBLE);
                idMessageNum.setText(count+"");
            } else {
                idMessageNum.setVisibility(View.VISIBLE);
                idMessageNum.setText(R.string.no_read_message);
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CretinAutoUpdateUtils.getInstance(this).destroy();
    }
}
