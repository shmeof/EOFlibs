package com.eof.libs.base.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.eof.libs.base.debug.Log;


/**
 * 手机信息工具类，提供获取手机信息的方法
 * 
 */
public final class PhoneInfoUtil {

	private static final String TAG = "PhoneInfoUtil";

	/**
	 * 没有服务提供商
	 */
	public static final int NO_NETOP = -2;
	/**
	 * 其它
	 */
	public static final int OTHER = -1;
	/**
	 * 中国移动
	 */
	public static final int CHINA_MOBILE = 0;
	/**
	 * 中国联通
	 */
	public static final int CHINA_UNICOM = 1;
	/**
	 * 中国电信
	 */
	public static final int CHINA_TELECOM = 2;

	private final static int kSystemRootStateUnknow = -1;
	private final static int kSystemRootStateDisable = 0;
	private final static int kSystemRootStateEnable = 1;
	private static int systemRootState = kSystemRootStateUnknow;

	/**
	 * 获取IMEI码
	 * 
	 * @param context
	 * 
	 * @return IMEI码
	 */
	public static String getIMEI(Context context) {
		String imei = null;
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			imei = telephonyManager.getDeviceId();
		} catch (Exception e) {
			//做为区分是不是异常时的Imei
			imei = "00000000000001";
		}
		if(null == imei){
			imei = "00000000000000";
		}
		return imei;
	}

	/**
	 * 获取IMSI码
	 * 
	 * @param context
	 * 
	 * @return IMSI码
	 * //TODO Dualsim  
	 */
	public static String getIMSI(Context context) {
		String imsi = null;
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			imsi = telephonyManager.getSubscriberId();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (null == imsi) {
			return "000000000000000";
		}
		
		return imsi;
	}

	/**
	 * 获取MSISDN码
	 * 
	 * @param context
	 * 
	 * @return MSISDN码
	 */
	public static String getMSISDN(Context context) {
		String number = null;
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			number = telephonyManager.getLine1Number();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return number;
	}

	/**
	 * 获取MAC地址
	 * 
	 * @param context
	 * @return MAC地址
	 */
	public static String getMAC(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		String mac = null;
		if (info != null) {
			mac = info.getMacAddress();
		}
		return mac;
	}

	/**
	 * 获取ICCID码，即SIM卡序列号
	 * 
	 * @param context
	 * 
	 * @return ICCID码
	 */
	public static String getICCID(Context context) {
		String number = null;
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			number = telephonyManager.getSimSerialNumber();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return number;
	}

	/**
	 * 获取AndroidId
	 * 
	 * @return AndroidId
	 */
	public static String getAndoidId() {
		return Secure.ANDROID_ID;
	}

	/**
	 * 获取设备的名称
	 * 
	 * @return 设备名称
	 */
	public static String getModelName() {
		return android.os.Build.MODEL;
	}

	/**
	 * 获取设备的开发名称或代号
	 * 
	 * @return 设备的开发名称或代号
	 */
	public static String getProductName() {
		return android.os.Build.PRODUCT;
	}

	/**
	 * 获取手机屏幕宽度
	 * 
	 * @param context
	 * 
	 * @return 屏幕宽度
	 */
	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	/**
	 * 获取手机屏幕高度
	 * 
	 * @param context
	 * 
	 * @return 屏幕高度
	 */
	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}
	
	/**
	 * 使用隐藏api获取屏幕的原始大小
	 * 包括4.0系统一般有的虚拟home键（navigation-bar）
	 * @param context
	 * @return
	 */
	public static Rect getRawScreenRect(Context context) {
		Rect ret = new Rect();
		try{
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			int rawHeight = 0;
			int rawWidth = 0;
			if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) { // 4.0及4.1反射获取
				rawHeight = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
				rawWidth = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
	        }else if (Build.VERSION.SDK_INT >= 17) {						 // 4.2及以上反射获取
	        	Point realSize = new Point();
				Display.class.getMethod("getRealSize", Point.class).invoke(display,realSize);  
				rawHeight = realSize.y;
				rawWidth = realSize.x;
	        }else {															 // 4.0以下直接使用原有接口获取
	        	rawHeight = display.getHeight();
	        	rawWidth = display.getWidth();
	        }
			ret.set(0, 0, rawWidth, rawHeight);
		} catch (Exception e){
			ret.set(0, 0, getScreenWidth(context), getScreenHeight(context));
		}
		return ret;
	}

	/**
	 * 拨打电话
	 * 
	 * @param context
	 * @param number 电话号码
	 */
	public static void Call(Context context, String number) {
		Uri telUri = Uri.parse("tel:" + number);
		Intent intent = new Intent(Intent.ACTION_DIAL, telUri);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * 获取网络运营商的代码
	 * 
	 * @param context
	 * @return 网络运营商的代码
	 * //TODO Dualsim 双卡适配
	 */
	public static int getNetworkOperatorCode(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (PhoneInfoUtil.hasSIM(context)) {
			String networdoperator = tm.getNetworkOperator();
			if (networdoperator.equals("46000") || networdoperator.equals("46002") || networdoperator.equals("46007")) {
				return PhoneInfoUtil.CHINA_MOBILE;
			} else if (networdoperator.equals("46001")) {
				return PhoneInfoUtil.CHINA_UNICOM;
			} else if (networdoperator.equals("46003")) {
				return PhoneInfoUtil.CHINA_TELECOM;
			} else if (!networdoperator.equals("")) {
				return PhoneInfoUtil.OTHER;
			} else {
				return PhoneInfoUtil.NO_NETOP;
			}
		} else {
			// int AirPlaneModeOn =
			// Settings.System.getInt(QQPimSecureApp.getContext().getContentResolver(),
			// Settings.System.AIRPLANE_MODE_ON, 0);
			// if(AirPlaneModeOn == 1){
			// Log.i("", "==FLIGHT_MODE==");
			// return PhoneInfoUtil.NO_NETOP;
			// }else{
			return PhoneInfoUtil.NO_NETOP;
			// }
		}
	}

	/**
	 * 判断是否存在SIM卡
	 * 
	 * @param context
	 * 
	 * @return 是否存在 SIM卡
	 * 
	 * //TODO Dualsim  
	 */
	public static boolean hasSIM(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimState() != TelephonyManager.SIM_STATE_ABSENT;
	}

	/**
	 * @param info
	 *            大小信息
	 * 
	 *            获取手机存储容量
	 */
	public static void getPhoneStorageSize(SizeInfo info) {
		getSizeInfo(Environment.getDataDirectory(), info);
	}

	/**
	 * 查询指定目录空间信息
	 * 
	 * @param path
	 * @param info
	 */
	public static void getSizeInfo(File path, SizeInfo info) {
		try {
			StatFs statfs = new StatFs(path.getPath());
			
			long blockSize = statfs.getBlockSize();// 获取block的SIZE
			info.availdSize = statfs.getAvailableBlocks() * blockSize;
			info.totalSize = statfs.getBlockCount() * blockSize;
		} catch (Exception e) {
			Log.e(TAG, "getSizeInfo err:" + e.getMessage(), e);
		}
	}

	/**
	 * 
	 * 大小信息
	 * 
	 */
	public static class SizeInfo {
		/**
		 * 可用大小
		 */
		public long availdSize;

		/**
		 * 总共大小
		 */
		public long totalSize;

		/**
		 * 已用百分比
		 * 
		 * @return 百分比值
		 */
		public int percent() {
			int percent = 0;
			if (totalSize > 0) {
				float hadused = (float) (totalSize - availdSize) / (float) totalSize;
				percent = (int) (hadused * 100);
			}
			return percent;
		}
	}

	/**
	 * dip转px
	 * 
	 * @param context
	 * @param dipValue
	 *            转换前的值
	 * @return 转换后的值
	 */
	public static int dip2px(Context context, float dipValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);

	}

	/**
	 * px转dip
	 * 
	 * @param context
	 * @param pxValue
	 *            转换前的值
	 * @return 转换后的值
	 */
	public static int px2dip(Context context, float pxValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	
	private static String ESCAPE_SERVICE = "tms_";
	
	private static String TELEPHONY_SERVICE = "[com.android.internal.telephony.ITelephony]";

	private static String TELEPHONY_REGISTRY = "[com.android.internal.telephony.ITelephonyRegistry]";
	
	private static Boolean sDualSimDevice = null;
	
	/**
	 * 获取设备版本增量号
	 * 即特定rom的版本号
	 * 
	 * @return 版本增量号
	 */
	public static String getVersionIncremental(){
		return android.os.Build.VERSION.INCREMENTAL;
	}
	
	/**
	 * 获取设备用户可视版本号
	 * 一般对应某一个sdk号的细分版本，如4.0.4
	 * 
	 * @return 用户可视版本号
	 */
	public static String getVersionRelease(){
		return android.os.Build.VERSION.RELEASE;
	}
	
	/**
	 * 获取设备品牌名
	 * 
	 * @return 设备的品牌名
	 */
	public static String getBrand() {
		return android.os.Build.BRAND;
	}
	
	/**
	 * 获取设备
	 */
	public static String getDevice() {
		return android.os.Build.DEVICE;
	}

	/**
	 * 获取平台
	 */
	public static String getBoard() {
		return android.os.Build.BOARD;
	}

	/**
	 * 获取运营商
	 * @param context
	 * @return
	 *
	 */
	public static int getOper(Context context) {
		String imsi = getIMSI(context);
		int mmcMnc = SimUtil.getMccMncType(imsi);
		return mmcMnc;
	}
	

	public static String getKernelVersion() {
		String kernelVersion = "";
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream("/proc/version");
		} catch (Throwable e) {
			Log.e(TAG , e);
			return kernelVersion;
		}
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream), 8 * 1024);
		String info = "";
		String line = "";
		try {
			while ((line = bufferedReader.readLine()) != null) {
				info += line;
			}
		} catch (Throwable e) {
			Log.e(TAG , e);
		} finally {
			if(bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (Throwable e) {
					Log.e(TAG , e);
				}
			}
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch (Throwable e) {
					Log.e(TAG , e);
				}
			}
		}
		try {
			if (info != "") {
				final String keyword = "version ";
				int index = info.indexOf(keyword);
				line = info.substring(index + keyword.length());
				index = line.indexOf(" ");
				kernelVersion = line.substring(0, index);
			}
		} catch (Throwable e) {
			Log.e(TAG , e);
		}
		return kernelVersion;
	}
	/**
	 * 获取前置摄像头像素
	 * @author josephkwok
	 */
	private static Object cameraLock = new Object();
	private static Integer frontCameraPixel = null;
	public static int getFrontCameraPixel() {
		if(frontCameraPixel == null) {
			synchronized (cameraLock) {
				if(frontCameraPixel == null) {
					Camera camera = open(1);
					if(camera == null) {
						return 0;
					}
					Parameters p = camera.getParameters();
					camera.release();
					frontCameraPixel = getMaxCarmeraPixels(p);
				}
			}
		}
		return frontCameraPixel;
	}

	/**
	 * 获取后置置摄像头像素
	 * @author josephkwok
	 */
	private static Integer backCameraPixel = null;
	public static int getBackCameraPixel() {
		if(backCameraPixel == null) {
			synchronized (cameraLock) {
				if(backCameraPixel == null) {
					Camera camera = open(0);
					if(camera == null) {
						return 0;
					}
					Parameters p = camera.getParameters();
					camera.release();
					backCameraPixel = getMaxCarmeraPixels(p);
				}
			}
		}
		return backCameraPixel;
	}
	
	private static int getMaxCarmeraPixels(Parameters parameters) {
		if(parameters == null) {
			return 0;
		}
		List<Size> list = parameters.getSupportedPictureSizes();
		if(list == null || list.size() == 0) {
			return 0;
		}
		Size size = list.get(0);
		if(size == null) {
			return 0;
		}
		int ret = size.width * size.width;
		for(int i = 1; i < list.size(); ++i) {
			size = list.get(i);
			if(size == null) {
				return ret;
			}
			int t = size.width * size.width;
			if(t > ret) {
				ret = t;
			}
		}
		return ret;
	}
	private static Camera open(int cameraId) {
		Camera ret = null;
		try {
			Class<?> clazz = Class.forName("android.hardware.Camera");
			Method m = clazz.getMethod("open", int.class);
			ret = (Camera) m.invoke(null, cameraId);
		} catch(Throwable t) {
			Log.w(TAG, t);
		}
		return ret;
	}
	
	/**
	 * 获取基带版本
	 */
	public static String getRadioVersion() {
		String ret = "";
		try {
			Class<?> clazz = Class.forName("android.os.Build");
			Method m = clazz.getMethod("getRadioVersion");
			ret = (String) m.invoke(null);
		} catch(Throwable t) {
			Log.w(TAG, t);
		}
		return ret;
	}
	
	/**
	 * 获取cpu信息
	 * @param context
	 * @return
	 */
	public static String getCpuInfo(Context context) {
		String cpuInfo = "";
		String cpu_args_file = "/proc/cpuinfo";
		try {
			String rst = FileUtil.readFile(cpu_args_file);
			String[] lines = rst.split("\\n");
			cpuInfo = lines[0];  // 获得CPU型号			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cpuInfo;
	}

	/**
	 * 获取cpu最大频率
	 */
	public static String getMaxCpuFreq() {
        StringBuilder builder = new StringBuilder();
        ProcessBuilder cmd;
        try {
                String[] args = { "/system/bin/cat",
                                "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" };
                cmd = new ProcessBuilder(args);
                Process process = cmd.start();
                InputStream in = process.getInputStream();
                byte[] re = new byte[24];
                while (in.read(re) != -1) {
                        builder.append(new String(re));
                }
                in.close();
        } catch (IOException ex) {
                ex.printStackTrace();
                builder = new StringBuilder("N/A");
        }        
        return builder.toString().trim();
    }
	
	/**
	 * 获取总内存大小
	 * @return
	 */
	public static long getTotalMemery() {
		long ret = 1;
		File file = new File("/proc/meminfo");
		DataInputStream in = null;
		if(file.exists()) {
			try {
				in = new DataInputStream(new FileInputStream(file));
				String line = in.readLine();
				if(line == null) {
					throw new IOException("/proc/meminfo is empty!");
				}
				line = line.trim();
				String[] temp = line.split("[\\s]+");
				ret = Long.parseLong(temp[1]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} finally {
				if(null != in) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					in = null;
				}
			}
		}
		return ret;
	}

	/**
	 * 获取内部存储容量
	 * 
	 * @return
	 */
	public static long getRomSize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long blockCount = stat.getBlockCount();
		return blockSize * blockCount;
	}

	/**
	 * 
	 * @param info
	 *            大小信息
	 * 
	 *            获取存储卡存储量
	 */
	public static void getStorageCardSize(SizeInfo info) {
		if (FileUtil.hasStorageCard()) {
			getSizeInfo(Environment.getExternalStorageDirectory(), info);
		} else {
			info.availdSize = 0;
			info.totalSize = 0;
		}
	}

	/**
	 * 是否已root
	 * @return
	 */
	public static boolean isRootSystem() {
		if (systemRootState == kSystemRootStateEnable) {
			return true;
		} else if (systemRootState == kSystemRootStateDisable) {
			return false;
		}
		File f = null;
		final String kSuSearchPaths[] = { "/system/bin/", "/system/xbin/",
				"/system/sbin/", "/sbin/", "/vendor/bin/" };
		try {
			for (int i = 0; i < kSuSearchPaths.length; i++) {
				f = new File(kSuSearchPaths[i] + "su");
				if (f != null && f.exists()) {
					systemRootState = kSystemRootStateEnable;
					return true;
				}
			}
		} catch (Exception e) {
		}
		systemRootState = kSystemRootStateDisable;
		return false;
	}

}
