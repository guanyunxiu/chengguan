package com.swsdkj.wsl.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.adapter.CollectItemAdapter;
import com.swsdkj.wsl.base.BaseActivity;
import com.swsdkj.wsl.bean.CollectTypeBean;
import com.swsdkj.wsl.view.LoadStateManager;
import com.swsdkj.wsl.view.MultiStateView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 内容：
 * Created by 关云秀 on 2017\10\28 0028.
 */

public class CollectListSelectActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener{
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.multiStateView)
    MultiStateView multiStateView;
    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    CollectItemAdapter collectItemAdapter;
    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_collect_list;
    }

    @Override
    protected void initViews() {
        initTitle(true,"信息列表");
        initContent();
    }

    @Override
    protected void updateViews() {
        mLoadStateManager.setState(LoadStateManager.LoadState.Success);
    }
    /**
     * 初始化中间内容
     */
    public void initContent(){
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                startActivity(new Intent(CollectListSelectActivity.this, CollectSelectInfoActivity.class));
            }
        });
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
    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoadMoreRequested() {

    }
}
