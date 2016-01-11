package com.dyn.beans;

import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;

public class NavigationInfo {
	/**
	 * 经度
	 */
	private double longitude; 
	/**
	 * 纬度
	 */
	private double latitude; 
	/**
	 * 地名
	 */
	private String place; 
	/**
	 * 地点描述
	 */
	private String description; 
	/**
	 * 坐标类型
	 */
	private CoordinateType coType;
	
	
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public CoordinateType getCoType() {
		return coType;
	}
	public void setCoType(CoordinateType coType) {
		this.coType = coType;
	}
	
	
}
