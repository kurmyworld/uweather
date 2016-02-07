package com.uweather.app.util;

import android.util.Log;

/**
 * Created by ringr on 2016/2/7.
 */
public class LogUtil {
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;
    public static final int LEVEL = VERBOSE;

    public static void v(String tag,String msg){
        if (LEVEL <= VERBOSE){
            Log.d(tag,msg);
        }
    }
    public static void d(String tag,String msg){
        if (LEVEL <= DEBUG){
            Log.d(tag,msg);
        }
    }
    public static void i(String tag,String msg){
        if (LEVEL <= INFO){
            Log.d(tag,msg);
        }
    }
    public static void w(String tag,String msg){
        if (LEVEL <= WARN){
            Log.d(tag,msg);
        }
    }
    public static void e(String tag,String msg){
        if (LEVEL <= ERROR){
            Log.d(tag,msg);
        }
    }
}
