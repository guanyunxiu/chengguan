package com.swsdkj.wsl.bean;

import com.swsdkj.wsl.activity.ResultParser;

import org.xutils.http.annotation.HttpResponse;

/**
 * 作者：武帅龙 on 2016/9/29 10:45
 * 邮箱：15513025865@163.com
 * 电话：15513025865
 * 描述：
 */
@HttpResponse(parser = ResultParser.class)
public class ResponseEntity {
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
