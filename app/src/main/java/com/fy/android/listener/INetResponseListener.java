package com.fy.android.listener;


import com.fy.android.net.NetResponse;

public interface INetResponseListener {
	
	/**
	 * 网络请求返回
	 * @param result
	 */
	public void OnRequestComplete(NetResponse result);
	
}
