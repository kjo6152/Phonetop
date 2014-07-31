package com.android.phonetop;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.util.Log;

public class PhonetopDisplayManager {
	private static String tag = "PhonetopDisplayManager";
	
	DisplayManager mDisplayManager;
	
	
	public PhonetopDisplayManager(Context context){
		mDisplayManager = (DisplayManager)context.getSystemService(Context.DISPLAY_SERVICE);
	}
	
	public void connectUsbDisplay(String deviceAddress){
		if(mDisplayManager==null){
			Log.i(tag,"connectUsbDisplay : mDisplayManager is null");
			return;
		}
		if(deviceAddress==null)deviceAddress="0.0.0.0";
		mDisplayManager.connectUsbDisplay(deviceAddress);
	}
	
	public void pauseUsbDisplay(){
		if(mDisplayManager==null){
			Log.i(tag,"pauseUsbDisplay : mDisplayManager is null");
			return;
		}
		mDisplayManager.pauseUsbDisplay();
	}
	
	public void resumeUsbDisplay(){
		if(mDisplayManager==null){
			Log.i(tag,"resumeUsbDisplay : mDisplayManager is null");
			return;
		}
		mDisplayManager.resumeUsbDisplay();
	}
	
	public void disconnectUsbDisplay(){
		if(mDisplayManager==null){
			Log.i(tag,"disconnectUsbDisplay : mDisplayManager is null");
			return;
		}
		mDisplayManager.disconnectUsbDisplay();
	}
}
