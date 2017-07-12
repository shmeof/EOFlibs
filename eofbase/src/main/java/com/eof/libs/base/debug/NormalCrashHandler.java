package com.eof.libs.base.debug;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.eof.libs.base.utils.SDCardUtil;

/**
 * 
 */
public class NormalCrashHandler implements CrashMonitor.ICrashHandler {

	private Context mContext;

	public NormalCrashHandler(Context context) {
		mContext = context;
	}

	@Override
	public boolean handleException(final Throwable ex) {
		if (ex == null) {
			return true;
		}
		
		// 使用Toast来显示异常信息
		boolean isOpen = true;
		if (isOpen) {
			new Thread("CrashHandler") {
				@Override
				public void run() {
					Looper.prepare();
					String str = "程序出错了!Log:\n";
					str += ex.toString();
					boolean sdcardAvialable = SDCardUtil.isSDCardMounted();
					if (!sdcardAvialable) {
						str += " (SD卡不可用，无法输入日志。)";
					}
					if (false/*GlobalConfig.CRASH_TOAST*/) {
						Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();
					}
					Looper.loop();
				}

			}.start();
			Log.e(Log.TAG, ex);
		}
		
		return isOpen;
	}

}
