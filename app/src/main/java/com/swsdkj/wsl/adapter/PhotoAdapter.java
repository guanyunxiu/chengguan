package com.swsdkj.wsl.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.bean.InfoBean;
import com.swsdkj.wsl.bean.PhotoBean;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.IOException;
import java.util.List;

/**
 * 内容：公告adapter
 * Created by 关云秀 on 2017\8\9 0009.
 */

public class PhotoAdapter extends BaseMultiItemQuickAdapter<PhotoBean> {
   private Context context;
   public PhotoAdapter(List<PhotoBean> data, Context context) {
        super(data);
        this.context = context;
        addItemType(PhotoBean.PHOTO, R.layout.activity_photo_item1);
        addItemType(PhotoBean.VIDEO, R.layout.activity_photo_imte2);
        }
    @Override
    protected void convert(final BaseViewHolder helper, final PhotoBean item) {
            switch (helper.getItemViewType()) {
            case PhotoBean.PHOTO:
                ImageView imageView = (ImageView) helper.getConvertView().findViewById(R.id.imageview);
                Glide.with(context).load(item.getAddress()).error(R.mipmap.ic_launcher).into(imageView);
            break;
            case PhotoBean.VIDEO:
                ScalableVideoView scalableVideoView = (ScalableVideoView) helper.getConvertView().findViewById(R.id.id_video_sdv2);
                playVideo(item.getAddress(),scalableVideoView);
            break;
            }
       }
    public void playVideo(String url,final ScalableVideoView idVideoSdv){
        try {
            idVideoSdv.setDataSource(url);
            idVideoSdv.setVolume(0, 0);
            idVideoSdv.setLooping(true);
            idVideoSdv.prepare(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    idVideoSdv.start();
                }
            });
        } catch (IOException ioe) {
            //ignore
        }
    }
}
