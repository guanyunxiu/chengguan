package com.swsdkj.wsl.net;

/**
 * Created by Administrator on 2017/5/3 0003.
 */

public class BaseUrl {
    public final static String UMAGE_URL="http://www.saiwensida.com:8080";
    //public final static String UMAGE_URL="http://192.168.2.111:8080";
    public final static String URL="http://www.saiwensida.com:8080/CWA/";
    //public final static String URL="http://192.168.2.111:8080/CWA/";
    public final static String selectAllUser="selectAllUser.action";

    public final static String register ="register.action";
    public final static String register2 =URL+"register.action";

    public final static String register1=URL+"register.action";
    //获取短信验证码
    public final static String sendCode="smsCheck1.action";
    //登录或者注册
    public final static String goto1 = "login.action";
    //现场寻查
    public final static String addsign = URL+"addsign.action";
    //签到
    public final static String addsign2 = "addsigns.action";
    //条件查看打卡记录
    public final static String selsignby = URL+"selsignby.action";
    //查看某月全部签到记录
    public final static String selmonthsign = URL+"selmonthsign.action";
    //
    public final static String selsxft = URL+"selsxft.action";
  //查看用户是否存在
    public final static String userft = "userft.action";



}
