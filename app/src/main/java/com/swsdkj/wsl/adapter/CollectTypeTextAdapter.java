package com.swsdkj.wsl.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.bean.CollectTypeBean;

import java.util.List;

/**
 * 内容：
 * Created by 关云秀 on 2017\8\9 0009.
 */

public class CollectTypeTextAdapter extends BaseQuickAdapter<CollectTypeBean> {
    public CollectTypeTextAdapter(List<CollectTypeBean> list) {
        super(R.layout.activity_collect_info_tv,list);
    }
    @Override
    protected void convert(final BaseViewHolder helper, CollectTypeBean collectTypeBean) {
    }
}
