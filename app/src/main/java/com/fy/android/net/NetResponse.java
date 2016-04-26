package com.fy.android.net;

public class NetResponse
{
	
	final public static int EAdResponse = 0;// 通用
	final public static int EGetAdResponse = 1;
	final public static int ESendInfoResponse = 2;

	final public static int EDownLoadResponse = 3;
	final public static int EFULLPICDownLoadResponse = 4;
	final public static int EImangeResponse = 5;
	final public static int EGetCityResponse = 6;
	 
	final public static int EBeforeRequest = -1;// 还未请求广告时的状态值
	final public static int ENetworkError = -2;	
	final public static int EParseError = 	-3;
	final public static int ERemoteError =  -4;	//服务端错误
	final public static int EOnlyWifiError = -5;
	final public static int ENetTimeOutError = -6;
	final public static int EExceptionError = -7;
	final public static int EError = -8;
	
	final public static int ESuccess = 0;
	final public static int EAdExpired = 2;// 广告过期
	final public static int EServerNoAd = 3;// 服务端无广告
	
	Exception exception=null;
	
	int type=0;
	public String resultStr = null;
	
	public int iResult = -1;
	
	public int getType()
	{
		return type;
	}
	public NetResponse(Error err)
	{
		exception = new Exception(err.getLocalizedMessage());
	}
	
	public NetResponse(Exception e)
	{
		exception=e;
	}
	public void setException(Exception e)
	{
		exception=e;
	}
	public Exception getException()
	{
		return exception;
	}
	public NetResponse(int type)
	{
		this.type=type;
	}
	public boolean isSuccess()
	{
		return iResult == ESuccess;
	}
	
	
	
}
