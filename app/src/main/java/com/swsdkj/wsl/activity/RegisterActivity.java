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

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.user_et)
    EditText userEt;
    @BindView(R.id.pass_et)
    EditText passEt;
    @BindView(R.id.confirm_pass_et)
    EditText confirmPassEt;
    @BindView(R.id.reg_btn)
    Button regBtn;
    @BindView(R.id.register_tv)
    TextView registerTv;

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_reg;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void updateViews() {

    }

    @OnClick({R.id.reg_btn, R.id.register_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reg_btn:
                break;
            case R.id.register_tv:
                startActivity(new Intent(context,LoginActivity.class));
                finish();
                break;
        }
    }
}
