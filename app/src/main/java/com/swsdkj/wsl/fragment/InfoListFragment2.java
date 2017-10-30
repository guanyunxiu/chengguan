package com.swsdkj.wsl.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.maning.mndialoglibrary.MProgressDialog;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.activity.CollectSelectInfoActivity;
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
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 内容：
 * Created by 关云秀 on 2017\10\19 0019.
 */

public class InfoListFragment2 extends BaseFragment implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

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
    CollectItemAdapter collectItemAdapter;
    MProgressDialog mMProgressDialog;
    private List<ProType> typeList;
    TypeAdapter adapter;
    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_infolist2;
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消注册
    }

}
