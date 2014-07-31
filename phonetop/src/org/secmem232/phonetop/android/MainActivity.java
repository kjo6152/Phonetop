package org.secmem232.phonetop.android;

import org.secmem232.phonetop.R;
import org.secmem232.phonetop.android.service.PhonetopServiceConnection;
import org.secmem232.phonetop.android.service.PhonetopService;
import org.secmem232.phonetop.android.util.Util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static String tag = "MainActivity";
	private ViewGroup mouseLayout;
	private ViewGroup keyboardLayout;
	private ViewGroup monitorLayout;
	private ViewGroup tetheringLayout;
	private ViewGroup preferenceLayout;
	// private ViewGroup helpLayout;
	public CheckBox mouseCb;
	public CheckBox keyboardCb;
	public CheckBox monitorCb;
	public CheckBox tetheringCb;

	public TextView isConnecting;
	public TextView isConnected;

	private PhonetopServiceConnection phonetopServiceConnection = null;
	public Switch sw;

	static public Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(tag, "onCreate");
	
		setContentView(R.layout.activity_main);
		isConnecting = (TextView) findViewById(R.id.is_connecting);
		isConnected = (TextView) findViewById(R.id.is_connected);

		
		
		sw = (Switch) findViewById(R.id.phonetop_switch);
		sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					//컨넥션을 생성하고 서비스를 실행시키고 컨넥션에 바인드한다.
					phonetopServiceConnection = new PhonetopServiceConnection();
					startService(new Intent(MainActivity.this,PhonetopService.class));
					bindService(new Intent(MainActivity.this,PhonetopService.class), phonetopServiceConnection, Context.BIND_AUTO_CREATE);
					
					//서비스는 돌아가고 있는데 액티비티가 destroy된 후 다시 crate되는 것이라면
					//기존에 addedClient에서 값을 확인하여 연결중인지, 연결됨인지 확인하여 UI에 적용한다.
					if(Util.getBooleanPreferences(MainActivity.this, "isConnected"))sendMessageHandelr(UIHandler.SERVICE_CONNECTED);
					else sendMessageHandelr(UIHandler.SERVICE_CONNECTING);
				} else {
					// 컨넥션 unbind - 서비스 stop - UI 변경 - UI 상태 저장 - 컨넥션 null로 변경 순으로 진행
					// 스위치 온과 반대 순서로 진행된다.
					unbindService(phonetopServiceConnection);
					stopService(new Intent(MainActivity.this,PhonetopService.class));
					sendMessageHandelr(UIHandler.SERVICE_CLOSE);
					phonetopServiceConnection = null;
				}
			}
		});

		mouseCb = (CheckBox) findViewById(R.id.checkbox_mouse);
		mouseCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					if(phonetopServiceConnection!=null)phonetopServiceConnection.setInputMode(mouseCb.isChecked(), keyboardCb.isChecked());
					if(phonetopServiceConnection!=null)phonetopServiceConnection.setMouseViewVisible(true);
				} else {
					if(phonetopServiceConnection!=null)phonetopServiceConnection.setInputMode(mouseCb.isChecked(), keyboardCb.isChecked());
					if(phonetopServiceConnection!=null)phonetopServiceConnection.setMouseViewVisible(false);
				}
				Util.saveBooleanPreferences(MainActivity.this, "mouseCb", isChecked);
			}
		});
		mouseLayout = (ViewGroup) findViewById(R.id.mouse);
		mouseLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mouseCb.isEnabled()) {
					mouseCb.setChecked(!mouseCb.isChecked());
				}
			}
		});
		keyboardCb = (CheckBox) findViewById(R.id.checkbox_keyboard);
		keyboardCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				// TODO Auto-generated method stub
				if(phonetopServiceConnection!=null)phonetopServiceConnection.setInputMode(mouseCb.isChecked(), keyboardCb.isChecked());
				Util.saveBooleanPreferences(MainActivity.this, "keyboardCb", isChecked);
			}
		});
		keyboardLayout = (ViewGroup) findViewById(R.id.keyboard);
		keyboardLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (keyboardCb.isEnabled()) {
					keyboardCb.setChecked(!keyboardCb.isChecked());
				}
			}
		});
		monitorCb = (CheckBox) findViewById(R.id.checkbox_monitor);
		monitorCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				Log.i(tag, "monitor onCheckedChanged");
				// TODO Auto-generated method stub
				if (isChecked) {
					if(phonetopServiceConnection!=null)phonetopServiceConnection.startMonitorService();
				} else {
					if(phonetopServiceConnection!=null)phonetopServiceConnection.endMonitorService();
				}
				Util.saveBooleanPreferences(MainActivity.this, "monitorCb", isChecked);
			}
		});
		monitorLayout = (ViewGroup) findViewById(R.id.monitor);
		monitorLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (monitorCb.isEnabled()) {
					monitorCb.setChecked(!monitorCb.isChecked());
				}
			}
		});
		tetheringCb = (CheckBox) findViewById(R.id.checkbox_reverse_tethering);
		tetheringCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					if(phonetopServiceConnection!=null)phonetopServiceConnection.startTetheringService();
				} else {
					if(phonetopServiceConnection!=null)phonetopServiceConnection.endTetheringService();
				}
				Util.saveBooleanPreferences(MainActivity.this, "tetheringCb", isChecked);
			}
		});
		tetheringLayout = (ViewGroup) findViewById(R.id.reverse_tethering);
		tetheringLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (tetheringCb.isEnabled()) {
					tetheringCb.setChecked(!tetheringCb.isChecked());
				}
			}
		});

		preferenceLayout = (ViewGroup) findViewById(R.id.preference);
		preferenceLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this,
						PreferencesActivity.class);
				startActivity(i);
			}
		});
		
		// 체크박스, 레이아웃 변경을 해주는 핸들러
		handler = new UIHandler(this);
		restoreUI();
	}

	/**
	 * 액티비티가 destroy된 후 다시 create될 때 기존에 서비스의 상태를 복구하기 위한 함수
	 * onCreate() 매서드에서 호출되며 SharedPreference로부터 저장된 값을 읽어 UI에 적용한다.
	 */
	public void restoreUI(){
		if(checkServiceRunning()){
			sw.setChecked(true);
			if(Util.getBooleanPreferences(MainActivity.this, "isConnected")){
				mouseCb.setChecked(Util.getBooleanPreferences(MainActivity.this, "mouseCb"));
				keyboardCb.setChecked(Util.getBooleanPreferences(MainActivity.this, "keyboardCb"));
				monitorCb.setChecked(Util.getBooleanPreferences(MainActivity.this, "monitorCb"));
				tetheringCb.setChecked(Util.getBooleanPreferences(MainActivity.this, "tetheringCb"));
			}
		}
	}
	
	/**
	 * PhonetopService가 실행중인지 확인할 때 쓰이는 매서드
	 * restoreUI() 매서드에서 사용된다.
	 * @return
	 */
	private boolean checkServiceRunning(){
		ActivityManager manager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
    	    if ("org.secmem232.phonetop.android.service.PhonetopService".equals(service.service.getClassName())) {
    	        return true;
    	    }
    	}
    	return false;
	}
	
	/**
	 * 서비스 컨넥션은 unbind한다.
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(phonetopServiceConnection!=null)unbindService(phonetopServiceConnection);
		handler = null;
		Log.i(tag, "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
	
	public void sendMessageHandelr(int what) {
		if (handler == null)
			return;
		handler.sendEmptyMessage(what);
	}
	
	
}
