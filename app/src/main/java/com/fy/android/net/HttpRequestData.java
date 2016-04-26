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

	 public final static byte[] ecodeStr2 =
		{0x7, 0x77, (byte) 0xd5,
		  (byte) 0xc1, 0x7d, 0x40, 0x66,(byte) 0xb8 };
	 
	 
	 /*
		 *生成密钥 encodeKeyA.length=24
		 */
		public static byte[] genCroptyKey(byte[] encodeKeyA, String randomStrB)
		{
			if (encodeKeyA == null)
			{
				return null;
			}
			
			//byte[] B = new byte[24];
			byte[] C =null;
			try {
				C=randomStrB.getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		
			int clen = C.length;
			if (encodeKeyA.length != 24 || (clen < 8 || clen > 20))
				return null;
			
			byte[] result = new byte[24];
			for (int i = 0; i < encodeKeyA.length; i++)
			{ //0 &  1 |  2 ^

				switch (i % 3)
				{
					case 0:
						result[i] = (byte) (encodeKeyA[i] & (C[i%C.length]+i));
						break;
					case 1:
						result[i] = (byte) (encodeKeyA[i] | (C[i%C.length]+i));
						break;
					case 2:
						result[i] = (byte) (encodeKeyA[i] ^ (C[i%C.length]+i));
						break;
				}

			}

			//System.out.println("key=" + result.toString());

			return result;
		}
}
