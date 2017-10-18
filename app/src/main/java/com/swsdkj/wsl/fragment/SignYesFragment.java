package com.swsdkj.wsl.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.swsdkj.wsl.R;
import com.swsdkj.wsl.adapter.SignAdapter;
import com.swsdkj.wsl.adapter.SignYesAdapter;
import com.swsdkj.wsl.bean.SignBean;
import com.swsdkj.wsl.contract.SignYesContract;
import com.swsdkj.wsl.net.BaseUrl;
import com.swsdkj.wsl.presenter.SignYesPresenterImpl;
import com.swsdkj.wsl.tool.CommonUtil;
import com.swsdkj.wsl.tool.EndLessOnScrollListener;
import com.swsdkj.wsl.tool.MyDecoration;
import com.swsdkj.wsl.view.AutoSwipeRefreshLayout;

import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/25 0025.
 */

public class SignYesFragment extends Fragment implements SignYesContract.View{
    private SignYesPresenterImpl yesPresenter;
    private AutoSwipeRefreshLayout layout_swipe_refresh;
    private RecyclerView id_yes_sign;
    private int page = 1,size=20;
    private SignAdapter signYesAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private View layoutView;
    private List<SignBean> signBeanList;
    private int year,month,day;
    int lastVisibleItemPosition;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_signyes, null);

        layout_swipe_refresh = (AutoSwipeRefreshLayout)layoutView.findViewById(R.id.layout_swipe_refresh);
        id_yes_sign = (RecyclerView)layoutView.findViewById(R.id.id_yes_sign);
        yesPresenter = new SignYesPresenterImpl(this);

        CommonUtil.setRecycler(layout_swipe_refresh,id_yes_sign);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        id_yes_sign.setLayoutManager(mLinearLayoutManager);
        //添加分隔线
        id_yes_sign.addItemDecoration(new MyDecoration(getActivity(), MyDecoration.VERTICAL_LIST));
        id_yes_sign.setAdapter(signYesAdapter =
                new SignAdapter(getActivity(),signBeanList = new ArrayList<>()));
        layout_swipe_refresh.autoRefresh();

        initView();
        setOnClick();
        return layoutView;
    }
   public void setOnClick(){
       //下拉刷新
       layout_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           //下拉刷新是触发该回调函数。
           @Override
           public void onRefresh() {
               //添加刷新内容
               page=1;
               signBeanList.clear();
               initData(page,size);
           }
       });
       //上拉加载
       id_yes_sign.addOnScrollListener(new RecyclerView.OnScrollListener() {
           @Override
           public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
               super.onScrollStateChanged(recyclerView, newState);
               if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition + 1 == signYesAdapter.getItemCount()) {
                   boolean isRefreshing = layout_swipe_refresh.isRefreshing();
                   if (isRefreshing) {
                       return;
                   }
                   //加载更多数据
                   page++;
                   initData(page,size);
               }
           }
           @Override
           public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
               super.onScrolled(recyclerView, dx, dy);
               lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
           }
       });
   }
    private void initView() {
        year = CommonUtil.getYear();
        month = CommonUtil.getMouth();
        day = CommonUtil.getDay();
    }


    //初始化一开始加载的数据
    private void initData(int page,int size){
        RequestParams params=new RequestParams(BaseUrl.selsignby);
        params.addBodyParameter("month",month+"");
        params.addBodyParameter("day",day+"");
        params.addBodyParameter("year",year+"");
        params.addBodyParameter("category","");
        params.addBodyParameter("sx","");
        params.addBodyParameter("page",page+"");
        params.addBodyParameter("size",size+"");
        params.addBodyParameter("ft","1");
        yesPresenter.selsignby(getActivity(),params);

    }

    public void selectBytime(int page,int size,int year,int Mouth,int dau){
        page=1;
        signBeanList.clear();
        this.year = year;
        this.month = Mouth;
        this.day = dau;
        RequestParams params=new RequestParams(BaseUrl.selsignby);
        params.addBodyParameter("month",Mouth+"");
        params.addBodyParameter("day",dau+"");
        params.addBodyParameter("year",year+"");
        params.addBodyParameter("category","");
        params.addBodyParameter("sx","");
        params.addBodyParameter("page",page+"");
        params.addBodyParameter("size",size+"");
        params.addBodyParameter("ft","1");
        yesPresenter.selsignby(getActivity(),params);
    }

    @Override
    public void onSuccess(List<SignBean> list) {
        signBeanList.addAll(list);
        signYesAdapter.notifyDataSetChanged();

        layout_swipe_refresh.setRefreshing(false);

    }

    @Override
    public void onFail() {

    }

}
