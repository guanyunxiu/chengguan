package com.swsdkj.wsl.tool;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.swsdkj.wsl.base.BaseApplication;
import com.swsdkj.wsl.bean.SealExtensionModule;
import com.swsdkj.wsl.config.SharedConstants;

import java.util.List;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Administrator on 2016/5/17.
 */
public class ConnectRong {
    /**
     *连接融云服务器
     *
     */
    public static void setConnect(final Context mcontext){
        // String tokens = "CUcaRphzDOdXFvL2tq0lPD0qhwjV4CVqWMLOK9qZLpLPnHyl+4NB2JvNaHGsDns9awaosagkNP5Xsm9fsqmJYQ==";
       // Log.i("result", token + "<<<<");

        String token = BaseApplication.mSharedPrefUtil.getString(SharedConstants.TOKEN,"");
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                Log.e("fail", "------onfail----" );
            }
            @Override
            public void onSuccess(String userId) {
                Log.e("success", "------onSuccess----" + userId);
               // RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, provider);
                setSerUserInfor(userId);
                //new SharedPrefUtil(mcontext).setPreferences_rongid(userId);
            }
            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.e("MainActivity", "------onError----" + errorCode);
            }
        });
    }
    /**
     * 设置用户头像
     * @param userId 用户ID
     */
    /**
     * 设置用户头像
     * @param userId 用户ID
     */
    public static void setSerUserInfor(String userId){
            Log.i("photo",BaseApplication.mSharedPrefUtil.getString(SharedConstants.PHOTO,"")+"****photo");
            RongIM.getInstance().setCurrentUserInfo(new UserInfo(userId, BaseApplication.mSharedPrefUtil.getString(SharedConstants.NAME,""), Uri.parse(BaseApplication.mSharedPrefUtil.getString(SharedConstants.PHOTO,""))));
            RongIM.getInstance().setMessageAttachedUserInfo(true);
         /* Group groupInfo = new Group("001","工作群", Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1493698139722&di=e4e4e8601579afa1d0427596ea3e581d&imgtype=0&src=http%3A%2F%2Fglwkh.com%2Fimages%2FHotel%2F20141131156451.jpg"));
            RongIM.getInstance().refreshGroupInfoCache(groupInfo);*/


    }

}
