package com.swsdkj.wsl.view;

import com.swsdkj.wsl.base.BaseStateManager;

/**
 * Created by Administrator on 2016/11/3.
 */

public class LoadStateManager extends BaseStateManager<LoadStateManager.LoadState> {
    public static enum LoadState{
        Init,
        Success,
        Failure,
        NoData
        ;
    }
}
