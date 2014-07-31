package com.android.phonetop;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.MotionEvent;

public class PhonetopInputManager {
	Instrumentation mInstrumentation;
	
	public PhonetopInputManager(){
		mInstrumentation = new Instrumentation();
	}
	
	public void sendKeyDownUpSync(int keycode){
		mInstrumentation.sendKeyDownUpSync( keycode );
	}
	public void sendPointerSync(int x,int y){
		mInstrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
				SystemClock.uptimeMillis(),MotionEvent.ACTION_DOWN,x, y, 0));
	}
}
