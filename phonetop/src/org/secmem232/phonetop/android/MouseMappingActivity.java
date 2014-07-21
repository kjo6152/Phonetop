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

public class MouseMappingActivity extends Activity {
	private ViewGroup leftButtonLayout;
	private ViewGroup rightButtonLayout;
	private ViewGroup wheelButtonLayout;

	private ServiceConnection mappingServiceConnection;

	int key;
	int value;

	int btnLeftPosition;
	int btnRightPosition;
	int btnWheelPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mouse_keymapping_view);

		mappingServiceConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				((PhonetopServiceBinder) service).setMouseMapping(key,value);
				unbindService(this);
			}

			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub

			}
		};

		leftButtonLayout = (ViewGroup) findViewById(R.id.btn_left);
		leftButtonLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				btnLeftPosition = Util.getIntegerPreferences(
						MouseMappingActivity.this, "btn_left");
				final String items[] = { "touch(Click)", "back", "home", "menu" };
				ContextThemeWrapper cw = new ContextThemeWrapper(
						MouseMappingActivity.this, R.style.AlertDialogTheme);
				AlertDialog.Builder ab = new AlertDialog.Builder(cw);
				ab.setTitle("left button");
				ab.setSingleChoiceItems(items, btnLeftPosition,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// �� ����Ʈ�� ����������
							}
						})
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// OK ��ư Ŭ���� , ���⼭ ������ ���� ����
										// Activity �� �ѱ�� �ȴ�.
										key = PhonetopService.LEFT_BUTTON;
										value = ((AlertDialog) dialog)
												.getListView()
												.getCheckedItemPosition();
										Util.saveIntegerPreferences(
												MouseMappingActivity.this,
												"btn_left", value);
										bindService(new Intent(
												MouseMappingActivity.this,
												PhonetopService.class),
												mappingServiceConnection, 0);
									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// Cancel ��ư Ŭ����
									}
								});
				ab.show();
			}
		});

		rightButtonLayout = (ViewGroup) findViewById(R.id.btn_right);
		rightButtonLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				btnRightPosition = Util.getIntegerPreferences(
						MouseMappingActivity.this, "btn_right");
				final String items[] = { "touch(Click)", "back", "home", "menu" };
				ContextThemeWrapper cw = new ContextThemeWrapper(
						MouseMappingActivity.this, R.style.AlertDialogTheme);
				AlertDialog.Builder ab = new AlertDialog.Builder(cw);
				ab.setTitle("right button");
				ab.setSingleChoiceItems(items, btnRightPosition,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// �� ����Ʈ�� ����������
							}
						})
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// OK ��ư Ŭ���� , ���⼭ ������ ���� ����
										// Activity �� �ѱ�� �ȴ�.
										key = PhonetopService.RIGHT_BUTTON;
										value = ((AlertDialog) dialog)
												.getListView()
												.getCheckedItemPosition();
										Util.saveIntegerPreferences(
												MouseMappingActivity.this,
												"btn_right", value);
										bindService(new Intent(
												MouseMappingActivity.this,
												PhonetopService.class),
												mappingServiceConnection, 0);
									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// Cancel ��ư Ŭ����
									}
								});
				ab.show();
			}
		});

		wheelButtonLayout = (ViewGroup) findViewById(R.id.btn_wheel);
		wheelButtonLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				btnWheelPosition = Util.getIntegerPreferences(
						MouseMappingActivity.this, "btn_wheel");
				final String items[] = { "touch(Click)", "back", "home", "menu" };
				ContextThemeWrapper cw = new ContextThemeWrapper(
						MouseMappingActivity.this, R.style.AlertDialogTheme);
				AlertDialog.Builder ab = new AlertDialog.Builder(cw);
				ab.setTitle("wheel button");
				ab.setSingleChoiceItems(items, btnWheelPosition,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// �� ����Ʈ�� ����������
							}
						})
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// OK ��ư Ŭ���� , ���⼭ ������ ���� ����
										// Activity �� �ѱ�� �ȴ�.
										key = PhonetopService.WHEEL_BUTTON;
										value = ((AlertDialog) dialog)
												.getListView()
												.getCheckedItemPosition();
										Util.saveIntegerPreferences(
												MouseMappingActivity.this,
												"btn_wheel", value);
										bindService(new Intent(
												MouseMappingActivity.this,
												PhonetopService.class),
												mappingServiceConnection, 0);
									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// Cancel ��ư Ŭ����
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
