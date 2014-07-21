package org.secmem232.phonetop.android;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

/**
 * MainActivity의 UI를 관리하는 Handler
 * 버튼 클릭시 이벤트뿐만 아니라 서비스에서도 이 핸들러를 사용한다.
 * 서비스 연결중, 연결됨, 종료 세 가지 상태를 가지며
 * 서비스에서 클라이언트로부터 연결 메시지를 받으면 호출한다.
 * @author jiwon
 *
 */
public class UIHandler extends Handler {
	private static String tag = "UIHandler";
	
	MainActivity mMainActivity;
	
	public static final int SERVICE_CONNECTING = 0;
	public static final int SERVICE_CLOSE = 1;
	public static final int SERVICE_CONNECTED = 2;
	
	public static int SET_VISIBLE_OR_TRUE = 0;
	public static int SET_INVISIBLE_OR_FALSE = 1;
	
	// private ViewGroup helpLayout;
	private CheckBox mouseCb;
	private CheckBox keyboardCb;
	private CheckBox monitorCb;
	private CheckBox tetheringCb;

	private TextView isConnecting;
	private TextView isConnected;
	private Switch sw;
	
	// 체크박스, 레이아웃 변경을 해주는 핸들러
	public UIHandler(MainActivity pMainActivity){
		this.mMainActivity = pMainActivity;
		this.mouseCb = pMainActivity.mouseCb;
		this.keyboardCb = pMainActivity.keyboardCb;
		this.monitorCb = pMainActivity.monitorCb;
		this.tetheringCb = pMainActivity.tetheringCb;
		
		this.isConnecting = pMainActivity.isConnecting;
		this.isConnected = pMainActivity.isConnected;
		this.sw = pMainActivity.sw;
	}
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
			case SERVICE_CONNECTING:
				isConnected.setVisibility(View.GONE);
				isConnecting.setVisibility(View.VISIBLE);
				break;
				
			case SERVICE_CONNECTED:
				isConnected.setVisibility(View.VISIBLE);
				isConnecting.setVisibility(View.GONE);
				mouseCb.setEnabled(true);
				keyboardCb.setEnabled(true);
				monitorCb.setEnabled(true);
				tetheringCb.setEnabled(true);
				break;
				
			case SERVICE_CLOSE:
				isConnected.setVisibility(View.GONE);
				isConnecting.setVisibility(View.GONE);
				mouseCb.setChecked(false);
				mouseCb.setEnabled(false);
				keyboardCb.setChecked(false);
				keyboardCb.setEnabled(false);
				monitorCb.setChecked(false);
				monitorCb.setEnabled(false);
				tetheringCb.setChecked(false);
				tetheringCb.setEnabled(false);
				sw.setChecked(false);
				break;
		}
	}
}
