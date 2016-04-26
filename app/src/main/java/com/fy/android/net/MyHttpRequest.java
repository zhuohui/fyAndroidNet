package com.fy.android.net;



import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/*
 * 有反馈的http请求操作
 * */
    public class MyHttpRequest extends AbstractRequester {

    private String mActUrl = null;
    private boolean mbGet = true;
    private List<NameValuePair> paraPairList = new ArrayList<NameValuePair>();

    private HashMap<String, String> headers = new HashMap<String, String>();
    @Override
    HttpRequestData createData()
    {
        HttpRequestData rd = new HttpRequestData();
        rd.setUrl(mActUrl);
        rd.setGet(mbGet);
        if(!mbGet)
        {
            rd.addPostData(paraPairList);
        }
        rd.addHeader(headers);
        return rd;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addPost(String key, String value)
    {
        mbGet = false;
        BasicNameValuePair param = new BasicNameValuePair(key, value);
        paraPairList.add(param);
    }

    @Override
    NetResponse parseResponse(InputStream in)
    {
        NetResponse result = new NetResponse(NetResponse.EAdResponse);

        ByteArrayOutputStream os=new ByteArrayOutputStream();
        int c=0;
        try {
            while((c=in.read())!=-1)
            {
                os.write(c);
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            //System.out.println(e1);
            result.iResult=NetResponse.ENetworkError;
            result.setException(e1);
            if(this.listener != null)
            {
                this.listener.OnRequestComplete(result);
            }
            return result;
        }

        byte data[] = os.toByteArray();

        try {
            result.iResult = 0;
            result.resultStr = new String(data,"gb2312");
            //KLog.d("MyHttpRequest result=" + result.resultStr);

            return result;
        }catch (Exception e) {
            result.setException(e);
            e.printStackTrace();
        }

        return result;
    }

    public MyHttpRequest(String url)
    {
        mActUrl = url;
        mbGet = true;
    }
}
