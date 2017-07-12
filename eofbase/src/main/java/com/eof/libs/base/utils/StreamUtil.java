package com.eof.libs.base.utils;

import com.eof.libs.base.debug.Log;
import com.eof.libs.base.network.INetworkProgress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * 
 */
public class StreamUtil {
	
	private final static String TAG = "StreamUtil";

	/**
	 * 获取去除头部之后的数据
	 * @param data
	 * @return
	 */
	public static byte[] delHead(final byte[] data) {
		if (data.length < 8) {
			return null;
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(data, 8, data.length - 8);
		return baos.toByteArray();
	}
	
	/**
	 * headsize含义举例：高16位（协议扩展预留）、低16位（buf长度大小）
	 * @param headVersionBigdata TODO
	 * @param dataLenght TODO
	 * @param data
	 * @return
	 */
	public static byte[] addHead(final int headVersionBigdata, 
			final int headVersionMode, 
			final int dataLenght, 
			final byte[] data) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// 预留
		baos.write(0);
		// 预留
		baos.write(0);
		// 大数据通道协议号
		baos.write(headVersionBigdata);
		// 业务通道协议号
		baos.write(headVersionMode);
		
		// 数据长度
		baos.write(dataLenght >>> 24);
		baos.write(dataLenght >>> 16);
		baos.write(dataLenght >>> 8);
		baos.write(dataLenght);

		try {
			baos.write(data);
		} catch(Throwable t) {
			Log.e(TAG, t);
			return null;
		} finally {
			try {
				baos.flush();
				baos.close();
			} catch (IOException e) {
				Log.e(TAG, e);
			}
		}
		return baos.toByteArray();
	}
	
	public static byte[] getBytesFromIS(InputStream is, int start, int len, INetworkProgress progress) throws IOException {
		int pos = start;
		byte[] buffer = new byte[len];
		int actualSize = 0;
		int tempLen = len;
		while(actualSize < len && tempLen > 0) {
			int rcvSize = is.read(buffer, pos, tempLen);
			if (rcvSize < 0) {
				if (null != progress){
					progress.onProgress(true, actualSize, len);
				}
				break;
			}
			actualSize += rcvSize;
			pos += rcvSize;
			tempLen -= rcvSize;
			
			if (null != progress){
				progress.onProgress(true, actualSize, len);
			}
			
			if (tempLen > 0){
				int b = 3;
				b++;
			}
		}
		
		if (actualSize != len){
			return null;
		}
		return buffer;
	}
}

