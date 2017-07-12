package com.eof.libs.base.network;

/**
 * 
 */
public class IPEndPoint {

	public static final int OPERATOR_CHINA_COMMON = 0; // 公用
	public static final int OPERATOR_CHINA_MOBILE = 1; // 中国移动
	public static final int OPERATOR_CHINA_UNICOM = 2; // 中国联通
	public static final int OPERATOR_CHINA_TELECOM = 3; // 中国电信
	public static final int OPERATOR_CHINA_WIFI = 4; // wifi
	public static final int OPERATOR_CHINA_DOMAIN = 100; // 域名

	private int mOper;
	private int mPort;
	private String mIp;

	// ip举例
//	private static final int PORT_8080 = 8080;
//	private static final String BAIDU = "baidu.com";
//	private final IPPoint DEFAULT_CONN_SVR1_DOMAIN1 = new IPPoint(BAIDU, PORT_8080, IPPoint.OPERATOR_CHINA_DOMAIN);
//	private final IPPoint DEFAULT_CONN_SVR1_IP = new IPPoint("120.196.210.101", PORT_8080, IPPoint.OPERATOR_CHINA_COMMON);
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		IPEndPoint ip = new IPEndPoint(mIp, mPort, mOper);
		return ip;
	}
	
	public IPEndPoint() {
	}

	public IPEndPoint(String ip, int port, int oper) {
		mOper = oper;
		mIp = ip;
		mPort = port;
	}

	public IPEndPoint(String website) {
		mIp = website;
		mPort = -1;
	}

	public void setPort(int port) {
		mPort = port;
	}

	public int getPort() {
		return mPort;
	}

	public void setIp(long ip) {
		String[] temp = new String[4];
		temp[0] = new Integer((int) ((ip) & 0xff)).toString();
		temp[1] = new Integer((int) ((ip >> 8) & 0xff)).toString();
		temp[2] = new Integer((int) ((ip >> 16) & 0xff)).toString();
		temp[3] = new Integer((int) ((ip >> 24) & 0xff)).toString();
		mIp = temp[0] + "." + temp[1] + "." + temp[2] + "." + temp[3];
	}

	public void setIp(String ip) {
		mIp = ip;
	}

	public String getIp() {
		return mIp;
	}

	public void setOper(int oper) {
		mOper = oper;
	}

	public int getOper() {
		return mOper;
	}

	@Override
	public String toString() {
		if (mPort >= 0) {
			return mIp + ":" + mPort;
		} else {
			return mIp;
		}
	}

}
