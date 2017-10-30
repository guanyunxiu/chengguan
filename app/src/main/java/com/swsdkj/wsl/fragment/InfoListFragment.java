package com.swsdkj.wsl.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.maning.mndialoglibrary.MProgressDialog;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.activity.CollectSelectInfoActivity;
import com.swsdkj.wsl.adapter.CollectItemAdapter;
import com.swsdkj.wsl.base.BaseFragment;
import com.swsdkj.wsl.bean.CollectTypeBean;
import com.swsdkj.wsl.tool.CommonUtil;
import com.swsdkj.wsl.view.LoadStateManager;
import com.swsdkj.wsl.view.MultiStateView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 内容：
 * Created by 关云秀 on 2017\10\19 0019.
 */

public class InfoListFragment extends BaseFragment implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.search_et)
    EditText searchEt;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.multiStateView)
    MultiStateView multiStateView;
    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    CollectItemAdapter collectItemAdapter;
    MProgressDialog mMProgressDialog;
    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_infolist;
    }

    @Override
    protected void initViews() {
        initTitle(false, "信息列表");
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
    public List<CollectTypeBean> getList(){
        List<CollectTypeBean> collectTypeBeanList = new ArrayList<>();
        for(int i=0;i<10;i++){
            CollectTypeBean collectTypeBean = new CollectTypeBean();
            collectTypeBean.setName("下水井盖");
            collectTypeBeanList.add(collectTypeBean);
        }
        return  collectTypeBeanList;
    }
    @Override
    protected void updateViews() {
        mLoadStateManager.setState(LoadStateManager.LoadState.Success);
        searchClick();
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
    public void searchClick(){
        searchEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                // 修改回车键功能
                if (keyCode == KeyEvent.KEYCODE_ENTER  && event.getAction() == KeyEvent.ACTION_DOWN ) {
                    // 先隐藏键盘
                    CommonUtil.hideSoftInput(mContext,searchEt);
                    mMProgressDialog = CommonUtil.configDialogDefault(mContext);
                    mMProgressDialog.show("搜索中...");
                }
                return false;
            }
        });
    }
}
