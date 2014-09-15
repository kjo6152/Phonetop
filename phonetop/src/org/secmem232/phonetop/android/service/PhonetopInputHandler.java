package org.secmem232.phonetop.android.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.secmem232.phonetop.android.MouseView;
import org.secmem232.phonetop.android.natives.InputHandler;
import org.secmem232.phonetop.android.util.Util;

import android.app.Service;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

public class PhonetopInputHandler {
	private static String tag = "PhonetopInputHandler";
	
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

	static final public byte INPUT_MOUSE_START = 1;
	static final public byte INPUT_MOUSE_SLEEP = 2;
	static final public byte INPUT_KEYBOARD_START = 3;
	static final public byte INPUT_KEYBOARD_SLEEP = 4;
	static final public byte INPUT_MONITOR_START = 5;
	static final public byte INPUT_MONITOR_SLEEP = 6;
	static final public byte INPUT_MONITOR_PORTRAIT = 7;
	static final public byte INPUT_MONITOR_LANDSCAPE = 8;
	
	static final public byte OUTPUT_MONITOR = 1;
	static final public byte UTIL_THETHERING = 1;

	static final public int LEFT_BUTTON = 272;
	static final public int RIGHT_BUTTON = 273;
	static final public int WHEEL_BUTTON = 274;

	static final public int LEFT_BUTTON_SDL = 501;
	static final public int RIGHT_BUTTON_SDL = 502;
	static final public int WHEEL_BUTTON_SDL = 503;
	static final public int WHEEL_UP_SDL = 504;
	static final public int WHEEL_DOWN_SDL = 505;
	static final public int MOUSE_MOVE_SDL = 506;
	
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

	private Context context;
	private Socket client;

	private boolean isEnd;

	InputHandler inputHandler;
	InputStream in;
	OutputStream out;
	byte a[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0 };
	ByteBuffer buffer;
	int type;
	int code;
	int value;
	int x = 0;
	int y = 0;
	int inputMode;

	int btnLeft;
	int btnRight;
	int btnWheel;

	int wheelSpeed;


	public PhonetopInputHandler(Context context,Socket client) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.client = client;

		wheelSpeed = Util.getIntegerPreferences(context, "wheel");
		btnLeft = Util.getIntegerPreferences(context, "btn_left");
		btnRight = Util.getIntegerPreferences(context, "btn_right");
		btnWheel = Util.getIntegerPreferences(context, "btn_wheel");

		if (btnLeft < 0)
			btnLeft = 0;
		if (btnRight < 0)
			btnRight = 1;
		if (btnWheel < 0)
			btnWheel = 2;

		inputHandler = new InputHandler(context);
		
	}

	public void start() {
//		Log.i("Phonetop", "InputHandler Start");
		// TODO Auto-generated method stub
		try {
			in = client.getInputStream();
			out = client.getOutputStream();
			buffer = ByteBuffer.allocate(300);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		WindowManager wm = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
		if(wm.getDefaultDisplay().getRotation()==0){
			setEventType(INPUT_MONITOR_PORTRAIT);
		}else {
			setEventType(INPUT_MONITOR_LANDSCAPE);
		}
		
		inputHandler.open();

		while (!isEnd) {
//			Log.i(tag, "while (!isEnd)");
			buttonEvents();
		}
		inputHandler.close();
//		Log.i("Phonetop", "InputHandler end");
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

	public void setEventType(byte EventType){
		try {
			out.write(EventType);
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
				isEnd = true;
				return;
			}
			buffer.rewind();
			buffer.put(a);
			//32bit용
			
			type = buffer.getShort(8);
			code = buffer.getShort(10);
			value = buffer.getInt(12);

			switch (type) {
			case 1:
				if (code == LEFT_BUTTON) {// 마우스 좌버튼 클릭시
					executeMouseFunction(btnLeft);
				} else if (code == RIGHT_BUTTON) {// 마우스 우버튼 클릭시
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
				if (code == MOUSE_MOVE_SDL) {// 마우스 이동
					x = buffer.getInt(0);
					y = buffer.getInt(4);
					inputHandler.sendEvent(3, 0, x);
					inputHandler.sendEvent(3, 1, (int)((double)y*0.91));
				} else if (code == 8) {
					if (value > 0) {// 휠아래로
						for(int i=0;i<=this.wheelSpeed;i++)
						inputHandler.keyStroke(106);
					} else if (value < 0) {// 휠위로
						for(int i=0;i<=this.wheelSpeed;i++)
						inputHandler.keyStroke(105);
					}
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

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stop() {
		// TODO Auto-generated method stub
		isEnd=true;
	}

	public void sendEvent(int type,int code,int value){
		inputHandler.sendEvent(type, code, value);
	}
}