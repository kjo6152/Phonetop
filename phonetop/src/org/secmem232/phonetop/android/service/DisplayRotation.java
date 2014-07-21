package org.secmem232.phonetop.android.service;


import android.app.Service;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

public class DisplayRotation {
	public String tag = "DisplayRotation";

	//Rotation Info
	Context context;
	WindowManager wm;
	LinearLayout orientationChanger;
	int windowType = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
	int[] OrientationConstant = { ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
			ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
			ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,
			ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE };
	
	LayoutParams orientationLayout = new WindowManager.LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
			windowType, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
					| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
			PixelFormat.RGBA_8888);
	int OrientationIndex;

	
	public DisplayRotation(Context context){
		this.context = context;
	}
	
	//Rotation Info ---------------------------------------
	public void setDefaultOrientation(){
		setDeviceOrientation(0);
	}
	private void nextOrientationIndex(){
		wm = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
		this.OrientationIndex = wm.getDefaultDisplay().getRotation();
		OrientationIndex = (OrientationIndex+1)%OrientationConstant.length;
	}
	public void setDeviceOrientation(){
		nextOrientationIndex();
		Log.i(tag, "OrientationIndex : "+OrientationIndex);
		setDeviceOrientation(OrientationIndex);
	}
	public void setDeviceOrientation(int Orientation) {
		OrientationIndex = Orientation;
		int ScreenOrientation = OrientationConstant[OrientationIndex];
		Log.i(tag, "ScreenOrientation : "+ScreenOrientation);
		wm = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
		
		//Device Orientation Setting 1
		android.provider.Settings.System.putInt(context.getContentResolver(), android.provider.Settings.System.ACCELEROMETER_ROTATION, 1);
		
		orientationChanger = new LinearLayout(context);
		orientationChanger.setClickable(false);
		orientationChanger.setFocusable(false);
		orientationChanger.setFocusableInTouchMode(false);
		orientationChanger.setLongClickable(false);


		wm.addView(orientationChanger, orientationLayout);
		orientationChanger.setVisibility(View.GONE);

		orientationLayout.screenOrientation = ScreenOrientation;
		wm.updateViewLayout(orientationChanger, orientationLayout);
		orientationChanger.setVisibility(View.VISIBLE);
	}
	public void setCurrentOrientationIndex(){
		wm = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
		this.OrientationIndex = wm.getDefaultDisplay().getRotation();
	}
	//End Rotation Info --------------------------------------
}
