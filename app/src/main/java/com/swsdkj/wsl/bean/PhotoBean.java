package com.swsdkj.wsl.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * 内容：
 * Created by 关云秀 on 2017\10\19 0019.
 */

public class PhotoBean extends MultiItemEntity {
    public static final int PHOTO = 0; //照片
    public static final int VIDEO = 1; //视频
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
