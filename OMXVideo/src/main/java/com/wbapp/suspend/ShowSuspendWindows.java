package com.wbapp.suspend;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class ShowSuspendWindows {
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mLayout;
	private DesktopLayout mDesktopLayout;
	private long startTime;
	float x, y;
	int top;
	private static Activity activity;

	public ShowSuspendWindows(Activity activity){
		this.activity = activity;
	}
	public void start()
	{
		createSuspend(activity);
	}

	public void createSuspend(Activity activity){
		createWindowManager(activity);
		createDesktopLayout(activity);
		showDesk();
	}

	public static void setShowStatus(boolean status)
	{
		if (status && DesktopLayout.surfaceView != null)
			DesktopLayout.surfaceView.setVisibility(View.VISIBLE);
		else if (!status && DesktopLayout.surfaceView != null)
			DesktopLayout.surfaceView.setVisibility(View.INVISIBLE);
	}

	private void createDesktopLayout(final Activity activity) {
		mDesktopLayout = new DesktopLayout(activity);
		//mDesktopLayout.setOnTouchListener(new OnTouchListener() {
		DesktopLayout.surfaceView.setOnTouchListener(new OnTouchListener() {
			float mTouchStartX;
			float mTouchStartY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				x = event.getRawX();
				y = event.getRawY() - top;
				Log.i("startP", "startX" + mTouchStartX + "====startY"
						+ mTouchStartY);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					Log.i("startP", "startX" + mTouchStartX + "====startY"
							+ mTouchStartY);
					long end = System.currentTimeMillis() - startTime;
					if (end < 300) {
//						closeDesk(activity);
					}
					startTime = System.currentTimeMillis();
					break;
				case MotionEvent.ACTION_MOVE:
					mLayout.x = (int) (x - mTouchStartX);
					mLayout.y = (int) (y - mTouchStartY);
					mWindowManager.updateViewLayout(mDesktopLayout, mLayout);
					//mWindowManager.updateViewLayout(v, mLayout);
					break;
				case MotionEvent.ACTION_UP:
					mLayout.x = (int) (x - mTouchStartX);
					mLayout.y = (int) (y - mTouchStartY);
					mWindowManager.updateViewLayout(mDesktopLayout, mLayout);
					//mWindowManager.updateViewLayout(v, mLayout);
					mTouchStartX = mTouchStartY = 0;
					break;
				}
				return true;
			}
		});
	}

	private void showDesk() {
		mWindowManager.addView(mDesktopLayout, mLayout);
	}
	private void closeDesk() {
		mWindowManager.removeView(mDesktopLayout);
	}
	private void createWindowManager(Activity activity) {
        // 获取WindowManager
        mWindowManager = (WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE);;
		mLayout = new WindowManager.LayoutParams();
        // 类型
		mLayout.type = WindowManager.LayoutParams.TYPE_PHONE;
		mLayout.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 不设置这个弹出框的透明遮罩显示为黑色
		mLayout.format = PixelFormat.TRANSLUCENT;
		mLayout.gravity = Gravity.TOP | Gravity.LEFT;
		mLayout.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mLayout.height = WindowManager.LayoutParams.WRAP_CONTENT;
	}
}
