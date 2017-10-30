package com.swsdkj.wsl.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * 内容：
 * Created by 关云秀 on 2017\10\19 0019.
 */

public class InfoBean extends MultiItemEntity {
    public static final int ET = 0; //editText框
    public static final int XL = 1; //下拉选择框
    public static final int TV= 2; //textview框

    private int id;
    private String name;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
