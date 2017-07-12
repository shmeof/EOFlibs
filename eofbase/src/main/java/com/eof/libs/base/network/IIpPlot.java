package com.eof.libs.base.network;


/**
 * 
 */
public interface IIpPlot {
	public final static int IP_CONFIG_TYPE_RELEASE = 1; // 环境连接类型：正式环境
	public final static int IP_CONFIG_TYPE_HUIDU = 2; // 环境连接类型：灰度环境
	public final static int IP_CONFIG_TYPE_TEST = 3; // 环境连接类型：测试环境
	
	IPEndPoint getPlotIPPoint();
	void tryNext();
}

