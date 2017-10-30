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

public class VideoAdapter extends BaseQuickAdapter<PhotoBean> {
    private Context context;
    public VideoAdapter(List<PhotoBean> list, Context context) {
        super(R.layout.activity_photo_imte2,list);
        this.context = context;
    }
    @Override
    protected void convert(BaseViewHolder helper, PhotoBean photoBean) {
        ScalableVideoView scalableVideoView = (ScalableVideoView) helper.getConvertView().findViewById(R.id.id_video_sdv2);
        playVideo(photoBean.getAddress(),scalableVideoView);
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
