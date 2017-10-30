package com.swsdkj.wsl.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.swsdkj.wsl.R;
import com.swsdkj.wsl.bean.ProType;

import java.util.List;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> {
    public int selectTypeId=1;
    public Context activity;
    public List<ProType> dataList;

    public TypeAdapter(Context activity, List<ProType> dataList) {
        this.activity = activity;
        this.dataList = dataList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProType item = dataList.get(position);
        holder.bindData(item);
    }

    @Override
    public int getItemCount() {
        if(dataList==null){
            return 0;
        }
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView type;
        private ProType item;
        public ViewHolder(View itemView) {
            super(itemView);

            type = (TextView) itemView.findViewById(R.id.type);
            itemView.setOnClickListener(this);
        }

        public void bindData(ProType item){
            this.item = item;
            type.setText(item.getTypename());
            if(item.getId()==selectTypeId){
                itemView.setBackgroundColor(Color.WHITE);
            }else{
                itemView.setBackgroundResource(R.color.background);
            }

        }

        @Override
        public void onClick(View v) {
            typeItem.TypeItemImpl(item.getId());
           // activity.onTypeClicked(item.typeId);
        }
    }
    public interface TypeItem{
        void TypeItemImpl(int typeId);
    }
    public TypeItem typeItem;

    public TypeItem getTypeItem() {
        return typeItem;
    }

    public void setTypeItem(TypeItem typeItem) {
        this.typeItem = typeItem;
    }
}
