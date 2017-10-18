package com.swsdkj.wsl.activity;

import com.swsdkj.wsl.bean.ResponseEntity;

import org.xutils.http.app.ResponseParser;
import org.xutils.http.request.UriRequest;

import java.lang.reflect.Type;

/**
 * 作者：武帅龙 on 2016/9/29 10:45
 * 邮箱：15513025865@163.com
 * 电话：15513025865
 * 描述：
 */
public class ResultParser implements ResponseParser {
    @Override
    public void checkResponse(UriRequest request) throws Throwable {

    }

    @Override
    public Object parse(Type resultType, Class<?> resultClass, String result) throws Throwable {

        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setResult(result);
        //返回ResponseEntity对象
        return responseEntity;
    }
}
