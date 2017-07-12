package com.eof.libs.base.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;

import com.eof.libs.base.debug.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;

/**
 * 文件操作
 * 
 */
public final class FileUtil {
	private static final String Tag = "FileUtil";

	/**
	 * 
	 * @param strFileName
	 * @return
	 */
	public static InputStream readFile(Context context, String strFileName) {
		if (context == null || strFileName == null || strFileName.length() == 0) {
			return null;
		}

		FileInputStream inputStream = null;
		try {
			File file = new File(strFileName);
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			Log.i(Tag, e);
		}

		return inputStream;
	}
	
	/**
	 * 复制文件
	 * 
	 * @return 成功返回true，失败返回false
	 */
	public static boolean copyExternalFileBytes(String srcPath, String destPath) {
		// 参数检查
		if (TextUtil.isNullOrEmptyWithTrim(srcPath) || TextUtil.isNullOrEmptyWithTrim(destPath)) {
			return false;
		}
		
		FileChannel in = null;
		FileChannel out = null;
		try {
			in = new FileInputStream(srcPath).getChannel();
			File outFile = new File(destPath);
			out = new FileOutputStream(outFile).getChannel();
			in.transferTo(0, in.size(), out);
			return true;
		} catch (Throwable e) {
			Log.i(Log.TAG, e);
			return false;
		} finally {
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
				Log.i(Log.TAG, e);
			}
		}
	}
	
	public static boolean deleteFile(final String path) {
		if (TextUtil.isNullOrEmptyWithoutTrim(path)) {
			return false;
		}

		if (!isFileExist(path)) {
			return true;
		}
		
		File file = new File(path);
		return deleteFile(file);
	}
	
	public static boolean deleteFile(final File file) {
		try {
			return file.delete();
		} catch (Exception e) {
			Log.i(Log.TAG, e);
			return false;
		}
	}

	/**
	 * 读取文件到内存
	 * 
	 * @return 成功返回非null，失败返回null
	 */
	public static byte[] readExternalFileBytes(String strFileName) {
		// 参数检查
		if (strFileName == null || strFileName.length() == 0) {
			return null;
		}
		
		FileInputStream inputStream = null;
		byte[] res;
		try {
			inputStream = new FileInputStream(strFileName);
			int size = inputStream.available();
			if (size <= 0) {
				return null;
			}
			res = new byte[size];
			inputStream.read(res);
		} catch (Throwable e) {
			Log.i(Tag, e);
			return null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					Log.i(Tag, e);
					return null;
				}
			}
		}

		return res;
	}

	public static String getInternalPath(Context context, final String fileName) {
		String packageName = context.getPackageName();
		String path = "/data/data/" + packageName + "/files/" + fileName;
		return path;
	}

	public static File getInternalDir(Context context) {
		String packageName = context.getPackageName();
		String path = "/data/data/" + packageName + "/files/";
		return new File(path);
	}

	/**
	 *
	 * @param context
	 * @param strFilePath
	 */
	public static boolean writeFile(Context context, String strFilePath,
			String strText) {
		// 参数检查
		if (strText == null || strText.length() == 0 || context == null
				|| strFilePath == null || strFilePath.length() == 0)
			return false;

		boolean bRet = true;
		try {
			OutputStream os = context.openFileOutput(strFilePath,
					Context.MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
			osw.write(strText);
			osw.close();
			os.close();
		} catch (FileNotFoundException e) {
			Log.i(Tag, "FileNotFoundException + " + e.getMessage());
			bRet = false;
		} catch (IOException e) {
			Log.i(Tag, "IOException + " + e.getMessage());
			bRet = false;
		}

		return bRet;
	}

	/**
	 * 获取SDCARD路径
	 * 
	 * @return SDCARD路径
	 */
	public static File getSDCardDir() {
		return new File(Environment.getExternalStorageDirectory().getPath());
	}

	/**
	 * 把数据写入文件，以追加的方式写入
	 * 
	 * @param file
	 *            要写入的文件
	 * @param buffer
	 *            数据缓存
	 * @throws IOException
	 */
	public static boolean write(File file, StringBuffer buffer)
			throws IOException {
		FileOutputStream fout;
		// 文件不存在，要先创建文件，然后写入数据
		if (!checkAndCreadFile(file)) {
			Log.i(Tag, "write log fail");
			return false;
		}
		fout = new FileOutputStream(file, true);
		fout.write(buffer.toString().getBytes());
		fout.close();
		return true;
	}

	/**
	 * 把数据写入文件，以追加的方式写入
	 * 
	 * @param file
	 *            要写入的文件
	 * @param buffer
	 *            数据缓存
	 * @throws IOException
	 */
	public static boolean write(File file, byte[] buffer) throws IOException {
		return write(file, buffer, true);
	}

	/**
	 * 把数据写入文件
	 * 
	 * @param file
	 *            要写入的文件
	 * @param buffer
	 *            数据缓存
	 * @throws IOException
	 */
	public static boolean write(final File file, final byte[] buffer, final boolean append)
			throws IOException {
		FileOutputStream fout = null;
		try {
			// 文件不存在，要先创建文件，然后写入数据
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			if (!file.canWrite()) {
				return false;
			}
			fout = new FileOutputStream(file, append);
			fout.write(buffer);
		} catch (Throwable t) {
			return false;
		} finally {
			if (null != fout) {
				fout.close();
			}
		}
		return true;
	}

	public static boolean isFileExist(final String path) {
		if (null == path) {
			return false;
		}
		
		File file = new File(path);
		return file.exists();
	}

	/**
	 * 检查文件是否存在，不存在则创建文件
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static boolean checkAndCreadFile(File file) throws IOException {
		boolean flag = true;
		// 文件不存在，要先创建文件，然后写入数据
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			flag = file.createNewFile();
		}
		return flag;
	}

	public static boolean deleteResFile(Context context, String fileName) {
		String path = "/data/data/" + context.getPackageName() + "/files/"
				+ fileName;
		File file = new File(path);
		if (file != null && file.exists()) {
			return file.delete();
		}
		return false;
	}

	/**
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String getAssetsFileName(Context context, String fileName) {
		String path = "/data/data/" + context.getPackageName() + "/files/" + fileName;
		try {
			File file = new File(path);
			if (!file.exists()) {
				InputStream is = context.getResources().getAssets()
						.open(fileName, AssetManager.ACCESS_RANDOM);
				byte[] dbBuffer = new byte[is.available()];
				is.read(dbBuffer, 0, is.available());
				is.close();
				FileOutputStream fos = context.openFileOutput(fileName,
						Context.MODE_PRIVATE);
				fos.write(dbBuffer);
				fos.close();
			}
			return fileName;
		} catch (IOException e) {
			Log.e("getCommonFilePath", e);
		}
		return null;
	}

	/**
	 * 删除某个目录所有文件
	 * 
	 * @param dir
	 */
	public static void delDir(File dir) {
		if (!dir.exists()) {
			return;
		}
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (String subFile : children) {
				File temp = new File(dir, subFile);
				if (temp.isDirectory()) {
					delDir(temp);
				} else {
					temp.delete();
				}
			}
			dir.delete();
		}
	}
	
	
	public static int getFileSize(String path) {
		if (null == path) {
			return 0;
		}
		try {
			return (int) new File(path).length();
		} catch (Throwable t) {
			Log.e(Log.TAG, t);
			return 0;
		}
	}

	/**
	 * 获取文件夹大小
	 * 
	 * @param file
	 * @return
	 */
	public static long getFolderSize(File file) {
		if (null == file) {
			return 0;
		}

		long size = 0;
		File flist[] = file.listFiles();
		if (null == flist) {
			return 0;
		}

		for (int i = 0; i < flist.length; i++) {
			if (null == flist[i]) {
				continue;
			}

			if (flist[i].isDirectory()) {
				size = size + getFolderSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}
	
	/**
	 * 读取文件
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static byte[] getAssetFile(Context context, String fileName) {
		if (TextUtil.isNullOrEmptyWithoutTrim(fileName)) {
			return null;
		}

		byte[] ret = null;
		InputStream inputStream = null;
		try {
			inputStream = context.getResources().getAssets().open(fileName, AssetManager.ACCESS_RANDOM);
			int size = inputStream.available();
			if (size <= 0) {
				return null;
			}
			ret = new byte[size];
			inputStream.read(ret);
		} catch (IOException e) {
			Log.e(Tag, "getAssetFile", "e", e.toString());
			e.printStackTrace();
			
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					Log.i(Tag, "getAssetFile", "e", e.toString());
					return null;
				}
			}
		}
		
		return ret;
	}
	
	public static boolean renameTo(final String filePathSrc,
			final String dirDst, final String filenameDst) {
		File dir = new File(dirDst);
		if(!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}
		boolean result = new File(filePathSrc).renameTo(new File(dirDst + filenameDst));
		Log.d(Tag, "renameTo()", result);
		return result;
	}

	/**
	 * 
	 * @param context
	 * @param fileName
	 * @param path
	 * @return
	 */
	public static synchronized String getAssetFile(Context context, String fileName, String path) {
		if (path == null || path.equals("")) {
			path = context.getFilesDir().toString();
		}
		//如果files目录不存在，创建files目录
		File dir = new File(path);
		if(!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}
		path += File.separator + fileName;

		InputStream in = null;
		FileOutputStream out = null;
		try {
			File file = new File(path);
			if (!file.exists()) {
				in = context.getResources().getAssets().open(fileName, AssetManager.ACCESS_RANDOM);
				out = new FileOutputStream(file);
				byte[] dbBuffer = new byte[8192];
				int lenght = -1;
				while ((lenght = in.read(dbBuffer)) > 0) {
					out.write(dbBuffer, 0, lenght);
				}
			}
		} catch (IOException e) {
			Log.e("getCommonFilePath", "getCommonFilePath error");
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		return path;
	}

	/**
	 * 从文件下载URL得到下载文件名称
	 * 
	 * @param url
	 * @param defaultName
	 * @return
	 */
	public static final String guessFileName(String url, String defaultName) {
		String fileName = null;

		// If all the other http-related approaches failed, use the plain uri
		if (fileName == null) {
			String decodedUrl = Uri.decode(url);
			if (decodedUrl != null) {
				int queryIndex = decodedUrl.indexOf('?');
				// If there is a query string strip it, same as desktop browsers
				if (queryIndex > 0) {
					decodedUrl = decodedUrl.substring(0, queryIndex);
				}
				if (!decodedUrl.endsWith("/")) {
					int index = decodedUrl.lastIndexOf('/') + 1;
					if (index > 0) {
						fileName = decodedUrl.substring(index);
					}
				}
			}
		}

		if (fileName == null) {
			fileName = defaultName;
		}
		// Finally, if couldn't get filename from URI, get a generic filename
		if (fileName == null) {
			fileName = "downloadfile";
		}

		return fileName;
	}

	/**
	 * 读取文件内容
	 * 
	 * @param absolutePath
	 *            文件绝对路径
	 * @return
	 */
	public static String readFile(String absolutePath) {
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(absolutePath));
			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = bis.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			String result = new String(baos.toByteArray());
			return result;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != baos) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				baos = null;
			}
			if (null != bis) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				bis = null;
			}
		}
		return "";
	}
	
	/**
	 * 是否插有存储卡
	 * 
	 * @return 是否插有存储卡
	 */
	public static boolean hasStorageCard() {
		String state=android.os.Environment.getExternalStorageState();
		if(state==null){
			// rdm上报空指针，所以加了非空判断
			return false;
		}else{
			return state.equals(android.os.Environment.MEDIA_MOUNTED);
		}
	}

	public static String[] readLinesFromMemoryFile(final File srcFile) {
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(srcFile));
			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = bis.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			return new String(baos.toByteArray()).split("\\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != bis) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				bis = null;
			}
			if (null != baos) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				baos = null;
			}
		}
		return null;
	}

	public static String[] readLinesFromFile(String absolutePath) {
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(absolutePath));
			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = bis.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			return new String(baos.toByteArray()).split("\\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != bis) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				bis = null;
			}
			if (null != baos) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				baos = null;
			}
		}
		return null;
	}
	
	public static String[] readLinesFromAssetsFile(Context context, String fileName) {
		InputStream bis = null;
		ByteArrayOutputStream baos = null;
		try {
			bis = context.getResources().getAssets()
					.open(fileName, AssetManager.ACCESS_RANDOM);
			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = bis.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			return new String(baos.toByteArray(), "UTF8").split("\\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != bis) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				bis = null;
			}
			if (null != baos) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				baos = null;
			}
		}
		return null;
	}
	
}
