package org.secmem232.phonetop.android.natives;


import org.secmem232.phonetop.android.util.CommandLine;

import android.content.Context;
import android.util.DisplayMetrics;

public class InputHandler {
	private final String LOG = "InputHandler";
	private boolean isDeviceOpened = false;
	
	private int displayWidth;
	private int displayHeight;
	
	/**
	 * uinput's touch input will be mapped into 4096*4096 matrix,<br/>
	 * where center coordinate is (0,0) and each axis ranges from -2047 to 2048.<br/>
	 * We should re-map original coordinate to send event properly.
	 */
	private static final int DIMENSION = 4096;
	private static final int HALF_DIMENSION =2048;
	
	public InputHandler(Context context){
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		displayWidth = metrics.widthPixels;
		displayHeight = metrics.heightPixels;
	}
	
	@Override
	protected void finalize() throws Throwable {
		if(isDeviceOpened){
			close();
		}
	}

	public boolean isDeviceOpened(){
		return isDeviceOpened;
	}
	
	/**
	 * Opens uinput(User-level input) device for event injection.
	 * @return true device has opened without error, false otherwise
	 */
	public boolean open(){
		isDeviceOpened = openInputDevice(displayWidth, displayHeight);
		return isDeviceOpened;
	}
	
	/**
	 * Closes uinput device.
	 */
	public void close(){
		closeInputDevice();
		isDeviceOpened = false;
	}
	
	static{
		System.loadLibrary("phonetop");
	}
	
	/**
	 * Set /dev/uinput's permission to 666, to read/write events via uinput.
	 */
	public void grantUinputPermission(){
	}
	
	/**
	 * Revert /dev/uinput's permission to 660.
	 */
	public void revertUinputPermission(){
		CommandLine.execAsRoot("chmod 660 /dev/input");
	}
	
	/**
	 * Opens uinput(User-level input) device for event injection.
	 * @return true device has opened without error, false otherwise
	 */
	private native boolean openInputDevice(final int scrWidth, final int scrHeight);
	
	/**
	 * Open input device using suinput, without setting permission 666 to /dev/uinput.<br/>
	 * If user has su binary that doesn't supports 'su -c' option, which enables running shell command with root permission,
	 * Change permission through org.secmem.remoteroid.util.ComandLine.execAsRoot() first, then use this command to open device.
	 * @return true device has opened without error, false otherwise
	 */
	public native boolean openInputDeviceWithoutPermission();
	
	/**
	 * Closes uinput device.
	 */
	private native void closeInputDevice();
	
	/**
	 * Close input device, without reverting back /dev/uinput's permission to 660.
	 */
	public native void closeInputDeviceWithoutRevertPermission();
	
	/**
	 * Injects keyDown event.
	 * @param keyCode a KeyCode of KeyEvent
	 * @see org.secmem.remoteroid.data.NativeKeyCode NativeKeyCode
	 */
	public native void keyDown(int keyCode);
	
	/**
	 * Injects keyUp event.
	 * @param keyCode a KeyCode of KeyEvent
	 * @see org.secmem.remoteroid.data.NativeKeyCode NativeKeyCode
	 */
	public native void keyUp(int keyCode);
	
	/**
	 * Injects key stroke (keyDown and keyUp) event.
	 * @param keyCode a KeyCode of KeyEvent
	 * @see org.secmem.remoteroid.data.NativeKeyCode NativeKeyCode
	 */
	public native void keyStroke(int keyCode);
	
	/**
	 * Injects touch down (user touched screen) event.<br/>
	 * This event just represents <b>'touching a screen'</b> event. Setting touch screen's coordinate is processed on touchSetPtr(int, int) method.
	 * @see #touchSetPtr(int, int)
	 */
	public synchronized native void touchDown();
	
	/**
	 * Injects touch up (user removed finger from a screen) event.
	 */
	public synchronized native void touchUp();
	
	/**
	 * Set coordinates where user has touched on the screen.<br/>
	 * When user touches the screen, this method called first to set where user has touched, then {@link #touchDown()} called to notify user has touched screen.
	 * @param x x coordinate that user has touched
	 * @param y y coordinate that user has touched
	 */
	public synchronized native void touchSetPtr(int x, int y);
	
	/**
	 * Injects 'touch once' event, touching specific coordinate once.<br/>
	 * This method calls {@link #touchSetPtr(int, int)}, {@link #touchDown()}, and {@link #touchUp()} in sequence.
	 * @param x x coordinate that user has touched
	 * @param y y coordinate that user has touched
	 */
	public void touchOnce(int x, int y){
		touchSetPtr(x, y);
		touchDown();
		touchUp();
	}
	
	public native void sendEvent(int type,int code,int value);
	
	public native void sendEventByLow(byte[] buf);
}
