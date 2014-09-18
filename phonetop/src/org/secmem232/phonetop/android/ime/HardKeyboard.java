package org.secmem232.phonetop.android.ime;

import java.util.Locale;

import org.secmem232.phonetop.R;
import org.secmem232.phonetop.android.util.Util;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.os.Build;
import android.text.method.MetaKeyKeyListener;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class HardKeyboard extends InputMethodService {

	static final boolean PROCESS_HARD_KEYS = true;
	public static final String DEF_CHARSET = "UTF-8";

	private StringBuilder mComposing = new StringBuilder();
	private long mState;
	private boolean capsLock = false;

	private HangulAutomata automata = new HangulAutomata();
	private boolean hangulMode = true;

	private String mWordSeparators;

	@Override
	public void onCreate() {
		super.onCreate();
		mWordSeparators = getResources().getString(R.string.word_separators);
	}

	@Override
	public void onStartInput(EditorInfo attribute, boolean restarting) {
		super.onStartInput(attribute, restarting);
		automata.reset();

		Locale loc = Util.InputMethod.getLastLocale(getApplicationContext());
		if (loc.equals(Locale.KOREA) || loc.equals(Locale.KOREAN))
			hangulMode = true;
		else
			hangulMode = false;

		mComposing.setLength(0);

		if (!restarting) {

			mState = 0;
		}

	}

	@Override
	public void onFinishInput() {
		super.onFinishInput();
		mComposing.setLength(0);
		automata.reset();
	}

	@Override
	public void onUpdateSelection(int oldSelStart, int oldSelEnd,
			int newSelStart, int newSelEnd, int candidatesStart,
			int candidatesEnd) {
		super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
				candidatesStart, candidatesEnd);

		if (mComposing.length() > 0
				&& (newSelStart != candidatesEnd || newSelEnd != candidatesEnd)) {
			mComposing.setLength(0);
			InputConnection ic = getCurrentInputConnection();
			if (ic != null) {
				ic.finishComposingText();
				if (hangulMode) {
					automata.reset();
				}
			}
		}
	}

	private boolean translateKeyDown(int keyCode, KeyEvent event) {
	
		mState = MetaKeyKeyListener.handleKeyDown(mState, keyCode, event);
		Log.d("PhoneTopIme" ,"mState Keydown :" +mState);

		int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mState));
		mState = MetaKeyKeyListener.adjustMetaAfterKeypress(mState);
		Log.d("PhoneTopIme" ,"mState adjustMetaAfterKeyPress :" +mState);
		
		InputConnection ic = getCurrentInputConnection();
		ic.clearMetaKeyStates((int) mState);
		if (c == 0 || ic == null) {
			return false;
		}

		if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
			c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
		}

		if (mComposing.length() > 0) {
			char accent = mComposing.charAt(mComposing.length() - 1);
			int composed = KeyEvent.getDeadChar(accent, c);
			if (composed != 0) {
				c = composed;
				mComposing.setLength(mComposing.length() - 1);
			}
		}
		Log.d("PhoneTopIme", "shift code : "+keyCode);
		onKey(c, null);

		return true;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (this.isInputViewShown()) {
				automata.reset();
				this.hideWindow();
			}
			break;

		case KeyEvent.KEYCODE_CAPS_LOCK:
			if (Build.VERSION.SDK_INT <= 9) {
				if ((event.getMetaState() & KeyEvent.META_CAPS_LOCK_ON) == KeyEvent.META_CAPS_LOCK_ON) {
					capsLock = true;
				} else {
					capsLock = false;
				}
			} else {
				capsLock = event.isCapsLockOn();
			}
			break;

		case KeyEvent.KEYCODE_DEL:

			if (mComposing.length() > 0) {
				onKey(Keyboard.KEYCODE_DELETE, null);
				return true;
			}
			break;

		case KeyEvent.KEYCODE_ENTER:
			if (hangulMode)
				automata.reset();
			return false;
		case KeyEvent.KEYCODE_SPACE:
			if (hangulMode)
				automata.reset();
			break;

		default:
		
			if (PROCESS_HARD_KEYS) {
				Log.d("PhoneTopIme", "shift"+keyCode);
				if (event.getScanCode() == 122) {
					switchLanguage();
					return true;
				}
				
				if (translateKeyDown(keyCode, event)) {
					
					return true;
				}
			}
		}

			return super.onKeyDown(keyCode, event);
		
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (PROCESS_HARD_KEYS) {

			mState = MetaKeyKeyListener.handleKeyUp(mState, keyCode, event);

		}

		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onFinishInputView(boolean finishingInput) {
		super.onFinishInputView(finishingInput);
		automata.reset();
	}


	private void commitTyped(InputConnection inputConnection) {
		if (mComposing.length() > 0) {
			inputConnection.commitText(mComposing, mComposing.length());
			mComposing.setLength(0);
		}
	}


	private void updateShiftKeyState(EditorInfo attr) {
		

		
		Log.d("PhoneTopIme" , "shift1");
		if (attr != null) {
			Log.d("PhoneTopIme" , "shift2");
			EditorInfo ei = getCurrentInputEditorInfo();
			
			if (ei != null && ei.inputType != EditorInfo.TYPE_NULL) {
				Log.d("PhoneTopIme" , "shift3");
				getCurrentInputConnection().getCursorCapsMode(attr.inputType);
			}
		}
	}


	private boolean isAlphabet(int code) {
		if (Character.isLetter(code)) {
			return true;
		} else {
			return false;
		}
	}


	private void keyDownUp(int keyEventCode) {
		getCurrentInputConnection().sendKeyEvent(
				new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
		getCurrentInputConnection().sendKeyEvent(
				new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
	}

	private void sendKey(int keyCode) {

		switch (keyCode) {
		case '\n':
			keyDownUp(KeyEvent.KEYCODE_ENTER);
			break;
		default:
			if (keyCode >= '0' && keyCode <= '9') {
				keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
			} else {
				getCurrentInputConnection().commitText(
						String.valueOf((char) keyCode), 1);
			}
			break;
		}
	}

	public void onKey(int primaryCode, int[] keyCodes) {
		Log.d("PhoneTopIme", "onkey : " + primaryCode);
	
	   if (isWordSeparator(primaryCode)) {
			// Handle separator

			if (mComposing.length() > 0) {
				commitTyped(getCurrentInputConnection());
				if (hangulMode) {
					Log.d("PhoneTopIme", "space2 : "+primaryCode);	
					automata.reset();
				}
			}
			sendKey(primaryCode);
			updateShiftKeyState(getCurrentInputEditorInfo());
		}
		else if (primaryCode == Keyboard.KEYCODE_DELETE) {
			handleBackspace();
		} else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
			handleClose();
			return;
		} else {
			handleCharacter(primaryCode, keyCodes); 
		}
	}

//	public void onText(CharSequence text) {
//		InputConnection ic = getCurrentInputConnection();
//		if (ic == null)
//			return;
//		ic.beginBatchEdit();
//		if (mComposing.length() > 0) {
//			commitTyped(ic);
//		}
//		ic.commitText(text, 0);
//		ic.endBatchEdit();
//		updateShiftKeyState(getCurrentInputEditorInfo());
//	}

	private void handleBackspace() {
		final int length = mComposing.length();
		if (length > 1) {
			mComposing.delete(length - 1, length);
			// Handle hangul
			if (hangulMode) {
				int result = automata.deleteCharacter();
				if (result != -1)
					mComposing.append((char) result);
			}
			
			getCurrentInputConnection().setComposingText(mComposing, 1);
		} else if (length > 0 ) {
		
			if (hangulMode) {
				automata.reset();
			}
			mComposing.setLength(0);
			getCurrentInputConnection().commitText("", 0);
		} else {
			keyDownUp(KeyEvent.KEYCODE_DEL);
		}
		updateShiftKeyState(getCurrentInputEditorInfo());
		
	}

	private void switchLanguage() {
		automata.reset();
		// Switch language
		hangulMode = !hangulMode;

		// Save switched language
		Util.InputMethod.setLastLocale(getApplicationContext(),
				hangulMode ? Locale.KOREAN : Locale.ENGLISH);

	}

	private void handleCharacter(int primaryCode, int[] keyCodes) {
		
		if (capsLock) {
			primaryCode = Character.toUpperCase(primaryCode);
		}

		if (isAlphabet(primaryCode) && !hangulMode) {
	
			mComposing.append((char) primaryCode);
			getCurrentInputConnection().setComposingText(mComposing, 1);
			getCurrentInputConnection().commitText(String.valueOf((char) primaryCode), 1);
			updateShiftKeyState(getCurrentInputEditorInfo());
		} else if (hangulMode) {
		
			InputConnection ic = getCurrentInputConnection();
			int length = mComposing.length();

			ic.beginBatchEdit();
			if (automata.getBuffer() != -1 && 0 < length)
				mComposing.delete(length - 1, length);
			int ret[] = automata.appendCharacter(HangulAutomata.toHangulCode(primaryCode));

			for (int i = 0; i < ret.length - 1; i++) {
				if (ret[i] != -1)
					mComposing.append((char) ret[i]);
			}
			Log.d("PhoneTopIme" , mComposing.toString());
			ic.commitText(mComposing, 1);
			
			mComposing.setLength(0);
			if (ret[2] != -1) {
				mComposing.append((char) ret[2]);
				ic.setComposingText(mComposing, 1);
			}
			ic.endBatchEdit();
		} else {
			
//			mComposing.append((char) primaryCode);
//			getCurrentInputConnection().setComposingText(mComposing, 1);
			getCurrentInputConnection().commitText(
					String.valueOf((char) primaryCode), 1);
		}
	}

	private void handleClose() {
		commitTyped(getCurrentInputConnection());
		automata.reset();
		requestHideSelf(0);
	}

	private String getWordSeparators() {
		return mWordSeparators;
	}

	public boolean isWordSeparator(int code) {
		String separators = getWordSeparators();
		return separators.contains(String.valueOf((char) code));
	}

}