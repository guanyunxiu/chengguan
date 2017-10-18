package com.swsdkj.wsl.tool;

import android.content.Context;

import zhangphil.iosdialog.widget.ActionSheetDialog;
import zhangphil.iosdialog.widget.AlertDialog;

/**
 * 作者： 关云秀 on 2017/2/8.
 * 描述：
 */
public class DialogUtil {
    public interface OnItemClickListener {
        void onConfirmClicked();
    }

    public static OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public static AlertDialog alertDialog(Context context,String content){
       return new AlertDialog(context)
                .builder()
                .setTitle("提示")
               .setCancelable(false)
                .setMsg(content);

    }
    public static ActionSheetDialog alertDialog2(Context context){
        return  new ActionSheetDialog(context)
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true);
    }
}
