package com.fy.android.net;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HttpRequestData {
	
	private String url;

    private String encode = "utf-8";
    private boolean get=false;
    private String cookie=null;
    private boolean isFileDownload = false;
	private HashMap<String, String> headers = new HashMap<String, String>();
	private List<NameValuePair> paraPairList = new ArrayList<NameValuePair>();


    public void setFileDownFlag()
    {
    	isFileDownload = true;
    }

	public HashMap<String, String> getHeaders()
	{
		return this.headers;
	}

	public void addHeader(HashMap<String, String> reqHeader) {
		headers.putAll(reqHeader);
	}


	public boolean getFileDownloadFlag()
    {
    	return isFileDownload;
    }
    
    
    public String getCookie() {
		return cookie;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	public String getEncode()
    {
        return encode;
    }
    public void setEncode(String encode)
    {
        this.encode = encode;
    }
    public String getUrl()
    {
        return url;
    }
    public void setUrl(String url)
    {
        this.url = url;
    }


	public void addPostData(List<NameValuePair> pList)
	{
		paraPairList.addAll(pList);
	}
	public void addPostData(String key, String value)
	{
		BasicNameValuePair param = new BasicNameValuePair(key, value);
		paraPairList.add(param);
	}

	public List<NameValuePair> getParamList()
	{
		return paraPairList;
	}

	public boolean isGet() {
		return get;
	}
	public void setGet(boolean get) {
		this.get = get;
	}


}
