package org.secmem232.phonetop.android.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.secmem232.phonetop.android.MainActivity;
import org.secmem232.phonetop.android.MouseView;
import org.secmem232.phonetop.android.UIHandler;
import org.secmem232.phonetop.android.util.Util;

import com.android.phonetop.PhonetopDisplayManager;
import com.android.phonetop.PhonetopTetheringManager;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class PhonetopService extends Service {
	static String tag = "PhonetopService";
	/**
	 * 화면 세로/가로 상황에 따른 고유값 
	 */
	static final public int ORIENTATION_PORTRAIT = 0;
	static final public int ORIENTATION_LANDSCAPE = 1;

	//클라이언트 추가 유/무 판별 변수
	int addedClient = 0;
	//실제 마우스를 표현 할 최상위뷰
	public MouseView view;

	//서비스 종료시점을 판단해 줄 flag 변수 
	//서비스관련 기능을 수행하는 Binder객체, PhonetopServiceConnection객체를 통해 MainActivity로부터 서비스를 관리. 
	public PhonetopServiceBinder mPhonetopServiceBinder;
	//실제 이벤트 관련한 기능 수행 핸들러.
	public PhonetopInputHandler inputEventHandler;

	PhonetopDisplayManager mPhonetopDisplayManager;
	PhonetopTetheringManager mPheontopTetheringManager;
	
	boolean viewAddFlag;

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
					if(MainActivity.handler!=null){
						addedClient++;
						setAddedClient(addedClient);
					}
					inputEventHandler = new PhonetopInputHandler(PhonetopService.this,client,view);
					runInputService();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}


	private void runInputService() {
		new Thread() {
			@Override
			public void run() {
				if (client == null) {
					Log.d("TCP/IPtest", "Socket is NULL");
					return;
				}				
				inputEventHandler.start();				
			}
		}.start();
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
		if(inputEventHandler!=null){
			inputEventHandler.stop();
		}
//		isEnd = true;
		if (view != null) // 서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
		{
			((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(view);
			view = null;
		}
		view = null;
		try {
			if (client != null)
				client.close();
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setAddedClient(0);
		mPhonetopDisplayManager.disconnectUsbDisplay();
		if(MainActivity.handler!=null)MainActivity.handler.sendEmptyMessage(UIHandler.SERVICE_CLOSE);
		super.onDestroy();
	}

	public void setAddedClient(int ClientCnt){
		Util.saveIntegerPreferences(PhonetopService.this, "addedClient", ClientCnt);
		if(ClientCnt>=1){
			MainActivity.handler.sendEmptyMessage(UIHandler.SERVICE_CONNECTED);
		}
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
         inputEventHandler.sendEvent(3, 0, view.getValueX());// ABS_X:0 EV_ABS:3
         inputEventHandler.sendEvent(3, 1, view.getValueY());// ABS_Y:1
	}
}