package com.eof.libs.base.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;

import com.eof.libs.base.debug.Log;
import com.eof.libs.base.utils.NetworkUtil;
import com.eof.libs.base.utils.StreamUtil;


/**
 * Tcp网络
 */
public class TcpNetwork implements RetryPlot.IRetryThing {
	private final String TAG = "TcpNetwork";
	
	public final static byte ASYNC_MODE = 0; // 异步模式
	public final static byte SYNC_MODE = 1;  // 同步模式
	
	public final static int ERRCODE_COLSED_BY_SERVER = 0; // 服务器关闭连接
	public final static int ERRCODE_COLSED_BY_COMMON = 1; // 终端主动关闭连接
	public final static int ERRCODE_COLSED_BY_COMMON_RESTART = 2; // 终端重启策略关闭导致的关闭
	public final static int ERRCODE_START_BEGINING = 3; // 正在打开连接
	public final static int ERRCODE_START_BY_COMMON_SUCC = 4; // 普通打开连接成功
	public final static int ERRCODE_START_BY_RESTART_SUCC = 5; // 终端重启策略自动重新打开连接成功
	public final static int ERRCODE_HANDLE_THROWABLE = 6; // 处理包异常
	public final static int ERRCODE_DOMAIN_CONNECTFAILED = 7; // 域名:端口 连接异常
	public final static int ERRCODE_IP_CONNECTFAILED = 8; // Ip:端口 连接异常

	private byte mMode = ASYNC_MODE;
	private boolean mUseDataHead = true; // 使用数据头标识
	private final int SOCKET_READ_TIMEOUT = 15000;
	private final int MAX_CONNECT_TIMEOUT = 15000; // 超时时间：15s
	
	private Context mContext;
	
	private boolean mStopped = true; // 停止标记位
	private Thread mRcvThread; //接收线程
	private Socket mSocket;
	private DataOutputStream mSocketWriter; // 写入流
	private DataInputStream mSocketReader; // 读取流
	
	protected IIpPlot mIIpPlot;
	private IPEndPoint mIPPoint;
	
	private ITcpNetworkListner mITcpNetworkListner;
	
	public static interface ITcpNetworkListner {
		void handleData(final int dataHead, final byte[] data);
		void handleCode(final int errCode, Object object);
	}
	
	/**
	 * ASYNC_MODE/SYNC_MODE
	 * @param mode
	 * @param useDataHead TODO
	 */
	public TcpNetwork(final byte mode, boolean useDataHead) {
		mMode = mode;
		mUseDataHead = useDataHead;
	}
	
	/**
	 * 已经启动
	 * @return
	 *
	 * @author danyangguo in 2012-12-3
	 */
	public boolean isStarted() {
		return !mStopped;
	}
	
	public void setTcpNetworkListner(ITcpNetworkListner tcpNetworkListner) {
		mITcpNetworkListner = tcpNetworkListner;
	}
	
	public boolean start(final Context context, final IIpPlot ipPlot) {
		return start(context, ipPlot, false);
	}
	
	/**
	 * 启动网络
	 * @param context
	 * @param ipPlot ip策略
	 * @param isRestart 是否重启策略
	 * @return 是否启动成功
	 *
	 * @author danyangguo in 2012-10-29
	 */
	private synchronized boolean start(final Context context, final IIpPlot ipPlot, boolean isRestart) {
		Log.d(TAG, "start()", "isRestart", isRestart);
		if (isStarted()) {	
			Log.d(TAG, "start()", "isStarted()", isStarted());
			return true;
		}
		
		if (null == ipPlot) {
			Log.d(TAG, "start()", "null == ipPlot");
			return false;
		}
		
		mContext = context;
		if (!NetworkUtil.isNetworkConnected()) { // 无物理网络连接
			Log.d(TAG, "start()", "!NetworkUtil.isNetworkConnected()");
			return false;
		}
		
		mIIpPlot = ipPlot;
		
		// 开始连接
		boolean ret = checkSocketWithRetry(mIIpPlot);
		if (!ret) {
			Log.d(TAG, "start()", "checkSocket()", "!ret");
			return false;
		}

		mStopped = false;
		
		if (ASYNC_MODE == mMode) { // 异步模式才起接收线程
			Log.d(TAG, "start()", "startRcvThread()");
			startRcvThread();
		}

		// 连接成功
		if (null != mITcpNetworkListner) {
			if (isRestart) {
				mITcpNetworkListner.handleCode(ERRCODE_START_BY_RESTART_SUCC, null);
			} else {
				mITcpNetworkListner.handleCode(ERRCODE_START_BY_COMMON_SUCC, null);
			}
		}
		return true;
	}

