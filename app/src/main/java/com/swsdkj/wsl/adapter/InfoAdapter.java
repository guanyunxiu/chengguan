package com.swsdkj.wsl.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.bean.InfoBean;
import java.util.List;

/**
 * 内容：公告adapter
 * Created by 关云秀 on 2017\8\9 0009.
 */

public class InfoAdapter extends BaseMultiItemQuickAdapter<InfoBean> {
   private Context context;
   public InfoAdapter(List<InfoBean> data,Context context) {
        super(data);
        this.context = context;
        addItemType(InfoBean.ET, R.layout.activity_collect_info_et);
        addItemType(InfoBean.XL, R.layout.activity_collect_info_xiala);
        addItemType(InfoBean.TV, R.layout.activity_collect_info_tv);
        }
    @Override
    protected void convert(final BaseViewHolder helper, final InfoBean item) {
            switch (helper.getItemViewType()) {
            case InfoBean.ET:
                    helper.setText(R.id.name_tv,item.getName()+"：");
                    helper.setText(R.id.content_tv,item.getContent());
            break;
            case InfoBean.XL:
                    helper.setText(R.id.name_tv,item.getName()+"：");
                    final TextView textView = (TextView) helper.getConvertView().findViewById(R.id.content_tv);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActionSheetDialogNoTitle(textView);
                        }
                    });

            break;
            case InfoBean.TV:
                    helper.setText(R.id.name_tv,item.getName()+"：");
                    helper.setText(R.id.content_tv,item.getContent());
            break;

            }
       }
    private void ActionSheetDialogNoTitle(final TextView textView) {
        final String[] stringItems = {"事假", "病假","年假","调休","婚假","产假","陪产假","路途假","其他"};
        final ActionSheetDialog dialog = new ActionSheetDialog(context, stringItems, null);
        dialog.isTitleShow(false).show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                textView.setText(stringItems[position]);
                dialog.dismiss();
            }
        });
    }
}
