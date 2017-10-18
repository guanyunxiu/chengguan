package com.swsdkj.wsl.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.swsdkj.wsl.R;
import com.swsdkj.wsl.base.BaseActivity;
import com.swsdkj.wsl.bean.User;
import com.swsdkj.wsl.contract.RegisterContract;
import com.swsdkj.wsl.net.BaseUrl;
import com.swsdkj.wsl.presenter.RegisterPreseterImpl;
import com.swsdkj.wsl.tool.CommonUtil;
import com.swsdkj.wsl.tool.RongGenerate;
import com.swsdkj.wsl.view.MyDialog;

import org.xutils.http.RequestParams;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/5/9 0009.
 */

public class RegisterActivity extends BaseActivity implements RegisterContract.View{
    private RegisterPreseterImpl preseter;
    private Context context;
    @BindView(R.id.id_reg_login_tv)
    TextView regLoginTV;
    @BindView(R.id.id_nameET)
    EditText nameET;
    @BindView(R.id.id_phoneET)
    EditText phoneET;
    @BindView(R.id.id_password)
    EditText passwordET;
    @BindView(R.id.id_idcardET)
    EditText idCardEt;
    @BindView(R.id.id_regBut)
    Button regSub;
    MyDialog dialog;
    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_reg);
        context = this;
        preseter = new RegisterPreseterImpl(this);
    }

    @Override
    protected void init() {

    }


    @OnClick({R.id.id_reg_login_tv,R.id.id_regBut})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_reg_login_tv:
                gotoReg();
                break;
            case R.id.id_phoneET:

                break;
            case R.id.id_password:

                break;
            case R.id.id_regBut:
                getSubmit();
                break;

        }
    }
    private void gotoReg(){
        Intent intent = new Intent(context,LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void getSubmit(){
        String nameStr  =nameET.getText().toString();
        if (TextUtils.isEmpty(nameStr)){
            CommonUtil.showToast(context,"请输入名字");
            return;
        }
        String phoneStr  =phoneET.getText().toString();
        if (TextUtils.isEmpty(phoneStr)){
            CommonUtil.showToast(context,"请输入手机号");
            return;
        }
        if (!CommonUtil.isMobile(phoneStr)){
            CommonUtil.showToast(context,"请输入正确的手机号");
            return;
        }
        String idcard = idCardEt.getText().toString();
        if(TextUtils.isEmpty(idcard)){
            CommonUtil.showToast(context,"请输入正确的身份证号");
            return;
        }
        String passwordStr  =passwordET.getText().toString();
    /*    if (!CommonUtil.isPassword(passwordStr)){
            CommonUtil.showToast(context,"请输入英文数字密码");
            return;
        }*/
        if(TextUtils.isEmpty(passwordStr)){
            CommonUtil.showToast(context,"请输入密码");
            return;
        }

        String photo = RongGenerate.generateDefaultAvatar(nameStr,CommonUtil.getRandom()+"");
        Log.i("photo",photo+"**********");
        Map<String,String> map = new HashMap<>();
        map.put("name",nameStr);
        map.put("phone",phoneStr);
        map.put("password",passwordStr);
        map.put("idcard",idcard);

        dialog = MyDialog.showDialog(context);
        dialog.show();
        preseter.register(context,phoneStr,nameStr,passwordStr,idcard,photo);
    }

    @Override
    public void onRegisterSuccess(User user) {
        dialog.dismiss();
        CommonUtil.showToast(this,"注册成功");
        Intent intent = new Intent(context,MainActivity.class);
        startActivity(intent);
        finish();

        LoginActivity.loginActivity.finish();
    }

    @Override
    public void onRegisterFail(int code) {
        if(code == 7){
            CommonUtil.showToast(this,"用户已存在");
        }else if(code == 10){
            CommonUtil.showToast(this,"身份证重复");
        }else {
            CommonUtil.showToast(this, "注册失败");
        }
        dialog.dismiss();
    }
}
