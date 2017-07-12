package com.eof.libs.base.network;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.eof.libs.base.debug.Log;
import com.eof.libs.base.utils.AlarmerUtil;


/**
 * 定时重试策略
 */
public class RetryPlot {
	private static final String TAG = "RetryPlot";
	
	public static final String ACTION_RETRY_CONNECT_ALARM = "com.plot.RetryPlot.ACTION_RETRY_PLOT_ALARM";

	private static final int MAX_RETRY_COUNT = 12; // 允许重试次数
	private static final int MAX_RETRY_TIME = 10000; // 每次重试间隔时长：毫秒
	
	private int mRetryMaxCount = MAX_RETRY_COUNT;
	private int mRetryGapTime = MAX_RETRY_TIME;
	private int mRetryCount = 0;

	private Context mContext;
	private IRetryThing mIRetryThing;
	private RetryPlotReceiver mRetryPlotReceiver;
	
	public interface IRetryThing {
		boolean retryMe();
	}
	
	public RetryPlot(Context context, IRetryThing retryThing) {
		mContext = context;
		mIRetryThing = retryThing;
	}
	
	/**
	 * 设置重试参数
	 * @param retryMaxCount 重试次数
	 * @param retryGapTime 重试间隔时长
	 *
	 * @author danyangguo in 2012-10-26
	 */
	public void setRetryMax(final int retryMaxCount, final int retryGapTime) {
		mRetryMaxCount = retryMaxCount;
		mRetryGapTime = retryGapTime;
	}

	/**
	 * 根据配置启动重试，直至成功
	 *
	 * @author danyangguo in 2012-10-26
	 */
	public void startRetry() {
		Log.d(TAG, "startRetry");
		if (isRetrying()) {
			Log.d(TAG, "isRetrying");
			return;
		}
		
		if (null == mRetryPlotReceiver) {
			mRetryPlotReceiver = new RetryPlotReceiver();
		}

		// 先stop一次
		stopRetry();
		
		// 重试
		goonRetry();
	}
	
	private boolean isRetrying() {
		Log.d(TAG, "isRetrying()", mRetryCount > 0);
		return mRetryCount > 0;
	}
	
	private void goonRetry() {
		Log.d(TAG, "goonRetry");
		if (!enableRetry()) { // 超过重试次数
			Log.d(TAG, "!enableRetry()");
			stopRetry();
			return;
		}
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_RETRY_CONNECT_ALARM);
		mContext.registerReceiver(mRetryPlotReceiver, filter);
		
		AlarmerUtil.addAlarm(mContext, ACTION_RETRY_CONNECT_ALARM, Calendar.MILLISECOND, mRetryGapTime);
		addRetryCount();
	}
	
	private void stopRetry() {
		Log.d(TAG, "stopRetry");
		// 清除标记
		resetRetryCount();
		// 清除闹钟
		AlarmerUtil.delAlarm(mContext, ACTION_RETRY_CONNECT_ALARM);
		// 反注册receiver
		try {
			mContext.unregisterReceiver(mRetryPlotReceiver);
		} catch (Exception e) {
			
		}
	}
	
	private void handleRetry() {
		Log.d(TAG, "handleRetry");
		if (null == mIRetryThing) {
			return;
		}

		stopRetry();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if (null == mIRetryThing) {
					Log.d(TAG, "handleRetry run() null == mIRetryThing");
				}

				Log.d(TAG, "handleRetry run() mIRetryThing.retryMe()");
				boolean ret = mIRetryThing.retryMe();
				if (!ret) { // 不成功，继续重试
					Log.d(TAG, "handleRetry run() mIRetryThing.retryMe()", "ret", ret);
					goonRetry();
				}
			}
		}, "RetryPlot.handleRetry()").start();
	}
	
	private void resetRetryCount() {
		mRetryCount = 0;
	}

	private void addRetryCount() {
		++mRetryCount;
	}

	private boolean enableRetry() {
		Log.d(TAG, "enableRetry", mRetryCount <= mRetryMaxCount);
		return mRetryCount <= mRetryMaxCount;
	}

	/**
	 * 重试闹钟
	 */
	private class RetryPlotReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "RetryPlotReceiver.onReceive()");
			String action = intent.getAction();
			if (null == action) {
				Log.d(TAG, "RetryPlotReceiver.onReceive() action");
				return;
			}
			
			if (!ACTION_RETRY_CONNECT_ALARM.equals(action)) {
				Log.d(TAG, "RetryPlotReceiver.onReceive() not my action");
				return;
			}

			Log.d(TAG, "RetryPlotReceiver.onReceive() handleRetry");
			
			// 异步通知重试时机已到达
			handleRetry();
		}
	}
}
