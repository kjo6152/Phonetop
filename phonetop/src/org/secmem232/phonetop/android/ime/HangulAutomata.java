

package org.secmem232.phonetop.android.ime;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class HangulAutomata {

	public static int toHangulCode(int engCode) {
		switch (engCode) {
		case 'a':
		case 'A':
			return 12609;
		case 'b':
		case 'B':
			return 12640;
		case 'c':
		case 'C':
			return 12618;
		case 'd':
		case 'D':
			return 12615;
		case 'e':
			return 12599;
		case 'E':
			return 12600;
		case 'f':
		case 'F':
			return 12601;
		case 'g':
		case 'G':
			return 12622;
		case 'h':
		case 'H':
			return 12631;
		case 'i':
		case 'I':
			return 12625;
		case 'j':
		case 'J':
			return 12627;
		case 'k':
		case 'K':
			return 12623;
		case 'l':
		case 'L':
			return 12643;
		case 'm':
		case 'M':
			return 12641;
		case 'n':
		case 'N':
			return 12636;
		case 'o':
			return 12624;
		case 'O':
			return 12626;
		case 'p':
			return 12628;
		case 'P':
			return 12630;
		case 'q':
			return 12610;
		case 'Q':
			return 12611;
		case 'r':
			return 12593;
		case 'R':
			return 12594;
		case 's':
		case 'S':
			return 12596;
		case 't':
			return 12613;
		case 'T':
			return 12614;
		case 'u':
		case 'U':
			return 12629;
		case 'v':
		case 'V':
			return 12621;
		case 'w':
			return 12616;
		case 'W':
			return 12617;
		case 'x':
		case 'X':
			return 12620;
		case 'y':
		case 'Y':
			return 12635;
		case 'z':
		case 'Z':
			return Hangul.Jaeum.KHIEUKH;
		default:
			return engCode;

		}
	}

	private static final int[] PREF_CHO = { Hangul.Jaeum.KIYEOK,
			Hangul.Jaeum.SSANGKIYEOK, Hangul.Jaeum.NIEUN, Hangul.Jaeum.TIKEUT,
			Hangul.Jaeum.SSANGTIKEUT, Hangul.Jaeum.RIEUL, Hangul.Jaeum.MIEUM,
			Hangul.Jaeum.PIEUP, Hangul.Jaeum.SSANGPIEUP, Hangul.Jaeum.SIOS,
			Hangul.Jaeum.SSANGSIOS, Hangul.Jaeum.IEUNG, Hangul.Jaeum.CIEUC,
			Hangul.Jaeum.SSANGCIEUC, Hangul.Jaeum.CHIEUCH,
			Hangul.Jaeum.KHIEUKH, Hangul.Jaeum.THIEUTH, Hangul.Jaeum.PHIEUPH,
			Hangul.Jaeum.HIEUH };

	private static final int[] PREF_JUNG = { Hangul.Moeum.A, Hangul.Moeum.AE,
			Hangul.Moeum.YA, Hangul.Moeum.YAE, Hangul.Moeum.EO, Hangul.Moeum.E,
			Hangul.Moeum.YEO, Hangul.Moeum.YE, Hangul.Moeum.O, Hangul.Moeum.WA,
			Hangul.Moeum.WAE, Hangul.Moeum.OE, Hangul.Moeum.YO, Hangul.Moeum.U,
			Hangul.Moeum.WEO, Hangul.Moeum.WE, Hangul.Moeum.WI,
			Hangul.Moeum.YU, Hangul.Moeum.EU, Hangul.Moeum.YI, Hangul.Moeum.I };

	private static final int[] PREF_JONG = { Hangul.Jaeum.KIYEOK,
			Hangul.Jaeum.SSANGKIYEOK, Hangul.Jaeum.KIYEOK_SIOS,
			Hangul.Jaeum.NIEUN, Hangul.Jaeum.NIEUN_CIEUC,
			Hangul.Jaeum.NIEUN_HIEUH, Hangul.Jaeum.TIKEUT, Hangul.Jaeum.RIEUL,
			Hangul.Jaeum.RIEUL_KIYEOK, Hangul.Jaeum.RIEUL_MIEUM,
			Hangul.Jaeum.RIEUL_PIEUP, Hangul.Jaeum.RIEUL_SIOS,
			Hangul.Jaeum.RIEUL_THIEUTH, Hangul.Jaeum.RIEUL_PHIEUPH,
			Hangul.Jaeum.RIEUL_HIEUH, Hangul.Jaeum.MIEUM, Hangul.Jaeum.PIEUP,
			Hangul.Jaeum.PIEUP_SIOS, Hangul.Jaeum.SIOS, Hangul.Jaeum.SSANGSIOS,
			Hangul.Jaeum.IEUNG, Hangul.Jaeum.CIEUC, Hangul.Jaeum.CHIEUCH,
			Hangul.Jaeum.KHIEUKH, Hangul.Jaeum.THIEUTH, Hangul.Jaeum.PHIEUPH,
			Hangul.Jaeum.HIEUH };

	private static final String mChoseong = new String(PREF_CHO, 0,
			PREF_CHO.length);
	private static final String mJungseong = new String(PREF_JUNG, 0,
			PREF_JUNG.length);

	public static final int HANGUL_NONE = -1;
	public static final int HANGUL_JA = 0;
	public static final int HANGUL_MO = 1;

	public static final int HANGUL_CHO1 = 0;
	public static final int HANGUL_CHO2 = 1;
	public static final int HANGUL_JUNG1 = 2;
	public static final int HANGUL_JUNG2 = 3;
	public static final int HANGUL_JONG1 = 4;
	public static final int HANGUL_JONG2 = 5;
	public static final int HANGUL_FINISH1 = 6;
	public static final int HANGUL_FINISH2 = 7;
	public static final int HANGUL_FINISH3 = 8;
	public static final int HANGUL_FINISH4 = 9;

	private int mCurrentState = HANGUL_NONE;
	private int mHangulCharBuffer[] = new int[3];

	private int mWorkingChar;

	public HangulAutomata() {
		reset();
	}

	public void reset() {
		mCurrentState = HANGUL_NONE;
		Arrays.fill(mHangulCharBuffer, -1);
		mWorkingChar = -1;
	}

	public int getBuffer() {
		return mWorkingChar;
	}

	public int getState() {
		return mCurrentState;
	}

	public int[] appendCharacter(int primaryCode) {

		int[] codes = { primaryCode };
		String strPrimaryCode = new String(codes, 0, 1);
		CharSequence cPrimaryCode = strPrimaryCode.subSequence(0,
				strPrimaryCode.length());

		int c = HANGUL_NONE;

		if (mChoseong.contains(cPrimaryCode))
			c = HANGUL_JA;
		else if (mJungseong.contains(cPrimaryCode))
			c = HANGUL_MO;
		else
			return saveUnknownCharacter(mCurrentState, primaryCode);

		switch (mCurrentState) {
		case HANGUL_NONE:
			if (c == HANGUL_JA)
				mCurrentState = HANGUL_CHO1;
			else
				mCurrentState = HANGUL_JUNG1;
			break;
		case HANGUL_CHO1:
			if (c == HANGUL_JA) {
				if (getJongseongPair(mHangulCharBuffer[0], primaryCode) >= 0)
					mCurrentState = HANGUL_CHO2;
				else
					mCurrentState = HANGUL_FINISH1;
			} else
				mCurrentState = HANGUL_JUNG1;
			break;
		case HANGUL_CHO2:
			if (c == HANGUL_JA)
				mCurrentState = HANGUL_FINISH1;
			else
				mCurrentState = HANGUL_JUNG1;
			break;
		case HANGUL_JUNG1:

			if (c == HANGUL_JA) {
				if (primaryCode == 12611
						|| /* primaryCode == 12614 || */primaryCode == 12617)
					mCurrentState = HANGUL_FINISH2;
				else
					mCurrentState = HANGUL_JONG1;

			} else {
				if (getJungseongPair(mHangulCharBuffer[1], primaryCode) >= 0)
					mCurrentState = HANGUL_JUNG2;
				else
					mCurrentState = HANGUL_FINISH3;
			}
			break;
		case HANGUL_JUNG2:
			if (c == HANGUL_JA)
				mCurrentState = HANGUL_JONG1;
			else
				mCurrentState = HANGUL_FINISH3;
			break;
		case HANGUL_JONG1:
			if (c == HANGUL_JA) {

				if (getJongseongPair(mHangulCharBuffer[2], primaryCode) >= 0)
					mCurrentState = HANGUL_JONG2;
				else
					mCurrentState = HANGUL_FINISH1;
			} else
				mCurrentState = HANGUL_FINISH4;
			break;
		case HANGUL_JONG2:
			if (c == HANGUL_JA)
				mCurrentState = HANGUL_FINISH1;
			else
				mCurrentState = HANGUL_FINISH4;
			break;
		default:
			break;
		}

		return saveCharacter(mCurrentState, primaryCode);
	}

	public void setBuffer(int primaryCode) {
		reset();
		if (primaryCode >= 0X3131 && primaryCode <= 0X3136) {
			int[] codes = { primaryCode };
			String strPrimaryCode = new String(codes, 0, 1);
			CharSequence cPrimaryCode = strPrimaryCode.subSequence(0,
					strPrimaryCode.length());

			if (mChoseong.contains(cPrimaryCode)) {
				mCurrentState = HANGUL_CHO1;
				mHangulCharBuffer[0] = primaryCode;
			} else if (mJungseong.contains(cPrimaryCode)) {
				if (isJungseongPair(primaryCode))
					mCurrentState = HANGUL_JUNG1;
				else
					mCurrentState = HANGUL_JUNG2;
				mHangulCharBuffer[1] = primaryCode;
			} else {
				mCurrentState = HANGUL_CHO2;
				mHangulCharBuffer[2] = primaryCode;
			}
		} else if (primaryCode >= 0XAC00 && primaryCode <= 0XD7A3) {
			int c;
			int value = primaryCode - 0xAC00;
			int divJong = value / 28;
			c = value % 28;
			if (c > 0)
				mHangulCharBuffer[2] = PREF_JONG[c - 1];
			else
				mHangulCharBuffer[2] = -1;

			int divJung = divJong / 21;
			c = divJong % 21;
			mHangulCharBuffer[1] = PREF_JUNG[c];

			c = divJung % 19;
			mHangulCharBuffer[0] = PREF_CHO[c];
			if (-1 != mHangulCharBuffer[2]) {
				if (isJongseongPair(mHangulCharBuffer[2]))
					mCurrentState = HANGUL_JONG2;
				else
					mCurrentState = HANGUL_JONG1;
			} else if (-1 != mHangulCharBuffer[1]) {
				if (isJungseongPair(mHangulCharBuffer[1]))
					mCurrentState = HANGUL_JUNG2;
				else
					mCurrentState = HANGUL_JUNG1;
			}
		}
	}

	public int deleteCharacter() {

		switch (mCurrentState) {
		case HANGUL_CHO1:
			mCurrentState = HANGUL_NONE;
			mHangulCharBuffer[0] = -1;
			mWorkingChar = -1;
			break;
		case HANGUL_CHO2:
			if (isJongseongPair(mHangulCharBuffer[0])) {
				mCurrentState = HANGUL_CHO1;
				int[] cho = resolveJongseongPair(mHangulCharBuffer[0]);
				mWorkingChar = mHangulCharBuffer[0] = cho[0];
			} else {
				mCurrentState = HANGUL_NONE;
				mHangulCharBuffer[0] = -1;
				mWorkingChar = -1;
			}
			break;
		case HANGUL_JUNG1:
			mHangulCharBuffer[1] = -1;
			if (-1 != mHangulCharBuffer[0]) {
				mCurrentState = HANGUL_CHO1;
				mWorkingChar = mHangulCharBuffer[0];
			} else {
				mCurrentState = HANGUL_NONE;
				mWorkingChar = -1;
			}
			break;
		case HANGUL_JUNG2:
			if (isJungseongPair(mHangulCharBuffer[1])) {
				mCurrentState = HANGUL_JUNG1;

				int jung[] = resolveJungseongPair(mHangulCharBuffer[1]);
				mHangulCharBuffer[1] = jung[0];
				if (-1 != mHangulCharBuffer[0])
					mWorkingChar = makeHangul(mHangulCharBuffer);
				else
					mWorkingChar = mHangulCharBuffer[1];
			} else {
				if (-1 != mHangulCharBuffer[0])
					mCurrentState = HANGUL_CHO1;
				else
					mCurrentState = HANGUL_NONE;
				mHangulCharBuffer[1] = -1;
				mWorkingChar = mHangulCharBuffer[0];
			}
			break;
		case HANGUL_JONG1:
			if (isJungseongPair(mHangulCharBuffer[1]))
				mCurrentState = HANGUL_JUNG2;
			else
				mCurrentState = HANGUL_JUNG1;
			mHangulCharBuffer[2] = -1;
			mWorkingChar = makeHangul(mHangulCharBuffer);
			break;
		case HANGUL_JONG2:
			if (isJongseongPair(mHangulCharBuffer[2])) {
				mCurrentState = HANGUL_JONG1;
				int[] jong = resolveJongseongPair(mHangulCharBuffer[2]);
				mHangulCharBuffer[2] = jong[0];
				mWorkingChar = makeHangul(mHangulCharBuffer);
			} else {
				if (isJungseongPair(mHangulCharBuffer[1]))
					mCurrentState = HANGUL_JUNG2;
				else
					mCurrentState = HANGUL_JUNG1;
				mHangulCharBuffer[2] = -1;
				mWorkingChar = makeHangul(mHangulCharBuffer);
			}
			break;
		case HANGUL_NONE:
		default:
			break;
		}

		return mWorkingChar;
	}

	private int makeHangul(int[] buffer) {
		if (buffer.length != 3)
			return -1;

		return 0xAC00 + getChoseongIndex(buffer[0]) * 588
				+ getJungseongIndex(buffer[1]) * 28
				+ ((-1 != buffer[2]) ? (getJongseongIndex(buffer[2]) + 1) : 0);
	}

	private int[] saveCharacter(int state, int primaryCode) {
		int completedChar = -1;

		switch (state) {
		case HANGUL_CHO1:
			mHangulCharBuffer[0] = primaryCode;
			mWorkingChar = mHangulCharBuffer[0];
			break;
		case HANGUL_CHO2:
			mHangulCharBuffer[0] = getJongseongPair(mHangulCharBuffer[0],
					primaryCode);
			mWorkingChar = mHangulCharBuffer[0];
			break;
		case HANGUL_JUNG1:
			mHangulCharBuffer[1] = primaryCode;
			if (-1 != mHangulCharBuffer[0]) {
				if (isJongseongPair(mHangulCharBuffer[0])) {
					int[] cho = resolveJongseongPair(mHangulCharBuffer[0]);
					mHangulCharBuffer[0] = cho[1];
					completedChar = cho[0];
				}
				mWorkingChar = makeHangul(mHangulCharBuffer);
			} else
				mWorkingChar = mHangulCharBuffer[1];
			break;
		case HANGUL_JUNG2:
			mHangulCharBuffer[1] = getJungseongPair(mHangulCharBuffer[1],
					primaryCode);
			if (-1 != mHangulCharBuffer[0])
				mWorkingChar = makeHangul(mHangulCharBuffer);
			else
				mWorkingChar = mHangulCharBuffer[1];
			break;
		case HANGUL_JONG1:

			mHangulCharBuffer[2] = primaryCode;
			if (-1 != mHangulCharBuffer[0] && -1 != mHangulCharBuffer[1])
				mWorkingChar = makeHangul(mHangulCharBuffer);
			else {
				mCurrentState = HANGUL_CHO1;
				completedChar = mHangulCharBuffer[1];
				mHangulCharBuffer[0] = mHangulCharBuffer[2];
				mHangulCharBuffer[1] = -1;
				mHangulCharBuffer[2] = -1;
				mWorkingChar = mHangulCharBuffer[0];
			}
			break;
		case HANGUL_JONG2:
			mHangulCharBuffer[2] = getJongseongPair(mHangulCharBuffer[2],
					primaryCode);
			if (-1 != mHangulCharBuffer[0] && -1 != mHangulCharBuffer[1]) {
				mWorkingChar = makeHangul(mHangulCharBuffer);
			} else {
				mCurrentState = HANGUL_CHO2;
				completedChar = mHangulCharBuffer[1];
				mHangulCharBuffer[0] = mHangulCharBuffer[2];
				mHangulCharBuffer[1] = -1;
				mHangulCharBuffer[2] = -1;
				mWorkingChar = mHangulCharBuffer[0];
			}
			break;
		case HANGUL_FINISH1:
			mCurrentState = HANGUL_CHO1;
			if (-1 != mHangulCharBuffer[1])
				completedChar = makeHangul(mHangulCharBuffer);
			else
				completedChar = mHangulCharBuffer[0];
			mHangulCharBuffer[0] = primaryCode;
			mHangulCharBuffer[1] = -1;
			mHangulCharBuffer[2] = -1;
			mWorkingChar = mHangulCharBuffer[0];
			break;
		case HANGUL_FINISH2:
			mCurrentState = HANGUL_CHO1;
			if (-1 != mHangulCharBuffer[0] && -1 != mHangulCharBuffer[1])
				completedChar = makeHangul(mHangulCharBuffer);
			else
				completedChar = mHangulCharBuffer[1];
			mHangulCharBuffer[0] = primaryCode;
			mHangulCharBuffer[1] = -1;
			mHangulCharBuffer[2] = -1;
			mWorkingChar = mHangulCharBuffer[0];
			break;
		case HANGUL_FINISH3:
			mCurrentState = HANGUL_JUNG1;
			if (-1 == mHangulCharBuffer[0])
				completedChar = mHangulCharBuffer[1];
			else
				completedChar = makeHangul(mHangulCharBuffer);
			mHangulCharBuffer[0] = -1;
			mHangulCharBuffer[1] = primaryCode;
			mHangulCharBuffer[2] = -1;
			mWorkingChar = mHangulCharBuffer[1];
			break;
		case HANGUL_FINISH4:
			if (isJongseongPair(mHangulCharBuffer[2])) {
				mCurrentState = HANGUL_JUNG1;

				int[] jong = resolveJongseongPair(mHangulCharBuffer[2]);
				if (-1 != mHangulCharBuffer[0] && -1 != mHangulCharBuffer[1]) {
					mHangulCharBuffer[2] = jong[0];
					completedChar = makeHangul(mHangulCharBuffer);
				} else
					completedChar = jong[0];

				mHangulCharBuffer[0] = jong[1];
				mHangulCharBuffer[1] = primaryCode;
				mHangulCharBuffer[2] = -1;

				mWorkingChar = makeHangul(mHangulCharBuffer);
			} else {
				mCurrentState = HANGUL_JUNG1;
				int tmp = mHangulCharBuffer[2];
				mHangulCharBuffer[2] = -1;
				completedChar = makeHangul(mHangulCharBuffer);

				mHangulCharBuffer[0] = tmp;
				mHangulCharBuffer[1] = primaryCode;
				mWorkingChar = makeHangul(mHangulCharBuffer);
			}
			break;
		default:
			break;
		}

		int[] ret = { -1, -1, -1 };
		ret[1] = completedChar;
		ret[2] = mWorkingChar;

		return ret;
	}

	private int[] saveUnknownCharacter(int state, int primaryCode) {

		int[] ret = { -1, -1, -1 };
		ret[0] = mWorkingChar;
		ret[1] = primaryCode;
		reset();

		return ret;
	}

	public static String encodingCode(int code) {

		String str = null;
		if (-1 != code) {
			int[] codes = { code };
			String c = new String(codes, 0, 1);
			try {
				str = new String(c.getBytes(), HardKeyboard.DEF_CHARSET);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else
			str = new String(" ");

		return str;
	}

	public static String encodingString(String text) {
		String str = null;
		try {
			str = new String(text.getBytes(), HardKeyboard.DEF_CHARSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	private int getChoseongIndex(int primaryCode) {
		for (int i = 0; i < PREF_CHO.length; i++) {
			if (primaryCode == PREF_CHO[i])
				return i;
		}
		return -1;
	}

	private int getJungseongIndex(int primaryCode) {
		for (int i = 0; i < PREF_JUNG.length; i++) {
			if (primaryCode == PREF_JUNG[i])
				return i;
		}

		return -1;
	}

	private int getJongseongIndex(int primaryCode) {
		for (int i = 0; i < PREF_JONG.length; i++) {
			if (primaryCode == PREF_JONG[i])
				return i;
		}

		return -1;
	}

	private static boolean isJungseongPair(int v) {
		switch (v) {
		case 12632:
		case 12633:
		case 12634:
		case 12637:
		case 12638:
		case 12639:
		case 12642:
			return true;
		default:
			return false;
		}
	}

	private static int[] resolveJungseongPair(int v) {
		int r[] = new int[2];
		switch (v) {
		case 12632:
			r[0] = 12631;
			r[1] = 12623;
			break;
		case 12633:
			r[0] = 12631;
			r[1] = 12624;
			break;
		case 12634:
			r[0] = 12631;
			r[1] = 12643;
			break;
		case 12637:
			r[0] = 12636;
			r[1] = 12627;
			break;
		case 12638:
			r[0] = 12636;
			r[1] = 12628;
			break;
		case 12639:
			r[0] = 12636;
			r[1] = 12643;
			break;
		case 12642:
			r[0] = 12641;
			r[1] = 12643;
			break;
		default:
			r[0] = -1;
			r[1] = -1;
			break;
		}

		return r;
	}

	private int getJungseongPair(int v1, int v2) {
		switch (v1) {
		case 12631:
			switch (v2) {
			case 12623:
				return 12632;
			case 12624:
				return 12633;
			case 12643:
				return 12634;
			default:
				break;
			}
			break;
		case 12636:
			switch (v2) {
			case 12627:
				return 12637;
			case 12628:
				return 12638;
			case 12643:
				return 12639;
			default:
				break;
			}
			break;
		case 12641:
			switch (v2) {
			case 12643:
				return 12642;
			default:
				break;
			}
			break;
		default:
			break;
		}
		return -1;
	}

	private static boolean isJongseongPair(int v) {
		switch (v) {
		case 12595:
		case 12597:
		case 12598:
		case 12602:
		case 12603:
		case 12604:
		case 12605:
		case 12606:
		case 12607:
		case 12608:
		case 12612:
			return true;
		default:
			return false;
		}
	}

	private static int[] resolveJongseongPair(int v) {
		int r[] = new int[2];
		switch (v) {
		case 12595:
			r[0] = 12593;
			r[1] = 12613;
			break;
		case 12597:
			r[0] = 12596;
			r[1] = 12616;
			break;
		case 12598:
			r[0] = 12596;
			r[1] = 12622;
			break;
		case 12602:
			r[0] = 12601;
			r[1] = 12593;
			break;
		case 12603:
			r[0] = 12601;
			r[1] = 12609;
			break;
		case 12604:
			r[0] = 12601;
			r[1] = 12610;
			break;
		case 12605:
			r[0] = 12601;
			r[1] = 12613;
			break;
		case 12606:
			r[0] = 12601;
			r[1] = 12620;
			break;
		case 12607:
			r[0] = 12601;
			r[1] = 12621;
			break;
		case 12608:
			r[0] = 12601;
			r[1] = 12622;
			break;
		case 12612:
			r[0] = 12610;
			r[1] = 12613;
			break;
		default:
			r[0] = -1;
			r[1] = -1;
			break;
		}

		return r;
	}

	private static int getJongseongPair(int v1, int v2) {
		switch (v1) {
		case Hangul.Jaeum.KIYEOK:
			switch (v2) {
			case Hangul.Jaeum.SIOS:
				return Hangul.Jaeum.KIYEOK_SIOS;
			default:
				break;
			}
			break;
		case Hangul.Jaeum.NIEUN:
			switch (v2) {
			case Hangul.Jaeum.CIEUC:
				return Hangul.Jaeum.NIEUN_CIEUC;
			case Hangul.Jaeum.HIEUH:
				return Hangul.Jaeum.NIEUN_HIEUH;
			default:
				break;
			}
			break;
		case Hangul.Jaeum.RIEUL:
			switch (v2) {
			case Hangul.Jaeum.KIYEOK:
				return Hangul.Jaeum.RIEUL_KIYEOK;
			case 12609:
				return 12603;
			case 12610:
				return 12604;
			case 12613:
				return 12605;
			case 12620:
				return 12606;
			case 12621:
				return 12607;
			case 12622:
				return 12608;
			default:
				break;
			}
			break;
		case 12610:
			switch (v2) {
			case 12613:
				return 12612;
			default:
				break;
			}
			break;
		default:
			break;
		}

		return -1;
	}

	public static String encode(String text) {
		String out = new String();

		if (null != text && text.length() > 0) {
			HangulAutomata ha = new HangulAutomata();

			int[] ret;
			for (int i = 0; i < text.length(); i++) {
				ret = ha.appendCharacter((int) text.charAt(i));
				if (-1 != ret[0])
					out += (char) ret[0];
				if (-1 != ret[1])
					out += (char) ret[1];

			}

			int c = ha.getBuffer();
			if (-1 != c)
				out += (char) c;
		}
		return out;
	}

	public static String decode(String text) {
		String out = new String();
		int[] buffer = new int[5];
		char c;

		for (int i = 0; i < text.length(); i++) {
			c = text.charAt(i);
			if (c >= 0X3131 && c <= 0X3163) {
				if (isJongseongPair((int) c)) {
					int[] jong = resolveJongseongPair(c);
					out += (char) jong[0];
					out += (char) jong[1];
				} else if (isJungseongPair((int) c)) {
					int[] jung = resolveJungseongPair(c);
					out += (char) jung[0];
					out += (char) jung[1];
				} else
					out += c;
			} else if (c >= 0XAC00 && c <= 0XD7A3) {
				Arrays.fill(buffer, -1);
				int n;
				int value = c - 0xAC00;
				int divJong = value / 28;
				n = value % 28;
				if (n > 0)
					buffer[3] = PREF_JONG[n - 1];

				int divJung = divJong / 21;
				n = divJong % 21;
				buffer[1] = PREF_JUNG[n];

				n = divJung % 19;
				buffer[0] = PREF_CHO[n];

				if (-1 != buffer[3]) {
					if (isJongseongPair(buffer[3])) {
						int[] jong = resolveJongseongPair(buffer[3]);
						buffer[3] = jong[0];
						buffer[4] = jong[1];
					}
				}
				if (-1 != buffer[1]) {
					if (isJungseongPair(buffer[1])) {
						int[] jung = resolveJungseongPair(buffer[1]);
						buffer[1] = jung[0];
						buffer[2] = jung[1];
					}
				}
				for (int j = 0; j < buffer.length; j++) {
					if (-1 != buffer[j])
						out += (char) buffer[j];
				}
			} else
				out += c;
		}

		return out;
	}

	public static int countCharacter(char c) {
		if (c >= 0X3131 && c <= 0X3163) {
			if (isJongseongPair((int) c))
				return 2;
			else if (isJungseongPair((int) c))
				return 2;
			else
				return 1;
		} else if (c >= 0XAC00 && c <= 0XD7A3) {
			int n;
			int value = c - 0xAC00;
			int divJong = value / 28;
			int jung = -1, jong = -1;
			n = value % 28;
			if (n > 0)
				jong = PREF_JONG[n - 1];

			n = divJong % 21;
			jung = PREF_JUNG[n];

			n = 0;
			if (-1 != jong)
				n += isJongseongPair(jong) ? 2 : 1;
			if (-1 != jung)
				n += isJungseongPair(jung) ? 2 : 1;
			n++;

			return n;
		} else
			return 1;
	}

}