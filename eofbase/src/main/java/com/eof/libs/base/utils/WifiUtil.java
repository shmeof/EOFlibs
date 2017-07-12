package com.eof.libs.base.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.eof.libs.base.GlobalData;
import com.eof.libs.base.debug.Log;
import com.eof.libs.base.network.WifiApproveException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class WifiUtil {
	private static final String TEST_URL = "http://ouerDomain/cw.html";   //地址被修改时，相应的如TEST_URL_xxx都要修改
	private static final String CUSTOM_HEADER = "Meri";	// 自定义的http响应头key-value值

	/**
	 * wifi网络的认证地址
	 */
	public static String sRedirectLocation;
	
	/**
	 * 判断网络连接是否是wifi
	 * @return boolean
	 */
	public static boolean isWifiNetwork() {
		NetworkInfo networkInfo = null;
		try {
			networkInfo = NetworkUtil.getActiveNetworkInfo();
		} catch (NullPointerException e) {
			Log.w("getActiveNetworkInfo", " getActiveNetworkInfo NullPointerException--- \n" + e.getMessage());
		}
		if (networkInfo == null 
				|| networkInfo.getType() != ConnectivityManager.TYPE_WIFI) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 获取wifi的SSID
	 * @return 如果当前active的网络不是wifi，将返回null
	 */
	public static String getSSID() {
		WifiManager wifiManager = (WifiManager) GlobalData.mApplicationContext.getSystemService(Context.WIFI_SERVICE);
		if(wifiManager != null) {
			WifiInfo info = wifiManager.getConnectionInfo();  
			if(info != null) {
				return info.getSSID();
			}
		}
		return "";
	}
	
	/**
	 * 获取wifi信号强度
	 * @return 
	 */
	public static int calculateSignalLevel(int numLevels) {
		if(!isWifiNetwork()) {
			return -1;
		}
		WifiManager wifiManager = (WifiManager) GlobalData.mApplicationContext.getSystemService(Context.WIFI_SERVICE);  
		return WifiManager.calculateSignalLevel(wifiManager.getConnectionInfo().getRssi(), numLevels);
	}
	
	/**
	 * 异步判断wifi网络是否需要认证
	 * @return 如果wifi需要认证，返回true，否则返回false
	 * @throws WifiApproveException 
	 */
	public static String needWifiApprove(final IWifiApproveCheckCallBack callback) throws WifiApproveException {
		String location = null;
		boolean networkError = false;
		HttpURLConnection httpConn = null;
		try {
			httpConn = (HttpURLConnection) new URL(TEST_URL).openConnection();
			if(SDKUtil.getSDKVersion() < SDKUtil.OS_2_2) {
				System.setProperty("http.keepAlive", "false");
			} 
			httpConn.setUseCaches(false); 
			httpConn.setRequestProperty("Pragma", "no-cache"); 
			httpConn.setRequestProperty("Cache-Control", "no-cache");
			httpConn.setInstanceFollowRedirects(false);
			httpConn.setRequestMethod("GET");
			httpConn.setReadTimeout(5000);
			int responseCode = httpConn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK || 
					(responseCode >= HttpURLConnection.HTTP_MOVED_PERM && responseCode <= HttpURLConnection.HTTP_USE_PROXY)) {
				String customHeader = httpConn.getHeaderField(CUSTOM_HEADER);
				if (customHeader == null || !customHeader.equals(CUSTOM_HEADER)) {
					location = getRedirectUrl(httpConn);
				}
			} else {
				networkError = true;
			}
		} catch (IOException e) {
			// e.printStackTrace();
		} catch (WifiApproveException e) {
			throw e;
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			if (httpConn != null) {
				httpConn.disconnect();
			}
			
			if (location == null) {
				callback.onWifiApproveCheckFinished(false, networkError);
			} else {
				sRedirectLocation = location;
				callback.onWifiApproveCheckFinished(true, networkError);
			}
		}

		return location;
	}
	
	private static String getRedirectUrl(HttpURLConnection httpConn) throws WifiApproveException {
		String location = null;
		InputStream inputStream = null;
		try {
			if (!new URL(TEST_URL).getHost().equals(httpConn.getURL().getHost())) {
				location = httpConn.getURL().toExternalForm();
			} 
			if (location == null && httpConn.getHeaderField("Location") != null) {
				location = httpConn.getHeaderField("Location");
			} 
			if (location == null && httpConn.getHeaderField("Refresh") != null) {
				String[] items = httpConn.getHeaderField("Refresh").split(";");
				if (items.length == 2) {
					location = items[1].trim();
				}
			} 
			if (location == null) {
				inputStream = httpConn.getInputStream();
				if (inputStream != null) {
					String url = parsePage(inputStream);
					if (url != null) {
						location = url;
					}
				}
			}
		} catch (IOException e) {
			// e.printStackTrace();
		} catch (WifiApproveException e) {
			throw e;
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
		return location;
	}
	
	private static String parsePage(InputStream is) throws WifiApproveException {
		String location = null;
		String pageContent = getPageContent(is);
		// 优先级最高的排在前面
		String patterns[] = {
				"http-equiv\\s*=\\s*[\"']*refresh[\"']*\\s*content\\s*=\\s*[\"']*[^;]*;\\s*url\\s*=\\s*[\"']*([^\"'\\s>]+)",
				"[^\\w](?:location.href\\s*=|location\\s*=|location.replace\\s*\\()\\s*[\"']*([^\"'>]+)",
				"<NextURL>([^<]+)",
				"\\s+action\\s*=\\s*[\"']*([^\"'>]+)[\"'>\\s]*.*submit",
				"<LoginURL>([^<]+)"
		};
		int patternsLength = patterns.length;
		Matcher matcher;
		for (int i = 0; i < patternsLength && location == null; i++) {
			matcher = Pattern.compile(patterns[i], Pattern.CASE_INSENSITIVE).matcher(pageContent);
			while (matcher.find() && location == null) {
				location = matcher.group(matcher.groupCount());
				// 有些规则拿出来的url链接可能不正确，跟进protocol过滤掉不正确的url
				if (location != null && !location.trim().toLowerCase().startsWith("http")) {
					location = null;
				}
			}
		}
		if (location == null) {
			throw new WifiApproveException("0725SSID:" + getSSID() + " page head content: " + pageContent);
		}
		return location;
	}
	
	private static String getPageContent(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	/**
	 * 检查是否需要wifi认证的回调
	 * @author serenazhou
	 *
	 */
	public interface IWifiApproveCheckCallBack {
		/**
		 * @param needWifiApprove 是否需要wifi认证，true需要认证，false则不需要
		 * @param receivedError 判断时是否出现了错误，默认出现错误时不提示需要wifi认证
		 */
		public abstract void onWifiApproveCheckFinished(boolean needWifiApprove, boolean receivedError);
		
	}
	
}
