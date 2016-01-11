package com.dyn.utils;

import android.annotation.SuppressLint;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.dyn.beans.WeatherInfo;

@SuppressLint("NewApi") 
public class TestWeather {
	public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent =  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";
 
    //配置您申请的KEY
    public static final String APPKEY ="a8b3e410b281f5105ebb23da5ca0703c";
 
    //1.根据城市查询天气
    public static String getRequest1(){
        String result = "";
        String url ="http://op.juhe.cn/onebox/weather/query";//请求接口地址
        Map<String, Object> params = new HashMap<String, Object>();//请求参数
            params.put("cityname","深圳");//要查询的城市，如：温州、上海、北京
            params.put("key",APPKEY);//应用APPKEY(应用详细页查询)
            params.put("dtype","json");//返回数据的格式,xml或json，默认json
 
        try {
            result =net(url, params, "GET");
            JSONObject object = new JSONObject(result);
            if(object.getInt("error_code")==0){
                System.out.println(object.get("result"));
            }else{
                System.out.println(object.get("error_code")+":"+object.get("reason"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
 
    public static List<WeatherInfo> parseJson(String json){
		StringBuffer ret = new StringBuffer();
		LinkedList<WeatherInfo> info = new LinkedList<WeatherInfo>();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);
			int error_code = joResult.getInt("error_code");
			if(0==error_code){
				JSONObject data = joResult.getJSONObject("result").getJSONObject("data");
				if(data.has("realTime")){
				JSONObject realTime = data.getJSONObject("realTime");
				WeatherInfo todayWeather = new WeatherInfo();
				//城市代码
				todayWeather.setCitycode(realTime.getString("city_code"));
				//城市名称
				todayWeather.setCityname(realTime.getString("city_name"));
				//日期
				todayWeather.setDate(realTime.getString("date"));
				//时间
				todayWeather.setTime(realTime.getString("time"));
				//星期几,值是阿拉伯数字
				realTime.getInt("week");
				//不懂
				realTime.getString("moon");
				//数据更新的时间
				realTime.getString("dataUpTime");
				
				JSONObject real_weather = realTime.getJSONObject("weather");
				//当前温度
				todayWeather.setTemperatureHight(real_weather.getString("temperature"));
                //当前湿度
				todayWeather.setHumidity(real_weather.getString("humidity"));
				//当前天气
				todayWeather.setWeather(real_weather.getString("info"));
				//不懂
				real_weather.getString("img");
				
				JSONObject real_wind = realTime.getJSONObject("wind");
				//风向
				todayWeather.setWindDirection(real_wind.getString("direct"));
				//风力
				todayWeather.setWind(real_wind.getString("power"));
				info.add(todayWeather);
				}
				if(data.has("weather")){
				     JSONArray weather = data.getJSONArray("weather");
				     int days = weather.length() > 5 ? 5 : weather.length();
				     for (int i = 0; i < days; i++) {
				    	 WeatherInfo weatherInfo = new WeatherInfo();
				    	 JSONObject day = weather.getJSONObject(i);
				    	 weatherInfo.setDate(day.getString("date"));
				    	 weatherInfo.setWeek(day.getString("week"));
				    	 weatherInfo.setNongli(day.getString("nongli"));
				    	 
				    	 JSONArray weather_info_day = day.getJSONObject("info").getJSONArray("day");
				    	 weatherInfo.setWeatherID(weather_info_day.getString(0));
				    	 weatherInfo.setWeather(weather_info_day.getString(1));
				    	 weatherInfo.setTemperatureHight(weather_info_day.getString(2));
				    	 weatherInfo.setWindDirection(weather_info_day.getString(3));
				    	 weatherInfo.setWind(weather_info_day.getString(4));
				    	 
				    	 
				    	 JSONArray weather_info_night = day.getJSONObject("info").getJSONArray("night");
				    	 weatherInfo.setTemperatureLow(weather_info_night.getString(2));
				    	 
				    	 
				    	 info.add(weatherInfo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret.append(json);
		}

		return info;
	}
 
    public static void main(String[] args) {
 
    }
 
    /**
     *
     * @param strUrl 请求地址
     * @param params 请求参数
     * @param method 请求方法
     * @return  网络请求字符串
     * @throws Exception
     */
    public static String net(String strUrl, Map<String, Object> params,String method) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            if(method==null || method.equals("GET")){
                strUrl = strUrl+"?"+urlencode(params);
            }
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if(method==null || method.equals("GET")){
                conn.setRequestMethod("GET");
            }else{
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent", userAgent);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params!= null && method.equals("POST")) {
                try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
                    out.writeBytes(urlencode(params));
                }
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }
 
    //将map型转为请求参数型
    public static String urlencode(Map<String, ?> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, ?> i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
