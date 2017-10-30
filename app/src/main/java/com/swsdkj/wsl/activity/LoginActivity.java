package com.swsdkj.wsl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.swsdkj.wsl.R;
import com.swsdkj.wsl.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/5/9 0009.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.user_et)
    EditText userEt;
    @BindView(R.id.pass_et)
    EditText passEt;
    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.register_tv)
    TextView registerTv;

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void updateViews() {

    }

    @OnClick({R.id.login_btn, R.id.register_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                startActivity(new Intent(context,MainActivity.class));
                break;
            case R.id.register_tv:
                startActivity(new Intent(context,RegisterActivity.class));
                finish();
                break;
        }
    }
}
