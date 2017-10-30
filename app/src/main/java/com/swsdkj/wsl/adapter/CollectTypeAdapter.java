package com.swsdkj.wsl.adapter;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.bean.CollectTypeBean;

import java.util.List;

/**
 * 内容：
 * Created by 关云秀 on 2017\8\9 0009.
 */

public class CollectTypeAdapter extends BaseQuickAdapter<CollectTypeBean> {
    public CollectTypeAdapter(List<CollectTypeBean> list) {
        super(R.layout.activity_collect_type_item,list);
    }
    @Override
    protected void convert(final BaseViewHolder helper, CollectTypeBean collectTypeBean) {
        helper.setText(R.id.type_tv,collectTypeBean.getName());
    }
}
