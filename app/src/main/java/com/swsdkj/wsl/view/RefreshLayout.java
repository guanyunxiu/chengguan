package com.swsdkj.wsl.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.swsdkj.wsl.R;
import com.swsdkj.wsl.tool.DisplayUtil;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;


/**
 * Created by _hxb on 2016/11/1.
 */

public class RefreshLayout extends PtrFrameLayout {
    public RefreshLayout(Context context) {
        super(context);
        initView();
    }


    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        disableWhenHorizontalMove(true);
        final MaterialHeader header = new MaterialHeader(getContext());
        int[] colors = {R.color.yellow,R.color.black,R.color.actionsheet_blue,R.color.alertdialog_line};
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, DisplayUtil.dip2px(15), 0, DisplayUtil.dip2px(10));
        setLoadingMinTime(1000);
        setDurationToCloseHeader(1500);
        setHeaderView(header);
        View view = View.inflate(getContext(), R.layout.item_foot, this);
        setFooterView(view);
        addPtrUIHandler(header);
    }
}
