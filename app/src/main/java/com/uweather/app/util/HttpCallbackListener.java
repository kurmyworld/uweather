package com.uweather.app.util;

/**
 * Created by ringr on 2016/2/6.
 */
public interface HttpCallbackListener {
    //存放页面回馈数据
    void onFinish(String response);
    //存放异常信息
    void onError(Exception e);
}
