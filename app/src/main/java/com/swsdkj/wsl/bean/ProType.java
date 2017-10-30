package com.swsdkj.wsl.bean;

/**
 * Created by Administrator on 2016/9/3.
 * 功能：商品分类
 */
public class ProType {
    private int id;
    private String typename;  //商品名称
    private String typeiconurl;   //商品icon路径


    public ProType(int id, String typename, String typeiconurl)
    {
        super();
        this.id=id;
        this.typename=typename;
        this.typeiconurl=typeiconurl;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTypename() {
        return typename;
    }
    public void setTypename(String typename) {
        this.typename = typename;
    }
    public String getTypeiconurl() {
        return typeiconurl;
    }
    public void setTypeiconurl(String typeiconurl) {
        this.typeiconurl = typeiconurl;
    }


}
