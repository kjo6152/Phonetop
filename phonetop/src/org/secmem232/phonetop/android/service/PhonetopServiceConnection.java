package org.secmem232.phonetop.android.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class PhonetopServiceConnection implements ServiceConnection{
	public PhonetopServiceBinder ptservice;
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		ptservice = (PhonetopServiceBinder) service;
	}
	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		// TODO Auto-generated method stub
		ptservice = null;
	}
	
	public void setMouseViewVisible(boolean flag){
		if(ptservice!=null)ptservice.setMouseViewVisible(flag);
	}
	
	public void setInputMode(boolean isMouse,boolean isKeyboard){
		if(ptservice!=null)ptservice.setInputMode(isMouse,isKeyboard);
	}
	public void startKeyboardService(){
		if(ptservice!=null)ptservice.startKeyboardService();
	}
	
	public void endKeyboardService(){
		if(ptservice!=null)ptservice.endKeyboardService();
	}
	
	public void startMonitorService(){
		if(ptservice!=null)ptservice.startMonitorService();
	}
	
	public void endMonitorService(){
		if(ptservice!=null)ptservice.endMonitorService();
	}
	
	public void startTetheringService(){
		if(ptservice!=null)ptservice.startTetheringService();
	}
	
	public void endTetheringService(){
		if(ptservice!=null)ptservice.endTetheringService();
	}
			
	public void setMousePointerIcon(int whichPointer) {
		if(ptservice!=null)ptservice.setMousePointerIcon(whichPointer);
	}

	public void setMouseWheelVolume(int whichWheel) {
		if(ptservice!=null)ptservice.setMouseWheelVolume(whichWheel);
	}

	public void setMouseSpeed(int whichSpeed) {
		if(ptservice!=null)ptservice.setMouseSpeed(whichSpeed);
	}
	
	//화면 회전
	public void setDisplayOrientation(int whichOrientation){
		if(ptservice!=null)ptservice.setDisplayOrientation(whichOrientation);
	}
	
	public void setMouseMapping(int key, int value) {
		if(ptservice!=null)ptservice.setMouseMapping(key, value);
		
	}
}