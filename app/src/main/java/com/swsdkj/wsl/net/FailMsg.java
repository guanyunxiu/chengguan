package com.swsdkj.wsl.net;

import android.content.Context;
import com.swsdkj.wsl.tool.CommonUtil;

/**
 * 作者： 关云秀 on 2017/2/17.
 * 描述：
 */
public class FailMsg {
    private int code;
    public static final int ERR_INVALIDATE = 1; //无效数据
    public static final int ERR_NO_LOGIN = 2; //没有登录
    public static final int ERR_SMS_FAILED = 3; //sms验证码验证失败
    public static final int ERR_NO_PRE_CHECK_PHONE = 4;//错误的手机号
    public static final int ERR_SQL_FAILED = 5; //sql操作失败
    public static final int ERR_SIGN_EXIT=6;//已签到或签退
    public static final int ERR_EXIST=7;//用户已存在
    public static final int ERR_NOTEXIST=8;//用户不存在
    public static final int ERR_USRNAME_OR_PWD_WRONG=9;//用户名或密码错误
    public static final int ERR_INTERNAL = 1000; //内部错误（抛异常）
    public static boolean showMsg(Context context,int code) {
        switch (code){
            case ERR_INVALIDATE:
                CommonUtil.showToast(context, "无效数据");
                return false;
            case ERR_EXIST:
                CommonUtil.showToast(context,"(用户)已经存在");
                return false;
            case ERR_USRNAME_OR_PWD_WRONG:
                CommonUtil.showToast(context,"用户名或密码错误");
                return false;
            case ERR_NO_LOGIN:
                CommonUtil.showToast(context,"没有登录");
                return false;
            case ERR_SMS_FAILED:
                CommonUtil.showToast(context,"sms验证码验证失败");
                return false;
            case ERR_SQL_FAILED:
                CommonUtil.showToast(context,"sql操作失败");
                return false;

            case ERR_NO_PRE_CHECK_PHONE:
                CommonUtil.showToast(context,"错误的手机号");
                return false;

            case ERR_INTERNAL:
                CommonUtil.showToast(context,"内部错误（抛异常）用户相关");
                return false;
            case ERR_NOTEXIST:
                CommonUtil.showToast(context,"用户不存在");

        }
        return true;
    }
}
