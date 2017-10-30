package com.swsdkj.wsl.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hhl.library.FlowTagLayout;
import com.hhl.library.OnTagClickListener;
import com.hhl.library.OnTagSelectListener;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.activity.CollectInfoActivity;
import com.swsdkj.wsl.adapter.CollectTypeAdapter;
import com.swsdkj.wsl.adapter.TagAdapter;
import com.swsdkj.wsl.base.BaseFragment;
import com.swsdkj.wsl.bean.CollectTypeBean;
import com.swsdkj.wsl.tool.CommonUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 内容：
 * Created by 关云秀 on 2017\10\19 0019.
 */

public class CollectTypeFragment extends BaseFragment {
    @BindView(R.id.size_flow_layout)
    FlowTagLayout flowTagLayout;
    List<CollectTypeBean> list;
    private TagAdapter mSizeTagAdapter;
    private int type,typeFlag;//typeFlag为0时是添加数据，为1时是查询数据
    public static CollectTypeFragment newInstance(int type,int typeFlag) {
        CollectTypeFragment fragment = new CollectTypeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type",type);
        bundle.putInt("typeFlag",typeFlag);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_collect_type;
    }

    @Override
    protected void initViews() {
        type = getArguments().getInt("type");
        typeFlag = getArguments().getInt("typeFlag");
        mSizeTagAdapter = new TagAdapter(mContext);
        flowTagLayout.setAdapter(mSizeTagAdapter);
        mSizeTagAdapter.onlyAddAll(getList());
        flowTagLayout.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onItemClick(FlowTagLayout parent, View view, int position) {
                mSizeTagAdapter.selectTypeId = list.get(position).getId();
                mSizeTagAdapter.notifyDataSetChanged();
                if(typeFlag == 0) {
                    startActivity(new Intent(mContext, CollectInfoActivity.class));
                }else if(typeFlag == 1){
                    EventBus.getDefault().post("信息查询："+position);
                }
            }
        });
    }

    @Override
    protected void updateViews() {

    }
    public List<CollectTypeBean> getList(){
        list = new ArrayList<>();
        for(int i=0;i<46;i++){
            CollectTypeBean collectTypeBean = new CollectTypeBean();
            collectTypeBean.setId(i);
            collectTypeBean.setName("上水井盖");
            list.add(collectTypeBean);
        }

        CollectTypeBean collectTypeBean2 = new CollectTypeBean();
        collectTypeBean2.setId(20);
        collectTypeBean2.setName("自动售货机");
        list.add(collectTypeBean2);
        return list;
    }

}
