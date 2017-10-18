package com.swsdkj.wsl.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/5/8 0008.
 */

public class ResultMsg<T> {
    private int code;
    private String msg;
    private List<T> list;
    private String srvTime;
    private T user;
    private T obj;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }



    public String getSrvTime() {
        return srvTime;
    }

    public void setSrvTime(String srvTime) {
        this.srvTime = srvTime;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public T getUser() {
        return user;
    }

    public void setUser(T user) {
        this.user = user;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}
