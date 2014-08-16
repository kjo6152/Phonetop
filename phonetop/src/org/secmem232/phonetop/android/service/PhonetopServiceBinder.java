package org.secmem232.phonetop.android.service;

import java.io.File;
import java.io.IOException;

import org.secmem232.phonetop.android.util.Util;

import android.os.Binder;
import android.view.View;
import android.widget.Toast;

public class PhonetopServiceBinder extends Binder{
	PhonetopService mPhonetopService;
	public PhonetopServiceBinder(PhonetopService phonetopService){
		mPhonetopService = phonetopService;
	}
	public boolean isConnected(){
		return mPhonetopService.isConnected;
	}
	public void setMouseViewVisible(boolean flag){
		if(flag) mPhonetopService.setVisibleMouseView(View.VISIBLE); 
		else mPhonetopService.setVisibleMouseView(View.INVISIBLE);
	}

	public void setInputMode(boolean isMouse,boolean isKeyboard){
		if(isMouse)mPhonetopService.inputEventHandler.setEventType(PhonetopInputHandler.INPUT_MOUSE_START);
		else mPhonetopService.inputEventHandler.setEventType(PhonetopInputHandler.INPUT_MOUSE_SLEEP);
		if(isKeyboard)mPhonetopService.inputEventHandler.setEventType(PhonetopInputHandler.INPUT_KEYBOARD_START);
		else mPhonetopService.inputEventHandler.setEventType(PhonetopInputHandler.INPUT_KEYBOARD_SLEEP);
		
	}
	public void startMonitorService(){
		mPhonetopService.inputEventHandler.setEventType(PhonetopInputHandler.INPUT_MONITOR_START);
	}

	public void endMonitorService(){
		mPhonetopService.inputEventHandler.setEventType(PhonetopInputHandler.INPUT_MONITOR_SLEEP);
	}

	public void startTetheringService(){
		File TetheringFile = new File("/data/data/org.secmem232.phonetop/shared_prefs/isReverseTethering");
		try {
			if(!TetheringFile.exists())TetheringFile.createNewFile();
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		Toast.makeText(mPhonetopService, "역테더링 연결되었습니다.", Toast.LENGTH_SHORT).show();
	}

	public void endTetheringService(){
		File TetheringFile = new File("/data/data/org.secmem232.phonetop/shared_prefs/isReverseTethering");
		if(TetheringFile.exists())TetheringFile.delete();
		Toast.makeText(mPhonetopService, "역테더링 해제 되었습니다.", Toast.LENGTH_SHORT).show();
	}

	public void setMousePointerIcon(int whichPointer) {
		if (mPhonetopService.view == null) return;
		Util.saveIntegerPreferences(mPhonetopService, "cursor", whichPointer);
		mPhonetopService.view.settingMyCursor(whichPointer);
	}

	public void setMouseWheelVolume(int whichWheel) {
		if (mPhonetopService.view == null) return;
		Util.saveIntegerPreferences(mPhonetopService, "wheel", whichWheel);
		mPhonetopService.inputEventHandler.wheelSpeed=whichWheel;
	}

	public void setMouseSpeed(int whichSpeed) {
		if (mPhonetopService.view == null) return;
		Util.saveIntegerPreferences(mPhonetopService, "speed", whichSpeed);
		mPhonetopService.view.setMySpeed(whichSpeed);
	}

	//화면 회전
	public void setDisplayOrientation(int whichOrientation){
		mPhonetopService.dr.setDeviceOrientation(whichOrientation);
	}

	public void setMouseMapping(int key, int value) {
		switch (key) {
		case PhonetopInputHandler.LEFT_BUTTON:
			mPhonetopService.inputEventHandler.btnLeft = value;
			Util.saveIntegerPreferences(mPhonetopService, "btn_left", value);
			break;
		case PhonetopInputHandler.RIGHT_BUTTON:
			mPhonetopService.inputEventHandler.btnRight = value;
			Util.saveIntegerPreferences(mPhonetopService, "btn_right", value);
			break;
		case PhonetopInputHandler.WHEEL_BUTTON:
			mPhonetopService.inputEventHandler.btnWheel = value;
			Util.saveIntegerPreferences(mPhonetopService, "btn_wheel", value);
			break;
		}

	}
}