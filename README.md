前言
--

大家都知道网络操作的响应时间是不定的，所有的网络操作都应该放在一个异步操作中处理，而且为了模块解耦，我们希望网络操作由专门的类来处理。所有网络数据发送，数据接收都有某几个类来实现，外部其它模块只要调用和处理回调函数即可。外部模块和网络模块之间的调用关系可以用如下图表示： 
![这里写图片描述](http://img.blog.csdn.net/20160411174237130)
调用端只要创建Request对象，设置参数，发起请求，即可。最后结果通过回调函数返回。右边的异步任务，HttpClient创建，参数解析，错误处理全部交给网络处理模块来完成。由此来看，这个网络模块其实是重复性很高的开发工作，为了避免重复造轮子，下面就给大家介绍下我们工作中实现的网络操作模块及相应类的实现。

1.调用端代码：
--------
Get或Post请求

```
protected void doWebTest()
    {
        String url = "http://www.target.com/abcd";
        
        try {
            MyHttpRequest http = new MyHttpRequest(url);
            //如果是Post请求，则设置.如果是get请求，请不要设置post参数
            //http.addPost("data", "testpostvalue");
			//post参数结束，get请求，不要添加上面addPost代码
            http.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            http.addHeader("Accept-Encoding", "gzip, deflate, sdch");
            http.addHeader("Accept-Language","zh-CN,zh;q=0.8");
            http.addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.80 Safari/537.36");

            http.StartRequest(new INetResponseListener() {
                @Override
                public void OnRequestComplete(NetResponse result) {
                    if(result.isSuccess())
                    {
                        String resultStr = result.resultStr;
                        //resultStr就是网络返回的数据
                    }else
                    {
                        Exception ee = result.getException();
                    }
               }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```
以上是调用端需要实现的源码，是不是很少代码就可以完成网络请求？下面就为大家揭开这个网络处理类的面纱。
2.主要类介绍
-------
1. AbstractRequester： 一个抽象的网络请求类
2. MyHttpRequest:：实例化的网络请求类。如果开发者有其它特殊处理需求，可以继承AbstractRequester 自己重新实现parseResponse
3. HttpEngine：HttpClient网络引擎类，最终的网络请求由这个类处理
4. HttpRequestData：传递给AsyncTask的参数类
5. NetResponse：返回给调用者的响应类
6. INetResponseListener：异步回调的interface

类之间的关系如下图所示：


![这里写图片描述](http://img.blog.csdn.net/20160412145335897)

**2.1 AbstractRequester**
抽象的网络管理类，类定义如下：

```
public abstract class AbstractRequester extends AsyncTask<HttpRequestData,Integer,NetResponse> {
	...
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
	
	//执行完成后的回调函数	
	protected void onPostExecute(NetResponse result) {
		super.onPostExecute(result);
		if(listener!=null)
		{
			listener.OnRequestComplete(result);
		}
	}
	
	//网络请求开始处理
	protected NetResponse doInBackground(HttpRequestData... reqArgs) {
		//因内容比较多。具体查看源码
	}
	
	
	public void StartRequest(INetResponseListener aListener){
	//具体查看源码
	}
}
```

对于一些有其它特殊需求的开发者，只需要继承这个类，并实现它的抽象接口，如果是通用需求，则可以直接使用下面的MyHttpRequest类：
```
abstract HttpRequestData createData();
abstract NetResponse parseResponse(InputStream in);
```
**2.2 MyHttpRequest**
继承并实现了AbstractRequester的网络处理类，支持Get和Post类型，支持自定义添加headers参数。
对于普通的网络请求和返回已经够用，开发者可以直接使用。

**2.3 HttpEngine**
网络请求引擎类。主要流程如下：

 1. 创建createHttpClient
 2. 判断参数有效性
 3. 判断get或post
 4. 创建对应的HttpGet或HttpPost
 5. 设置各类参数
 6. 调用httpClient.execute执行网络请求
 7. 解析和判断网络返回结果
 8. 生成Stream对象并返回

**2.4 HttpRequestData**

传递给AsyncTask的参数类，用于设置http请求的各类参数。

**2.5 NetResponse**
返回给调用者的类对象，包括状态、数据和异常。

**2.6 INetResponseListener**
定义很简单，源码如下：
```
public interface INetResponseListener {
	
	/**
	 * 网络请求返回
	 * @param result
	 */
	public void OnRequestComplete(NetResponse result);
	
}

```
调用者只要实现这个interface，异步接收返回结果即可，返回结果就是NetResponse对象。

 - 常见问题
-------
**1. AsyncTask说明** 
大家都知道AsyncTask是异步的，所以网络请求通过AsyncTask来实现，通过AyncTask的好处是轻量级异步，而且可以操作ui线程，比如更新进度条等。但AsyncTask也存在以下一些问题：

 - task需要在ui线程中创建和启用，所以AbstractRequester 对象的创建和调用StartRequest请在ui线程中完成
 - API 11也就是3.0以后，AsyncTask是排队执行，所以如果其中一个任务出现较耗时，其它网络请求需要等待；如果你想同步执行，要修改execute为executeOnExecutor(但不推荐做这个修改)

**2. 权限**
在xml中添加网络操作权限：

```
<uses-permission android:name="android.permission.INTERNET" />
```









