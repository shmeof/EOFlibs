package com.eof.libs.base.utils;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.eof.libs.base.debug.Log;

/**
 * 闹钟工具类
 */
public class AlarmerUtil {
	private final static String Tag = "AlarmerUtil";
	
	/**
	 * 设置一次性闹钟
	 * 
	 * @param action
	 * @param timeUnit
	 *            时间单位
	 * @param time
	 *            延迟触发时间
	 */
	public static PendingIntent addAlarm(final Context context, String action, int timeUnit, int time) {
		Log.d(Tag, "addAlarm()", action, timeUnit, time);
		Intent intent = new Intent(action);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(timeUnit, time);

		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		// AlarmManager.RTC_WAKEUP会触发cpu启动，用Handler的话(假如锁屏了，系统电源管理让cpu休眠了)，系统不一定会启动cpu或会慢一点响应
		am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
		return sender;
	}

	/**
	 * 取消一次性闹钟
	 *
	 * @param action
	 */
	public static void delAlarm(final Context context, final String action) {
		Log.d(Tag, "addAlarm()", action);
		Intent intent = new Intent(action);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(sender);
	}
}

