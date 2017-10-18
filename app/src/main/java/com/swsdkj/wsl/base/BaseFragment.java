package com.swsdkj.wsl.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.swsdkj.wsl.tool.SharedPrefUtil;

import butterknife.ButterKnife;

/**
 * 作者： 关云秀 on 2016/11/22.
 * 描述：
 */
public abstract class BaseFragment extends Fragment {
    public SharedPrefUtil mSharedPrefUtil;
    protected Context mContext;
    protected Activity mActivity;
    protected View mContainer;
    private Toolbar toolBar;
    public TextView titleName,rightName;
    //控件是否已经初始化
    public boolean isCreateView = false;
    //是否已经加载过数据
    public boolean isLoadData = false;
    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);

        mContext = activity;
        mActivity = activity;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                       Bundle savedInstanceState) {
        mContainer = createView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, mContainer);
        isCreateView = true;
       // initToolbar();
        return mContainer;
    }
    /*private void initToolbar() {
        toolBar = (Toolbar) mContainer.findViewById(R.id.toolbar);
        titleName = (TextView) mContainer.findViewById(R.id.title_name);
        rightName = (TextView) mContainer.findViewById(R.id.title_rightTv);
        // titleBack = (ImageView)findViewById(R.id.title_back);
       // setSupportActionBar(toolBar);

    }*/

    protected void setTitleName(boolean leftImg,String title,String rightTitle){
      /*  if(leftImg){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);*/
        titleName.setText(title);
        if(!TextUtils.isEmpty(rightTitle)){
            rightName.setVisibility(View.VISIBLE);
            rightName.setText(rightTitle);
        }
    }
    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(mContainer, savedInstanceState);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
    }



    /**
     * 为Activity设置布局
     */
    protected abstract View createView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState);

    protected abstract void init(View container, Bundle savedInstanceState);
    /**
     * 通过Class跳转界面
     **/
    public void startActivity(Class<?> cls) {
        startActivityWithBundle(cls, null);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivityWithBundle(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }
    /**
     * 有回调的Activity跳转
     */
    protected void startActivityForResult(Class<?> cls, int requestCode,Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }
    //此方法在控件初始化前调用，所以不能在此方法中直接操作控件会出现空指针
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isCreateView) {
            lazyInitData();
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //第一个fragment会调用
        if (getUserVisibleHint())
            lazyInitData();
    }
    public abstract void lazyInitData();

}
