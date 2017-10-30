package com.swsdkj.wsl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.swsdkj.wsl.R;
import com.swsdkj.wsl.adapter.CollectTypeTextAdapter;
import com.swsdkj.wsl.adapter.PhotoAdapter2;
import com.swsdkj.wsl.adapter.VideoAdapter;
import com.swsdkj.wsl.adapter.VideoAdapter2;
import com.swsdkj.wsl.base.BaseActivity;
import com.swsdkj.wsl.bean.CollectTypeBean;
import com.swsdkj.wsl.bean.PhotoBean;
import com.swsdkj.wsl.net.BaseUrl;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 内容：
 * Created by 关云秀 on 2017\10\23 0023.
 */

public class CollectSelectInfoActivity extends BaseActivity {
    @BindView(R.id.jc_rlv)
    RecyclerView jcRlv;
    @BindView(R.id.latlng_tv)
    TextView latlngTv;
    @BindView(R.id.address_tv)
    TextView addressTv;
    @BindView(R.id.photo_rlv)
    RecyclerView photoRlv;
    @BindView(R.id.video_rlv)
    RecyclerView videoRlv;
    CollectTypeTextAdapter collectTypeTextAdapter;
    PhotoAdapter2 photoAdapter;
    VideoAdapter2 videoAdapter;
    private List<PhotoBean> photoBeanList = new ArrayList<>();
    private List<PhotoBean> videoBeanList = new ArrayList<>();
    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_collect_select_info;
    }

    @Override
    protected void initViews() {
        initTitle(true, "部件信息");
        jcRlv.setLayoutManager(new LinearLayoutManager(this));
        collectTypeTextAdapter = new CollectTypeTextAdapter(getList());
        jcRlv.setAdapter(collectTypeTextAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        photoRlv.setLayoutManager(linearLayoutManager);
        photoAdapter = new PhotoAdapter2(getPhotoList(), context);
        photoAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        photoRlv.setAdapter(photoAdapter);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        videoRlv.setLayoutManager(linearLayoutManager2);
        videoAdapter = new VideoAdapter2(getVideoList(), context);
        videoAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        videoRlv.setAdapter(videoAdapter);
        photoAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                ArrayList<String> urls = new ArrayList<>();
                for (PhotoBean imageBean : photoBeanList) {
                    urls.add(imageBean.getAddress());
                }
                Intent intent = new Intent(context, ImagePagerActivity.class);
                // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, i);
                context.startActivity(intent);
            }
        });
        videoAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                Intent intent = new Intent(context, PlayVideo.class);
                intent.putExtra("path", "");
                context.startActivity(intent);
            }
        });
    }

    @Override
    protected void updateViews() {

    }
    public List<CollectTypeBean> getList(){
        List<CollectTypeBean> list = new ArrayList<>();
        for(int i=0;i<5;i++){
            CollectTypeBean collectTypeBean = new CollectTypeBean();
            collectTypeBean.setName("上水井盖");
            list.add(collectTypeBean);
        }
        return list;
    }
    public List<PhotoBean> getPhotoList(){
        for(int i=0;i<8;i++){
            PhotoBean photoBean = new PhotoBean();
            photoBean.setAddress("https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1508743284&di=e648a728de84dbbc1621c5c364c5330c&src=http://imgsrc.baidu.com/imgad/pic/item/3801213fb80e7beca9004ec5252eb9389b506b38.jpg");
            photoBeanList.add(photoBean);
        }
        return photoBeanList;
    }
    public List<PhotoBean> getVideoList(){
        List<PhotoBean> listVideo = new ArrayList<>();
        for(int i=0;i<2;i++){
            PhotoBean photoBean = new PhotoBean();
            photoBean.setAddress("https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1508743284&di=e648a728de84dbbc1621c5c364c5330c&src=http://imgsrc.baidu.com/imgad/pic/item/3801213fb80e7beca9004ec5252eb9389b506b38.jpg");
            listVideo.add(photoBean);
        }
        return listVideo;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_update,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_update:
                startActivity(new Intent(this,CollectInfoActivity.class));
                return true;
            case R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
