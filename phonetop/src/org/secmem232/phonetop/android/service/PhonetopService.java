package org.secmem232.phonetop.android.service;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.secmem232.phonetop.android.MainActivity;
import org.secmem232.phonetop.android.MouseView;
import org.secmem232.phonetop.android.UIHandler;
import org.secmem232.phonetop.android.util.Util;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.phonetop.PhonetopDisplayManager;
import com.android.phonetop.PhonetopTetheringManager;

public class PhonetopService extends Service {
	static String tag = "PhonetopService";
	/**
	 * 화면 세로/가로 상황에 따른 고유값 
	 */
	public static boolean isFirst=false;
	static final public int ORIENTATION_PORTRAIT = 0;
	static final public int ORIENTATION_LANDSCAPE = 1;

	//실제 마우스를 표현 할 최상위뷰
	public MouseView view;

	//서비스 종료시점을 판단해 줄 flag 변수 
	//서비스관련 기능을 수행하는 Binder객체, PhonetopServiceConnection객체를 통해 MainActivity로부터 서비스를 관리. 
	public PhonetopServiceBinder mPhonetopServiceBinder;
	//실제 이벤트 관련한 기능 수행 핸들러.
	public PhonetopInputHandler inputEventHandler;
	public String DefaultInputMethod = "";
	public String EnableInputMethod = "";
	
	PhonetopDisplayManager mPhonetopDisplayManager;
	PhonetopTetheringManager mPheontopTetheringManager;
	
	boolean viewAddFlag;
	public boolean isConnected = false;
	DisplayRotation dr;
	private ServerSocket server;
	private Socket client;
	

	@Override
	public IBinder onBind(Intent intent) {
		return mPhonetopServiceBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(tag, "service oncreate");


		//마우스뷰 생성
		view = new MouseView(this); // MainActivity.view=view;
		//마우스뷰 최상위뷰로 등록
		makeView();

		dr = new DisplayRotation(this);
		mPhonetopServiceBinder = new PhonetopServiceBinder(this);
		mPhonetopDisplayManager = new PhonetopDisplayManager(this);
		mPheontopTetheringManager = new PhonetopTetheringManager(this);
		//Usb Display Server Open
		mPhonetopDisplayManager.connectUsbDisplay("0.0.0.0");
		mPheontopTetheringManager.setUsbTethering(true);
		setInputMethod();
		startInputServer();
	}

	private void startInputServer(){
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					server = new ServerSocket(6155);
					client = server.accept();
					if(MainActivity.handler!=null)MainActivity.handler.sendEmptyMessage(UIHandler.SERVICE_CONNECTED);
					Util.saveBooleanPreferences(PhonetopService.this, "isConnected",true);
					inputEventHandler = new PhonetopInputHandler(PhonetopService.this,client,view);
					inputEventHandler.start();
					Log.i(tag, "inputEventHandler end");
					if(MainActivity.handler!=null)MainActivity.handler.sendEmptyMessage(UIHandler.SERVICE_CLOSE);
					stopSelf();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void setInputMethod(){
		DefaultInputMethod = Settings.Secure.getString( this.getContentResolver(),Settings.Secure.DEFAULT_INPUT_METHOD);
		EnableInputMethod = Settings.Secure.getString( this.getContentResolver(),Settings.Secure.ENABLED_INPUT_METHODS);
		Settings.Secure.putString( this.getContentResolver(),Settings.Secure.ENABLED_INPUT_METHODS,EnableInputMethod+":org.secmem232.phonetop/.android.ime.HardKeyboard");
		Settings.Secure.putString( this.getContentResolver(),Settings.Secure.DEFAULT_INPUT_METHOD,"org.secmem232.phonetop/.android.ime.HardKeyboard");
	}
	public void restoreInputMethod(){
		Settings.Secure.putString( this.getContentResolver(),Settings.Secure.ENABLED_INPUT_METHODS,EnableInputMethod);
		Settings.Secure.putString( this.getContentResolver(),Settings.Secure.DEFAULT_INPUT_METHOD,DefaultInputMethod);
	}
	public void makeView(){
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,// TYPE_SYSTEM_ALERT,//TYPE_SYSTEM_OVERLAY,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, // will
				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.LEFT | Gravity.TOP;
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		wm.addView(view, params); // 최상위 윈도우에 뷰 넣기. 권한 필요.
		view.setVisibility(View.INVISIBLE);
	}

	public void setVisibleMouseView(int visible){
		view.setVisibility(visible);
	}

	public void onDestroy() {
		Log.d("PhonetopService", "onDestroy()");
		//InputService 종료
		if(inputEventHandler!=null){
			inputEventHandler.stop();
		}
		// 서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
		if (view != null) 
		{
			((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(view);
			view = null;
		}
		//소켓 종료
		try {
			if (client != null)
				client.close();
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//UsbDisplay 종료
		mPhonetopDisplayManager.disconnectUsbDisplay();
//		mPheontopTetheringManager.setUsbTethering(false);
		//UI 변경
		Util.saveBooleanPreferences(PhonetopService.this, "isConnected",false);
		Util.removeReverseTethering();
		//teterhing 파일 삭제
		
		restoreInputMethod();
		

		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub

		super.onConfigurationChanged(newConfig);
         switch(newConfig.orientation){
            case Configuration.ORIENTATION_LANDSCAPE:
            	view.settingOrientation(MouseView.ORIENTATION_LANDSCAPE);
            	break;
            case Configuration.ORIENTATION_PORTRAIT: 
            	view.settingOrientation(MouseView.ORIENTATION_PORTRAIT);
            	break;
         }
         if(inputEventHandler==null)return;
         inputEventHandler.sendEvent(3, 0, view.getValueX());// ABS_X:0 EV_ABS:3
         inputEventHandler.sendEvent(3, 1, view.getValueY());// ABS_Y:1
	}
}