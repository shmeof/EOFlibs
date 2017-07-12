package com.eof.libs.base.network;

/**
 * wifi认证异常
 *
 */
public class WifiApproveException extends Exception {
	private static final long serialVersionUID = 1L;

	public String getErrMsg() {
		String message = getMessage();
		if (message == null) {
			Throwable cause = getCause();
			if (cause != null)
				message = cause.getMessage();
		}

		return message != null ? message : "";
	}

	public WifiApproveException(String errMsg) {
		super(errMsg);
	}

	public WifiApproveException(Throwable t) {
		super(t.getMessage(), t);
	}

	public WifiApproveException(String errMsg, Throwable t) {
		super(errMsg, t);
	}
}
