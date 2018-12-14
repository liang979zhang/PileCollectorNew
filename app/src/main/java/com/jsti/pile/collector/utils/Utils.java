package com.jsti.pile.collector.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Utils {
	private Utils() {
	}

	public static void checkVisibility(View view, int visibility) {
		if (view == null) {
			return;
		}
		if (view.getVisibility() != visibility) {
			view.setVisibility(visibility);
		}
	}

	public static boolean intToBool(int value) {
		return value == 0;
	}

	public static int boolToInt(boolean value) {
		return value ? 0 : 1;
	}

	public static String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			try {
				digest.update(s.getBytes("UTF8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			byte[] messageDigest = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String hex = Integer.toHexString(0xff & messageDigest[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	//
	public static boolean isAppOnForeground(Context context, ActivityManager activityMannager) {
		if (context == null) {
			return false;
		}
		String packageName = context.getPackageName();
		if (packageName == null) {
			return false;
		}
		if (activityMannager == null) {
			activityMannager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			if (activityMannager == null) {
				return false;
			}
		}
		// Returns a list of application processes that are running on the
		// device
		List<RunningAppProcessInfo> appProcesses = activityMannager.getRunningAppProcesses();
		if (appProcesses == null || appProcesses.isEmpty())
			return false;
		for (RunningAppProcessInfo appProcess : appProcesses) {
			// importance:
			// The relative importance level that the system places
			// on this process.
			// May be one of IMPORTANCE_FOREGROUND, IMPORTANCE_VISIBLE,
			// IMPORTANCE_SERVICE, IMPORTANCE_BACKGROUND, or IMPORTANCE_EMPTY.
			// These constants are numbered so that "more important" values are
			// always smaller than "less important" values.
			// processName:
			// The name of the process that this object is associated with.
			if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
					&& appProcess.processName.equals(packageName)) {
				return true;
			}
		}
		return false;
	}

	public static String getAppVersionName(Context context) {
		String versionname = "0";
		PackageManager pm = context.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionname = pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionname;
	}

	public static void toast(Context context, int msgResId) {
		toast(context, context.getString(msgResId));
	}

	public static void toast(Context context, String msg) {
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, -150);
		toast.show();
	}

	public static boolean isSDCardMounted() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	public static String fileToBase64String(File file) {
		if (file == null || !file.exists() || !file.isFile()) {
			return null;
		}
		String result = null;
		try {
			byte[] bytes = new byte[(int) file.length()];
			FileInputStream is = null;
			try {
				is = new FileInputStream(file);
				is.read(bytes);
				result = Base64.encodeToString(bytes, Base64.DEFAULT);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.gc();
		}
		return result;
	}

	public static void exportToCSV(Cursor c, String fileName) {

		int rowCount = 0;
		int colCount = 0;
		FileWriter fw;
		BufferedWriter bfw;
		File sdCardDir = Environment.getExternalStorageDirectory();
		File saveFile = new File(sdCardDir, fileName);
		try {

			rowCount = c.getCount();
			colCount = c.getColumnCount();
			fw = new FileWriter(saveFile);
			bfw = new BufferedWriter(fw);
			if (rowCount > 0) {
				c.moveToFirst();
				// 写入表头
				for (int i = 0; i < colCount; i++) {
					if (i != colCount - 1)
						bfw.write(c.getColumnName(i) + ',');
					else
						bfw.write(c.getColumnName(i));
				}
				// 写好表头后换行
				bfw.newLine();
				// 写入数据
				for (int i = 0; i < rowCount; i++) {
					c.moveToPosition(i);
					// Toast.makeText(mContext, "正在导出第"+(i+1)+"条",
					// Toast.LENGTH_SHORT).show();
					Log.v("导出数据", "正在导出第" + (i + 1) + "条");
					for (int j = 0; j < colCount; j++) {
						if (j != colCount - 1)
							bfw.write(c.getString(j) + ',');
						else
							bfw.write(c.getString(j));
					}
					// 写好每条记录后换行
					bfw.newLine();
				}
			}
			// 将缓存数据写入文件
			bfw.flush();
			// 释放缓存
			bfw.close();
			// Toast.makeText(mContext, "导出完毕！", Toast.LENGTH_SHORT).show();
			Log.v("导出数据", "导出完毕！");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			c.close();
		}
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			return cm.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}

	private static long lastBackTime;

	public static void onBackPressedCheckExit(Activity act) {
		if (System.currentTimeMillis() - lastBackTime < 1500) {
			act.finish();
		} else {
			lastBackTime = System.currentTimeMillis();
			toast(act, "再按一次退出");
		}
	}

	public static String formatServerTime(long time) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(time * 1000));
	}

	public static String linkArray(String[] array, String splitor) {
		StringBuilder sb = new StringBuilder();
		for (String s : array) {
			sb.append(s);
			sb.append(splitor);
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public static Integer toInteger(EditText ed, Integer def) {
		return toInteger(ed.getText().toString(), def);
	}

	public static Float toFloat(EditText ed, Float def) {
		return toFloat(ed.getText().toString(), def);
	}

	public static Integer toInteger(String str, Integer def) {
		try {
			return Integer.parseInt(str.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return def;
		}
	}

	public static Float toFloat(String str, Float def) {
		try {
			return Float.parseFloat(str);
		} catch (Exception e) {
			e.printStackTrace();
			return def;
		}
	}

	public static boolean isIntStyle(String str) {
		return str.matches("^[0-9]+$");
	}

	public static boolean isFloatStyle(String str) {
		return str.matches("^[0-9]*.?[0-9]*$");
	}

	public static long currentTimeSeconds() {
		return System.currentTimeMillis() / 1000;
	}
}