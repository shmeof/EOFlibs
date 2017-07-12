package com.eof.libs.base.utils;

import android.os.Environment;
import android.os.StatFs;

public final class SDCardUtil {
	/**
	 * sdcard 是否已经加载
	 * 
	 * @return
	 */
	public static boolean isSDCardMounted() {
		String status = Environment.getExternalStorageState();

		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取存储卡的剩余容量，单位为字节
	 * 
	 * @param filePath
	 * @return availableSpare
	 */
	public static long getAvailableStore() {
		String path = Environment.getExternalStorageDirectory().getPath();
		if (null == path)
			return 0;
		// 取得sdcard文件路径
		StatFs statFs = new StatFs(path);
		// 获取block的SIZE
		long blocSize = statFs.getBlockSize();
		// 获取BLOCK数量
		long totalBlocks = statFs.getBlockCount();
		// 可使用的Block的数量
		long availaBlock = statFs.getAvailableBlocks();
		// long total = totalBlocks * blocSize;
		long availableSpare = availaBlock * blocSize;
		return availableSpare;
	}
	
	/**
	 * 
	 * @return  如果路径是在sd卡中但sd卡不可用返回false，其他路径直接返回true
	 */
	public static boolean isAvailablePathInSDCard(String path){
		String rootpath = Environment.getExternalStorageDirectory().getPath();
		if(path.contains(rootpath)){
			return isSDCardMounted();
		}
		return true;
	}
}
