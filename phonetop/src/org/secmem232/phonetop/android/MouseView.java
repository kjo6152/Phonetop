package org.secmem232.phonetop.android;

import org.secmem232.phonetop.R;
import org.secmem232.phonetop.android.util.Util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

public class MouseView extends ViewGroup {
	static final public int ORIENTATION_PORTRAIT = 1;
	static final public int ORIENTATION_LANDSCAPE = 2;
	
	static final public int SPEED_SLOW = 0;
	static final public int SPEED_NORMAL = 1;
	static final public int SPEED_FAST = 2;
	
	static final public int CURSOR_BASIC_S = 0;
	static final public int CURSOR_BASIC_M = 1;
	static final public int CURSOR_BASIC_L = 2;
	static final public int CURSOR_FINGER = 3;
	static final public int CURSOR_STICK = 4;
		
	private DisplayMetrics metrics;
	private Paint mPaint;
	private int displayWidth,displayHeight;
	private Drawable cursor;
	private int x,y;
	private double speedFlag;
	private int myCursor;
	private int mySpeed;
	private int myWheelVolume;
	private int myWindowMode;
	
	Context context;
	public MouseView(Context context) {
		super(context);
		this.context=context;
		//cursor = getResources().getDrawable(R.drawable.cusor);
		myCursor = Util.getIntegerPreferences(context, "cursor");
		settingMyCursor(myCursor);		
		mySpeed = Util.getIntegerPreferences(context, "speed");
		settingMySpeed(mySpeed);
		myWheelVolume = Util.getIntegerPreferences(context, "wheel");
		metrics = getResources().getDisplayMetrics();		
		Resources r = Resources.getSystem();
		Configuration config = r.getConfiguration();
		myWindowMode = config.orientation;
		settingOrientation(myWindowMode);  
		
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(2);
		mPaint.setAntiAlias(true);				
	}
   
	
	public void settingOrientation(int orientation) {
		// TODO Auto-generated method s		tub
		switch(orientation){
		case Configuration.ORIENTATION_PORTRAIT:
			displayWidth = metrics.widthPixels;
			displayHeight = metrics.heightPixels;			
			myWindowMode = orientation;			
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			displayWidth = metrics.widthPixels;
			displayHeight = metrics.heightPixels;
			myWindowMode = orientation;
			break;
		}
	}
	
	public void settingMyCursor(int cursor) {
		// TODO Auto-generated method stub
		setMyCursor(cursor);
		Util.saveIntegerPreferences(context, "cursor", cursor);
		switch(cursor){
		case CURSOR_BASIC_S:
			this.cursor = getResources().getDrawable(R.drawable.pointer_base);
			break;
		case CURSOR_BASIC_M:
			this.cursor = getResources().getDrawable(R.drawable.pointer_base2);
			break;
		case CURSOR_BASIC_L:
			this.cursor = getResources().getDrawable(R.drawable.pointer_base3);
			break;
		case CURSOR_FINGER:
			this.cursor = getResources().getDrawable(R.drawable.pointer_finger);
			break;
		case CURSOR_STICK:
			this.cursor = getResources().getDrawable(R.drawable.pointer_stick);
			break;		
		default:
			this.cursor = getResources().getDrawable(R.drawable.cusor);
			break;
		}
	}

	public int getValueX(){
		if(myWindowMode==ORIENTATION_PORTRAIT) 	
			return x;
		else 	return y; 
	}
	
	public int getValueY(){
		if(myWindowMode==ORIENTATION_PORTRAIT) 	
			return y;
		else 	return x;
	}
	
	public int getMyCursor() {
		return myCursor;
	}	
	
	public int getMyWheelVolume() {
		if(myWheelVolume<0) return 1;
		return myWheelVolume;
	}
	
	public void setMyWheelVolume(int myWheelVolume) {
		this.myWheelVolume = myWheelVolume;
	}

	public void setMyCursor(int myCursor) {
		this.myCursor = myCursor;
	}

	public int getMySpeed() {
		return mySpeed;
	}

	public void setMySpeed(int mySpeed) {
		this.mySpeed = mySpeed;
		settingMySpeed(mySpeed);
	}
	public void settingMySpeed(int mySpeed){
		switch(mySpeed){
		case SPEED_SLOW:
			this.speedFlag=1;
			break;
		case SPEED_NORMAL:
			this.speedFlag=2;
			break;
		case SPEED_FAST:
			this.speedFlag=3;
			break;
		default:
			break;
		}		
	}

	public void setAbsluteCurser(int x_value,int y_value){
		this.x=x_value;
		this.y=y_value;
	}
	public void setAbsluteCurser_X(int x_value){
		this.x=x_value;
	}
	public void setAbsluteCurser_Y(int y_value){
		this.y=y_value;
	}
	
	public void setRelativeCurser(int x_value,int y_value){
		this.x+=x_value;
		this.y+=y_value;
	}
	
	public void setRelativeCurser_X(int x_value){
		if(this.x<0){
			x=0;
			return;
		}else if(this.x>displayWidth){
			x=displayWidth;
			return;
		}		
		this.x+=x_value*this.speedFlag;		
	}
	
	public void setRelativeCurser_Y(int y_value){
		if(this.y<0){
			y=0;
			return;
		}else if(this.y>displayHeight){
			y=displayHeight;
			return;
		}
		if(myWindowMode==ORIENTATION_LANDSCAPE){
			y_value=-y_value;
		}
		this.y+=y_value*this.speedFlag;
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawColor(Color.TRANSPARENT);
		if(myWindowMode==ORIENTATION_LANDSCAPE){
			cursor.setBounds(x, displayHeight-y, x+cursor.getIntrinsicWidth(), displayHeight-y+cursor.getIntrinsicHeight());
		}else{
			cursor.setBounds(x, y, x+cursor.getIntrinsicWidth(), y+cursor.getIntrinsicHeight());	
		}
		cursor.draw(canvas);
	}

@Override
protected void onLayout(boolean changed, int l, int t, int r, int b) {
	// TODO Auto-generated method stub
	
}
}
