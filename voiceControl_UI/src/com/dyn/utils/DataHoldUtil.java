package com.dyn.utils;

/**
 * 数据持久化静态类
 * @author 邓耀宁
 */
public class DataHoldUtil{
	/**
	 *  保存语音中心发送过来的json数据
	 */
	private static String speechJson = null;
	/**
	 *  保存搜索结果
	 */
	private static String searchName = null;
	/**
	 *  若有websearch或是app等关键字，则说明search结果正确。两个flag用来判断对应的关键字，
	 *  优先寻找websearch关键字，若有则websearchFlag = true,appFlag = false;反之websearchFlag = false,appFlag = true;
	 */
	private static boolean websearchFlag = false, appFlag = false;	
	/**
	 *  若service是第一次创建，则不执行ControlAPPService中的onStartCommand的if语句
	 */
    private static boolean isServiceFirstStart = false;
    
    private static boolean isSmartAnswer = false;

    private static boolean isCallPeople = false;
    
    private static boolean isAlarm = false;
    
    private static boolean isSend_SMS = false;
    
    private static boolean isOpenWebsite = false;
    
    private static boolean isOpenMap = false;
    
    private static boolean isWeather = false;
    
    
    public static boolean isWeather() {
		return isWeather;
	}

	public static void setWeather(boolean isWeather) {
		DataHoldUtil.isWeather = isWeather;
	}

	public static boolean isOpenMap() {
		return isOpenMap;
	}

	public static void setOpenMap(boolean isOpenMap) {
		DataHoldUtil.isOpenMap = isOpenMap;
	}

	// searchName的getter and setter
	public static String getSearchName() {
		return searchName;
	}

	public static void setSearchName(String saveSearchName) {
		DataHoldUtil.searchName = saveSearchName;
	}

	
	// results的getter and setter
	public static String getSpeechJson() {
		return speechJson;
	}

	public static void setSpeechJson(String saveResults) {
		DataHoldUtil.speechJson = saveResults;
	}
	
	// websearchFlag的getter and setter
	public static boolean isWebsearchFlag() {
		return websearchFlag;
	}

	public static void setWebsearchFlag(boolean websearchFlag) {
		DataHoldUtil.websearchFlag = websearchFlag;
	}

	
	// appFlag的getter and setter
	public static boolean isAppFlag() {
		return appFlag;
	}

	public static void setAppFlag(boolean appFlag) {
		DataHoldUtil.appFlag = appFlag;
	}

	
	// isServiceFirstStart的getter and setter
	public static boolean isServiceFirstStart() {
		return isServiceFirstStart;
	}

	public static void setServiceFirstStart(boolean isServiceFirstStart) {
		DataHoldUtil.isServiceFirstStart = isServiceFirstStart;
	}

	public static boolean isSmartAnswer() {
		return isSmartAnswer;
	}

	public static void setSmartAnswer(boolean isSmartAnswer) {
		DataHoldUtil.isSmartAnswer = isSmartAnswer;
	}

	public static boolean isCallPeople() {
		return isCallPeople;
	}

	public static void setCallPeople(boolean isCallPeople) {
		DataHoldUtil.isCallPeople = isCallPeople;
	}

	public static boolean isAlarm() {
		return isAlarm;
	}

	public static void setAlarm(boolean isAlarm) {
		DataHoldUtil.isAlarm = isAlarm;
	}

	public static boolean isSend_SMS() {
		return isSend_SMS;
	}

	public static void setSend_SMS(boolean isSend_SMS) {
		DataHoldUtil.isSend_SMS = isSend_SMS;
	}

	public static boolean isOpenWebsite() {
		return isOpenWebsite;
	}

	public static void setOpenWebsite(boolean isOpenWebsite) {
		DataHoldUtil.isOpenWebsite = isOpenWebsite;
	}


	
}
