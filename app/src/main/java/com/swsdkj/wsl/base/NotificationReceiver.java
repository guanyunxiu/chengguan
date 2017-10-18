package com.swsdkj.wsl.base;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.swsdkj.wsl.activity.MainActivity;
import com.swsdkj.wsl.activity.WelComeActivity;
import com.swsdkj.wsl.tool.CommonUtil;

import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

/**
 * 作者： 关云秀 on 2017/5/19.
 * 描述：
 */
public class NotificationReceiver extends PushMessageReceiver {
    @Override
    public boolean onNotificationMessageArrived(Context context, PushNotificationMessage pushNotificationMessage) {
        return false;
    }

    @Override
    public boolean onNotificationMessageClicked(Context context, PushNotificationMessage pushNotificationMessage) {
       /* Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri.Builder builder = Uri.parse("rong://" + context.getPackageName()).buildUpon();
        builder.appendPath("conversationlist");
        Uri uri = builder.build();
        intent.setData(uri);
        context.startActivity(intent);*/
        Intent intent = new Intent(context, WelComeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      //  intent.putExtra("pos",2);
        context.startActivity(intent);

        return true;
    }
}
