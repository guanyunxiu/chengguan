package com.swsdkj.wsl.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.swsdkj.wsl.R;
import com.swsdkj.wsl.adapter.TypeAdapter;
import com.swsdkj.wsl.base.BaseActivity;
import com.swsdkj.wsl.bean.ProType;
import com.swsdkj.wsl.fragment.CollectTypeFragment;
import com.swsdkj.wsl.tool.CommonUtil;
import com.swsdkj.wsl.view.DividerDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 内容：
 * Created by 关云秀 on 2017\10\23 0023.
 */

public class DrawTestActivity extends BaseActivity {
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.rl_left_menu)
    RelativeLayout rlLeftMenu;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.listview)
    RecyclerView listview;
    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;
    @BindView(R.id.right_lv)
    LinearLayout rightLv;
    private List<ProType> typeList;
    TypeAdapter adapter;
    CollectTypeFragment collectTypeFragment;
    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_drawtest;
    }

    @Override
    protected void initViews() {
        initTitle(true, "信息查询");
    }

    @Override
    protected void updateViews() {
        getProType();
        listview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TypeAdapter(this, typeList);
        listview.setAdapter(adapter);
        listview.addItemDecoration(new DividerDecoration(this));
        //创建MyFragment对象
        collectTypeFragment = new CollectTypeFragment();
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, collectTypeFragment);
        //通过bundle传值给MyFragment
        Bundle bundle = new Bundle();
        bundle.putString("aa", "22");
        collectTypeFragment.setArguments(bundle);
        fragmentTransaction.commit();
        setOnClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shaixuan, menu);
        return true;
    }
    /***
     * 单击事件
     */
    public void setOnClick(){

        adapter.setTypeItem(new TypeAdapter.TypeItem() {
            @Override
            public void TypeItemImpl(int typeId) {
                adapter.selectTypeId = typeId;
                adapter.notifyDataSetChanged();
                CommonUtil.showToast(context,typeId+"***");
                collectTypeFragment = new CollectTypeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                        .beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, collectTypeFragment);
                Bundle bundle = new Bundle();
                bundle.putString("type", typeId+"");
                collectTypeFragment.setArguments(bundle);
                fragmentTransaction.commit();
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_shaixuan:
                // startActivity(new Intent(this,CollectInfoActivity.class));
                drawerLayout.openDrawer(rightLv);
                return true;
            case R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * 得到商品分类
     */
    public List<ProType> getProType(){
        typeList = new ArrayList<>();
        ProType proType = new ProType(1,"公共设施","");
        typeList.add(proType);
        ProType proType2 = new ProType(2,"道路交通","");
        typeList.add(proType2);
        ProType proType3 = new ProType(3,"市容环境","");
        typeList.add(proType3);
        ProType proType4 = new ProType(4,"园林绿化","");
        typeList.add(proType4);
        ProType proType5 = new ProType(5,"房屋土地","");
        typeList.add(proType5);
        ProType proType6 = new ProType(6,"其他设施","");
        typeList.add(proType6);
        return typeList;
    }
}
