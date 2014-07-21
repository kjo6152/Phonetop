package org.secmem232.phonetop.android.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.secmem232.phonetop.android.MainActivity;
import org.secmem232.phonetop.android.MouseView;
import org.secmem232.phonetop.android.UIHandler;
import org.secmem232.phonetop.android.natives.InputHandler;
import org.secmem232.phonetop.android.util.Util;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class PhonetopService extends Service {
	static String tag = "PhonetopService";
	static final public int ORIENTATION_PORTRAIT = 0;
	static final public int ORIENTATION_LANDSCAPE = 1;
	
	public static int CLOSE_DISPLAY_SERVICE = 0;
	public static int SET_DISPLAY_ORIENTATION = 10;
	public static int CHANGE_DISPLAY_ORIENTATION = 11;
	
	static final public byte  START_MOUSE_SERVICE = 11;
	static final public byte  END_MOUSE_SERVICE = 12;
	static final public byte  START_KEYBOARD_SERVICE = 13;
	static final public byte  END_KEYBOARD_SERVICE = 14;
	static final public byte  START_MONITOR_SERVICE = 15;
	static final public byte  END_MONITOR_SERVICE = 16;
	static final public byte  START_TETHERING_SERVICE = 17;
	static final public byte  END_TETHERING_SERVICE = 18;
	static final public byte  SET_MOUSE_WHEEL_VOLUEM = 19;
	static final public byte  SET_MOUSE_SPEED = 20;
	static final public byte  SET_MOUSE_MAPPING = 21;
	static final public byte  SET_KEYBOARD_MAPPING = 22;
	static final public byte  SET_MONITOR_ORIENTATION = 23;
	
	static final public byte NONE_DEVICE = 4;
	static final public byte INPUT_KEYBOARD = 1;
	static final public byte INPUT_MOUSE = 2;
	static final public byte INPUT_ALLDEVICE = 3;
	
	static final public byte OUTPUT_MONITOR = 1;
	static final public byte UTIL_THETHERING = 1;
	
	static final public int LEFT_BUTTON = 272;
	static final public int RIGHT_BUTTON = 273;
	static final public int WHEEL_BUTTON = 274;

	static final public int WHEEL_SLOW = 0;
	static final public int WHEEL_NORMAL = 1;
	static final public int WHEEL_FAST = 2;

	static final public int KEY_CLICK = 0;
	static final public int KEY_BACK = 1;
	static final public int KEY_HOME = 2;
	static final public int KEY_MENU = 3;
	
	static final public int LEFT_HOME = 125;
	static final public int RIGHT_HOME = 126;
	static final public int HOME = 102;
	static final public int VOLUME_DOWN = 114;
	static final public int VOLUME_UP = 115;
	static final public int KEY_F2 = 60;
	static final public int KEY_F3 = 61;
	static final public int KEY_F4 = 62;
	static final public int KEY_POWER = 116;

	int addedClient = 0;
	public MouseView view;
	private boolean isEnd;
	public PhonetopServiceBinder mPhonetopServiceBinder;
	InputHandler inputHandler;
	InputStream in;
	OutputStream out;
	byte a[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0 };
	ByteBuffer buffer;
	int type;
	int code;
	int value;
	int inputMode;

	int btnLeft;
	int btnRight;
	int btnWheel;

	int wheelSpeed;
	
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

		wheelSpeed = Util.getIntegerPreferences(this, "wheel");
		btnLeft = Util.getIntegerPreferences(this, "btn_left");
		btnRight = Util.getIntegerPreferences(this, "btn_right");
		btnWheel = Util.getIntegerPreferences(this, "btn_wheel");

		if (btnLeft < 0)
			btnLeft = 0;
		if (btnRight < 0)
			btnRight = 1;
		if (btnWheel < 0)
			btnWheel = 2;
		view = new MouseView(this); // MainActivity.view=view;

		makeView();
		
		mPhonetopServiceBinder.setMouseWheelVolume(Util.getIntegerPreferences(this, "wheel"));
		mPhonetopServiceBinder.setMouseSpeed(Util.getIntegerPreferences(this, "speed"));
		mPhonetopServiceBinder.setMouseMapping(LEFT_BUTTON, Util.getIntegerPreferences(this, "btn_left"));
		mPhonetopServiceBinder.setMouseMapping(RIGHT_BUTTON, Util.getIntegerPreferences(this, "btn_right"));
		mPhonetopServiceBinder.setMouseMapping(WHEEL_BUTTON, Util.getIntegerPreferences(this, "btn_wheel"));
		mPhonetopServiceBinder.setMousePointerIcon(Util.getIntegerPreferences(this, "cursor"));
		
		// view.setOnTouchListener(mViewTouchListener); //팝업뷰에 터치 리스너 등록
		inputHandler = new InputHandler(this);
		dr = new DisplayRotation(this);
		mPhonetopServiceBinder = new PhonetopServiceBinder(this);
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
					runInputService();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
	public void executeMouseFunction(int num) {
		Log.d("name", "num : " + num);
		switch (num) {
		case KEY_CLICK:
			if (value == 1) {// 누름,이동
				inputHandler.sendEvent(1, 330, 1);
				inputHandler.sendEvent(0, 0, 0);
			} else if (value == 0) {// 뗌
				inputHandler.sendEvent(1, 330, 0);// BTN_TOUCH:330 Ox14a
				inputHandler.sendEvent(0, 0, 0);
			}
			break;
		case KEY_BACK:
			if (value == 0) {// 뗌
				inputHandler.sendEvent(1, 158, 1);// KEY_BACK:158
				inputHandler.sendEvent(0, 0, 0);
				inputHandler.sendEvent(1, 158, 0);// KEY_BACK:158
				inputHandler.sendEvent(0, 0, 0);
			}
			break;
		case KEY_HOME:
			if (value == 1) {// 누름,이동
				inputHandler.sendEvent(1, 102, 1);
				inputHandler.sendEvent(0, 0, 0);
			} else if (value == 0) {// 뗌
				inputHandler.sendEvent(1, 102, 0);
				inputHandler.sendEvent(0, 0, 0);
			}
			break;
		case KEY_MENU:
			if (value == 1) {// 누름,이동
				inputHandler.sendEvent(1, 139, 1);
				inputHandler.sendEvent(0, 0, 0);
			} else if (value == 0) {// 뗌
				inputHandler.sendEvent(1, 139, 0);
				inputHandler.sendEvent(0, 0, 0);
			}
			break;
		}
	}

	public void setEventType(boolean isMouse,boolean isKeyboard){
		try {
			if(!isMouse&&!isKeyboard)
				out.write(NONE_DEVICE);
			else if(isMouse&&isKeyboard)
				out.write(INPUT_ALLDEVICE);
			else if(isMouse)
				out.write(INPUT_MOUSE);
			else//(isKeyboard)
				out.write(INPUT_KEYBOARD);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void buttonEvents() {
		try {
			//32bit용
			int ret = in.read(a, 0, 16);
			if(ret<=0){
				isEnd = false;
				return;
			}
			buffer.rewind();
			buffer.put(a);
			//32bit용
			type = buffer.getShort(8);
			code = buffer.getShort(10);
			value = buffer.getInt(12);

//			Log.d("TCP/IPtest", "type : " + type + ",code : " + code+ ",value : " + value);
			if (view == null)
				return;

			switch (type) {
			case 1:
				if (code == LEFT_BUTTON) {// 마우스 좌버튼 클릭시
					Log.d("name", "left : " + btnLeft);
					executeMouseFunction(btnLeft);
				} else if (code == RIGHT_BUTTON) {// 마우스 우버튼 클릭시
					Log.d("name", "right : " + btnRight);
					executeMouseFunction(btnRight);
				} else if (code == WHEEL_BUTTON) {// 마우스 휠 버튼 클릭시
					Log.d("name", "wheel : " + btnWheel);
					executeMouseFunction(btnWheel);
				} else if(code == LEFT_HOME || code == RIGHT_HOME){
					inputHandler.sendEvent(type, HOME, value);
				} else if(code == KEY_F2){
					inputHandler.sendEvent(type, VOLUME_DOWN, value);
				} else if(code == KEY_F3){
					inputHandler.sendEvent(type, VOLUME_UP, value);
				} else if(code == KEY_F4){
					System.out.println("POWER: " + KEY_POWER);
					inputHandler.sendEvent(type, KEY_POWER, value);
				} else if(code == 41){
					inputHandler.sendEvent(type,399, value);
				} else {
					inputHandler.sendEvent(type, code, value);
					return;
				}
				break;
			case 2:
				// 움직일때 x,y갱신
				if (code == 0) {// 좌우
					view.setRelativeCurser_X(value);
					view.postInvalidate();
				} else if (code == 1) {// 상하
					view.setRelativeCurser_Y(value);
					view.postInvalidate();
				} else if (code == 8) {
					if (value > 0) {// 휠아래로
						for(int i=0;i<=this.wheelSpeed;i++)
						inputHandler.keyStroke(103);
					} else if (value < 0) {// 휠위로
						for(int i=0;i<=this.wheelSpeed;i++)
						inputHandler.keyStroke(108);
					}
					//Util.makeToast(this, "Wheel : "+wheelSpeed, Toast.LENGTH_SHORT);
					return;
				}
				break;
			case 4:
				if (value < 100)
					inputHandler.sendEvent(type, code, value);
				return;
			default:
				inputHandler.sendEvent(type, code, value);
			}			
			inputHandler.sendEvent(3, 0, view.getValueX());// ABS_X:0 EV_ABS:3
			inputHandler.sendEvent(3, 1, view.getValueY());// ABS_Y:1

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 계속 마우스 입력 파일을 읽어서 setCursor()갱신시켜주는 로직
	}

	private void runInputService() {
		new Thread() {
			@Override
			public void run() {
				if (client == null) {
					Log.d("TCP/IPtest", "Socket is NULL");
					return;
				}
				try {
					in = client.getInputStream();
					out = client.getOutputStream();
					buffer = ByteBuffer.allocate(300);
					out.write(4);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				inputHandler.open();
				
				while (!isEnd) {
//					Log.d("TCP/IPtest", "before buttonEvents");
					buttonEvents();
				}

				inputHandler.close();
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
		isEnd = true;
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
        inputHandler.sendEvent(3, 0, view.getValueX());// ABS_X:0 EV_ABS:3
		 inputHandler.sendEvent(3, 1, view.getValueY());// ABS_Y:1
	}
}
