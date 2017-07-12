package com.eof.libs.base.network;

public interface INetworkProgress {
	void onProgress(boolean completed, int doneBytes, int totalBytes);
}