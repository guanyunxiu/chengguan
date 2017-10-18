package com.swsdkj.wsl.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swsdkj.wsl.R;
import com.swsdkj.wsl.adapter.StaffAdapter;
import com.swsdkj.wsl.bean.User;
import com.swsdkj.wsl.contract.SignNoContract;
import com.swsdkj.wsl.net.BaseUrl;
import com.swsdkj.wsl.presenter.SignNoPresenterImpl;
import com.swsdkj.wsl.tool.CommonUtil;
import com.swsdkj.wsl.tool.EndLessOnScrollListener;

import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/25 0025.
 */

public class SignNoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,SignNoContract.View{
    private View layoutView;
    private SwipeRefreshLayout layout_swipe_refresh;
    private RecyclerView no_sign;
    private GridLayoutManager gridLayoutManager;
    private List<User> staffBeanList;
    private StaffAdapter staffAdapter;
    private int page = 1,size=30;
    private SignNoPresenterImpl signNoPresenter;
    private List<User> userList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_signno, null);
        signNoPresenter = new SignNoPresenterImpl(this);
        initView();

        no_sign.addOnScrollListener(new EndLessOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                loadMoreData();
            }
        });

        initData(page,size);

        no_sign.setAdapter(staffAdapter =
                new StaffAdapter(getActivity(),userList =new ArrayList<User>()));

        return layoutView;
    }

    private void initView() {
        layout_swipe_refresh = (SwipeRefreshLayout)layoutView.findViewById(R.id.layout_swipe_refresh);
        no_sign = (RecyclerView)layoutView.findViewById(R.id.id_no_sign);

        layout_swipe_refresh.setEnabled(false);
        gridLayoutManager = new GridLayoutManager(getActivity(),5);
        no_sign.setLayoutManager(gridLayoutManager);

        layout_swipe_refresh.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        page=1;
        userList.clear();
        initData(page,size);
    }
    private void loadMoreData(){

    }

    //初始化一开始加载的数据
    private void initData(int page,int size){
        RequestParams params = new RequestParams(BaseUrl.selsxft);
        params.addBodyParameter("page",page+"");
        params.addBodyParameter("size",size+"");
        params.addBodyParameter("month", CommonUtil.getMouth()+"");
        params.addBodyParameter("day",CommonUtil.getDay()+"");
        params.addBodyParameter("year",CommonUtil.getYear()+"");
        params.addBodyParameter("sx","0");
        params.addBodyParameter("ft","1");

        signNoPresenter.getUsers(getActivity(),params);

    }


    @Override
    public void onLogoSuccess(List<User> users) {
        userList.addAll(users);
        staffAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLogoFail() {

    }
}
