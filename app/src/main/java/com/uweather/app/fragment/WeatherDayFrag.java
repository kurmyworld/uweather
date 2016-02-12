package com.uweather.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uweather.app.R;
import com.uweather.app.service.AutoUpdateService;
import com.uweather.app.util.HttpCallbackListener;
import com.uweather.app.util.HttpUtil;
import com.uweather.app.util.LogUtil;
import com.uweather.app.util.Utility;

/**
 * Created by ringr on 2016/2/12.
 */
public class WeatherDayFrag extends Fragment {

    public final static String COUNTY_CODE = "county_code";

    private Activity mainActivity;

    private LinearLayout weatherInfoLayout;
    //显示发布时间
    private TextView publishText;
    //显示天气描述信息
    private TextView weatherDespText;
    //显示最低气温
    private TextView temp1Text;
    //显示最高气温
    private TextView temp2Text;
    //显示更新时间
    private TextView currentDateText;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_main,container,false);
        mainActivity = getActivity();
        weatherInfoLayout = (LinearLayout)view.findViewById(R.id.weather_info_layout);
        publishText = (TextView)view.findViewById(R.id.publish_text);
        weatherDespText = (TextView)view.findViewById(R.id.weather_desp);
        temp1Text = (TextView)view.findViewById(R.id.temp1);
        temp2Text = (TextView)view.findViewById(R.id.temp2);
        currentDateText = (TextView)view.findViewById(R.id.current_date);
        String countyCode = getArguments().getString(COUNTY_CODE,"");
        if (!TextUtils.isEmpty(countyCode)){
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }else{
            showWeather();
        }
        return view;
    }
    /**
     * 查询县级代号所对应的天气代号。
     */
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }
    /**
     * 查询天气代号所对应的天气。
     */
    private void queryWeatherInfo(String weatherCode) {
        String address = "https://api.heweather.com/x3/weather?cityid="+weatherCode+"&key=2d1fa4e29e7e4871994f03550cdeec64";
        LogUtil.d("HomeActivity", address);
        queryFromServer(address, "weatherCode");
    }
    /**
     * 根据类型从服务器上查询信息
     */
    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = "CN" + array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    Utility.handleWeatherResponse(mainActivity, response);
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    /**
     * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
     */
    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDateText.setText(prefs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(mainActivity, AutoUpdateService.class);
        mainActivity.startService(intent);
    }
}