	/**
	 * 关闭
	 * @return
	 */
	public boolean stop() {
		return stop(false, false);
	}
	
	/**
	 * 关闭网络
	 * @param bySvr 是否服务器主动关闭
	 * @param isRestart 是否终端策略自动重连网络
	 * @return
	 *
	 * @author danyangguo in 2012-10-29
	 */
	private synchronized boolean stop(final boolean bySvr, boolean isRestart) {
		Log.d(TAG, "stop()", "bySvr", bySvr, "isRestart", isRestart);
		boolean ret = stopSocket();
		if (!ret) {
			return false;
		}
		
		mStopped = true;

		if (null != mITcpNetworkListner) {
			if (bySvr) {
				mITcpNetworkListner.handleCode(ERRCODE_COLSED_BY_SERVER, null);
			} else {
				if (isRestart) { // 内部重启
					mITcpNetworkListner.handleCode(ERRCODE_COLSED_BY_COMMON_RESTART, null);
				} else {
					mITcpNetworkListner.handleCode(ERRCODE_COLSED_BY_COMMON, null);
				}
			}
		}
		return true;
	}
	
	protected boolean reStart(IIpPlot ipPlot) {
		if (!stop(false, true)) { // 没有停成功
			return false;
		}
		
		return start(mContext, ipPlot, true);
	}
	
	/**
	 * 异步包
	 * @param data
	 * @return
	 * @throws NetworkException
	 *
	 * @author danyangguo
	 */
	public boolean sendDataAsync(final byte[] data) throws NetworkException {
		if (isSocketClosed()) {
			throw new NetworkException("socket is closed");
		}
		
		if (!isSocketConnected()) {
			throw new NetworkException("socket is not connected");
		}
		
		boolean ret = false;
		switch (mMode) {
		case ASYNC_MODE: {
			ret = sendDataInAsync(data);
		}
			break;
		case SYNC_MODE: {
			ret = sendDataInSync(data);
		}
			break;
		default:
			break;
		}
		
		return ret;
	}
	
