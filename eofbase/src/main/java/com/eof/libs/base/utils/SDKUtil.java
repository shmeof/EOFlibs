package com.eof.libs.base.utils;

/**
 * Android SDK工具类
 *
 */
public final class SDKUtil {
	
	/**
	 * 1.5固件
	 */
	public static final int OS_1_5 = 3;
	
	/**
	 * 1.6固件
	 */
	public static final int OS_1_6 = 4;
	
	/**
	 * 2.0固件
	 */
	public static final int OS_2_0 = 5;
	
	/**
	 * 2.0.1固件
	 */
	public static final int OS_2_0_1 = 6;
	
	/**
	 * 2.1固件
	 */
	public static final int OS_2_1 = 7;
	
	/**
	 * 2.2固件
	 */
	public static final int OS_2_2 = 8;
	
	/**
	 * 2.3固件
	 */
	public static final int OS_2_3 = 9;
	
	/**
	 * 2.3.3固件
	 */
	public static final int OS_2_3_3 = 10;
	
	/**
	 * 3.0固件
	 */
	public static final int OS_3_0 = 11;
	
	/**
	 * 3.1固件
	 */
	public static final int OS_3_1 = 12;
	
	/**
	 * 3.2固件
	 */
	public static final int OS_3_2 = 13;
	
	/**
	 * 4.0固件
	 */
	public static final int OS_4_0 = 14;
	
	/**
	 * 4.0.3固件
	 */
	public static final int OS_4_0_3 = 15;
	
	/**
	 * 4.1.0固件
	 */
	public static final int OS_4_1_0 = 16;
	
	/**
	 * 4.2.0固件
	 */
	public static final int OS_4_2_0 = 17;
	
	/**
	 * 4.3.0固件
	 */
	public static final int OS_4_3_0 = 18;
	
	/**
	 * @return SDK版本号
	 */
	public static int getSDKVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}
	
	
	/**
	 * @return SDK版本名
	 */
	public static String getSDKName() {
	    return android.os.Build.VERSION.SDK;
	}
	
	/**
	 * 中国移动定订的OMS，深度定制
	 */
	public static final int RELEASE_OMS = 1;
	
	/**
	 * 一般的ANDROID版本，改动不大
	 */
	public static final int RELEASE_ANDROID = 2;
	
	/**
	 * 联想深度定制的LePhone
	 */
	public static final int RELEASE_LEPHONE = 3;
	
	/**
	 * 魅族M9
	 */
	public static final int RELEASE_MEIZU_M9 = 4;
	
	/**
	 * @return ANDROID订制方信息
	 */
	public static int getAndroidReleaseName() {
	    String product = android.os.Build.PRODUCT;
	    if (product.contains("OMS") || product.contains("SnapperTD")) {
	        return RELEASE_OMS;
	    } else if (product.contains("qsd8250_surf") || product.contains("3GW100")) {
	        return RELEASE_LEPHONE;
	    } else if(product.contains("meizu_m9")){
	    	return RELEASE_MEIZU_M9;
	    } else {
	        return RELEASE_ANDROID;
	    }
	}
}
