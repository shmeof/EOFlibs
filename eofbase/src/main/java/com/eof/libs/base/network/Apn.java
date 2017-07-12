package com.eof.libs.base.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.eof.libs.base.debug.Log;
import com.eof.libs.base.utils.NetworkUtil;

/**
 * Apn
 *
 * @author guodanyang
 */
public class Apn {
    private static final String    TAG                = "Apn";

    public static final int        TYPE_UNKNOWN    = 0x000;
    public static final int        TYPE_NET        = 0x001;
    public static final int        TYPE_WAP        = 0x002;
    public static final int        TYPE_WIFI        = 0x004;

    public static final String    APN_UNKNOWN        = "N/A";
    public static final String    APN_NET            = "Net";
    public static final String    APN_WAP            = "Wap";
    public static final String    APN_WIFI        = "Wlan";

    public static int            M_APN_TYPE        = TYPE_WIFI;
    public static String        M_APN_PROXY        = null;
    public static int            M_APN_PORT        = 80;
    public static byte            M_PROXY_TYPE    = 0;
    public static boolean        M_USE_PROXY        = false;
    public static boolean         IS_INIT            = false;

    public static final byte    NETWORK_DEFAULT    = 0;
    public static final byte    NETWORK_WIFI    = 1;
    public static final byte    NETWORK_2G        = 2;
    public static final byte    NETWORK_3G        = 3;
    public static final byte    NETWORK_4G        = 4;
    public static final byte    NETWORK_OTHER    = 10;
    public static byte            NETWORK_TYPE    = NETWORK_OTHER; // wifi/3g/2g/other

    // 代理方式
    public static final byte    PROXY_TYPE_CM    = 0;
    public static final byte    PROXY_TYPE_CT    = 1;

    // 代理地址
    private static final String    PROXY_CTWAP        = "10.0.0.200";
    
    // APN 名称
    public static final String        APN_CMWAP            = "cmwap";  // 移动2g
    public static final String        APN_CMNET            = "cmnet";  // 移动2g
    public static final String        APN_3GWAP            = "3gwap";  // 联通3g(手机)
    public static final String        APN_3GNET            = "3gnet";  // 联通3g(电脑)
    public static final String        APN_UNIWAP            = "uniwap"; // 联通2g(手机)
    public static final String        APN_UNINET            = "uninet"; // 联通2g(电脑)
    public static final String        APN_CTWAP            = "ctwap";  // 电信2g
    public static final String        APN_CTNET            = "ctnet";  // 电信3g
    public static final String        APN_777                = "#777"; // 电信 777 是统一接入点，下面还分有代理和无代理
    public static         String        APN_NAME_DRI        = "unknown"; // 接入点名称

    public static final byte CMWAP = 0;
    public static final byte CMNET = 1; 
    public static final byte GWAP = 2;
    public static final byte GNET = 3; 
    public static final byte UNIWAP = 4; 
    public static final byte UNINET = 5;
    public static final byte CTWAP = 6;
    public static final byte CTNET = 7;
    public static final byte APN777 = 8;
    public static final byte UNKNOWN = 9;
    public static         byte APN_NAME_VALUE     = UNKNOWN; // 
    
    // 网络制式
    public static final int ENT_NONE = 0;
    public static final int ENT_WIFI = 1;
    public static final int ENT_GPRS = 2;
    public static final int ENT_EDGE = 3;
    public static final int ENT_UMTS = 4;
    public static final int ENT_HSDPA = 5;
    public static final int ENT_HSUPA = 6;
    public static final int ENT_HSPA = 7;
    public static final int ENT_CDMA = 8;
    public static final int ENT_EVDO_0 = 9;
    public static final int ENT_EVDO_A = 10;
    public static final int ENT_1xRTT = 11;
    public static final int  ENT_iDen = 12;
    public static final int ENT_EVDO_B = 13;
    public static final int ENT_LTE = 14;
    public static final int ENT_eHRPD = 15;
    public static final int ENT_HSPAPlus = 16;
    public static final int ENT_UNKNOWN = 17;
    public static         int ENT_VALUE = ENT_UNKNOWN; // 网络制式
    
    private static IOnChange mIOnChange = null;
    
    public static interface IOnChange {
        /**
         * 网络切换
         * @param networkType 网络类型
         * @param entValue ent值
         * @param apnNameValue apnNameValue值
         * @param apnNameDri apnNameDri值
         */
        void onNetworkChange(final byte networkType,
                             final int entValue,
                             final byte apnNameValue,
                             final String apnNameDri);
    }
    
    public static void checkInit(Context context) {
        if (IS_INIT) {
            return;
        }
        synchronized (Apn.class) {
            if (IS_INIT) {
                return;
            }
            init(context);
            IS_INIT = true;
        }
    }
    
