package com.eof.libs.base.network;

public class NetworkException extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -6884069089894826464L;

	String errorMsg;

	public String getErrorMsg() {
		return errorMsg;
	}

	public NetworkException(String detailMessage) {
		super(detailMessage);
		errorMsg = detailMessage;
	}

}
