package com.eof.libs.base.network;

import com.eof.libs.base.debug.Log;

import java.util.ArrayList;

/**
 * IP策略
 * 运营商、2g、3g、wifi、域名等
 */
public class IpPlot implements IIpPlot {
	
	private final static String Tag = "IpPlot";

	private final byte PlotState_Try_Domain = 0; // 域名列表尝试
	private final byte PlotState_Try_List1 = 1; // 第一个列表尝试
	private final byte PlotState_Try_List2 = 2; // 第二个列表尝试
	
	private IPList mIPListRelease;
	private IPList mIPListHuidu;
	private IPList mIPListTest;
	private int mDomainIdx = 0; // 域名列表索引
	private int mList1Idx = 0; // 第一个列表索引
	private int mList2Idx = 0; // 第二个列表索引
	private byte mCurPlotState = PlotState_Try_Domain; // 状态机

	protected static int mIpConfigType = IP_CONFIG_TYPE_RELEASE; // 环境类型
	
	public IpPlot(final IPList ipListRelease, final IPList ipListHuidu, final IPList ipListTest) {
		assert null != mIPListRelease : "null == mIPListRelease";
		assert null != mIPListHuidu : "null == mIPListHuidu";
		assert null != mIPListTest : "null == mIPListTest";
		
		mIPListRelease = ipListRelease;
		mIPListHuidu = ipListHuidu;
		mIPListTest = ipListTest;
		mIPListRelease.random();
		mIPListHuidu.random();
		mIPListTest.random();
	}
	
	/**
	 * 设置服务器环境
	 * @param serverType
	 */
	public void setServerType(final int serverType) {
		Log.i(Tag, "setServerType()", "serverType: " + serverType);
		mIpConfigType = serverType;
		resetState();
	}

	public int getServerType() {
		return mIpConfigType;
	}
	
	/**
	 * 获取策略IP
	 * @return
	 *
	 * @author danyangguo in 2012-10-27
	 */
	public IPEndPoint getPlotIPPoint() {
		if (null == getList()) {
			return null;
		}
		
		checkSizeState();
		checkSizeState();
		
		IPEndPoint ret = null;
		switch (mCurPlotState) {
		case PlotState_Try_Domain: ret = getListItem(getList().mDomainIpList, mDomainIdx) ; break;
		case PlotState_Try_List1: ret = getListItem(getList().mValueIPList_Port1, mList1Idx); break;
		case PlotState_Try_List2: ret = getListItem(getList().mValueIPList_Port2, mList2Idx); break;
		default: break;
		}
		
		return ret;
	}

	/**
	 * 策略失败，选取下一个
	 *
	 * @author danyangguo in 2012-10-27
	 */
	public void tryNext() {
		nextState();
	}
	
	private IPEndPoint getListItem(final ArrayList<IPEndPoint> list, final int idx) {
		if (null == list) {
			return null;
		}
		
		int size = list.size();
		if (idx < 0 || idx >= size) {
			return null;
		}
		
		return list.get(idx);
	}
	
	private IPList getList() {
		if (IIpPlot.IP_CONFIG_TYPE_RELEASE == mIpConfigType) {
			return mIPListRelease;
		} else if (IIpPlot.IP_CONFIG_TYPE_HUIDU == mIpConfigType) {
			return mIPListHuidu;
		} else if (IIpPlot.IP_CONFIG_TYPE_TEST == mIpConfigType) {
			return mIPListTest;
		}
		return null;
	}
	
	private void checkNull() {
		switch (mCurPlotState) {
		case PlotState_Try_Domain:
		{
			if (null == getList().mDomainIpList) {
				mCurPlotState = PlotState_Try_List1; 
			}
			break;
		}
		case PlotState_Try_List1:
		{
			if (null == getList().mValueIPList_Port1) {
				mCurPlotState = PlotState_Try_List2;
			}
			break;
		}
		case PlotState_Try_List2:
		{
			if (null == getList().mValueIPList_Port2) {
				resetState();
			}
			break;
		}
		default:
			break;
		}
	}
	
	private void checkSizeState() {
		checkNull();
		switch (mCurPlotState) {
		case PlotState_Try_Domain:
		{
			if (mDomainIdx >= getList().mDomainIpList.size() 
				|| getList().mDomainIpList.size() <= 0) {
				mCurPlotState = PlotState_Try_List1; 
			}
			break;
		}
		case PlotState_Try_List1:
		{
			if (null == getList().mValueIPList_Port1 
				|| mList1Idx >= getList().mValueIPList_Port1.size() 
				|| getList().mValueIPList_Port1.size() <= 0) {
				mCurPlotState = PlotState_Try_List2;
			}
			break;
		}
		case PlotState_Try_List2:
		{
			if (null == getList().mValueIPList_Port2 
				|| mList2Idx >= getList().mValueIPList_Port2.size()
				|| getList().mValueIPList_Port2.size() <= 0) {
				resetState();
			}
			break;
		}
		default:
			break;
		}
	}
	
	private void nextState() {
		switch (mCurPlotState) {
		case PlotState_Try_Domain:
		{
			++mDomainIdx;
			break;
		}
		case PlotState_Try_List1:
		{
			++mList1Idx;
			break;
		}
		case PlotState_Try_List2:
		{
			++mList2Idx;
			break;
		}
		default:
			break;
		}

		checkSizeState();
	}
	
	private void resetState() {
		mCurPlotState = PlotState_Try_Domain;
		mDomainIdx = 0;
		mList1Idx = 0;
		mList2Idx = 0;
		checkNull();
	}
	
	public static class IPList {
		private ArrayList<IPEndPoint> mDomainIpList; // 域名IP列表
		private ArrayList<IPEndPoint> mValueIPList_Port1; // 端口port1的IP列表
		private ArrayList<IPEndPoint> mValueIPList_Port2; // 端口port2的IP列表
		
		public IPList(ArrayList<IPEndPoint> domainIpList, ArrayList<IPEndPoint> list1, ArrayList<IPEndPoint> list2) {
			mDomainIpList = domainIpList;
			mValueIPList_Port1 = list1;
			mValueIPList_Port2 = list2;
		}
		
		public void random() {
			randomSvrList(mValueIPList_Port1);
			randomSvrList(mValueIPList_Port2);
		}

		private synchronized void randomSvrList(ArrayList<IPEndPoint> ipEndPoints){
			if (null == ipEndPoints) {
				return;
			}
			
			int size = ipEndPoints.size();
			for (int i = size - 1; i > 0; i --){
				int j = (int)(i * Math.random());
				IPEndPoint t = ipEndPoints.get(i);
				ipEndPoints.set(i, ipEndPoints.get(j));
				ipEndPoints.set(j, t);
			}
		}
	}
	
}

