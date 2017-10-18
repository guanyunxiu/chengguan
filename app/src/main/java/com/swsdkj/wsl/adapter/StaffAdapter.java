package com.swsdkj.wsl.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.swsdkj.wsl.R;
import com.swsdkj.wsl.bean.User;
import com.swsdkj.wsl.tool.StringTool;

import java.util.List;

/**
 * Created by Administrator on 2017/4/26 0026.
 */

public class StaffAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private List<User> staffBeanList;
    public StaffAdapter(Context context, List<User> staffBeanList){
        this.context=context;
        this.staffBeanList=staffBeanList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(context).inflate(R.layout.item_satff, parent, false);
        return new StaffAdapter.MyHolder(layout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StaffAdapter.MyHolder){
            final User staffBean = staffBeanList.get(position);

            if (staffBean!=null){
                if (!StringTool.getString2Front(staffBean.getName()).equals("")){
                    ((StaffAdapter.MyHolder)holder).iconTV.setText(StringTool.getString2Front(staffBean.getName())+"");
                    ((StaffAdapter.MyHolder)holder).nameTV.setText(staffBean.getName()+"");
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return staffBeanList.size();
    }
    class MyHolder extends RecyclerView.ViewHolder{

        TextView iconTV,nameTV;
        public MyHolder(View itemView) {
            super(itemView);
            iconTV = (TextView)itemView.findViewById(R.id.id_icon);
            nameTV = (TextView)itemView.findViewById(R.id.id_name);

        }
    }
}
