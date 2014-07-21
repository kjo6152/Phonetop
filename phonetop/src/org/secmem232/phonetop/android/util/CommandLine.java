package org.secmem232.phonetop.android.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

/**
 * Contains method for Command-line command execution.
 * 
 * @author Taeho Kim
 * 
 */
public class CommandLine {
	/**
	 * Determine this device has root permission or not.
	 * 
	 * @return true if device has rooted, false otherwise
	 */
	public static boolean isRootAccessAvailable() {
		boolean result = false;
		Process suProcess;

		try {
			suProcess = Runtime.getRuntime().exec("su");
			DataOutputStream outStream = new DataOutputStream(
					suProcess.getOutputStream());
			DataInputStream inStream = new DataInputStream(
					suProcess.getInputStream());
			BufferedReader inReader = new BufferedReader(new InputStreamReader(
					inStream));

			if (null != outStream && null != inStream) {
				// Getting the id of the current user to check if this is root
				outStream.writeBytes("id\n");
				outStream.flush();

				String currUid = inReader.readLine();
				boolean exitSu = false;

				if (currUid == null) {
					result = false;
					exitSu = false;
					Log.d("ROOT", "Can't get root access or denied by user");
				} else if (currUid.contains("uid=0")) {
					result = true;
					exitSu = true;
					Log.d("ROOT", "Root access granted");
				} else {
					result = false;
					exitSu = true;
					Log.d("ROOT", "Root access rejected: " + currUid);
				}

				if (exitSu) {
					outStream.writeBytes("exit\n");
					outStream.flush();
				}
			}
		} catch (Exception e) {
			// Can't get root !
			// Probably broken pipe exception on trying to write to output
			// stream after su failed, meaning that the device is not rooted
			result = false;
			Log.d("ROOT", "Root access rejected [" + e.getClass().getName()
					+ "] : " + e.getMessage());
		}
		return result;
	}

	/**
	 * Execute command as root.
	 * 
	 * @param cmd
	 *            Command to be executed as ROOT
	 * @return true if execution succeed, false otherwise
	 */
	public static boolean execAsRoot(String cmd) {
		if (cmd == null || cmd.equals(""))
			throw new IllegalArgumentException();

		boolean retval = false;

		try {
			Process suProcess = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(
					suProcess.getOutputStream());

			os.writeBytes(cmd + "\n");
			os.flush();
			os.writeBytes("exit\n");
			os.flush();

			try {
				int suProcessRetval = suProcess.waitFor();
				if (255 != suProcessRetval) {
					// Root access granted
					retval = true;
				} else {
					// Root access denied
					retval = false;
				}
			} catch (Exception ex) {
				Log.e("Error executing root action", ex.toString());
			}
		} catch (IOException ex) {
			Log.w("ROOT", "Can't get root access", ex);
		} catch (SecurityException ex) {
			Log.w("ROOT", "Can't get root access", ex);
		} catch (Exception ex) {
			Log.w("ROOT", "Error executing internal operation", ex);
		}
		return retval;
	}

	/**
	 * Execute list of commands.
	 * 
	 * @param cmds
	 *            list of commands to be executed as ROOT
	 * @return true execution succeed, false otherwise
	 */
	public static boolean execAsRoot(ArrayList<String> cmds) {
		if (cmds == null || cmds.size() == 0)
			throw new IllegalArgumentException();

		boolean retval = false;
		try {
			Process suProcess = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(
					suProcess.getOutputStream());
			for (String cmd : cmds) {
				os.writeBytes(cmd + "\n");
				os.flush();
			}
			os.writeBytes("exit\n");
			os.flush();
			try {
				int suProcessRetval = suProcess.waitFor();
				if (255 != suProcessRetval) {
					// Root access granted
					retval = true;
				} else {
					// Root access denied
					retval = false;
				}
			} catch (Exception ex) {
				Log.e("Error executing root action", ex.toString());
			}

		} catch (IOException ex) {
			Log.w("ROOT", "Can't get root access", ex);
		} catch (SecurityException ex) {
			Log.w("ROOT", "Can't get root access", ex);
		} catch (Exception ex) {
			Log.w("ROOT", "Error executing internal operation", ex);
		}

		return retval;
	}

	public static boolean isDriverExists(Context context) {
		File file;
		try {
			// Check IDC
			file = new File("/system/usr/idc/passu.idc");
			if (!file.exists())
				return false;
			else
				return true;
		} catch (Exception e) {
			return false;
		}
	}
}
