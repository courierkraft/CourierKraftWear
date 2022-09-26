package com.courierkraft.mobile;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class LogoGestureListener extends GestureDetector.SimpleOnGestureListener {
	Context context;
	public LogoGestureListener(Context c){
		context = c;
	}

	@Override
	public boolean onDown(MotionEvent event) {
		Log.d("TAG","onDown: ");
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		Log.i("TAG", "onSingleTapConfirmed: ");
		CommonUtils.Vibrate(context, 15, 1, 25);
		Intent intent = new Intent(context, OptionsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		Log.i("TAG", "onLongPress: ");
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		Log.i("TAG", "onDoubleTap: ");
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
		Log.i("TAG", "onScroll: ");
		return true;
	}

	@Override
	public boolean onFling(MotionEvent event1, MotionEvent event2,
						   float velocityX, float velocityY) {
		Log.d("TAG", "onFling: ");
		return true;
	}
}
