package com.wbapp.omxvideo;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class ReplayListActivity extends Activity {
    
    TabHost mTabHost;
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.replay_tabhost);
        setupTabs();
    }
    
    private void setupTabs() {
        mTabHost = (TabHost) this.findViewById(R.id.replay_tabhost);
        mTabHost.setup();
        
        String[] tags = new String[] {"video", "audio", "image"};
        String[] title = new String[] { "视频", "音频", "图片" };
        int[] tabIds = new int[] { R.id.video_tab, R.id.audio_tab, R.id.image_tab };
        for (int i = 0; i < title.length; i++) {
            //Button button = new Button(this);
            //button.setText(title[i]);
            //button.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.tab_lable));  //自定义按钮样式
            mTabHost.addTab(mTabHost.newTabSpec(tags[i]).setIndicator(title[i]).setContent(tabIds[i]));
        }
        
        mTabHost.setOnTabChangedListener(mTabChangeListener);
        
        FragmentManager manager = getFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.replace(R.id.video_tab, ReplayListFragment.createForVideo(), "video");
        trans.commit();
    }

    OnTabChangeListener mTabChangeListener = new OnTabChangeListener() {
        @Override
        public void onTabChanged(String tag) {
            FragmentManager manager = ReplayListActivity.this.getFragmentManager();

            if (manager.findFragmentByTag(tag) != null)
                return;
            
            Fragment frag = null;
            int contentViewID = 0;
            if (tag.equals("video")) {
                frag = ReplayListFragment.createForVideo();
                contentViewID = R.id.video_tab;
            } else if (tag.equals("audio")) {
                frag = ReplayListFragment.createForAudio();
                contentViewID = R.id.audio_tab;
            }
            else if (tag.equals("image")) {
                frag = ReplayListFragment.createForImage();
                contentViewID = R.id.image_tab;
            }
            if (frag == null)
                return;
            Log.i("", "new fragment replace");

            FragmentTransaction trans = manager.beginTransaction();
            trans.replace(contentViewID, frag, tag);
            trans.commit();
        }
    };
}