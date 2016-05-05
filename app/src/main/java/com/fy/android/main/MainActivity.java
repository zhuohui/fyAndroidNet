package com.fy.android.main;

import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.fy.android.listener.INetResponseListener;
import com.fy.android.net.MyHttpRequest;
import com.fy.android.net.NetResponse;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        doWebTest();

    }

    protected void doWebTest()
    {
        String url = "http://www.target.com/abcd";

        try {
            MyHttpRequest http = new MyHttpRequest(url);
            //http.addPost("data", "testpost");
            //post参数结束，get请求，不要添加上面setPost代码
            http.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            http.addHeader("Accept-Encoding", "gzip, deflate, sdch");
            http.addHeader("Accept-Language","zh-CN,zh;q=0.8");
           // http.addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.80 Safari/537.36");
            http.StartRequest(new INetResponseListener() {
                @Override
                public void OnRequestComplete(NetResponse result) {
                    if(result.isSuccess())
                    {
                        String resultStr = result.resultStr;

                        KLog.i("ret:" + resultStr.length());
                    }else
                    {
                        Exception ee = result.getException();
                        KLog.e("ret error:", ee);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
