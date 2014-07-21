package org.secmem232.phonetop.android.util;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Util {

	static final public String LOG_NAME = "PT_LOG";
	static final public boolean DEBUG_MODE = true;

	// �� �ҷ�����
	static public String getStringPreferences(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences("pref",
				Context.MODE_PRIVATE);
		return pref.getString(key, null);
	}

	// �� �����ϱ�
	static public void saveStringPreferences(Context context, String key,
			String value) {
		SharedPreferences pref = context.getSharedPreferences("pref",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	// ������ �ҷ�����
	static public int getIntegerPreferences(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences("pref",
				Context.MODE_PRIVATE);
		return pref.getInt(key, 0);
	}

	// ������ �����ϱ�
	static public void saveIntegerPreferences(Context context, String key,
			int value) {
		SharedPreferences pref = context.getSharedPreferences("pref",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	static public boolean getBooleanPreferences(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences("pref",
				Context.MODE_PRIVATE);
		return pref.getBoolean(key, false);
	}

	static public void saveBooleanPreferences(Context context, String key,
			boolean value) {
		SharedPreferences pref = context.getSharedPreferences("pref",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
		
	// ��(Key Data) �����ϱ�
	static public void removePreferences(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences("pref",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.remove(key);
		editor.commit();
	}

	// ��(ALL Data) �����ϱ�
	static public void removeAllPreferences(Context context) {
		SharedPreferences pref = context.getSharedPreferences("pref",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}
	
	public static class InputMethod{
		private static final String KEY_LOCALE = "last_ime_locale";
		public static void setLastLocale(Context context, Locale locale){
			getPrefEditor(context).putString(KEY_LOCALE, locale.toString()).commit();
		}
		
		public static Locale getLastLocale(Context context){
			return new Locale(getPref(context).getString(KEY_LOCALE, Locale.ENGLISH.toString()));
		}
	}
	
	private static SharedPreferences getPref(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	private static SharedPreferences.Editor getPrefEditor(Context context){
		return getPref(context).edit();
	}
	
	static public void makeToast(Context context, String message, int time) {
		//Toast.makeText(context, message, time).show();
	}

	static public void makeLog(String message) {
		if (DEBUG_MODE)
			Log.d(LOG_NAME, message);
	}
}
