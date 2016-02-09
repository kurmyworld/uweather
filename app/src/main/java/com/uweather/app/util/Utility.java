package com.uweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.uweather.app.db.UWeatherDB;
import com.uweather.app.model.City;
import com.uweather.app.model.County;
import com.uweather.app.model.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Button;

/**
 * Created by ringr on 2016/2/6.
 */
public class Utility {
    /**
     * 解析及处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse
    (UWeatherDB uWeatherDB,String response){
        if (!TextUtils.isEmpty(response)){
            String [] allProvinces = response.split(",");
            if (allProvinces!=null && allProvinces.length>0){
                for (String p : allProvinces){
                    String [] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    uWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器放回的市级数据
     */
    public static boolean handleCitiesResponse
    (UWeatherDB uWeatherDB,String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            String [] allCities = response.split(",");
            if (allCities!=null && allCities.length>0){
                for (String c : allCities){
                    String [] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    uWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountiesResponse
    (UWeatherDB uWeatherDB,String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            String [] allCounties = response.split(",");
            if (allCounties!=null && allCounties.length>0){
                for (String c : allCounties){
                    String [] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    uWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的天气数据
     */
    public static void handleWeatherResponse(Context context,String response){
        try {
            JSONArray array = new JSONObject(response).getJSONArray("HeWeather data service 3.0");
            //获取城市基本信息
            JSONObject basic = ((JSONObject)array.opt(0)).getJSONObject("basic");
            String weathreId = basic.getString("id");
            String city = basic.getString("city");
            String[] update = basic.getJSONObject("update").getString("loc").split(" ");
            //获取天气预报信息
            JSONArray daily = ((JSONObject)array.opt(0)).getJSONArray("daily_forecast");
            //获取第一天的信息（后期需要获取一周的信息）
            JSONObject day = daily.getJSONObject(0);
            JSONObject cond = day.getJSONObject("cond");
            String desp = cond.getString("txt_d");
            String night = cond.getString("txt_n");
            JSONObject tmp = day.getJSONObject("tmp");
            String max = tmp.getString("max");
            String min = tmp.getString("min");
            saveWeatherInfo(context,city,weathreId,max,min,desp,update);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中。
     */
    public static void saveWeatherInfo(Context context, String cityName,
                                       String weatherCode, String temp1, String temp2, String weatherDesp,
                                       String [] update) {
        String date = update[0];
        String time = update[1];
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", time);
        editor.putString("current_date", date);
        editor.commit();
    }
}
