package com.swsdkj.wsl.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.bean.PhotoBean;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.IOException;
import java.util.List;

/**
 * 内容：
 * Created by 关云秀 on 2017\10\20 0020.
 */

public class VideoAdapter2 extends BaseQuickAdapter<PhotoBean> {
    private Context context;
    public VideoAdapter2(List<PhotoBean> list, Context context) {
        super(R.layout.activity_video_item,list);
        this.context = context;
    }
    @Override
    protected void convert(BaseViewHolder helper, PhotoBean photoBean) {
        ImageView imageView = (ImageView) helper.getConvertView().findViewById(R.id.imageview);
        Glide.with(context).load(photoBean.getAddress()).placeholder(R.mipmap.icon_defalut_img).error(R.mipmap.icon_defalut_img).into(imageView);
    }

}
