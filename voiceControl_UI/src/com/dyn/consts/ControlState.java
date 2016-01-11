package com.dyn.consts;

/**
 * model通过该类的命令参数处理不同的业务。
 * @author 邓耀宁
 *
 */
public class ControlState {

	/**
	 * 
	 */
	public static final int MODEL_GET_VCODE = 0;
	
	
	public static final int MODEL_SURE_REGISTER = 1;
	
	/**
	 * setting model control state
	 */
	public static final int MODEL_SETTING_CLEARCACHE = 20;
	
	/**
	 * View 
	 */
	public static final int VIEW_VCODE_CHANGE = 100;
	
	
	/**
	 * Model 天气查询
	 */
	public static final int MODEL_WEATHER_QUERY = 200;
	
	/**
	 * View 天气查询 
	 */
	public static final int VIEW_WEATHER_QUERY = 300;
}
