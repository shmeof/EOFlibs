package com.eof.libs.base.debug;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.format.DateFormat;

import com.eof.libs.base.GlobalData;
import com.eof.libs.base.dao.DaoConst;
import com.eof.libs.base.dao.sp.SpManager;
import com.eof.libs.base.utils.Base64Util;
import com.eof.libs.base.utils.FileUtil;
import com.eof.libs.base.utils.TextUtil;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 日志工具
 */
public final class Log {
	
	public static final boolean IS_LOG_MOD = true; // 是否开启日志
	public static final boolean ERR_TO_FILE = false; // 内部版本才写日志

	public static final String TAG = "Log";

	public static final int LEVEL_WRITE_ALL = 0;
	public static final int LEVEL_DEBUG = 1;
	public static final int LEVEL_INFO = 2;
	public static final int LEVEL_WARN = 3;
	public static final int LEVEL_ERROR = 4;

	/**
	 * 默认的打印级别，可以修改后面的参数, 假如设置成LEVEL_RECORDALL，则写所有日志到文件， 默认打印warn和error的日志到文件
	 * LOG_LEVEL表示打印大于等于该基本的日志
	 */
	public static int LOG_LEVEL = LEVEL_DEBUG;

	public static final String CRASH_REPORTER_SD_DIR = "/log/zyj/";
	
	private static final String CRASH_REPORTER_FILE_NAME = "zyj_";

	public static final String FORMATSTR_FOR_LOG_FILE = "MMddkk";

	private static final String VERSION_NAME = "versionName";

	private static final String VERSION_CODE = "versionCode";

	private static Ilog instance;
	static {
		if (IS_LOG_MOD) {
			instance = new WithLog(LOG_LEVEL);
			Log.d("Log", "IS_LOG_MOD WithLog");
		} else {
			instance = new WithOutLog();
			Log.d("Log", "IS_LOG_MOD WithOutLog");
		}
	}

	/**
	 * 设置日志是否开启
	 * 
	 * @param withLog
	 *            true，把日志打印到文件
	 */
	public static void setWithLog(boolean withLog) {
		if (withLog) {
			instance = new WithLog(LEVEL_DEBUG);
		} else {
			instance = new WithOutLog();
		}
	}
	
	public static void setLevel(int level) {
		instance.setLevel(level);
	}

	/**
	 * 打印出错信息，异常对象可以用此方法打印，如Log.e(TAG,e);
	 * 
	 * @param tag
	 * @param datas
	 */
	public static void e(String tag, Object... datas) {
		instance.e(tag, datas);
	}

	/**
	 * 打印警告信息，异常对象可以用此方法打印，如Log.w(TAG,e);
	 * 
	 * @param tag
	 * @param datas
	 */
	public static void w(String tag, Object... datas) {
		instance.w(tag, datas);
	}

	public static void i(String tag, Object... datas) {
		instance.i(tag, datas);
	}

	public static void d(String tag, Object... datas) {
		instance.d(tag, datas);
	}

	public static void writeFile(String filename, String content) {
		instance.writeFile(filename, content);
	}
	
	/**
	 * 跟踪方法调用堆栈
	 * @param tag
	 *
	 */
	public static void tracer(String tag){
		instance.tracer(tag);
	}

	/**
	 * @param ex
	 */
	public static void saveCrashInfoToSdCard(Context context, String tag, Throwable ex) {
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		ex.printStackTrace(printWriter);

		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}

		String result = info.toString();
		result = deviceInfo2String(collectCrashDeviceInfo(context)) + result + "------------------\r\n";
		printWriter.close();
		Date date = new Date();

