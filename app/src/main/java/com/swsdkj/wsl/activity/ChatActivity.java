package com.swsdkj.wsl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.swsdkj.wsl.R;
import com.swsdkj.wsl.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imkit.RongIM;

/**
 * 作者： 关云秀 on 2017/5/19.
 * 描述：
 */
public class ChatActivity extends BaseActivity {
    @BindView(R.id.id_chat_name)
    TextView idChatName;
    @BindView(R.id.id_left_img)
    ImageView idLeftImg;

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initViews() {
        idChatName.setText("安全管理");
        RongIM.getInstance().enableNewComingMessageIcon(true);//显示新消息提醒
        idLeftImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void updateViews() {

    }
}
