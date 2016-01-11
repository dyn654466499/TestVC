package com.dyn.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

/**
 * 以下函数只针对科大讯飞语音云端返回的Json结果进行解析
 * 
 * @author 鄧耀寧
 * @since 20140603
 */
public class JsonUtil {

	private static final String TAG = "JsonUtil";
	
	public static String getName(String json) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);
			joResult = joResult.getJSONObject("semantic");
			joResult = joResult.getJSONObject("slots");
			if(joResult.has("name"))ret.append(joResult.getString("name"));
		} catch (Exception e) {
			e.printStackTrace();
			ret.append(json);
		}
		return ret.toString();
	}

	public static HashMap<String,String> getWebsiteNameAndUrl(String json) {
		HashMap<String,String> result = null;
		try {
			result = new HashMap<String,String>();
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);
			joResult = joResult.getJSONObject("semantic");
			joResult = joResult.getJSONObject("slots");
			if(joResult.has("name"))result.put("name",joResult.getString("name"));
			if(joResult.has("url"))result.put("url",joResult.getString("url"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	
	public static String getKeywords(String json) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);
			joResult = joResult.getJSONObject("semantic");
			joResult = joResult.getJSONObject("slots");
			if(joResult.has("keywords"))ret.append(joResult.getString("keywords"));
		} catch (Exception e) {
			e.printStackTrace();
			ret.append(json);
		}
		return ret.toString();
	}

	public static String[] ResultArray(String json) {
		String[] result = new String[4];
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);
			result[0] = joResult.getString("rc");
			if("0".equals(result[0])){
				result[1] = joResult.getString("text");
				result[2] = joResult.getString("service");
				result[3] = joResult.getString("operation");
				}
				else {
				result[1] = joResult.getString("text");
				}
		} catch (Exception e) {
			e.printStackTrace();

		}

		return result;
	}


	public static boolean isHaveMoreResults(String json) {

		try {

			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			return joResult.has("moreResults");

		} catch (JSONException e) {

			e.printStackTrace();
		}
		return false;
	}

    /**
     * 该函数判断语音中心返回的json数据中是否存在service为app或是websearch的字段；
     * 若有，将其中的name或keywords提取出来作为搜索关键字。
     * @param joResult
     * @return name
     */
	public static String returnSearchName(JSONObject joResult) {
		String name = null;
		try {
			if ("websearch".equals(joResult.getString("service"))) {
				joResult = joResult.getJSONObject("semantic");
				joResult = joResult.getJSONObject("slots");
				name = joResult.getString("keywords");
				Log.v(TAG, "returnSearchName if:" + name);
				DataHoldUtil.setWebsearchFlag(true);
				DataHoldUtil.setAppFlag(false);
			} else if ("app".equals(joResult.getString("service"))) {
					joResult = joResult.getJSONObject("semantic");
					joResult = joResult.getJSONObject("slots");
					if (joResult.has("name")) {
						name = joResult.getString("name");
					}
					if (joResult.has("category")) {
						name = joResult.getString("category");
					}
					Log.v(TAG, "returnSearchName else if:" + name);
					DataHoldUtil.setWebsearchFlag(false);
					DataHoldUtil.setAppFlag(true);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return name;
	}

	/**
	 * 该函数解析整个json数据，优先寻找websearch关键字，返回其keywords的值；若没有，则返回app关键字的name值；
	 * 若两者都没有，则返回null。
	 * @param json
	 * @return
	 */
	public static String parseSearchResults(String json) {
		String name = null;
		String saveName = null;
		try {

			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);
			if (isHaveMoreResults(json)) {

				name = returnSearchName(joResult);
				if (DataHoldUtil.isWebsearchFlag() == true) {
					Log.v(TAG, "parseSearchResults if:return " + name);
					return name;
				}
				if (DataHoldUtil.isAppFlag() == true) {
					saveName = name;
				}
				JSONArray jsonArray = joResult.getJSONArray("moreResults");
				for (int i = 0; i < jsonArray.length(); i++) {// 遍历JSONArray

					JSONObject jb = jsonArray.getJSONObject(i);
					name = returnSearchName(jb);
					if (DataHoldUtil.isWebsearchFlag() == true) {
						Log.v(TAG, "parseSearchResults else if:return " + name);
						return name;
					}

				}
				// 当json数据遍历完之后，若没有找到websearch关键字，则返回app关键字对应的name
				if (DataHoldUtil.isAppFlag() == true) {
					return saveName;
				}
			} else {
				name = returnSearchName(joResult);
			}

		} catch (JSONException e) {
			name = "catch";
			e.printStackTrace();
		}
		return name;
	}
	
	/**
	 * 解析智能回答json
	 * @param json
	 * @return
	 */
	public static String parseSmartAnswer(String json){
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);
			joResult = joResult.getJSONObject("answer");
			ret.append(joResult.getString("text"));
		} catch (Exception e) {
			e.printStackTrace();
			ret.append(json);
		}

		return ret.toString();
	}
	/**
	 * 判断是否是天气查询
	 * @param json
	 * @return
	 */
	public static boolean isWeather(String json) {
		boolean result = false;
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joRoot = new JSONObject(tokener);
			do{
				if( !joRoot.has("service") ) 
					break;
				String str = joRoot.getString("service");
				if( !str.equals("weather") ) 
					break;
				
				result = true;
			}while(false);

		} catch (Exception e) {
		}
		return result;
	}
	
	/**
	 * 解析天气结果
	 * @param json
	 * @return 城市名和日期
	 * @throws JSONException
	 */
	public static ArrayList<String> parseWeatherResult(String json) throws JSONException {
		ArrayList<String> list = new ArrayList<String>();
		JSONTokener tokener = new JSONTokener(json);
		JSONObject joRoot = new JSONObject(tokener);
		
		if( !joRoot.has("service") ) 
			throw new JSONException(json);
		if( !joRoot.getString("service").equals("weather") ) 
			throw new JSONException(json);
		try{
			String strDate = joRoot.getJSONObject("semantic")
			  .getJSONObject("slots")
			  .getJSONObject("datetime")
			  .getString("date");
			
			String city_name = joRoot.getJSONObject("semantic")
					  .getJSONObject("slots")
					  .getJSONObject("location")
					  .getString("city");
			
			list.add(city_name);
			list.add(strDate);
		}catch(JSONException e){
			
		}
		return list;
	}
	
	/**
	 * 解析地图位置查询结果
	 * @param json
	 * @return 城市名和日期
	 * @throws JSONException
	 */
	public static ArrayList<String> parseMapPositionResult(String json) throws JSONException {
		ArrayList<String> list = new ArrayList<String>();
		JSONTokener tokener = new JSONTokener(json);
		JSONObject joRoot = new JSONObject(tokener);
		
		if( !joRoot.has("service") ) 
			throw new JSONException(json);
		if( !joRoot.getString("service").equals("map") ) 
			throw new JSONException(json);
		try{
			JSONObject location = joRoot.getJSONObject("semantic")
			  .getJSONObject("slots")
			  .getJSONObject("location");
			if(location.has("city")){
				list.add(location.getString("city"));
				String address = "";
				if(location.has("cityAddr")){
					address+=location.getString("cityAddr");
				}
				if(location.has("areaAddr")){
					address+=location.getString("areaAddr");
				}
				if(location.has("poi")){
					address+=location.getString("poi");
				}
				list.add(address);
			}else if(location.has("area")){
				list.add(location.getString("area"));
				String address = location.getString("areaAddr");
				if(location.has("poi")){
					address+=location.getString("poi");
				}
				list.add(address);
			}
			
		}catch(JSONException e){
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 解析地图路径规划查询结果
	 * @param json
	 * @return 城市名和日期
	 * @throws JSONException
	 */
	public static ArrayList<String> parseMapRouteResult(String json) throws JSONException {
		ArrayList<String> list = new ArrayList<String>();
		JSONTokener tokener = new JSONTokener(json);
		JSONObject joRoot = new JSONObject(tokener);
		
		if( !joRoot.has("service") ) 
			throw new JSONException(json);
		if( !joRoot.getString("service").equals("map") ) 
			throw new JSONException(json);
		try{
			JSONObject startLoc = joRoot.getJSONObject("semantic")
			  .getJSONObject("slots")
			  .getJSONObject("startLoc");
			if(startLoc.has("city")){
				list.add(startLoc.getString("city"));
				String address = "";
				if(startLoc.has("cityAddr")){
					address+=startLoc.getString("cityAddr");
				}
				if(startLoc.has("areaAddr")){
					address+=startLoc.getString("areaAddr");
				}
				if(startLoc.has("poi")){
					address+=startLoc.getString("poi");
				}
				list.add(address);
			}else if(startLoc.has("area")){
				list.add(startLoc.getString("area"));
				String address = startLoc.getString("areaAddr");
				if(startLoc.has("poi")){
					address+=startLoc.getString("poi");
				}
				list.add(address);
			}
			
			
			JSONObject endLoc = joRoot.getJSONObject("semantic")
					  .getJSONObject("slots")
					  .getJSONObject("endLoc");
					if(endLoc.has("city")){
						list.add(endLoc.getString("city"));
						String address = "";
						if(endLoc.has("cityAddr")){
							address+=endLoc.getString("cityAddr");
						}
						if(endLoc.has("areaAddr")){
							address+=endLoc.getString("areaAddr");
						}
						if(endLoc.has("poi")){
							address+=endLoc.getString("poi");
						}
						list.add(address);
					}else if(endLoc.has("area")){
						list.add(endLoc.getString("area"));
						String address = endLoc.getString("areaAddr");
						if(endLoc.has("poi")){
							address+=endLoc.getString("poi");
						}
						list.add(address);
					}
					
		}catch(JSONException e){
			e.printStackTrace();
		}
		return list;
	}
}
