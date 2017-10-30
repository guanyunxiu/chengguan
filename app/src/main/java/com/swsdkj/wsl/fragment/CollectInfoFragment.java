package com.swsdkj.wsl.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.tablayout.SlidingTabLayout;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.base.BaseFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 内容：
 * Created by 关云秀 on 2017\10\19 0019.
 */

public class CollectInfoFragment extends BaseFragment {
    @BindView(R.id.tablayout)
    SlidingTabLayout tablayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private ArrayList<Fragment> mFagments = new ArrayList<>();
    private String[] mTitles = {"公共设施", "道路交通","市容环境","园林绿化","房屋土地","其他设施"};
    MyPagerAdapter adapter;
    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_collect_info;
    }

    @Override
    protected void initViews() {
       initTitle(false,"采集信息");
        for(int i=0;i<mTitles.length;i++){
            mFagments.add(CollectTypeFragment.newInstance(i,0));
        }
        adapter = new MyPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        tablayout.setViewPager(viewPager, mTitles);
    }

    @Override
    protected void updateViews() {

    }
    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFagments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFagments.get(position);
        }
    }
}
