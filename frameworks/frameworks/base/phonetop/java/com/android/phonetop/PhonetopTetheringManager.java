package com.android.phonetop;

import java.io.File;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class PhonetopTetheringManager {
	private static String tag = "PhonetopUsbManager";

	ConnectivityManager mConnectivityManager;
//	private LinkProperties mLinkProperties = new LinkProperties();
	
	public PhonetopTetheringManager(Context context) {
		mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	public boolean setUsbTethering(boolean enabled) {
		if (mConnectivityManager.setUsbTethering(enabled) != ConnectivityManager.TETHER_ERROR_NO_ERROR) {
			return true;
		}
		return false;
	}
	public static boolean isReverseTethering() {
		File reverseTethering = new File("/data/data/org.secmem232.phonetop/shared_prefs/isReverseTethering");
		Log.i(tag,"reverseTethering.exists() : "+reverseTethering.exists());
		return reverseTethering.exists();
	}

	public static NetworkInfo getNetworkInfo() {
		Log.i(tag, "getNetworkInfo()");
		NetworkInfo mNetworkInfo = new NetworkInfo(ConnectivityManager.TYPE_WIFI,ConnectivityManager.TYPE_ETHERNET,"WIFI","ReverseTethering");
		mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.CONNECTED, "PhoneTop","Usb Reverse Tethering");
		mNetworkInfo.setIsAvailable(true);
		return mNetworkInfo;
	}
	
	public static String getPreference(String key){
		
		return null;
	}
}
