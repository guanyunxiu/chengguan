package com.swsdkj.wsl.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swsdkj.wsl.R;
import com.swsdkj.wsl.adapter.SignAdapter;
import com.swsdkj.wsl.adapter.SignAdapter2;
import com.swsdkj.wsl.base.BaseStateManager;
import com.swsdkj.wsl.bean.SignBean;
import com.swsdkj.wsl.contract.SignYesContract;
import com.swsdkj.wsl.net.BaseUrl;
import com.swsdkj.wsl.presenter.SignYesPresenterImpl;
import com.swsdkj.wsl.tool.CommonUtil;
import com.swsdkj.wsl.tool.DisplayUtil;
import com.swsdkj.wsl.tool.MyDecoration;
import com.swsdkj.wsl.view.LoadStateManager;
import com.swsdkj.wsl.view.MultiStateView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * Created by Administrator on 2017/4/25 0025.
 */

public class SignYesFragment2 extends Fragment implements SignYesContract.View {

    @BindView(R.id.rlv)
    RecyclerView rlv;
    @BindView(R.id.rotate_header_grid_view_frame)
    PtrClassicFrameLayout mPtrFrame;
    @BindView(R.id.multiStateViews)
    MultiStateView multiStateView;
    private SignYesPresenterImpl yesPresenter;
    private int page = 1, size = 20;
    private SignAdapter signYesAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private View layoutView;
    private List<SignBean> signBeanList = new ArrayList<>();
    private int year, month, day;
    private LoadStateManager mLoadStateManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.activity_custom, null);
        ButterKnife.bind(this, layoutView);
        //注册EventBus,先订阅才能传值
        EventBus.getDefault().register(this);

        yesPresenter = new SignYesPresenterImpl(this);
        installListeners();
        initView();
        setAdapter();
        setOnClick();

        return layoutView;
    }
    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(String flag){
        mLoadStateManager.setState(LoadStateManager.LoadState.Init);
        page=1;
        //signBeanList.clear();
        initData(page,size);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);//取消注册
    }
    public void installListeners() {
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
                initData(page,size);
            }
        });

    }

    public void setAdapter() {
        CommonUtil.setRecycler(null,rlv);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        rlv.setLayoutManager(mLinearLayoutManager);
        //添加分隔线
        rlv.addItemDecoration(new MyDecoration(getActivity(), MyDecoration.VERTICAL_LIST));
        signYesAdapter = new SignAdapter(getActivity(),signBeanList);
        rlv.setAdapter(signYesAdapter);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrDefaultHandler2() {
            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                page++;
                initData(page,size);
            }
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                page=1;
               // signBeanList.clear();
                initData(page,size);
            }
        });
        // the following are default settings

       CommonUtil.setHeader(getActivity(),mPtrFrame);
        // default is false
        mPtrFrame.setPullToRefresh(true);
        mPtrFrame.disableWhenHorizontalMove(true);//解决横向滑动冲突
        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                // mPtrFrame.autoRefresh();
               // CommonUtil.showToast(getActivity(),"下拉");
                mPtrFrame.autoRefresh(true);
            }
        }, 100);
        // updateData();

    }

    public void setOnClick() {

    }

    private void initView() {
        year = CommonUtil.getYear();
        month = CommonUtil.getMouth();
        day = CommonUtil.getDay();
    }


    //初始化一开始加载的数据
    private void initData(int page, int size) {
        RequestParams params = new RequestParams(BaseUrl.selsignby);
        params.addBodyParameter("month", month + "");
        params.addBodyParameter("day", day + "");
        params.addBodyParameter("year", year + "");
        params.addBodyParameter("category", "");
        params.addBodyParameter("sx", "");
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("size", size + "");
        params.addBodyParameter("ft", "1");
        yesPresenter.selsignby(getActivity(), params);

    }

    public void selectBytime(int page, int size, int year, int Mouth, int dau) {
        page = 1;
       // signBeanList.clear();
        this.year = year;
        this.month = Mouth;
        this.day = dau;
        mLoadStateManager.setState(LoadStateManager.LoadState.Init);
        initData(page,size);
    }

    @Override
    public void onSuccess(List<SignBean> list) {
        if(page == 1){
            signBeanList.clear();
        }
        if(page== 1 &&list.size() == 0) {
                mLoadStateManager.setState(LoadStateManager.LoadState.NoData);
        }else {
            signBeanList.addAll(list);
            signYesAdapter.notifyDataSetChanged();
            mPtrFrame.refreshComplete();
            mLoadStateManager.setState(LoadStateManager.LoadState.Success);
        }

    }

    @Override
    public void onFail() {
        mLoadStateManager.setState(LoadStateManager.LoadState.Failure);
    }


}
