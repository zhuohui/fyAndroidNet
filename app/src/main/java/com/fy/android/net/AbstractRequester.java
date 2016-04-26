package com.fy.android.net;


import java.io.InputStream;

import android.os.AsyncTask;

import com.fy.android.listener.INetResponseListener;



public abstract class AbstractRequester extends AsyncTask<HttpRequestData,Integer,NetResponse> {

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
		listener=null;
	}
	/**
	 * 根据需求创建传递给http发送的参数
	 * @return
	 */
	abstract HttpRequestData createData();
	/**
	 * 根据返回的流，解析生成相应的对象
	 * @param in
	 * @return
	 */
	abstract NetResponse parseResponse(InputStream in);

	INetResponseListener listener=null;
	
	@Override
	protected void onPostExecute(NetResponse result) {
		super.onPostExecute(result);
		if(listener!=null)
		{
			listener.OnRequestComplete(result);
		}
	}


	protected HttpEngine httpEngine=null;
	@Override
	protected NetResponse doInBackground(HttpRequestData... reqArgs) {

		NetResponse response=null;
		try {
			httpEngine=HttpEngine.create();
			httpEngine.addHeader(reqArgs[0].getHeaders());
			httpEngine.setCookie(reqArgs[0].getCookie());

		
			InputStream in= null;

			in = httpEngine.executeHttpRequest(reqArgs[0]);

			//KLog.d("doInBackground after httpresult, url = " + reqArgs[0].getUrl());
			response=parseResponse(in);
			
		}
		catch (Exception e) {
			response = new NetResponse(e);
			response.iResult = NetResponse.EExceptionError;
			//KLog.e("doInBackground Exception", e);
		}catch(Error error)
		{
			response = new NetResponse(error);
			response.iResult = NetResponse.EError;
			//KLog.e("doInBackground Error[" + error.getLocalizedMessage()+"]" , error);
		}
		
		return response;
	}

	public void StartRequest(INetResponseListener aListener)
	{
		this.listener=aListener;
		HttpRequestData[] params=new HttpRequestData[1];
		params[0]=createData();
		if(params[0]!=null) {
			this.execute(params);
		}
	}
	
	
}
