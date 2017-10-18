package com.swsdkj.wsl.net.util;


import java.util.List;

/**
 * 网络返回基类 支持泛型
 * Created by Tamic on 2016-06-06.
 */
public class BaseResponse<T> {

    private int code;
    private String msg;
    private String sessionId;
    private T user;
    private List<T> list;
    private String srvTime;
    private int praiseft;
    private int menuid;
    private int listSize;

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

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

    public boolean isOk() {
        return code == 0;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public T getUser() {
        return user;
    }

    public void setUser(T user) {
        this.user = user;
    }

    public String getSrvTime() {
        return srvTime;
    }

    public void setSrvTime(String srvTime) {
        this.srvTime = srvTime;
    }

    public int getPraiseft() {
        return praiseft;
    }

    public void setPraiseft(int praiseft) {
        this.praiseft = praiseft;
    }

    public int getMenuid() {
        return menuid;
    }

    public void setMenuid(int menuid) {
        this.menuid = menuid;
    }

    public int getListSize() {
        return listSize;
    }

    public void setListSize(int listSize) {
        this.listSize = listSize;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", user=" + user +
                ", list=" + list +
                ", srvTime='" + srvTime + '\'' +
                ", praiseft=" + praiseft +
                ", menuid=" + menuid +
                ", listSize=" + listSize +
                ", obj=" + obj +
                '}';
    }
}
