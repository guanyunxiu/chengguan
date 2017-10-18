package com.swsdkj.wsl.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.swsdkj.wsl.R;
import com.swsdkj.wsl.tool.CommonUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2017/4/24 0024.
 */

public class FootFragment extends Fragment {
    private View layoutView;
    private RadioButton signYes,signNo;

    private SignYesFragment2 signYesFragment2;
    private SignNoFragment signNoFragment;
    private List<String> stringList;
    private ArrayAdapter arrayAdapter;
    private Spinner spinner;
    private Button dateBut;
    private TextView timeTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_foot, null);
        initView();
        initFragment();
        setSelection(0);
        return layoutView;
    }

    private void initView() {
        dateBut = (Button)layoutView.findViewById(R.id.id_date);
        spinner = (Spinner)layoutView.findViewById(R.id.spinner2);
        timeTV = (TextView)layoutView.findViewById(R.id.id_time);
        timeTV.setText(CommonUtil.getYear()+"年"+
        CommonUtil.getMouth()+"月"+
        CommonUtil.getDay()+"日  "+
        CommonUtil.getHour()+":"+
        CommonUtil.getMinute());

        stringList = new ArrayList<>();
        stringList.add("施工队一");
        stringList.add("施工队二");
        stringList.add("施工队三");
        stringList.add("施工队四");

        arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, stringList);
        //设置样式
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arrayAdapter);

        timeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                choseDate();
            }
        });

        /*dateBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseDate();
            }
        });
*/

        signYes = (RadioButton)layoutView.findViewById(R.id.sign_yes);
        signYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelection(0);
            }
        });
        signNo = (RadioButton)layoutView.findViewById(R.id.sign_no);
        signNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelection(1);
            }
        });
    }

    private void initFragment() {

    }
    //日期选择器
    private void choseDate(){
        Log.i("choseDate",CommonUtil.getYear()+"*********"+CommonUtil.getMouth()+"************"+CommonUtil.getDay());
        DatePickerDialog datePicker=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                //Toast.makeText(getActivity(), year+"year "+(monthOfYear+1)+"month "+dayOfMonth+"day", Toast.LENGTH_SHORT).show();
                int month = monthOfYear+1;
                timeTV.setText(year+"年"+
                        (month)+"月"+
                        dayOfMonth+"日  "+
                        CommonUtil.getHour()+":"+
                        CommonUtil.getMinute());
                Log.i("choseDate",year+"#########"+month+"#########"+dayOfMonth);
                signYesFragment2.selectBytime(1,20,year,month,dayOfMonth);

            }
        }, CommonUtil.getYear(), CommonUtil.getMouth()-1, CommonUtil.getDay());
        datePicker.show();
    }





    //底部导航栏点击显示
    private int curSelection;
    public void setSelection(int index) {
        resetImg();
        FragmentTransaction  ft = getChildFragmentManager().beginTransaction();
        //设置刚进来为推荐页面
        int curSelection2 = curSelection;
        curSelection = index;
        switch (index) {
            //首页
            case 0:
                signYes.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_xuncha_on, 0, 0);
                if(signYesFragment2 == null){
                    signYesFragment2 = new SignYesFragment2();
                    ft.add(R.id.fg_content,signYesFragment2);
                }
                showFragment(ft, signYesFragment2);
                break;
            //足记
            case 1:
                signNo.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_qiandao_on, 0, 0);
                if (signNoFragment == null) {
                    signNoFragment = new SignNoFragment();
                    ft.add(R.id.fg_content, signNoFragment);
                }
                showFragment(ft, signNoFragment);
                break;

            default:
                curSelection = curSelection2;
                break;
        }
        ft.commit();
    }
    /**
     * 恢复默认图片
     */
    private void resetImg() {
        signYes.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_xuncha, 0, 0);
        signNo.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_qiandao, 0, 0);
    }

    private void showFragment(FragmentTransaction ft, Fragment fragment) {
        hideFragments(ft);
        ft.show(fragment);
    }

    private void hideFragments(FragmentTransaction ft) {
        if (signYesFragment2 != null) {
            ft.hide(signYesFragment2);
        }
        if (signNoFragment != null) {
            ft.hide(signNoFragment);
        }

    }
}
