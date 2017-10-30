package com.swsdkj.wsl.base;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.maps2d.MapView;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.view.LoadStateManager;
import com.swsdkj.wsl.view.MultiStateView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017\8\7 0007.
 */

public abstract class BaseFragment extends Fragment {
    @Nullable
    @BindView(R.id.multiStateView)
    public MultiStateView multiStateView;
    @Nullable
    @BindView(R.id.map)
    public MapView mapView;
    protected Context mContext;
    //缓存Fragment view
    private View mRootView;
    public Toolbar toolbar;
    public TextView titleName;
    private boolean mIsMulti = false;
    Unbinder unbinder;
    public LoadStateManager mLoadStateManager;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(attachLayoutRes(), null);
            unbinder = ButterKnife.bind(this, mRootView);
            if(mapView != null) {
                Log.i("map","mapview");
                mapView.onCreate(savedInstanceState);
            }
            initToolBar();
            initViews();

            if(multiStateView != null) {
                initMulState();
            }
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }
    public void initMulState(){
        //loadManager初始化
        mLoadStateManager = new LoadStateManager();
        mLoadStateManager.setOnStateChangeListener(new BaseStateManager.OnStateChangeListener<LoadStateManager.LoadState>() {
            @Override
            public void OnStateChange(LoadStateManager.LoadState state, Object obj) {
                switch (state) {
                    case Init:
                        multiStateView.setViewState(MultiStateView.ViewState.LOADING);
                        break;
                    case Success:
                        multiStateView.setViewState(MultiStateView.ViewState.CONTENT);
                        break;
                    case Failure:
                        multiStateView.setViewState(MultiStateView.ViewState.ERROR);
                        break;
                    case NoData:
                        multiStateView.setViewState(MultiStateView.ViewState.EMPTY);
                    default:
                        break;
                }
            }
        });
        mLoadStateManager.setState(LoadStateManager.LoadState.Init);
        multiStateView.setRefreshOnClickListener(new View.OnClickListener() {
            //TODO refreesh
            @Override
            public void onClick(View v) {
                mLoadStateManager.setState(LoadStateManager.LoadState.Init);
                updateViews();
            }
        });
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint() && mRootView != null && !mIsMulti) {
            mIsMulti = true;
            updateViews();
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && isVisible() && mRootView != null  && !mIsMulti) {
            mIsMulti = true;
            updateViews();
        } else {
            super.setUserVisibleHint(isVisibleToUser);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // unbinder.unbind();
    }
    /**
     * 绑定布局文件
     * @return  布局文件ID
     */
    protected abstract int attachLayoutRes();
    /**
     * 初始化视图控件
     */
    protected abstract void initViews();

    /**
     * 更新视图控件
     */
    protected abstract void updateViews();


    protected void initToolBar(){
        toolbar = (Toolbar)mRootView.findViewById(R.id.toolbar);
        titleName = (TextView)mRootView.findViewById(R.id.title_name);

    }
    /**
     * 初始化 Toolbar
     *
     * @param homeAsUpEnabled
     * @param title
     */
    protected void initTitle(boolean homeAsUpEnabled, String title) {
        ((BaseActivity)getActivity()).initTitle(toolbar, homeAsUpEnabled);
        if(!TextUtils.isEmpty(title)) {
            titleName.setText(title);
        }
    }

}
