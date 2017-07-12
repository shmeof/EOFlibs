package com.eof.libs.base.utils;

public class SimUtil {
	
	// ...待完善
	// 其他查看http://www.diypda.com/viewthread.php?tid=29172
	
	static public final int MCCMNCTYPE_OTHERS = -1;
	static public final int MCCMNCTYPE_CHINA_MOBILE = 0; // 中国移动
	static public final int MCCMNCTYPE_CHINA_UNICOM  = 1; // 中国联通
	static public final int MCCMNCTYPE_CHINA_TELECOM  = 2; // 中国电信
	
	/**
	 * 根据imsi获取Mcc和Mnc，获取所在国家及运营商
	 * @param imsi
	 * @return
	 */
	static public int getMccMncType(final String imsi) {
		if (null == imsi) {
			return MCCMNCTYPE_OTHERS;
		}
		
		if(imsi.startsWith("46000") 
			|| imsi.startsWith("46002")|| imsi.startsWith("46007")) {
			//因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号 
			//中国移动 
			return MCCMNCTYPE_CHINA_MOBILE;
		} else if(imsi.startsWith("46001")) { 
			//中国联通 
			return MCCMNCTYPE_CHINA_UNICOM;
		} else if(imsi.startsWith("46003")) { 
			//中国电信 
			return MCCMNCTYPE_CHINA_TELECOM;
		} else {
			return MCCMNCTYPE_OTHERS;
		}
	}
	
	/**
	 * 根据Operator获取所在国家及运营商
	 * @param imsiOperator TelephonyManager.getSimOperator()
	 * @return
	 */
	static public int getMccMncTypeByOper(final String imsiOperator) {
		if (null == imsiOperator) {
			return MCCMNCTYPE_OTHERS;
		}
		
        if (imsiOperator.equals("46000") || imsiOperator.equals("46002")|| imsiOperator.equals("46007")) { 
         //中国移动 
        	return MCCMNCTYPE_CHINA_MOBILE;
        } else if (imsiOperator.equals("46001")) { 
         //中国联通 
			return MCCMNCTYPE_CHINA_UNICOM;
        } else if (imsiOperator.equals("46003")) { 
         //中国电信 
			return MCCMNCTYPE_CHINA_TELECOM;
        } else {
        	return MCCMNCTYPE_OTHERS;
        }
	}
	
}