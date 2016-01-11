package com.dyn.beans;

public class WeatherInfo {
	private String date = "";
	private String week = "";
	private String cityname = "";
	private String citycode = "";
	private String weather = "";
	private String weatherID = "";
	private String nongli = "";
	private String location = "";
	private String temperatureHight = "";
	private String temperatureLow = "";
	private String wind = "";
	private String windDirection = "";
	//湿度
	private String humidity = "";
	private String time = "";
//	private String errorCode = "";
//	private String reason = "";
	
	private boolean isRealTime = false;
	
	public boolean isRealTime() {
		return isRealTime;
	}

	public void setRealTime(boolean isRealTime) {
		this.isRealTime = isRealTime;
	}

	public String getWeatherID() {
		return weatherID;
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getCitycode() {
		return citycode;
	}

	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	public void setWeatherID(String weatherID) {
		this.weatherID = weatherID;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public String getCityname() {
		return cityname;
	}
	public void setCityname(String cityname) {
		this.cityname = cityname;
	}
	public String getWeather() {
		return weather;
	}
	public void setWeather(String weather) {
		this.weather = weather;
	}
	public String getNongli() {
		return nongli;
	}
	public void setNongli(String nongli) {
		this.nongli = nongli;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getTemperatureHight() {
		return temperatureHight;
	}
	public void setTemperatureHight(String temperatureHight) {
		this.temperatureHight = temperatureHight;
	}
	public String getTemperatureLow() {
		return temperatureLow;
	}
	public void setTemperatureLow(String temperatureLow) {
		this.temperatureLow = temperatureLow;
	}
	public String getWind() {
		return wind;
	}
	public void setWind(String wind) {
		this.wind = wind;
	}
	public String getWindDirection() {
		return windDirection;
	}
	public void setWindDirection(String windDirection) {
		this.windDirection = windDirection;
	}
	
}
