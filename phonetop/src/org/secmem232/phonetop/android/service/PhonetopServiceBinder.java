package org.secmem232.phonetop.android.service;

import org.secmem232.phonetop.android.util.Util;

import android.os.Binder;
import android.view.View;
import android.widget.Toast;

public class PhonetopServiceBinder extends Binder{
	PhonetopService mPhonetopService;
	public PhonetopServiceBinder(PhonetopService phonetopService){
		mPhonetopService = phonetopService;
	}

	public void setMouseViewVisible(boolean flag){
		if(flag) mPhonetopService.setVisibleMouseView(View.VISIBLE); 
		else mPhonetopService.setVisibleMouseView(View.INVISIBLE);
	}

	public void setInputMode(boolean isMouse,boolean isKeyboard){
		mPhonetopService.inputEventHandler.setEventType(isMouse, isKeyboard);
	}
	public void startKeyboardService(){

	}

	public void endKeyboardService(){

	}

	public void startMonitorService(){
		//지원
	}

	public void endMonitorService(){
		//지원
	}

	public void startTetheringService(){
		Util.saveIntegerPreferences(mPhonetopService, "ReverseTethering", 1);
		Toast.makeText(mPhonetopService, "역테더링 연결되었습니다.", Toast.LENGTH_SHORT).show();
	}

	public void endTetheringService(){
		Util.saveIntegerPreferences(mPhonetopService, "ReverseTethering", 0);
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