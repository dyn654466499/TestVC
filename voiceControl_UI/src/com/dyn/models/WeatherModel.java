package com.dyn.models;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.dyn.beans.WeatherInfo;
import com.dyn.utils.CommonUtil;
import com.dyn.utils.NetUtil;
import com.dyn.utils.SharedPreferenceUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import static com.dyn.consts.ControlState.*;

/**
 * 处理天气的model
 * @author 邓耀宁
 *
 */
@SuppressLint("NewApi") 
public class WeatherModel extends BaseModel{
	private static final String DEF_CHATSET = "UTF-8";
	private static final int DEF_CONN_TIMEOUT = 30000;
    private static final int DEF_READ_TIMEOUT = 30000;
    private static final String userAgent =  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";
    private static final String WEATHER_APP_KEY ="a8b3e410b281f5105ebb23da5ca0703c";
    private static final String weather_url = "http://op.juhe.cn/onebox/weather/query";
    /**
     * 搜索第几天天气的索引
     */
    private int searchIndex = 0;
    private String searchDate = "";
    
    private Context mContext;
	public WeatherModel(Handler handler,Context context) {
		super(handler);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	@Override
	public void changeModelState(int changeState) {
		// TODO Auto-generated method stub
		switch (changeState) {
		case MODEL_WEATHER_QUERY:
			String result = startWeatherQuery(SharedPreferenceUtils.getCurrentCity(mContext));
			Object info = parseJson(result);
			Message.obtain(handler, VIEW_WEATHER_QUERY, searchIndex,-1,info).sendToTarget();
			break;

		default:
			break;
		}
	}

	@Override
	public void changeModelState(Message changeStateMessage) {
		// TODO Auto-generated method stub
		switch (changeStateMessage.what) {
		case MODEL_WEATHER_QUERY:
			ArrayList<String> list = (ArrayList<String>)changeStateMessage.obj;
			if(list!=null && list.size()>1){
			searchDate = list.get(1);
			
			String city = "";
			if("CURRENT_CITY".equals(list.get(0))) 
				city = SharedPreferenceUtils.getCurrentCity(mContext);
			else 
				city = list.get(0);
			String result = startWeatherQuery(city);
			
			Object info = parseJson(result);
			Message.obtain(handler, VIEW_WEATHER_QUERY, searchIndex,-1,info).sendToTarget();
			}else{
				Message.obtain(handler, VIEW_WEATHER_QUERY, searchIndex,-1,"未知错误").sendToTarget();
			}
			break;

		default:
			break;
		}
	}

	private Object parseJson(String json){
		StringBuffer ret = new StringBuffer();
		Object result = "未知的错误";
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);
			int error_code = joResult.getInt("error_code");
			switch (error_code) {
			case 0:
				LinkedList<WeatherInfo> info = new LinkedList<WeatherInfo>();
				JSONObject data = joResult.getJSONObject("result").getJSONObject("data");
				if(data.has("realtime")){
				JSONObject realTime = data.getJSONObject("realtime");
				WeatherInfo todayWeather = new WeatherInfo();
				//城市代码
				todayWeather.setCitycode(realTime.getString("city_code"));
				//城市名称
				todayWeather.setCityname(realTime.getString("city_name"));
				//日期
				todayWeather.setDate(realTime.getString("date"));
				//时间
				todayWeather.setTime(CommonUtil.getFormatTime(System.currentTimeMillis()));//realTime.getString("time")
				//星期几,值是阿拉伯数字
				realTime.getInt("week");
				//不懂
				realTime.getString("moon");
				//数据更新的时间
				realTime.getString("dataUptime");
				
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
				//判断是否是实时天气
				todayWeather.setRealTime(true);
				
				info.add(todayWeather);
				}
				
				if(data.has("weather")){
				     JSONArray weather = data.getJSONArray("weather");
				     int days = weather.length() > 5 ? 5 : weather.length();
				     for (int i = 0; i < days; i++) {
				    	 WeatherInfo weatherInfo = new WeatherInfo();
				    	 JSONObject day = weather.getJSONObject(i);
				    	 if(searchDate.equals(day.getString("date")))searchIndex = i;
				    	 
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
				
				result = info;
				break;
				
			//错误的查询城市名	
			case 207301:
				result = "sorry，亲，您输入错误的城市名，请重试！\n请按确定键结束";
				break;
				
			//查询不到该城市的相关信息
			case 207302:
				result = "sorry，亲，查询不到该城市的相关信息，请重试！\n请按确定键结束";
				break;
				
			//网络错误，请重试	
			case 207303:
				result = "sorry，亲，您的网络出现错误，请重试！\n请按确定键结束";
				break;
				
				//错误的请求KEY
			case 10001:	
				result = "sorry，亲，本应用的KEY出错了，请期待下个版本的更新！\n请按确定键结束";
				break;
				
				//该KEY无请求权限	
			case 10002:	
				result = "sorry，亲，本应用的KEY暂无请求权限，请期待下个版本的更新！\n请按确定键结束";
				break;
				
				//KEY过期	
			case 10003:
				result = "sorry，亲，本应用的KEY过期了，请期待下个版本的更新！\n请按确定键结束";
				break;
				
				//错误的OPENID
			case 10004:
				result = "sorry，亲，本应用的OPENID出错了，请期待下个版本的更新！\n请按确定键结束";
				break;
				
				//应用未审核超时，请提交认证	
			case 10005:
				result = "sorry，亲，本应用还未审核，请期待下个版本的更新！\n请按确定键结束";
				break;
				
				//未知的请求源	
			case 10007:
				result = "sorry，亲，您当前的IP请求次数超出限制！\n请按确定键结束";
				break;
				
				//被禁止的IP	
			case 10008:
				result = "sorry，亲，您当前的IP被禁止，请检查！\n请按确定键结束";
				break;
				
				//被禁止的KEY	
			case 10009:
				result = "sorry，亲，本应用的天气测试KEY被禁止了，请期待下个版本的更新！\n请按确定键结束";
				break;
				
				//当前IP请求超过限制	
			case 10011:
				result = "sorry，亲，您当前的IP请求次数超出限制！\n请按确定键结束";
				break;
				
				//请求超过次数限制	
			case 10012:
				result = "sorry，亲，超出了请求次数限制，使用天气接口还未审核！\n请按确定键结束";
				break;
				
				//测试KEY超过请求限制
			case 10013:
				result = "sorry，亲，超出了KEY请求次数限制，使用天气接口还未审核！\n请按确定键结束";
				break;
				
				//系统内部异常，请重试	
			case 10014:
				result = "sorry，亲，天气服务器异常，请重试！\n请按确定键结束";
				break;
				
				//接口维护	
			case 10020:
				result = "sorry，亲，天气接口正在维护中！\n请按确定键结束";
				break;
				
				// 接口停用
			case 10021:
				result = "sorry，亲，天气接口已被停用！\n请按确定键结束";
				break;
				
			default:
				break;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			ret.append(json);
		}

		return result;
	}
	
	 /**
	  * 根据城市查询天气
	  * @param cityName
	  * @return
	  */
    public String startWeatherQuery(String cityName){
        String result = "";
        Map<String, Object> params = new HashMap<String, Object>();//请求参数
            params.put("cityname",cityName);//要查询的城市，如：温州、上海、北京
            params.put("key",WEATHER_APP_KEY);//应用APPKEY(应用详细页查询)
            params.put("dtype","json");//返回数据的格式,xml或json，默认json
 
        try {
            result =httpRequest(weather_url, params, "GET");
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
    
	
	 /**
    *
    * @param strUrl 请求地址
    * @param params 请求参数
    * @param method 请求方法
    * @return  网络请求字符串
    * @throws Exception
    */
   private String httpRequest(String strUrl, Map<String, Object> params,String method) throws Exception {
       HttpURLConnection conn = null;
       BufferedReader reader = null;
       String rs = null;
       try {
           StringBuffer sb = new StringBuffer();
           if(method==null || method.equals("GET")){
               strUrl = strUrl+"?"+NetUtil.urlencode(params);
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
                   out.writeBytes(NetUtil.urlencode(params));
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
   
}