    public static void setIOnChange(final IOnChange onChange) {
        mIOnChange = onChange;
    }
    
    /**
     * 网络变化时处理
     * @param context 上下文
     *
     * @author danyangguo in 2012-9-27
     */
    public static void handleChange(Context context) {
        IS_INIT = false;
        checkInit(context);
        Log.i(TAG, "handleChange()" + " Apn.APN_NAME_VALUE: " + APN_NAME_VALUE
            + " APN_NAME_DRI: " + APN_NAME_DRI);
    }

    /**
     * apn设置初始化
     * @param context 上下文
     */
    public static void init(Context context) {
        NetworkInfo networkInfo = null;
        try {
            networkInfo = NetworkUtil.getActiveNetworkInfo();
        } catch (NullPointerException e) {
            Log.w("getActiveNetworkInfo", " getActiveNetworkInfo NullPointerException--- \n" + e.getMessage());
        }

        Log.d(TAG, "networkInfo : " + networkInfo);
        
        try {
            int type = -1;
            M_APN_TYPE = TYPE_UNKNOWN;
            NETWORK_TYPE = NETWORK_OTHER;

            String extraInfo = null;
            if (networkInfo != null) {
                type = networkInfo.getType();
                Log.d(TAG, "type: " + networkInfo.getType());
                Log.d(TAG, "typeName: " + networkInfo.getTypeName());

                extraInfo = networkInfo.getExtraInfo();
                if (extraInfo == null) {
                    M_APN_TYPE = TYPE_UNKNOWN;
                } else {
                    extraInfo = extraInfo.trim().toLowerCase();
                }
            }

            Log.d(TAG, "extraInfo : " + extraInfo);

            if (type == ConnectivityManager.TYPE_WIFI) {
                M_APN_TYPE = TYPE_WIFI;
                M_USE_PROXY = false;
                NETWORK_TYPE = NETWORK_WIFI;
                APN_NAME_DRI = "unknown";
                APN_NAME_VALUE = UNKNOWN;
            } else {
                // 接入点简要描述
                handleApnNameDri(extraInfo);
                
                // 判断是 wap 模式还是 net 模式
                if (extraInfo == null) {
                    M_APN_TYPE = TYPE_UNKNOWN;
                } else if (extraInfo.contains(APN_CMWAP)
                        || extraInfo.contains(APN_UNIWAP) 
                        || extraInfo.contains(APN_3GWAP)
                        || extraInfo.contains(APN_CTWAP)) {
                    NETWORK_TYPE = NETWORK_2G;
                    if (extraInfo.contains(APN_3GWAP)) {
                        NETWORK_TYPE = NETWORK_3G;
                    }
                    M_APN_TYPE = TYPE_WAP;
                } else if (extraInfo.contains(APN_CMNET)
                        || extraInfo.contains(APN_UNINET) 
                        || extraInfo.contains(APN_3GNET)
                        || extraInfo.contains(APN_CTNET)) {
                    NETWORK_TYPE = NETWORK_2G;
                    M_APN_TYPE = TYPE_NET;
                    if (extraInfo.contains(APN_3GNET) || extraInfo.contains(APN_CTNET)) {
                        NETWORK_TYPE = NETWORK_3G;
                    }
                } else if (extraInfo.contains(APN_777)) {
                    NETWORK_TYPE = NETWORK_3G; // 现实中，可以近似的认为 #777 都是 3G；电信 2G 基本没有网络了。by danyangguo
                    M_APN_TYPE = TYPE_UNKNOWN;
                } else if (networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE) {
                    NETWORK_TYPE = NETWORK_4G;
                    M_APN_TYPE = TYPE_UNKNOWN;
                } else {
                    M_APN_TYPE = TYPE_UNKNOWN;
                }

                M_USE_PROXY = false;
                if (isProxyMode(M_APN_TYPE)) {
                    M_APN_PROXY = android.net.Proxy.getDefaultHost();
                    M_APN_PORT = android.net.Proxy.getDefaultPort();

                    if (M_APN_PROXY != null) {
                        M_APN_PROXY = M_APN_PROXY.trim();
                    }

                    if (M_APN_PROXY != null && !"".equals(M_APN_PROXY)) {
                        M_USE_PROXY = true;
                        M_APN_TYPE = TYPE_WAP;

                        // 判断是否电信代理
                        if (PROXY_CTWAP.equals(M_APN_PROXY)) {
                            M_PROXY_TYPE = PROXY_TYPE_CT;
                        } else {
                            M_PROXY_TYPE = PROXY_TYPE_CM;
                        }
                    } else {
                        M_USE_PROXY = false;
                        M_APN_TYPE = TYPE_NET;
                    }
                }
            }
            
            if (null != mIOnChange) {
                mIOnChange.onNetworkChange(NETWORK_TYPE, ENT_VALUE, APN_NAME_VALUE, APN_NAME_DRI);
            }

            Log.d(TAG, "NETWORK_TYPE : " + NETWORK_TYPE);
            Log.d(TAG, "M_APN_TYPE : " + Apn.M_APN_TYPE);
            Log.d(TAG, "M_USE_PROXY : " + Apn.M_USE_PROXY);
            Log.d(TAG, "M_APN_PROXY : " + Apn.M_APN_PROXY);
            Log.d(TAG, "M_APN_PORT : " + Apn.M_APN_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        ENT_VALUE = getNetWorkType(context, networkInfo);
        
        Log.i(TAG, "init()" + " Apn.APN_NAME_VALUE: " + APN_NAME_VALUE
            + " APN_NAME_DRI: " + APN_NAME_DRI + " NETWORK_TYPE: " + NETWORK_TYPE + " ENT_VALUE: " + ENT_VALUE);
    }

    /**
     * 是否代理模式
     * 
     * @param apnType
     * @return
     */
    private static boolean isProxyMode(int apnType) {
        return apnType == TYPE_WAP || apnType == TYPE_UNKNOWN;
    }

    public static String getApnName(int apnType) {
        switch (apnType) {
            case TYPE_WAP:
                return APN_WAP;
            case TYPE_NET:
                return APN_NET;
            case TYPE_WIFI:
                return APN_WIFI;
            case TYPE_UNKNOWN:
                return APN_UNKNOWN;
            default:
                return APN_UNKNOWN;
        }
    }
    
    private static void handleApnNameDri(final String extraInfo) {
        if (null == extraInfo) {
            return;
        }
        
        if (extraInfo.contains(APN_CMWAP)) {
            APN_NAME_DRI = APN_CMWAP;
            APN_NAME_VALUE = CMWAP;
        } else if (extraInfo.contains(APN_CMNET)) {
            APN_NAME_DRI = APN_CMNET;
            APN_NAME_VALUE = CMNET;    
        } else if (extraInfo.contains(APN_3GWAP)) {
            APN_NAME_DRI = APN_3GWAP;
            APN_NAME_VALUE = GWAP;
        } else if (extraInfo.contains(APN_3GNET)) {
            APN_NAME_DRI = APN_3GNET;
            APN_NAME_VALUE = GNET;
        } else if (extraInfo.contains(APN_UNIWAP)) {
            APN_NAME_DRI = APN_UNIWAP;
            APN_NAME_VALUE = UNIWAP;
        } else if (extraInfo.contains(APN_UNINET)) {
            APN_NAME_DRI = APN_UNINET;
            APN_NAME_VALUE = UNINET;
        } else if (extraInfo.contains(APN_CTWAP)) {
            APN_NAME_DRI = APN_CTWAP;
            APN_NAME_VALUE = CTWAP;
        } else if (extraInfo.contains(APN_CTNET)) {
            APN_NAME_DRI = APN_CTNET;
            APN_NAME_VALUE = CTNET;
        } else if (extraInfo.contains(APN_777)) {
            APN_NAME_DRI = APN_777;
            APN_NAME_VALUE = APN777;
        }
    }
    
    private static int getNetWorkType(Context context, NetworkInfo networkInfo) {
        int netType = ENT_NONE;
        try {
            if (networkInfo == null) {
                return netType;
            }

            if (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) {
                netType = ENT_WIFI;
            } else if (ConnectivityManager.TYPE_MOBILE == networkInfo.getType()) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (null != tm) {
                    switch (tm.getNetworkType()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                            netType = ENT_GPRS;
                            break;
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                            netType = ENT_EDGE;
                            break;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                            netType = ENT_UMTS;
                            break;
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                            netType = ENT_HSDPA;
                            break;
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                            netType = ENT_HSUPA;
                            break;
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                            netType = ENT_HSPA;
                            break;
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                            netType = ENT_CDMA;
                            break;
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            netType = ENT_EVDO_0;
                            break;
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                            netType = ENT_EVDO_A;
                            break;
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                            netType = ENT_1xRTT;
                            break;
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            netType = ENT_iDen;
                            break;
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                            netType = ENT_EVDO_B;
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            netType = ENT_LTE;
                            break;
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                            netType = ENT_eHRPD;
                            break;
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            netType = ENT_HSPAPlus;
                            break;
                        default:
                            netType = ENT_UNKNOWN;
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return netType;
    }


}