		File file = new File(FileUtil.getSDCardDir() + CRASH_REPORTER_SD_DIR + CRASH_REPORTER_FILE_NAME
				+ date2String(date, FORMATSTR_FOR_LOG_FILE) + ".log");
		printCrashInfo(context, tag, ex);
		try {
			FileUtil.write(file, new StringBuffer(result));
		} catch (IOException e) {
		}
	}

	private static String emptyIfNull(String str) {
		if (null == str) {
			str = "";
		}
		return str;
	}
	
	
	/**
	 * 根据日期获取对应的crash log文件名
	 * @param date
	 * @return
	 */
	public static String getCrashLogFilePath(final Date date) {
		return FileUtil.getSDCardDir() + CRASH_REPORTER_SD_DIR + getCrashLogFileName(date);
	}

	public static String getCrashLogFileName(final Date date) {
		return CRASH_REPORTER_FILE_NAME + date2String(date, FORMATSTR_FOR_LOG_FILE) + ".log";
	}
	
	/**
	 * @param context
	 * @param tag
	 * @param result
	 */
	public static void saveInfoToSdCard(Context context, String tag, String result) {
		result = date2String(System.currentTimeMillis(), "yyyy-MM-dd kk:mm:ss") + ": "
				+ emptyIfNull(tag) + ":" + result + "\r\n";
		Date date = new Date();
		String path = getCrashLogFilePath(date);
		File file = new File(path);
		try {
			if (!FileUtil.isFileExist(path)) { // 首次文件写入
				String perhapsHave = SpManager.getInstance().getSPer(DaoConst.SpName.SP_GUID).getString(DaoConst.GUID.GUID_UUID, "");
				String userIdEncode = SpManager.getInstance().getSPer(DaoConst.SpName.SP_USID).getString(DaoConst.USID.USER_ID, "");
				String userIdDecode = userIdEncode;
				if (!TextUtil.isNullOrEmptyWithoutTrim(userIdDecode)) { // base64
					userIdDecode = new String(Base64Util.decode(userIdDecode));
				}
				String usid = userIdDecode;
				FileUtil.write(file, new StringBuffer(("guid: " + perhapsHave + "\n")));
				FileUtil.write(file, new StringBuffer(("usid: " + usid + "\n\n")));
			}

			FileUtil.write(file, new StringBuffer(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printCrashInfo(Context context, String tag, Throwable ex) {
		if (ex == null) {
			return;
		}
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		ex.printStackTrace(printWriter);

		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}

		String result = info.toString();
		android.util.Log.e(tag, result);

	}

	private static String deviceInfo2String(Map<String, Object> deviceCrashInfo) {
		String info = "-------------------------------------\n";
		Iterator<String> iter = deviceCrashInfo.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			info += key + ":" + deviceCrashInfo.get(key) + "\n";

		}
		return info;
	}

	/**
	 * 收集程序崩溃的设备信息
	 * 
	 * @param context
	 */
	private static Map<String, Object> collectCrashDeviceInfo(Context context) {
		Map<String, Object> deviceCrashInfo = new HashMap<String, Object>();
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				deviceCrashInfo.put(VERSION_NAME, pi.versionName == null ? "not set"
						: pi.versionName);
				deviceCrashInfo.put(VERSION_CODE, pi.versionCode);
			}
		} catch (Exception e) {

		}
		// 使用反射来收集设备信息.在Build类中包含各种设备信息,
		// 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
		// 具体信息请参考后面的截图
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				deviceCrashInfo.put(field.getName(), field.get(null));
			} catch (Exception e) {
			}
		}
		return deviceCrashInfo;

	}

	public static String converArrayToString(String tag, Object[] objects) {
		try {
			if (objects == null || objects.length == 0) {
				return "";
			} else {
				StringBuffer buffer = new StringBuffer();
				int size = objects.length;
				for (int i = 0; i < size; i++) {
					Object item = objects[i];
					if (item != null) {
						
						if (i != 0) { buffer.append("|"); }
						 
						try {
							buffer.append(item.toString());
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
				return buffer.toString();
			}
		} catch (Throwable t) {
			android.util.Log.e(tag, "converArrayToString t: " + t.toString());
			return "converArrayToString null";
		}

	}

	/**
	 * 日期格式化
	 * 
	 * @param date
	 *            时期
	 * @param pattern
	 *            格式化形式
	 * @return 格式化结果
	 */
	public static String date2String(Date date, String pattern) {
		if (date == null) {
			return null;
		}
		return DateFormat.format(pattern, date).toString();
	}

	public static String date2String(long timeStamp, String pattern) {
		return DateFormat.format(pattern, timeStamp).toString();
	}

}

/**
 * 打日志到Logcat
 * 
 */
class WithLog implements Ilog {

	private int mLevel = Log.LEVEL_WRITE_ALL; // 值越小，打印的日志越多

	public static Set<String> filterLogTagSet = new HashSet<String>();

	// 这里是打印到文件指定log的tag集合，可以自由修改，如何LOG_LEVEL是LEVEL_RECORDALL，则这个参数无效
	private static String[] filterLogTagArray = {/* Log.TAG_MICROMSG *//*
												 * HiAttachmentAssembler
												 * .TAG,
												 * TAG_MICROMSG_NETWORK2
												 */};

	public WithLog() {
	}

	public WithLog(int level) {
		mLevel = level;
		for (String tag_item : filterLogTagArray) {
			filterLogTagSet.add(tag_item);
		}
		filterLogTagArray = null;
	}
	
	public void setLevel(int level) {
		mLevel = level;
	}

	@Override
	public void d(String tag, Object... datas) {
		if (mLevel <= Log.LEVEL_DEBUG) {
			if (datas != null) {
				if (mLevel == Log.LEVEL_WRITE_ALL || filterLogTagSet.contains(tag)) {
					recordException(tag, datas);
				}
				android.util.Log.d(tag, Log.converArrayToString(tag, datas));
			}
		}
	}

	@Override
	public void i(String tag, Object... datas) {
		if (mLevel <= Log.LEVEL_INFO) {
			if (datas != null) {
				if (mLevel == Log.LEVEL_WRITE_ALL || filterLogTagSet.contains(tag)) {
					recordException(tag, datas);
				}
				android.util.Log.i(tag, Log.converArrayToString(tag, datas));
			}
		}
	}

	@Override
	public void w(String tag, Object... datas) {
		if (mLevel <= Log.LEVEL_WARN) {
			if (datas != null) {
				recordException(tag, datas);
				android.util.Log.w(tag, Log.converArrayToString(tag, datas));
			}
		}
	}

	@Override
	public void e(String tag, Object... datas) {
		if (mLevel <= Log.LEVEL_ERROR) {
			if (datas != null) {
				recordException(tag, datas);
				android.util.Log.e(tag, Log.converArrayToString(tag, datas));
			}
		}
	}
	
	private void recordException(String tag, Object[] datas) {
		try {
			if (datas == null || datas.length == 0) {
				android.util.Log.e(tag, "log message is null");
			} else if (datas[0] instanceof Throwable) {
				Log.saveCrashInfoToSdCard(GlobalData.mApplicationContext, tag,
						(Throwable) datas[0]);
			} else {
				if (Log.ERR_TO_FILE) {
					Log.saveInfoToSdCard(GlobalData.mApplicationContext, tag,
							Log.converArrayToString(tag, datas));
				}
			}
		} catch (Throwable t) {
			android.util.Log.e(tag, "recordException t: " + t.toString());
		}
	}

	@Override
	public void writeFile(String filename, String content) {
		content = Log.date2String(System.currentTimeMillis(), "yyyy-MM-dd kk:mm:ss") + ":"
				+ content + "\r\n";
		File file = new File(FileUtil.getSDCardDir() + Log.CRASH_REPORTER_SD_DIR + filename + ".log");
		try {
			FileUtil.write(file, new StringBuffer(content));
		} catch (IOException e) {
		}
	}

	@Override
	public void tracer(String tag) {
		Throwable ex = new Throwable();
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		ex.printStackTrace(printWriter);
		android.util.Log.i(tag, info.toString());
		printWriter.close();
		try {
			info.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

/**
 * 不打日志
 * 
 */
class WithOutLog implements Ilog {

	@Override
	public void d(String tag, Object... datas) {
		// TODO Auto-generated method stub

	}

	@Override
	public void e(String tag, Object... datas) {
		// TODO Auto-generated method stub

	}

	@Override
	public void i(String tag, Object... datas) {
		// TODO Auto-generated method stub

	}

	@Override
	public void w(String tag, Object... datas) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeFile(String filename, String content) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tracer(String tag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLevel(int level) {
		// TODO Auto-generated method stub
		
	}

}

/**
 * 日志接口
 * 
 */
interface Ilog {
	void i(String tag, Object... datas);

	void e(String tag, Object... datas);

	void d(String tag, Object... datas);

	void w(String tag, Object... datas);

	void writeFile(String filename, String content);
	
	void tracer(String tag);

	void setLevel(int level);
}