	/**
	 * 发送同步包
	 * @param data
	 * @param progress
	 * @return
	 *
	 * @author danyangguo
	 */
	public synchronized byte[] sendDataSync(final byte[] data, final INetworkProgress progress) {
		boolean ret = false;
		try {
			ret = sendDataAsync(data);
			if (!ret) {
				return null;
			}
		} catch (NetworkException e) {
			e.printStackTrace();
			return null;
		}
		
		try {
			return getRespDataInSync(progress);
		} catch (NetworkException e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * 发送同步包
	 * @param data
	 * @param progress
	 * @return
	 *
	 * @author danyangguo
	 */
	public synchronized String sendDataSync2(final byte[] data, final INetworkProgress progress) {
		boolean ret = false;
		try {
			ret = sendDataAsync(data);
			if (!ret) {
				return null;
			}
		} catch (NetworkException e) {
			e.printStackTrace();
			return null;
		}

		try {
			return getRespDataInSyncAll();
		} catch (NetworkException e) {
			e.printStackTrace();
			return null;
		}
	}

	private boolean sendDataInAsync(final byte[] data) throws NetworkException {
		if (ASYNC_MODE != mMode) { // 不是异步模式
			throw new NetworkException("not the current mode ASYNC_MODE");
		}

		assert null != mSocketWriter : "mSocketWriter is null";
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(data);
			synchronized (mSocket) {
				byte[] realSendData = baos.toByteArray();
				Log.i(TAG, "sendDataInAsync()", "realSendData.lenght", realSendData.length);
				mSocketWriter.write(realSendData);
			} 
		} catch (Throwable t) {
			throw new NetworkException("has a Throwable when sendDataInAsync()");
		}
		
		return true;
	}
	
	private boolean sendDataInSync(final byte[] data) throws NetworkException {
		if (SYNC_MODE != mMode) { // 不是同步模式
			throw new NetworkException("not the current mode SYNC_MODE");
		}
		
		try {
			OutputStream os = mSocket.getOutputStream();
			os.write(data);
		} catch (IOException e) {
			throw new NetworkException("has a Throwable when sendDataInsync()");
		}
		
		return true;
	}

	/**
	 * 按头部长度读取数据
	 * @param progress
	 * @return
	 * @throws NetworkException
	 */
	private byte[] getRespDataInSync(final INetworkProgress progress) throws NetworkException {
		try {
			DataInputStream socketReader = new DataInputStream(mSocket.getInputStream());
			int size = socketReader.readInt();
			Log.d(TAG, "getRespDataInSync()", "size", size);
			if (size < 4) {
				throw new NetworkException("sync size < 4");
			}
			
			byte[] respData = StreamUtil.getBytesFromIS(socketReader, 0, size - 4, progress);
			return respData;
		} catch (Throwable t) {
			throw new NetworkException("has a Throwable when getRespDataInSync()");
		}
	}

	/**
	 * 全部读取
	 * @return
	 * @throws NetworkException
	 */
	private String getRespDataInSyncAll() throws NetworkException {
		try {
			DataInputStream socketReader = new DataInputStream(mSocket.getInputStream());
			String line = socketReader.readLine();
			Log.d(TAG, "getRespDataInSyncAll()", "line", line);
			return line;
		} catch (Throwable t) {
			throw new NetworkException("has a Throwable when getRespDataInSync()");
		}
	}
	
	private void startRcvThread() {
		mRcvThread = new Thread("RcvThread") {
			public void run() {
				Log.i(TAG, "RcvThread start...");
				recv();
				Log.i(TAG, "RcvThread stop...");
			};
		};
		mRcvThread.start();
	}
	
	private void recv() {
		Log.i(TAG, "recv start...");

		while (!mStopped) {
			try {
				int socketHeadVersion = 0;
				int socketHeadLength = 0;
				if (mUseDataHead) {	
					socketHeadVersion = mSocketReader.readInt();
					socketHeadLength = mSocketReader.readInt();
				}
				int size = mSocketReader.readInt(); // 包长度
				assert size >= 0 : "recv() size < 4";
				Log.i(TAG, "recv() size: " + size);
				if (size < 4) {
					Log.i(TAG, "recv(), sync size < 4");
					continue;
				}
				
				if (size > 10240) { // 超过10k
					Log.i(TAG, "recv(), sync size > 10240");
					continue;
				}
				
				assert null != mSocketReader : "null != mSocketReader";
				byte[] respData = StreamUtil.getBytesFromIS(mSocketReader, 0, size - 4, null);
				if (null == respData) {
					Log.i(TAG, "recv(), respData == null");
				}

				Log.i(TAG, "recv(), respData != null", "length", respData.length);
				handleData(socketHeadVersion, socketHeadLength, respData);
			} catch (Throwable e) {
				Log.i(TAG, "recv() Throwable", e.toString());
				// 关闭网络
				stop(true, false);
			}
		}
		
		// 确保关闭
		stop();
		
		Log.i(TAG, "recv stop...");
	}
	
	private void handleData(final int dataHeadVersion, final int dataHeadLength, final byte[] data) {
		if (null != mITcpNetworkListner) {
			try {
				mITcpNetworkListner.handleData(dataHeadVersion, data);
			} catch (Throwable e) { // 业务层数据处理错误
				Log.i(TAG, "recv() handleData() Throwable", e.toString());
				mITcpNetworkListner.handleCode(ERRCODE_HANDLE_THROWABLE, null);
			}
		}
	}

	private Socket acquireSocketWithTimeOut(InetAddress dstAddress, int dstPort) throws IOException{
		Log.i("MMConnectionManager", "acquireSocketWithTimeOut, addr: " + dstAddress + ", port: " + dstPort);
		Socket socket = new Socket();
		socket.setSoLinger(false, 0);
		socket.connect(new InetSocketAddress(dstAddress, dstPort), MAX_CONNECT_TIMEOUT);
		return socket;
	}
	
	private boolean startSocket(final IPEndPoint ipPoint) throws IOException {
		if (!isSocketClosed()) {
			stopSocket();
		}
		
		mIPPoint = ipPoint;
		InetAddress serverAddr = InetAddress.getByName(ipPoint.getIp());
		mSocket = acquireSocketWithTimeOut(serverAddr, ipPoint.getPort());
		if (null == mSocket){
			return false;
		}
		
		switch (mMode) {
		case ASYNC_MODE: { // 异步模式
			mSocketWriter = new DataOutputStream(mSocket.getOutputStream());
			mSocketReader = new DataInputStream(mSocket.getInputStream());
		}
			break;
		case SYNC_MODE: { // 同步模式
			mSocket.setSoTimeout(SOCKET_READ_TIMEOUT);
		}
			break;
		default:
			break;
		}
		
		return isSocketConnected();
	}
	
	private boolean stopSocket() {
		if (isSocketClosed()) {
			return true;
		}
		
		// ===关reader
		if (!mSocket.isInputShutdown()) {
			try {
				mSocket.shutdownInput();
			} catch (Exception e) {
				Log.d(TAG, "stopSocket()", "mSocket.shutdownInput()", e);
			}
		}
		
		try {
			mSocketReader.close();
		} catch (Exception e) {
			Log.d(TAG, e);
		}
		
		// ===关writer
		if (!mSocket.isOutputShutdown()) {
			try {
				mSocket.shutdownOutput();
			} catch (Exception e) {
				Log.d(TAG, "stopSocket()", "mSocket.shutdownOutput()", e);
			}
		}
		
		try {
			mSocketWriter.close();
		} catch (Exception e) {
			Log.d(TAG, "stopSocket()", "mSocketWriter.close()", e);
		}
		
		boolean ret = true;
		try {
			mSocket.close();
			mSocket = null;
		} catch (IOException e) {
			ret = false;
			Log.d(TAG, "stopSocket()", "mSocket.close()", e);
		}
		return ret;
	}
	
	/**
	 * 从ip策略中尝试获取到可用ip，最多尝试3次
	 * @param iIPlot
	 * @return
	 *
	 * @author danyangguo in 2013-1-16
	 */
	private boolean checkSocketWithRetry(final IIpPlot iIPlot) {
		boolean ret = false;
		for (int i = 0; i < 3; ++i) {
			IPEndPoint ipPoint = iIPlot.getPlotIPPoint();
			if (null == ipPoint) {
				Log.d(TAG, "checkSocketWithRetry() getPlotIPPoint() is null");
				return false;
			}
	
			// 开始连接
			ret = checkSocket(ipPoint);
			Log.d(TAG, "start()", "checkSocket(ipPoint)", "ipPoint", ipPoint.toString(), "ret", ret);
			if (ret) { // 可连接了
				break;
			}
		}
		
		return ret;
	}
	
	private boolean checkSocket(final IPEndPoint ipPoint) {
		if (null == ipPoint) {
			return false;
		}
		
		if (isSocketConnected()) {
			return true;
		}
		
		boolean ret = false;
		try {
			ret = startSocket(ipPoint);
		} catch (UnknownHostException e) {
			Log.i(TAG, "checkSocket() UnknownHostException", e.toString());
			if (null != mITcpNetworkListner) {
				mITcpNetworkListner.handleCode(ERRCODE_DOMAIN_CONNECTFAILED, ipPoint);
			}
		} catch (Throwable t) {
			Log.i(TAG, "checkSocket() Throwable", t.toString());
			// 启动失败，通知外界
			if (null != mITcpNetworkListner) {
				mITcpNetworkListner.handleCode(ERRCODE_IP_CONNECTFAILED, ipPoint);
			}
		}
		
		return ret;
	}
	
	private boolean isSocketClosed() {
		if (null == mSocket) {
			return true;
		}
		
		return (null != mSocket && mSocket.isClosed());
	}
	
	private boolean isSocketConnected() {
		if (null == mSocket) {
			return false;
		}
		
		return (!isSocketClosed() && mSocket.isConnected());
	}
	
	private boolean isSocketBound() {
		if (null == mSocket) {
			return false;
		}
		
		return mSocket.isBound();
	}
	
	/**
	 * 仅供调试信息显示
	 * @return
	 *
	 * @author danyangguo in 2013-1-11
	 */
	public boolean getInfoSocketIsNull() {
		return (null == mSocket);
	}
	
	public boolean getInfoSocketIsOpened() {
		return isSocketConnected();
	}
	
	public boolean getInfoSocketIsBound() {
		return isSocketBound();
	}
	
	public boolean getInfoSocketIsOpen() {
		return !isSocketClosed();
	}

	public boolean getInfoIsInputShutdown() {
		if (null == mSocket) {
			return true;
		}
		
		return mSocket.isInputShutdown();
	}

	public boolean getInfoIsOutputShutdown() {
		if (null == mSocket) {
			return true;
		}

		return mSocket.isOutputShutdown();
	}

	public String getInfoSvrIp() {
		if (null == mSocket) {
			return "null";
		}
		
		if (null == mSocket.getInetAddress()) {
			return "null";
		}
		
		return mSocket.getInetAddress().toString();
	}

	public String getInfoLocalIp() {
		if (null == mSocket) {
			return "null";
		}

		return mSocket.getLocalAddress().toString();
	}

	public int getInfoLocalPort() {
		if (null == mSocket) {
			return 0;
		}

		return mSocket.getLocalPort();
	}
	
	public int getInfoSvrPort() {
		if (null == mSocket) {
			return 0;
		}

		return mSocket.getPort();
	}
	
	public IPEndPoint getCurIPEndPoint() {
		return mIPPoint;
	}
	
	@Override
	public boolean retryMe() {
		mIIpPlot.tryNext();
		
		try {
			// 睡眠3秒，待资源释放结束
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		boolean ret = reStart(mIIpPlot);
		return ret;
	}
	
}
