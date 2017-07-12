package com.eof.libs.base.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.provider.Settings;

import com.eof.libs.base.GlobalData;
import com.eof.libs.base.debug.Log;
import com.eof.libs.base.network.ConnectType;

/**
 * 网络相关工具类
 * 
 */
public class NetworkUtil {
	private static final String TAG = "NetworkUtil";
	
	private static int mTargetSdkVersion;
	
	/**
	 * 获取网络连接服务
	 * 
	 * @param context
	 * @return
	 */
	public static ConnectivityManager getConnectivityManager(Context context) {
		ConnectivityManager connectivityManager = null;
		try {
			connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
		} catch (Exception e) {
			// 权限限制可能会抛出异常
			e.printStackTrace();
		}
		return connectivityManager;
	}
	
	public static int gotoSystemNetworkSetting(Context context) {
		Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
		ComponentName cName = new ComponentName("com.android.phone", "com.android.phone.Settings");
		intent.setComponent(cName);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(intent);
			return 0;
		} catch (Exception e) {
			Log.e(TAG, "savePluginIcon, err: " + e.getMessage(), e);
		}
		return -1;
	}
	
	/**
	 * 获得当前联网是否已连接
	 * 
	 * @return
	 */
	public static boolean isNetworkAvaliable() {
		Context context = GlobalData.mApplicationContext;
		ConnectivityManager manager = getConnectivityManager(context);
		if (manager != null) {
			NetworkInfo[] info = manager.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 获取当前网络连接类型
	 * 
	 * @return 网络连接类型 ConnectType
	 */
	public static int getNetworkType() {
		NetworkInfo info = null;
		try {
			info = getActiveNetworkInfo();
		} catch (NullPointerException e) {
			Log.w("getActiveNetworkInfo", " getActiveNetworkInfo NullPointerException--- \n" + e.getMessage());
		}
		if (info == null) {
			return ConnectType.CT_NONE;
		}
		if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			return ConnectType.CT_WIFI;
		} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
			String host = getProxyHost();
			if(null != host && host.length() > 0 && getProxyPort() > 0) {
				return ConnectType.CT_GPRS_WAP;
			} else {
				return ConnectType.CT_GPRS_NET;
			}
		}
		return ConnectType.CT_GPRS_NET;
	}
	
	public static NetworkInfo getActiveNetworkInfo() {
	    	NetworkInfo activeNetInfo = null;
			try {
				ConnectivityManager mg = (ConnectivityManager) GlobalData.mApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
				activeNetInfo = mg.getActiveNetworkInfo();
			} catch (Exception e) {
				Log.w("getActiveNetworkInfo", " getActiveNetworkInfo NullPointerException--- \n" + e.getMessage());
		}
		return activeNetInfo;
	};
	
	/**
	 * 获取当前网络连接接入点名称，wifi获取ssid
	 * 
	 * @return 网络连接接入点名称，如果获取不到返回空字符串"".
	 */
	public static String getNetworkName() {
		String name = "";
		NetworkInfo info = null;
		try {
			info = getActiveNetworkInfo();
		} catch (NullPointerException e) {
			Log.w("getActiveNetworkInfo", " getActiveNetworkInfo NullPointerException--- \n" + e.getMessage());
		}
		if (info == null) {
			return name;
		}
		if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			name = WifiUtil.getSSID();
		} else {
			name = info.getExtraInfo();
		}
		if(null == name) {
			name = "";
		}
		return name;
	}

	/**
	 * 是否ICS版本之后
	 * @return
	 */
	public static boolean isLaterThanIcs()
	{
		return Build.VERSION.SDK_INT >= 14;//Build.VERSION_CODES.ICE_CREAM_SANDWICH=14
	}
	/**
	 * 获取代理主机
	 * 
	 * @return String
	 */
	public static String getProxyHost() {
		String host = null;
		if(isLaterThanIcs())
		{
			host = System.getProperty( "http.proxyHost" );	
		}else
		{
			host = android.net.Proxy.getHost(GlobalData.mApplicationContext);
		}
		return host;
	}

	/**
	 * 获取代理端口
	 * 
	 * @return int
	 */
	public static int getProxyPort() {
		int port = -1;
		if(isLaterThanIcs())
		{
			try 
			{
				port = Integer.parseInt(System.getProperty("http.proxyPort"));
	        } catch (NumberFormatException e) 
	        {
	            return -1;
	        }
		}else
		{
			port = android.net.Proxy.getPort(GlobalData.mApplicationContext);
		}
		return port;
	}
	
	/**
	 * 判断是否能在主线程上调用网络接口
	 * android3.0以下的固件都可以；
	 * android3.0及以上的固件上，如果应用的targetSDKVersion低于android2.3.3可以在主线程上调用网络相关接口，
	 * 否则不能调用，系统会抛出NetworkOnMainThreadException的异常
	 */
	public static boolean canNetworkOnMainThread() {
		//3.0以下的固件上都可以在主线程上调用网络接口
		if(SDKUtil.getSDKVersion() < SDKUtil.OS_3_0) {
			return true;
		}
		
		if(mTargetSdkVersion < 1) {
			mTargetSdkVersion = GlobalData.mApplicationContext.getApplicationInfo().targetSdkVersion;
		}
		if(mTargetSdkVersion < SDKUtil.OS_2_3_3) {
			return true;
		}
		
		return false;
	}

	/**
	 * 网络是否已经连接了
	 * 
	 * @return
	 */
	public static boolean isNetworkConnected() {
		NetworkInfo netinfo = getNetworkInfo();
		if (null == netinfo) {
			return false;
		}
		
		boolean isConnected = netinfo.isConnected();
		return isConnected;
	}

    public static NetworkInfo getNetworkInfo(){
		NetworkInfo netinfo = null;
		try {
			netinfo =  NetworkUtil.getActiveNetworkInfo();
		} catch (NullPointerException e) {
			Log.w("getActiveNetworkInfo", " getActiveNetworkInfo NullPointerException--- \n" + e.getMessage());
		}
		return netinfo;
    }
}
