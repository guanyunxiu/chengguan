package com.swsdkj.wsl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hhl.library.OnInitSelectedPosition;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.bean.CollectTypeBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HanHailong on 15/10/19.
 */
public class TagAdapter extends BaseAdapter implements OnInitSelectedPosition {
    public int selectTypeId=0;
    private final Context mContext;
    private final List<CollectTypeBean> mDataList;

    public TagAdapter(Context context) {
        this.mContext = context;
        mDataList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_collect_type_item, null);
        CollectTypeBean collectTypeBean= (CollectTypeBean) mDataList.get(position);
        TextView textView = (TextView) view.findViewById(R.id.type_tv);
        textView.setText(collectTypeBean.getName());
        if(collectTypeBean.getId() == selectTypeId){
            textView.setBackgroundResource(R.drawable.tag_sel);
            textView.setTextColor(mContext.getResources().getColor(R.color.white));
        }else{
            textView.setBackgroundResource(R.drawable.tag_no);
            textView.setTextColor(mContext.getResources().getColor(R.color.text_color));
        }
        return view;
    }

    public void onlyAddAll(List<CollectTypeBean> datas) {
        mDataList.addAll(datas);
        notifyDataSetChanged();
    }

    public void clearAndAddAll(List<CollectTypeBean> datas) {
        mDataList.clear();
        onlyAddAll(datas);
    }

    @Override
    public boolean isSelectedPosition(int position) {
        if (position % 2 == 0) {
            return true;
        }
        return false;
    }
}
