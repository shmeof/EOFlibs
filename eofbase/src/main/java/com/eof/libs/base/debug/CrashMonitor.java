package com.eof.libs.base.debug;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类 来接管程序,并记录错误报告.
 * 
 */
public class CrashMonitor implements UncaughtExceptionHandler {

	/** 系统默认的UncaughtException处理类 */
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	private ICrashHandler mCrashHandler;

	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
	 * 
	 * @param crashHandler
	 */
	public CrashMonitor(ICrashHandler crashHandler) {
		mCrashHandler = crashHandler;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		
		if ((null == mCrashHandler || !mCrashHandler.handleException(ex))
				&& mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			// Sleep一会后结束程序
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {

			}
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(10);
		}

	}

	/**
	 * crash处理接口，比如前后台进程会实现不同的处理方式
	 * 
	 */
	public interface ICrashHandler {
		boolean handleException(Throwable ex);
	}

}
