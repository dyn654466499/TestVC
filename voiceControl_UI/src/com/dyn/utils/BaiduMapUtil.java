package com.dyn.utils;

import static com.dyn.consts.Constants.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.dyn.voicecontrol.R;

/**
 * 百度地图工具类
 * @author 邓耀宁
 */
public class BaiduMapUtil {

	/**
	 * 创建一个mapview
	 * @param context
	 * @return MapView实例
	 */
	public static MapView getBaiduMapView(Context context){
		MapView mMapView = new MapView(context, new BaiduMapOptions().mapStatus(new MapStatus.Builder().zoom(MAP_BASE_ZOOM).build()));
		return mMapView;
	}
	
	/**
	 * 根据经纬度创建一个mapview
	 * @param context
	 * @param latitude
	 * @param longitude
	 * @return MapView实例
	 */
	public static MapView getBaiduMapView(Context context,double latitude,double longitude){
		LatLng p = new LatLng(latitude, longitude);
		MapView mMapView= new MapView(context,new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(p).zoom(MAP_BASE_ZOOM).build()));
		mMapView.getMap().addOverlay(new MarkerOptions().position(p).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka)));
		return mMapView;
	}
	
	/**
	 * 开始定位,并保存所在城市
	 * @param context
	 */
	public static void startLocation(final Context context,final Handler handler,final boolean isShowTip) {
		// 定位初始化
		final LocationClient mLocClient = new LocationClient(context.getApplicationContext());
		mLocClient.registerLocationListener(new BDLocationListener() {
			
			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				 // map view 销毁后不在处理新接收的位置
	            if (location == null) {
	            	if(handler!=null)Message.obtain(handler, LOCATION_FAIL).sendToTarget();
	            	mLocClient.stop();
	                return;
	            }
	            SharedPreferenceUtils.setCurrentCity(context, location.getCity());
	            SharedPreferenceUtils.setCurrentAddress(context, location.getAddrStr());
	            if(isShowTip)Toast.makeText(context, "定位成功，您所在的城市是"+location.getCity(), Toast.LENGTH_SHORT).show();
	            
	            SharedPreferences preferences = context.getSharedPreferences(CURRENT_LOCATION,Context.MODE_PRIVATE);
	            Editor editor = preferences.edit();
	            editor.putFloat(CURRENT_LATITUDE, (float)location.getLatitude());
	            editor.putFloat(CURRENT_LONGITUDE, (float)location.getLongitude());
	            editor.commit();
	            
	            mLocClient.stop();
	            if(handler!=null)Message.obtain(handler, LOCATION_SUCCEED).sendToTarget();
	        }
		});
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(0);
		//必须加上need address才能获取到城市，坑！
		option.setIsNeedAddress(true);
		option.setIsNeedLocationDescribe(true);
		option.setNeedDeviceDirect(true);
		option.setIsNeedLocationPoiList(true);
		option.setPriority(LocationClientOption.GpsFirst); 
		option.setIsNeedLocationPoiList(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}
	
	/**
	 * 开始定位
	 * @param context
	 * @return 定位的mapview
	 */
	public static MapView startLocation(final Context context,final Handler handler) {
		final MapView mapView = getBaiduMapView(context);
		final BaiduMap mBaiduMap = mapView.getMap();
		// 定位初始化
		final LocationClient mLocClient = new LocationClient(context.getApplicationContext());
		mLocClient.registerLocationListener(new BDLocationListener() {
			
			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				 // map view 销毁后不在处理新接收的位置
	            if (location == null || mapView == null) {
	            	Toast.makeText(context, "抱歉，定位失败", Toast.LENGTH_LONG).show();
	            	if(handler!=null)Message.obtain(handler, LOCATION_FAIL).sendToTarget();
	            	mLocClient.stop();
	                return;
	            }
	            mBaiduMap.clear();
                LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(ll,MAP_BASE_ZOOM));
				mBaiduMap.addOverlay(new MarkerOptions().position(ll).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka)));
	            /**
	             * 弹出气泡
	             */
				 setPopupOverlay(context, mBaiduMap, location.getAddrStr(), ll);
				
				 SharedPreferenceUtils.setCurrentCity(context, location.getCity());
		         SharedPreferenceUtils.setCurrentAddress(context, location.getAddrStr());
		            
		         SharedPreferences preferences = context.getSharedPreferences(CURRENT_LOCATION,Context.MODE_PRIVATE);
		         Editor editor = preferences.edit();
		         editor.putFloat(CURRENT_LATITUDE, (float)location.getLatitude());
		         editor.putFloat(CURRENT_LONGITUDE, (float)location.getLongitude());
		         editor.commit();
				
				mLocClient.stop();
				if(handler!=null)Message.obtain(handler, LOCATION_SUCCEED).sendToTarget();
	        }

			public void onReceivePoi(BDLocation poiLocation) {
			
			}
		});
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(0);
		//必须加上need address才能获取到城市，坑！
		option.setIsNeedAddress(true);
		option.setIsNeedLocationDescribe(true);
		option.setNeedDeviceDirect(true);
		option.setIsNeedLocationPoiList(true);
		option.setPriority(LocationClientOption.GpsFirst); 
		option.setIsNeedLocationPoiList(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
		
//		option.setLocationMode(LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//		option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//		int span=1000;
//		option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//		option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
//		option.setOpenGps(true);//可选，默认false,设置是否使用gps
//		option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
//		option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//		option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//		option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
//		option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
//		option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		return mapView;
	}
	
	/**
	 * 定位某个地点。
	 * @param context 上下文实例
	 * @param city 城市名
	 * @param address 地点
	 * @return 定位的mapview
	 */
	public static MapView locateSearchPlace(final Context context,final String city,final String address){
		MapView mapView = null;
		LatLng latLng = getCurrentCityLatLng(context);
		if(latLng!=null){
			mapView = getBaiduMapView(context,latLng.latitude,latLng.longitude);
		}else 
			mapView = getBaiduMapView(context);
		final BaiduMap mBaiduMap = mapView.getMap();
		// 初始化搜索模块，注册事件监听
		final GeoCoder mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
			@Override
			public void onGetGeoCodeResult(GeoCodeResult result) {
				if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
					Toast.makeText(context, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
					return;
				}
				LatLng ll = result.getLocation();
				
				mBaiduMap.clear();
				mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon_marka)));
				//mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(ll,MAP_BASE_ZOOM));
				
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(new LatLngBounds.Builder().include(ll).build()));
				/**
				 * 弹出气泡
				 */
				setPopupOverlay(context, mBaiduMap, result.getAddress(), ll);
				
				String strInfo = String.format("纬度：%f 经度：%f",
						result.getLocation().latitude, result.getLocation().longitude);
				Toast.makeText(context, strInfo, Toast.LENGTH_LONG).show();
				mSearch.destroy();
			}

			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
				if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
					Toast.makeText(context, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
					return;
				}
				mBaiduMap.clear();
				mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon_marka)));
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(result
						.getLocation(),MAP_BASE_ZOOM));
				Toast.makeText(context, result.getAddress(),
						Toast.LENGTH_LONG).show();
				mSearch.destroy();
			}
		});
		mSearch.geocode(new GeoCodeOption().city(city).address(address));
		return mapView;
	}
	
	/**
	 * 获取保存的当前城市的经纬度
	 * @param context 
	 * @return 如果没有保存，则返回null。
	 */
	public static LatLng getCurrentCityLatLng(Context context){
		SharedPreferences preferences = context.getSharedPreferences(CURRENT_LOCATION,Context.MODE_PRIVATE);
		float latitude = preferences.getFloat(CURRENT_LATITUDE, 0);
		float longtitude = preferences.getFloat(CURRENT_LONGITUDE, 0);
		if(latitude!=0 && longtitude!=0){
			return new LatLng(latitude,longtitude);
		}
		return null;
	}
	
	/**
	 * 弹出地点气泡
	 * @param context 上下文
	 * @param mBaiduMap 百度地图实例
	 * @param title 标题
	 * @param ll 经纬度
	 */
	public static void setPopupOverlay(Context context,BaiduMap mBaiduMap,String title,LatLng ll){
		title = CommonUtil.ToSBC(title);
		LayoutParams childParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		TextView textView = new TextView(context);
		textView.setLayoutParams(childParams);
		textView.setBackgroundResource(R.drawable.popup);
		textView.setPadding(10, 3, 5, 20);
		textView.setTextColor(Color.BLACK);
		textView.setTextSize(11);
		textView.setGravity(Gravity.CENTER);
		textView.setSingleLine(false);
		StringBuilder builder = new StringBuilder(title);
		/**
		 * 截取字符串，使得每一行只有十个字符
		 */
		int index = 12,sub_size = 12;
		if(builder.length() > 70){
			index = sub_size = 19;
		}else if(builder.length() > 60){
			index = sub_size = 17;
		}else if(builder.length() > 50){
			index = sub_size = 15;
		}

		if(builder.length() != sub_size){
		      if(title.length() >= sub_size){
			      textView.setGravity(Gravity.LEFT);
		      }
		     while(title.length() >= sub_size && index <= title.length()){
		    	   builder.insert(index,"\n");
		    	   /**
		    	    * 每插入一个\n算一个字符，所以s增1
		    	    */
		    	   index += sub_size+1;
		        }
		}
		textView.setText(builder.toString());
		InfoWindow mInfoWindow = new InfoWindow(textView, ll, -47);
		mBaiduMap.showInfoWindow(mInfoWindow);
	}
	
}
