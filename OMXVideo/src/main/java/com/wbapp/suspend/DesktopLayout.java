package com.wbapp.suspend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;

import com.wbapp.omxvideo.R;

public class DesktopLayout extends LinearLayout {
	public static SurfaceView surfaceView = null;
	public DesktopLayout(Context context) {
		super(context);
		setOrientation(LinearLayout.VERTICAL);
		this.setLayoutParams( new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		
		View view = LayoutInflater.from(context).inflate(
                R.layout.desklayout, null);

		surfaceView = (SurfaceView) view.findViewById(R.id.surfaceView);
		this.addView(view);
	}
}
