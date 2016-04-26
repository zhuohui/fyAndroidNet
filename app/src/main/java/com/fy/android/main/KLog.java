package com.fy.android.main;

import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class KLog
{
	public final static String TAG= "fyNetAndroid";
	public final static String Enter = "\r\n";
	public final static String Tab3 = "\t\t\t";
	

    public final static int LEVEL_ERROR = 0;
    public final static int LEVEL_WARN = 1;
    public final static int LEVEL_INFO = 2;
    public final static int LEVEL_DEBUG = 3;
    public final static int LEVEL_VERBOSE = 4;
    public static int log_level			= LEVEL_VERBOSE;
    


  
    public static void e(String msg)
    {
        if(log_level >= LEVEL_ERROR)
        {
            Log.e(TAG, msg);

        }
    }
  
       
   
    
    public static void w(String msg)
    {
        if(log_level >= LEVEL_WARN)
        {
            Log.w(TAG, msg);

        }
    }
    
  
    
    public static void i(String msg)
    {
        if(log_level >= LEVEL_INFO)
        {
            Log.i(TAG, msg);
        }
    }
    
  
    public static void d(String msg)
    {
        if(log_level >= LEVEL_DEBUG)
        {
            Log.d(TAG, msg);
        }
    }
    
    public static void v(String msg)
    {
        if(log_level >= LEVEL_VERBOSE)
        {
            Log.v(TAG, msg);
        }
    }
  
    
    
    public static void e(String msg, Throwable tr)
    {
    	if(log_level >= LEVEL_ERROR)
        {
            Log.e(TAG, msg, tr);
            
            StackTraceElement [] trlist = tr.getStackTrace();
            
            String message = msg + Enter;
            
            message += Tab3 + tr.toString() + Enter;
            
            for(StackTraceElement el : trlist)
            {
            	message += Tab3 + el.toString() + Enter;
            }

        }
    }
    

}
