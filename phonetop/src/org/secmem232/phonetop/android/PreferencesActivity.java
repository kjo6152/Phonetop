package org.secmem232.phonetop.android;

import org.secmem232.phonetop.R;
import org.secmem232.phonetop.android.service.PhonetopService;
import org.secmem232.phonetop.android.service.PhonetopServiceBinder;
import org.secmem232.phonetop.android.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class PreferencesActivity extends Activity {
	private ViewGroup pointerLayout;
	private ViewGroup optionLayout;
	private ViewGroup wheelLayout;
	private ViewGroup mappingLayout;
	private ViewGroup rotationLayout;

	private ServiceConnection cursorConnection;
	private ServiceConnection speedConnection;
	private ServiceConnection wheelConnection;
	private ServiceConnection RotationConnection;
	
	private int whichPointer;
	private int whichSpeed;
	private int whichWheel;
	private int whichRotation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sub);

		cursorConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				((PhonetopServiceBinder) service).setMousePointerIcon(whichPointer);
				unbindService(this);
			}

			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub

			}
		};
		
		speedConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				((PhonetopServiceBinder) service).setMouseSpeed(whichSpeed);
				unbindService(this);
			}

			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub

			}
		};
		
		wheelConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				((PhonetopServiceBinder) service).setMouseWheelVolume(whichWheel);
				unbindService(this);
			}

			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub

			}
		};
		
		RotationConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				((PhonetopServiceBinder) service).setDisplayOrientation(whichRotation);
				unbindService(this);
			}

			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub

			}
		};
		
		pointerLayout = (ViewGroup) findViewById(R.id.mouse_pointer);
		pointerLayout.setOnClickListener(new OnClickListener() {
			int cursorNum = Util.getIntegerPreferences(PreferencesActivity.this, "cursor");
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String items[] = { "basic(S)", "basic(M)", "basic(L)",
						"finger", "stick" };
				ContextThemeWrapper cw = new ContextThemeWrapper(
						PreferencesActivity.this, R.style.AlertDialogTheme);
				AlertDialog.Builder ab = new AlertDialog.Builder(cw);
				ab.setTitle("Mouse Pointer");
				ab.setSingleChoiceItems(items, cursorNum,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								whichPointer=whichButton;								
								bindService(new Intent(PreferencesActivity.this,PhonetopService.class),cursorConnection, 0);
								Util.saveIntegerPreferences(PreferencesActivity.this,"cursor", whichButton);
								cursorNum=whichPointer;
							}
						})
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {										
									}
								});
				ab.show();
			}
		});

		optionLayout = (ViewGroup) findViewById(R.id.mouse_pointer_option);
		optionLayout.setOnClickListener(new OnClickListener() {
			int speedNum = Util.getIntegerPreferences(PreferencesActivity.this, "speed");
			@Override
			public void onClick(View v) {
				final String items[] = { "little", "soso", "much" };
				ContextThemeWrapper cw = new ContextThemeWrapper(
						PreferencesActivity.this, R.style.AlertDialogTheme);
				AlertDialog.Builder ab = new AlertDialog.Builder(cw);
				ab.setTitle("Pointer Speed");
				ab.setSingleChoiceItems(items, speedNum,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								whichSpeed=whichButton;
								bindService(new Intent(PreferencesActivity.this,PhonetopService.class),speedConnection, 0);
								Util.saveIntegerPreferences(PreferencesActivity.this,"speed", whichButton);
								speedNum=whichSpeed;
							}
						})
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
									}
								});
				ab.show();
			}
			
		});

		wheelLayout = (ViewGroup) findViewById(R.id.mouse_wheel);
		wheelLayout.setOnClickListener(new OnClickListener() {
			int wheelNum = Util.getIntegerPreferences(PreferencesActivity.this, "wheel");
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String items[] = { "1-row/1-move", "2-row/1-move", "3-row/1-move"};
				ContextThemeWrapper cw = new ContextThemeWrapper(
						PreferencesActivity.this, R.style.AlertDialogTheme);
				AlertDialog.Builder ab = new AlertDialog.Builder(cw);
				ab.setTitle("Mouse Wheel");
				ab.setSingleChoiceItems(items, wheelNum,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								whichWheel=whichButton;
								bindService(new Intent(PreferencesActivity.this,PhonetopService.class),wheelConnection, 0);
								Util.saveIntegerPreferences(PreferencesActivity.this,"wheel", whichButton);
								wheelNum=whichWheel;
							}
						})
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {		
									}
								});
				ab.show();
			}
		});

		mappingLayout = (ViewGroup) findViewById(R.id.mouse_mapping);
		mappingLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(PreferencesActivity.this,
						MouseMappingActivity.class);
				startActivity(i);
			}
		});
		
		rotationLayout = (ViewGroup) findViewById(R.id.monitor_rotation);
		rotationLayout.setOnClickListener(new OnClickListener() {
			int rotationNum=0;
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String items[] = { "portrait", "landscape"};
				ContextThemeWrapper cw = new ContextThemeWrapper(
						PreferencesActivity.this, R.style.AlertDialogTheme);
				AlertDialog.Builder ab = new AlertDialog.Builder(cw);
				ab.setTitle("Monitor rotation");
				ab.setSingleChoiceItems(items, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								rotationNum=whichButton;
							}
						})
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {	
										if(whichRotation!=rotationNum){
											whichRotation=rotationNum;
											bindService(new Intent(PreferencesActivity.this,PhonetopService.class),RotationConnection, 0);
											//화면 방향 바뀌었을때		
										}
									}
								});
				ab.show();
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
