package com.eof.libs.base;

import android.content.Context;

import com.eof.libs.base.utils.FileUtil;

/**
 * 全局数据，慎用
 */
public class GlobalData {
	public static Context mApplicationContext;
	public static int mScreenWidth = 480; // 像素
	public static int mScreenHeigh = 800; // 像素

	private static class SingletonContainer {
		public static GlobalData mSingleInstance = new GlobalData();
	}
	
	public static GlobalData getInstance() {
		return SingletonContainer.mSingleInstance;
	}

	public static final String OneApp_SD_DIR = "/.OneApp/";
	
	/**
	 * 存储目录
	 * @return
	 */
	public static String getSaveDir() {
		return FileUtil.getSDCardDir() + OneApp_SD_DIR;
	}
	
	/**
	 * 图片缓存目录
	 * @return
	 */
	public static String getSaveDirBitmap() {
		return getSaveDir() + "bm/";
	}

	/**
	 * 文件缓存目录
	 * @return
	 */
	public static String getSaveDirFile() {
		return getSaveDir() + "file/";
	}

	/**
	 * 正在拍照缓存目录
	 * @return
	 */
	public static String getSaveDirCaptureTaking() {
		return getSaveDir() + "captureTaking/";
	}
	
	/**
	 * 拍照缓存目录
	 * @return
	 */
	public static String getSaveDirCapture() {
		return getSaveDir() + "capture/";
	}

	/**
	 * 待上传图片
	 * @return
	 */
	public static String getSaveDirUploadImage() {
		return getSaveDir() + "toUploadImage/";
	}

	/**
	 * 待上传语音
	 * @return
	 */
	public static String getSaveDirUploadAudio() {
		return getSaveDir() + "toUploadAudio/";
	}
	
	/**
	 * 临时上传目录
	 * @return
	 */
	public static String getSaveDirUploadTemp() {
		return getSaveDir() + "toUploadTemp/";
	}
	
	
}

