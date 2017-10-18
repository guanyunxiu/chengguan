package com.swsdkj.wsl.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.swsdkj.wsl.R;
import com.swsdkj.wsl.base.BaseActivity;
import com.swsdkj.wsl.bean.User;
import com.swsdkj.wsl.contract.LoginContract;
import com.swsdkj.wsl.presenter.LoginPresenterImpl;
import com.swsdkj.wsl.tool.CommonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/4/28 0028.
 */

public class LoginActivity_back extends BaseActivity implements LoginContract.View {
    private Context context;
    private List<String> stringList = new ArrayList<>();
    private ArrayAdapter arrayAdapter;
    private String engineeringStr;

    @BindView(R.id.id_spinner2)
    Spinner spinner;
    @BindView(R.id.id_photoNumber)
    EditText photoNumber;
    @BindView(R.id.id_code)
    EditText codeNumber;
    @BindView(R.id.id_name)
    EditText nameET;
    @BindView(R.id.id_but_sendCode)
    TextView sendCodeTV;
    @BindView(R.id.id_but_submit)
    Button submitBut;

    private LoginPresenterImpl loginPresenter;
    @Override
    protected void setContentView() {

        setContentView(R.layout.activity_longin);
        context = this;
        loginPresenter = new LoginPresenterImpl(this);


    }

    @Override
    protected void init() {

        initData();
    }

    private void initData() {
        stringList.add("施工队一");
        stringList.add("施工队二");
        stringList.add("施工队三");
        stringList.add("施工队四");

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, stringList);
        //设置样式
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String engineeringStr1 = stringList.get(position);
                engineeringStr=engineeringStr1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick({R.id.id_but_sendCode,R.id.id_but_submit,})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_but_sendCode:
                setSendCode();
                break;
            case R.id.id_but_submit:
                setSubmitBut();
                break;
        }
    }

    public void setSendCode(){
        String phoneStr = photoNumber.getText().toString();
        if (phoneStr==null||phoneStr.equals("")){
            CommonUtil.showToast(context,"请输入手机号");
            return;
        }
        String nameStr = "";
        if (nameET.getText().toString()==null){
            nameStr = "";
        }else {
            nameStr = nameET.getText().toString();
        }
        loginPresenter.sendCode(context,phoneStr,nameStr);
    }
    public void setSubmitBut(){
        String codeNumberStr = codeNumber.getText().toString();
        if (TextUtils.isEmpty(codeNumberStr)){
            return;
        }

        Map<String,String> map = new HashMap<>();
        map.put("smsCode",codeNumberStr);
        map.put("category",engineeringStr);
        loginPresenter.login(context,map);
    }
    @Override
    public void onLogoSuccess(User user) {

        Intent intent = new Intent(context,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLogoFail() {

    }

    @Override
    public void onSendCodeSuccess() {
        setCountdown(sendCodeTV);
    }

    @Override
    public void onSendCodeFail() {
        CommonUtil.showToast(context,"验证码获取失败");
    }
}
