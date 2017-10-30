package com.swsdkj.wsl.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.maning.mndialoglibrary.MProgressDialog;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.base.BaseApplication;
import com.swsdkj.wsl.bean.User;
import com.swsdkj.wsl.config.SharedConstants;
import com.swsdkj.wsl.net.BaseUrl;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * Created by Administrator on 2017/5/5 0005.
 */

public class CommonUtil {
    /**
     * 正则表达式：验证用户名
     */
    public static final String REGEX_USERNAME = "^[a-zA-Z]\\w{5,17}$";

    /**
     * 正则表达式：验证密码
     */
    public static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,16}$";

    /**
     * 正则表达式：验证手机号
     */
    public static final String REGEX_MOBILE = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";

    /**
     * 正则表达式：验证邮箱
     */
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 正则表达式：验证汉字
     */
    public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5],{0,}$";

    /**
     * 正则表达式：验证身份证
     */
    public static final String REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)";

    /**
     * 正则表达式：验证IP地址
     */
    public static final String REGEX_IP_ADDR = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";

    /**
     * 校验用户名
     *
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUsername(String username) {
        return Pattern.matches(REGEX_USERNAME, username);
    }

    /**
     * 校验密码
     *
     * @param password
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }

    /**
     * 校验手机号
     *
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }

    /**
     * 校验邮箱
     *
     * @param email
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }

    /**
     * 校验汉字
     *
     * @param chinese
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isChinese(String chinese) {
        return Pattern.matches(REGEX_CHINESE, chinese);
    }

    /**
     * 校验身份证
     *
     * @param idCard
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isIDCard(String idCard) {
        return Pattern.matches(REGEX_ID_CARD, idCard);
    }


    /**
     * 校验IP地址
     *
     * @param ipAddr
     * @return
     */
    public static boolean isIPAddr(String ipAddr) {
        return Pattern.matches(REGEX_IP_ADDR, ipAddr);
    }

    public static void showToast(Context context, String content){
        Toast.makeText(context,content,Toast.LENGTH_SHORT).show();
    }

    public static void saveUser(User user){
        BaseApplication.mSharedPrefUtil.putString(SharedConstants.PHONE,user.getPhone());  //手机号
        BaseApplication.mSharedPrefUtil.putString(SharedConstants.ID,user.getId()+"");  //ID
        BaseApplication.mSharedPrefUtil.putString(SharedConstants.NAME,user.getName());  //昵称
        BaseApplication.mSharedPrefUtil.putString(SharedConstants.TOKEN,user.getToken());  //token
        BaseApplication.mSharedPrefUtil.putString(SharedConstants.GROUPID,user.getGroupId());  //groupid
        BaseApplication.mSharedPrefUtil.putString(SharedConstants.GROUPNAME,user.getGroupName());  //groupname
        BaseApplication.mSharedPrefUtil.putString(SharedConstants.PHOTO,BaseUrl.UMAGE_URL+user.getPhoto());//头像
        BaseApplication.mSharedPrefUtil.putString(SharedConstants.DEPARTMENTID,user.getDepartmentid());
        BaseApplication.mSharedPrefUtil.putString(SharedConstants.COMPANYID,user.getCompanyid());
        BaseApplication.mSharedPrefUtil.putString(SharedConstants.CATEGORY,user.getCategory());
        BaseApplication.mSharedPrefUtil.putString(SharedConstants.SMSCODE,user.getSmsCode());
        BaseApplication. mSharedPrefUtil.commit();
    }


    public static File openCamera(Activity activity, int PHOTO_REQUEST_TAKEPHOTO) {
        String state = Environment.getExternalStorageState();
        File outFile = null;
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!outDir.exists()) {
                outDir.mkdirs();
            }
            outFile = new File(outDir, System.currentTimeMillis() + ".jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outFile));
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            activity.startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
        } else {
            Log.e("CAMERA", "请确认已经插入SD卡");
        }
        return outFile;
    }

    //保存图片到本地
    public static File saveImage(Bitmap bmp){
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    //获取当前年份
    public static int getYear(){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        return  year;
    }

    //获取当前月份
    public static int getMouth(){
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        return  month+1;
    }

    //获取当前日期

    public static int getDay(){
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        return  day;
    }
    //时
    public static int getHour(){
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        return  hour;
    }
    //分
    public static int getMinute (){
        Calendar c = Calendar.getInstance();
        int minute  = c.get(Calendar.MINUTE);
        Log.i("time","minute="+c.get(Calendar.MINUTE));
        return  minute ;
    }
    //秒
    public static int getSecond (){
        Calendar c = Calendar.getInstance();
        int second  = c.get(Calendar.SECOND);
        Log.i("time","second="+c.get(Calendar.SECOND));
        return  second ;
    }
    public static String getTime1(){
        long time=System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
        Date d1=new Date(time);
        String t1=format.format(d1);
        Log.e("msg", t1);
        return t1;
    }
    public static String getTime2(){
        long time=System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1=new Date(time);
        String t1=format.format(d1);
        Log.e("msg", t1);
        return t1;
    }
    public static String getTime3(){
        long time=System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日");
        Date d1=new Date(time);
        String t1=format.format(d1);
        Log.e("msg", t1);
        return t1;
    }
    /**
     * 把Bitmap转Byte
     */
    public static byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private static long lastClickTime;
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


    public static String getUUID(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
    public static void setRecycler(SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView){
        if(swipeRefreshLayout != null) {
            swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.YELLOW, Color.BLUE, Color.LTGRAY);
        }
        recyclerView.setHasFixedSize(true);
    }
    public static void setHeader(Context context, PtrClassicFrameLayout mPtrFrame){
        final MaterialHeader header = new MaterialHeader(context);
        int[] colors ={R.color.yellow,R.color.yellow,R.color.yellow,R.color.yellow};
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, DisplayUtil.dip2px(15), 0,DisplayUtil.dip2px(10));
        header.setPtrFrameLayout(mPtrFrame);
        mPtrFrame.setLoadingMinTime(1000);
        mPtrFrame.setDurationToCloseHeader(1500);
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);
    }
    public static int getRandom(){
        java.util.Random random=new java.util.Random();// 定义随机类
        int result=random.nextInt(100);// 返回[0,10)集合中的整数，注意不包括10
        return result;              // +1后，[0,10)集合变为[1,11)集合，满足要求
    }
    /**
     * 隐藏软键盘
     * @param context 上下文
     * @param view 控件
     */
    public static void hideSoftInput(Context context,View view){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }
    public static MProgressDialog configDialogDefault(Context context) {
        //新建一个Dialog
        MProgressDialog mMProgressDialog = new MProgressDialog(context);
        mMProgressDialog.setBackgroundWindowColor(R.color.b_t);
        mMProgressDialog.setCanceledOnTouchOutside(true);
        return mMProgressDialog;
    }
}
