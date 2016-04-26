package com.fy.android.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.Socket;

import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;

import org.apache.http.HttpResponse;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlSerializer;

import android.accounts.NetworkErrorException;
import android.text.TextUtils;
import android.util.Xml;

import com.fy.android.main.KLog;


public class HttpEngine {
	private static final String TAG = HttpEngine.class.getName();
	private static final int TIMEOUT = 15000; // 15s

	private HashMap<String, String> headers = new HashMap<String, String>();
	private DefaultHttpClient httpClient;

	// private ClientConnectionManager connectionManager;
	private String cookie;
	public int httpCode=-1;
	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	private int timeoutMs = TIMEOUT;


	public static HttpEngine create() {
		return new HttpEngine();
	}

	private HttpEngine() {
		httpClient = createHttpClient();

	}

	
	public void addHeader(String key, String value) {
		headers.put(key, value);
	}

	public void addHeader(HashMap<String, String> reqHeader)
	{
		this.headers.putAll(reqHeader);
	}

	public void reset() {
		headers.clear();
	}

	/**
	 * @param ms
	 *            set timeout to ms
	 * 
	 */
	public void setTimoutMs(int ms) {
		timeoutMs = ms;
		httpClient.getParams()
				.setIntParameter("http.socket.timeout", timeoutMs);
	}

	public static boolean isEmpty(CharSequence string)
	{
		if(string == null || string.length() == 0)
		{
			return true;
		}
		return false;
	}

	public static String getStackTrace(Throwable t)
	{
		if(t == null || t.getStackTrace() == null)
		{
			return null;
		}
		StackTraceElement[] stack = t.getStackTrace();
		String trace = "";
		for(StackTraceElement element : stack)
		{
			trace += element.toString();
			trace += "\n";
		}
		return trace;
	}
	/**
	 * @param request
	 *            send request
	 * @return return the response from server if got any. if any network error
	 *         happens,return null
	 * @throws NetworkErrorException
	 */
	public InputStream executeHttpRequest(HttpRequestData request)
			throws NetworkErrorException, Exception{
		HttpResponse response = null;
		HttpRequestBase httpRequest = null;
		// HttpsURLConnection httpsConn = null;
		if (request.getUrl() == null || request.getUrl().trim().length() == 0)
			return null;
		try {

			
			// HttpPost httpPost
			if (request.isGet())
				httpRequest = new HttpGet(request.getUrl());
			else
				httpRequest = new HttpPost(request.getUrl());


			if (!request.isGet() && request.getParamList() != null && request.getParamList().size() > 0) {

				HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
						request.getParamList());
				((HttpPost) httpRequest).setEntity(requestHttpEntity);
			}

			if (isEmpty(this.getCookie())) {
				httpRequest.setHeader("Cookie", this.getCookie());
			}

			
			// add HTTP headers
			Iterator<Entry<String, String>> it = headers.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = (Entry<String, String>) it.next();
				httpRequest.addHeader((String) entry.getKey(),
						(String) entry.getValue());

			}
			int statusCode;

			//response = httpClient.execute(httpRequest);
			response = httpClient.execute(httpRequest);
			statusCode = response.getStatusLine().getStatusCode();
			httpCode=statusCode;
			//KLog.d("Got response: " + "HTTP Code: " + statusCode + " " + httpRequest.getURI());

			switch (statusCode) {
			case 200:
				InputStream is = null;
				if (response != null) {
				    //碰到是文件就不需要在进行额外编码
					if (request.getFileDownloadFlag()) {
						 //取得相关信息 取得HttpEntiy  
		                HttpEntity httpEntity = response.getEntity();  
		                //获得一个输入流  
		                is = httpEntity.getContent();  
		                
					} else {
						// 针对可能存在乱码,先按默认方式转码,再返回gb2312编码的inputstream
						String tmpStr = EntityUtils.toString(
								response.getEntity(), HTTP.UTF_8);

						if (TextUtils.isEmpty(tmpStr)) {
							tmpStr = "";
						}
						is = new ByteArrayInputStream(tmpStr.getBytes("gb2312"));
					}
				}

				return is;
			case 302:
			case 301:
				// do redirect, this branch is just for feedback request
				XmlSerializer result = Xml.newSerializer();
				StringWriter writer = new StringWriter();
				result.setOutput(writer);
				result.startDocument("UTF-8", true);
				result.startTag("", "lottery");
				result.startTag("", "result");
				result.text("1");
				result.endTag("", "result");
				result.endTag("", "lottery");
				result.endDocument();
				String str = writer.toString();
				return new ByteArrayInputStream(str.getBytes(), 0, str.length());
			default:

				if (response != null) {
					//KLog.d("StatusLine: "+ response.getStatusLine().toString());
					response.getEntity().consumeContent();
				}
				throw new Exception("network error with error code "
						+ statusCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new NetworkErrorException(request.getUrl() + "\n"
					+ e.getMessage() + "\n" + getStackTrace(e));

		} finally {
            try {
                if (response != null && response.getEntity() != null && !request.getFileDownloadFlag()) {
                    response.getEntity().consumeContent();
                }
            } catch (Exception e) {
                throw new NetworkErrorException(request.getUrl() + "\n" + e.getMessage() + "\n"
                        + getStackTrace(e));
            }
		}
	}


	/**
	 * Create the default HTTP protocol parameters.
	 */
	private static final HttpParams createHttpParams() {
		final HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, TIMEOUT * 4); // socket
																// timeout is
																// 20s

		return params;
	}

	public static final DefaultHttpClient createHttpClient() {
		final SchemeRegistry supportedSchemes = new SchemeRegistry();
		SSLSocketFactory socketFactory = null;
		// SSL
		try {
			KeyStore trustStore;
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			socketFactory = new MySSLSocketFactory(trustStore);
		} catch (Exception e) {
			//KLog.e("createHttpClient",e);
			
		}
		if (socketFactory == null) {
			socketFactory = SSLSocketFactory.getSocketFactory();
		}
		X509HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
		socketFactory
				.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
		supportedSchemes.register(new Scheme("https", socketFactory, 443));
		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
		final SocketFactory sf = PlainSocketFactory.getSocketFactory();
		supportedSchemes.register(new Scheme("http", sf, 80));
		// Set some client http client parameter defaults.
		final HttpParams httpParams = createHttpParams();
		HttpClientParams.setRedirecting(httpParams, false);
		final ClientConnectionManager ccm = new ThreadSafeClientConnManager(
				httpParams, supportedSchemes);
		return new DefaultHttpClient(ccm, httpParams);
	}

	private static class MyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	private static class MyX509TrustManager implements X509TrustManager {
		public void checkClientTrusted(
				X509Certificate[] paramArrayOfX509Certificate,
				String paramString) throws CertificateException {
		}

		public void checkServerTrusted(
				X509Certificate[] paramArrayOfX509Certificate,
				String paramString) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	private static class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);
			TrustManager tm = new MyX509TrustManager();
			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}


		@Override
		public boolean isSecure(Socket sock) throws IllegalArgumentException {
			return true;
		}
	}

}
