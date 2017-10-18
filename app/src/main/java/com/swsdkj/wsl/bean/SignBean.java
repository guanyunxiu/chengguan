package com.swsdkj.wsl.bean;

import android.content.Intent;

/**
 * Created by Administrator on 2017/4/25 0025.
 */

public class SignBean {
    private String id;
    private Integer userid;
    private String time;
    private String address;
    private String photo1;
    private String photo2;
    private String face1;
    private String face2;
    private String context;
    private String year;
    private String month;
    private String day;
    private Integer category;
    private User user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoto1() {
        return photo1;
    }

    public void setPhoto1(String photo1) {
        this.photo1 = photo1;
    }

    public String getPhoto2() {
        return photo2;
    }

    public void setPhoto2(String photo2) {
        this.photo2 = photo2;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFace1() {
        return face1;
    }

    public void setFace1(String face1) {
        this.face1 = face1;
    }

    public String getFace2() {
        return face2;
    }

    public void setFace2(String face2) {
        this.face2 = face2;
    }

    @Override
    public String toString() {
        return "SignBean{" +
                "id='" + id + '\'' +
                ", userid=" + userid +
                ", time='" + time + '\'' +
                ", address='" + address + '\'' +
                ", photo1='" + photo1 + '\'' +
                ", photo2='" + photo2 + '\'' +
                ", context='" + context + '\'' +
                ", year='" + year + '\'' +
                ", month='" + month + '\'' +
                ", day='" + day + '\'' +
                ", category=" + category +
                ", user=" + user +
                '}';
    }
}
