package com.wbapp.omxvideo;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class ImageViewActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        imageView = new ImageView(this);
        setContentView(imageView);
        String path = getIntent().getExtras().getString("path");
        Uri uri = Uri.parse("file://"+path);
        imageView.setImageURI(uri);
    }
    
    ImageView imageView;
}
