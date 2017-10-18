package com.swsdkj.wsl.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.swsdkj.wsl.R;
import com.swsdkj.wsl.base.BaseActivity;
import com.swsdkj.wsl.base.CheckPermissionsActivity;
import com.swsdkj.wsl.bean.User;
import com.swsdkj.wsl.contract.LoginContract;
import com.swsdkj.wsl.presenter.LoginPresenterImpl;
import com.swsdkj.wsl.tool.CommonUtil;
import com.swsdkj.wsl.tool.ConnectRong;
import com.swsdkj.wsl.view.MyDialog;

import org.xutils.http.RequestParams;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/5/9 0009.
 */

public class LoginActivity extends CheckPermissionsActivity implements LoginContract.View{
    private LoginPresenterImpl loginPresenter;
    private Context context;
    @BindView(R.id.id_login_reg_tv)
    TextView loginRegTV;
    @BindView(R.id.id_phoneET)
    EditText phoneET;
    @BindView(R.id.id_passwordET)
    EditText passwordET;
    @BindView(R.id.button)
    Button subBut;
    MyDialog dialog;
    public static LoginActivity loginActivity;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_login);
        context=this;
        loginActivity = this;
        loginPresenter = new LoginPresenterImpl(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }


    @OnClick({R.id.id_login_reg_tv,R.id.button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_login_reg_tv:
                gotoReg();
                break;
            case R.id.button:
                goSubmit();
                break;

        }
    }
    private void gotoReg(){
        Intent intent = new Intent(context,RegisterActivity.class);
        startActivity(intent);
    }
    //信息登录
    private void goSubmit(){
        String phoneStr = phoneET.getText().toString();
        if (TextUtils.isEmpty(phoneStr)){
            CommonUtil.showToast(context,"请输入手机号");
            return;
        }
        if (!CommonUtil.isMobile(phoneStr)){
            CommonUtil.showToast(context,"请输入正确的手机号");
            return;
        }

        String passwordStr  =passwordET.getText().toString();
       /* if (!CommonUtil.isPassword(passwordStr)){
            CommonUtil.showToast(context,"请输入英文数字密码");
            return;
        }*/
        if(TextUtils.isEmpty(passwordStr)){
            CommonUtil.showToast(context,"请输入密码");
            return;
        }
        Map<String,String> map = new HashMap<>();
        map.put("phone",phoneStr);
        map.put("password",passwordStr);

        dialog = MyDialog.showDialog(context);
        dialog.show();
        loginPresenter.login(context,map);
    }



    @Override
    public void onLogoSuccess(User user) {
        dialog.dismiss();
        ConnectRong.setConnect(this);
        Intent intent = new Intent(context,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLogoFail() {
        dialog.dismiss();
    }

    @Override
    public void onSendCodeSuccess() {

    }

    @Override
    public void onSendCodeFail() {

    }
}
