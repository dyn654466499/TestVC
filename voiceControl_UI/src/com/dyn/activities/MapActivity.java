package com.dyn.activities;

import static com.dyn.consts.Constants.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.BusLineOverlay;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.core.SearchResult.ERRORNO;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteLine.DrivingStep;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption.DrivingTrafficPolicy;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.SuggestAddrInfo;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep.TransitRouteStepType;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteLine.WalkingStep;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanPreference;
import com.dyn.adapters.RouteLinesAdapter;
import com.dyn.adapters.TransitRouteLinesAdapter;
import com.dyn.beans.NavigationInfo;
import com.dyn.customview.AlwaysMarqueeTextView;
import com.dyn.utils.BaiduMapUtil;
import com.dyn.utils.CommonUtil;
import com.dyn.utils.ImageUtil;
import com.dyn.utils.SharedPreferenceUtils;
import com.dyn.voicecontrol.R;
import com.library.decrawso.DecRawso;

/**
 * 显示地图的界面
 * @author 邓耀宁
 */
@SuppressLint("NewApi")
public class MapActivity extends BaseActivity {
	/**
	 * 地图View
	 */
	private MapView mapView;
	/**
	 * 地图控制者
	 */
	private BaiduMap mBaiduMap;
			
    private Context mContext;
    /**
     * 路径规划搜索模块实例
     */
    private RoutePlanSearch routeSearch;
    /**
     * 公交路线搜索模块实例
     */
    private BusLineSearch busLineSearch;
    /**
     * 节点索引,供浏览节点时使用
     */
    private int nodeIndex = -1;
    /**
     * 规划路线实例
     */
    private RouteLine routeLine = null;
    /**
     * 存储导航文件的文件名
     */
	private static final String APP_FOLDER_NAME = "BaiduMapNavi";
	/**
	 * sd卡路径
	 */
	private String mSDCardPath = null;
	
	/**
	 * 导航信息
	 */
	private NavigationInfo startNaviInfo,endNaviInfo;
	/**
	 * 浏览节点
	 */
	private ImageView imageView_route_pre,imageView_route_next; 
	
	/**
	 * 导航按钮
	 */
	private Button button_route_navigate;
	/**
	 * 显示所有路线
	 */
	private Button button_route_lines;
	
	private boolean isFirstflag = true;
	/**
	 * 显示正常或异常信息
	 */
	private TextView textView_route_title;
	/**
	 * 显示起点名至终点名
	 */
	private AlwaysMarqueeTextView textView_route_place;
	/**
	 * 显示时间和路程
	 */
	private TextView textView_route_timeAndDistance;
	/**
	 * 头部显示正在加载的layout
	 */
	private LinearLayout linearLayout_route_title_load;
	/**
	 * 路径规划的起点
	 */
	private PlanNode stNode;
	/**
	 * 路径规划的终点
	 */
	private PlanNode enNode;
	
	private List<DrivingRouteLine> drivingRouteLines;
	private List<WalkingRouteLine> walkingRouteLines;
	private List<TransitRouteLine> transitRouteLines;
	
	private PopupWindow popupRouteLines;
	
	private DisplayMetrics dm;
	
	private static final int DrivingRouteLine=0x000,WalkingRouteLine=0x001,TransitRouteLine	=0x002;
	
	private ImageButton imageBtn_route_locate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		
		startNaviInfo = new NavigationInfo();
		endNaviInfo = new NavigationInfo(); 
		
		/**
		 * 语音或文本语义传过来的结果,如果是定位某个地方
		 */
		ArrayList<String> understand_location_result = getIntent().getStringArrayListExtra(LOCATE_SOMEWHERE);
		if (understand_location_result != null) {
			/**
			 * 默认第一个参数是城市，第二个是地址
			 */
			try {
			String city = understand_location_result.get(0),address = understand_location_result.get(1);
			
			if("CURRENT_CITY".equals(city) && "CURRENT_POI".equals(address)){
				mapView = BaiduMapUtil.startLocation(this,null);
				setContentView(mapView);
				return;
			}
			
			if("CURRENT_CITY".equals(city))
				city = SharedPreferenceUtils.getCurrentCity(this);
			
			mapView = BaiduMapUtil.locateSearchPlace(this, city, address);
			setContentView(mapView);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return;
		}
		/**
		 * 如果是路径规划
		 */
		final ArrayList<String> understand_route_result = getIntent().getStringArrayListExtra(ROUTE);
		if (understand_route_result != null) {
			setContentView(R.layout.activity_route);
			/**
			 * 这一步真他妈的关键！
			 */
	    	System.load(DecRawso.GetInstance().GetPath("app_BaiduNaviApplib"));
	    	System.load(DecRawso.GetInstance().GetPath("app_BaiduVIlib"));
	    	System.load(DecRawso.GetInstance().GetPath("bd_etts"));
	    	System.load(DecRawso.GetInstance().GetPath("bds"));
	    	System.load(DecRawso.GetInstance().GetPath("BDSpeechDecoder_V1"));
	    	System.load(DecRawso.GetInstance().GetPath("curl"));
	    	System.load(DecRawso.GetInstance().GetPath("gnustl_shared"));
			/**
			 * 初始化引擎相关
			 */
			if (initDirs()) {
				initNavi();		
			}
			dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			
			final ImageView imageView_route_bus = (ImageView)findViewById(R.id.imageView_route_bus);
			final ImageView imageView_route_car = (ImageView)findViewById(R.id.imageView_route_car);
			final ImageView imageView_route_foot = (ImageView)findViewById(R.id.imageView_route_foot);
			
		    textView_route_title = (TextView)findViewById(R.id.textView_route_title);
		    textView_route_place = (AlwaysMarqueeTextView)findViewById(R.id.textView_route_place);
		    textView_route_place.setMaxWidth(dm.widthPixels*5/9);
		    
		    textView_route_timeAndDistance = (TextView)findViewById(R.id.textView_route_timeAndDistance);
		    
		    /**
		     * 设置导航功能按钮
		     */
		    button_route_navigate = (Button)findViewById(R.id.button_route_navigate);
		    button_route_navigate.setTag(false);
		    /**
		     * 设置查看路线按钮
		     */
		    button_route_lines = (Button)findViewById(R.id.button_route_lines);
		    
		    imageBtn_route_locate = (ImageButton)findViewById(R.id.imageBtn_route_locate);
		    imageBtn_route_locate.setOnClickListener(this);
		    imageBtn_route_locate.setColorFilter(Color.rgb(239, 239, 239));
		    
			linearLayout_route_title_load = (LinearLayout)findViewById(R.id.linearLayout_route_title_load);
			
			/**
			 * 创建地图实例
			 */
			 Handler handler = new Handler(){

				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					super.handleMessage(msg);
					switch (msg.what) {
					case LOCATION_SUCCEED:
						try{
						/**
						 * 默认第一个参数是开始城市，第二个是开始地址；第三个参数是结束城市，第四个是结束地址；
						 */
						String startCity = understand_route_result.get(0),startAddress = understand_route_result.get(1);
						String endCity = understand_route_result.get(2),endAddress = understand_route_result.get(3);
						
						if(!TextUtils.isEmpty(startCity)){
							if("CURRENT_CITY".equals(startCity))
								startCity = SharedPreferenceUtils.getCurrentCity(mContext);
							
							if("CURRENT_CITY".equals(endCity))
								endCity = SharedPreferenceUtils.getCurrentCity(mContext);
							
							//Log.e(getTAG(), "startCity = "+startCity+",endCity = "+endCity+",startAddress = "+startAddress+",endAddress = "+endAddress);
							/**
							 * 规划路径起点
							 */
							String textView_route_place_startAddress = "",
									textView_route_place_endAddress = "";
							if("CURRENT_POI".equals(startAddress)){
								stNode = PlanNode.withLocation(SharedPreferenceUtils.getCurrentLatLng(mContext));
								textView_route_place_startAddress = "当前位置";
							}else{
								stNode = PlanNode.withCityNameAndPlaceName(startCity, startAddress);
								textView_route_place_startAddress = startAddress;
							}
							/**
							 * 规划路径终点
							 */
							if("CURRENT_POI".equals(endAddress)){
								enNode = PlanNode.withLocation(SharedPreferenceUtils.getCurrentLatLng(mContext));
								textView_route_place_endAddress = "当前位置";
							}else{
								enNode = PlanNode.withCityNameAndPlaceName(endCity, endAddress);
								textView_route_place_endAddress = endAddress;
							}
							/**
							 * 设置导航地名
							 */
							startNaviInfo.setPlace(startAddress);
							endNaviInfo.setPlace(endAddress);
							
							textView_route_place.setText(textView_route_place_startAddress+" - "+textView_route_place_endAddress);
							imageView_route_car.callOnClick();
						    setTitleVisibility(false,null);
						  }
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
						break;
						
                    case LOCATION_FAIL:
                    	postDelayed(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								finish();
							}
						}, 1000);
						break;
						
					default:
						break;
					}
				}
				
			};
			mapView = BaiduMapUtil.startLocation(mContext,handler);
			RelativeLayout layout_route_map = (RelativeLayout)findViewById(R.id.relativeLayout_route_mapView);
			layout_route_map.addView(mapView);
			mBaiduMap = mapView.getMap();
			/**
			 * 初始化搜索实例
			 */
			routeSearch = RoutePlanSearch.newInstance();
			/**
			 * 初始化公交路线搜索实例
			 */
			busLineSearch = BusLineSearch.newInstance();
			/**
			 * 设置搜索结果监听者
			 */
	        routeSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
				
				@Override
				public void onGetWalkingRouteResult(WalkingRouteResult result) {
					// TODO Auto-generated method stub
					    if (result == null) {
					    	showTip(getString(R.string.map_route_result_not_found));
					    	setRouteErrorState();
				            return;
				        }
				        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
				        	SuggestAddrInfo info = result.getSuggestAddrInfo();
				        	if(info.getSuggestStartNode() != null){
				        		showSuggestionListView(info.getSuggestStartNode(),getString(R.string.map_route_startNode),true,ROUTE_RESULT_WALK);
				        		return;
				        	}
                            if(info.getSuggestEndNode() != null){
                            	showSuggestionListView(info.getSuggestEndNode(),getString(R.string.map_route_endNode),false,ROUTE_RESULT_WALK);
				        	}
                            return;
				        }
				        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				        	walkingRouteLines = result.getRouteLines();
					        button_route_lines.setText(String.format(getString(R.string.map_route_lines), String.valueOf(walkingRouteLines.size())));
					        popupRouteLines = createLinesPopupWindow(WalkingRouteLine);
				        	
				        	mBaiduMap.clear();
				        	nodeIndex = -1;
				        	updateWalkingRoute(0);
				        	
				            setTitleVisibility(true,getString(R.string.map_route_title_walking));
				            setRoutingState(false);
				            button_route_navigate.setTag(true);
				            return;
				        }
				        
				        ERRORNO[] errorNos = SearchResult.ERRORNO.values();
				        for(ERRORNO errorNo:errorNos){
				        	if(result.error == errorNo){
				        		/**
				        		 * 通过name获取对应的string的资源id
				        		 */
				        		int resID = getResources().getIdentifier(errorNo.name(), "string", getPackageName());
				        		setTitleVisibility(true,getString(resID));
				        		setRouteErrorState();
				        		return;
				        	}
				        }
				}
				
				@Override
				public void onGetTransitRouteResult(TransitRouteResult result) {
					// TODO Auto-generated method stub
			        if (result == null) {
			        	showTip(getString(R.string.map_route_result_not_found));
			        	setRouteErrorState();
			            return;
			        }
			        
			        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			        	SuggestAddrInfo info = result.getSuggestAddrInfo();
			        	if(info.getSuggestStartNode() != null){
			        		showSuggestionListView(info.getSuggestStartNode(),getString(R.string.map_route_startNode),true,ROUTE_RESULT_TRANSIT);
			        		return;
			        	}
                        if(info.getSuggestEndNode() != null){
                        	showSuggestionListView(info.getSuggestEndNode(),getString(R.string.map_route_endNode),false,ROUTE_RESULT_TRANSIT);
			        	}
                        return;
			        }
			        
			        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			        	transitRouteLines = result.getRouteLines();
			            button_route_lines.setText(String.format(getString(R.string.map_route_lines), String.valueOf(transitRouteLines.size())));
			        	/**
			        	 * transitRouteLines有值才能弹出window
			        	 */
			            popupRouteLines = createLinesPopupWindow(TransitRouteLine);
			        	
			            mBaiduMap.clear();
			        	nodeIndex = -1;
			        	updateTransitionRoute(0);
			            setTitleVisibility(true,getString(R.string.map_route_title_transiting));
			            
			            setRoutingState(false);
			            button_route_navigate.setTag(true);
			            
			            return;
			        }
			        
			        ERRORNO[] errorNos = SearchResult.ERRORNO.values();
			        for(ERRORNO errorNo:errorNos){
			        	if(result.error == errorNo){
			        		/**
			        		 * 通过name获取对应的string的资源id
			        		 */
			        		int resID = getResources().getIdentifier(errorNo.name(), "string", getPackageName());
			        		setTitleVisibility(true,getString(resID));
			        		setRouteErrorState();
			        		return;
			        	}
			        }
				}
				
				@Override
				public void onGetDrivingRouteResult(DrivingRouteResult result) {
					// TODO Auto-generated method stub
			        if (result == null) {
			        	showTip(getString(R.string.map_route_result_not_found));
			        	if(isFirstflag){
				        	isFirstflag = false;
				        	setTitleVisibility(true,getString(R.string.map_route_result_not_found));
				        }
			        	setRouteErrorState();
			            return;
			        }
			        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			        	SuggestAddrInfo info = result.getSuggestAddrInfo();
			        	if(info.getSuggestStartNode() != null){
			        		showSuggestionListView(info.getSuggestStartNode(),getString(R.string.map_route_startNode),true,ROUTE_RESULT_DRIVE);
			        		return;
			        	}
                        if(info.getSuggestEndNode() != null){
                        	showSuggestionListView(info.getSuggestEndNode(),getString(R.string.map_route_endNode),false,ROUTE_RESULT_DRIVE);
			        	}
                        return;
			        }
			        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			        	drivingRouteLines = result.getRouteLines();
				        button_route_lines.setText(String.format(getString(R.string.map_route_lines), String.valueOf(drivingRouteLines.size())));
				            
				        popupRouteLines = createLinesPopupWindow(DrivingRouteLine);
				        
			        	mBaiduMap.clear();
			        	nodeIndex = -1;
			        	updateDrivingRoute(0);
			            setTitleVisibility(true,getString(R.string.map_route_title_driving));
			            setRoutingState(false);
			            button_route_navigate.setTag(true);
			            return;
			        }
			        ERRORNO[] errorNos = SearchResult.ERRORNO.values();
			        for(ERRORNO errorNo:errorNos){
			        	if(result.error == errorNo){
			        		/**
			        		 * 通过name获取对应的string的资源id
			        		 */
			        		int resID = getResources().getIdentifier(errorNo.name(), "string", getPackageName());
			        		setTitleVisibility(true,getString(resID));
			        		setRouteErrorState();
			        		return;
			        	}
			        }
				}
			});

	        busLineSearch = BusLineSearch.newInstance();
	        busLineSearch.setOnGetBusLineSearchResultListener(new OnGetBusLineSearchResultListener() {
				
				@Override
				public void onGetBusLineResult(BusLineResult result) {
					// TODO Auto-generated method stub
					if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
						Toast.makeText(MapActivity.this, "抱歉，未找到结果",
								Toast.LENGTH_LONG).show();
						return;
					}
					mBaiduMap.clear();
					BusLineOverlay overlay = new BusLineOverlay(mBaiduMap);
					overlay.removeFromMap();
					overlay.setData(result);
					overlay.addToMap();
					overlay.zoomToSpan();
				}
				
			});
	        
	        
			    /**
			     * 监听换乘路线
			     */
			    imageView_route_bus.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(stNode!=null && enNode != null){
			            routeSearch.transitSearch((new TransitRoutePlanOption()).from(stNode)
			                    .city(SharedPreferenceUtils.getCurrentCity(mContext))
			                    .to(enNode));
			            setTitleVisibility(false,null);
			            imageView_route_foot.setImageDrawable(getResources().getDrawable(R.drawable.navi_walk));
			            imageView_route_bus.setImageDrawable(getResources().getDrawable(R.drawable.navi_bus_highlight));
			            imageView_route_car.setImageDrawable(getResources().getDrawable(R.drawable.navi_drive));
			            
			            setRoutingState(true);
						}
					}
				});
			    /**
			     * 监听驾车路线
			     */
			    imageView_route_car.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(stNode!=null && enNode != null){
						routeSearch.drivingSearch((new DrivingRoutePlanOption()).
								from(stNode).
								to(enNode));
						setTitleVisibility(false,null);
						imageView_route_foot.setImageDrawable(getResources().getDrawable(R.drawable.navi_walk));
			            imageView_route_bus.setImageDrawable(getResources().getDrawable(R.drawable.navi_bus));
			            imageView_route_car.setImageDrawable(getResources().getDrawable(R.drawable.navi_drive_highlight));
					
			            setRoutingState(true);
						}
					}
				});
			    /**
			     * 监听行走路线
			     */
			    imageView_route_foot.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(stNode!=null && enNode != null){
						routeSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode).to(enNode));
			            setTitleVisibility(false,null);
			            imageView_route_foot.setImageDrawable(getResources().getDrawable(R.drawable.navi_walk_highlight));
			            imageView_route_bus.setImageDrawable(getResources().getDrawable(R.drawable.navi_bus));
			            imageView_route_car.setImageDrawable(getResources().getDrawable(R.drawable.navi_drive));
					
			            setRoutingState(true);
						}
					}
				});
			    
			    /**
			     * 设置上一个节点按钮功能
			     */
			    imageView_route_pre = (ImageView)findViewById(R.id.imageView_route_pre);
			    imageView_route_pre.setImageBitmap(ImageUtil.rotateBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pre_), 90));
			    imageView_route_pre.bringToFront();
			    /**
			     * 设置下一个节点按钮功能
			     */
			    imageView_route_next = (ImageView)findViewById(R.id.imageView_route_next);
			    imageView_route_next.setImageBitmap(ImageUtil.rotateBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.next_), 90));
			    imageView_route_next.bringToFront();

			    button_route_navigate.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
					if ((boolean) button_route_navigate.getTag()) {
						if (BaiduNaviManager.isNaviInited()) {
							routePlanToNavigate(startNaviInfo, endNaviInfo,
									CoordinateType.BD09LL);
						} else {
							showTip(getString(R.string.map_route_navi_uninit));
						}
					} else {
						showTip(getString(R.string.map_route_navi_fail));
					}
					}
				});
			    
			    button_route_lines.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(popupRouteLines != null){
						RelativeLayout relativeLayout_route_details = (RelativeLayout) MapActivity.this
								.findViewById(R.id.relativeLayout_route_details);
						/**
						 * 将X偏移量设置很大，就可置右
						 */
						popupRouteLines.showAsDropDown(
								relativeLayout_route_details, 9990, 0);
						}
					}
				});
			    
			    
			    
			    return;
		}
	}
	
	/**
	 * 生成路线popupwindow
	 * @return PopupWindow实例
	 */
	private PopupWindow createLinesPopupWindow(final int flag){
		final ListView listView = new ListView(mContext);
	    listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		listView.setBackgroundColor(getResources().getColor(R.color.white));
		

		int mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
		int mScreenHeight = dm.heightPixels;// 获取屏幕分辨率宽度
        
		ArrayList<HashMap<String, String>> lines = new ArrayList<>(); 
		RouteLinesAdapter adapter_temp = null;
		if(flag == DrivingRouteLine){
			for(DrivingRouteLine drivingRouteLine:drivingRouteLines){
				HashMap<String,String> map = new HashMap<>();
				String title = "";
				for(DrivingStep step:drivingRouteLine.getAllStep()){
					if(TextUtils.isEmpty(title))title += step.getInstructions();
               	    else title += ","+step.getInstructions();
				}
			    map.put(HASHMAP_KEY_ROUTELINES_CONTENT, title);
			    map.put(HASHMAP_KEY_ROUTELINES_DISTANCE, CommonUtil.getFormatDistance(drivingRouteLine.getDistance()));
			    map.put(HASHMAP_KEY_ROUTELINES_TIME, CommonUtil.getFormatTime(drivingRouteLine.getDuration()));
				lines.add(map);
			}
			adapter_temp = new RouteLinesAdapter(mContext,lines);
		}
		if(flag == TransitRouteLine){
			for(TransitRouteLine transitRouteLine:transitRouteLines){
				HashMap<String,String> map = new HashMap<>();
				String title = "",
					   stations = "";
				int	 walk_distance = 0;
                for(TransitStep step:transitRouteLine.getAllStep()){
                	if(step.getStepType().compareTo(TransitRouteStepType.BUSLINE)== 0 || 
                			step.getStepType().compareTo(TransitRouteStepType.SUBWAY)== 0){
                	 	
                     if(!TextUtils.isEmpty(step.getVehicleInfo().getTitle())){
                    	 String sub_symbol=null;
                    	 /**
                    	  * 如果是刚开始，就不插入symbol
                    	  */
                    	 if(TextUtils.isEmpty(title))sub_symbol = "";
                    	 else sub_symbol = " - ";
                    	 
                    	 stations = "("+step.getVehicleInfo().getPassStationNum()+"站)";
                    	 /**
                    	  * 用逗号隔开，在adapter中处理
                    	  */
                    	 title += sub_symbol+step.getVehicleInfo().getTitle()+","+stations+",";
                       }
                     
                    }else{
                    	/**
                    	 * 如果不是公交或地铁，那就能获取每段路步行的路程。
                    	 */
                    	walk_distance += step.getDistance();
                    }
                }
                	
			    map.put(HASHMAP_KEY_ROUTELINES_CONTENT, title);
			    map.put(HASHMAP_KEY_ROUTELINES_DISTANCE, CommonUtil.getFormatDistance(transitRouteLine.getDistance()));
			    map.put(HASHMAP_KEY_ROUTELINES_TIME, CommonUtil.getFormatTime(transitRouteLine.getDuration()));
			    map.put(HASHMAP_KEY_ROUTELINES_WALK_DISTANCE, CommonUtil.getFormatDistance(walk_distance));
				lines.add(map);
			}
			adapter_temp = new TransitRouteLinesAdapter(mContext,lines);
		}
		if(flag == WalkingRouteLine){
			for(WalkingRouteLine walkingRouteLine:walkingRouteLines){
				HashMap<String,String> map = new HashMap<>();
				String title = "";
				for(WalkingStep step:walkingRouteLine.getAllStep()){
					if(TextUtils.isEmpty(title))title += step.getInstructions();
               	    else title += ","+step.getInstructions();
				}
			    map.put(HASHMAP_KEY_ROUTELINES_CONTENT, title);
			    map.put(HASHMAP_KEY_ROUTELINES_DISTANCE, CommonUtil.getFormatDistance(walkingRouteLine.getDistance()));
			    map.put(HASHMAP_KEY_ROUTELINES_TIME, CommonUtil.getFormatTime(walkingRouteLine.getDuration()));
				lines.add(map);
			}
			adapter_temp = new RouteLinesAdapter(mContext,lines);
		}
		final RouteLinesAdapter adapter = adapter_temp;
		listView.setAdapter(adapter);
		
		PopupWindow popupWindow_temp = null;
		//if(lines.size()<5){
			popupWindow_temp = new PopupWindow(listView,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, false);
//		}else{
//			popupWindow_temp = new PopupWindow(listView,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT, false);
//		}
		//popupWindow_temp = new PopupWindow(listView,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT, false);
		final PopupWindow popupWindow = popupWindow_temp;
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				adapter.setSelectedPosition(position);
				adapter.notifyDataSetChanged();
				if(flag == DrivingRouteLine){
					updateDrivingRoute(position);
				}
				if(flag == TransitRouteLine){
					updateTransitionRoute(position);
				}
				if(flag == WalkingRouteLine){
					updateWalkingRoute(position);
				}
				/**
				 * 延迟popupwindow消失
				 */
				listView.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						popupWindow.dismiss();
					}
				}, 200);
			}
		});
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		/**
		 * 必须设置drawable
		 */
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		return popupWindow;
	}
	
	private void updateDrivingRoute(int position){
    	mBaiduMap.clear();
    	nodeIndex = -1;
    	
        DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
        
        mBaiduMap.setOnMarkerClickListener(overlay);
        overlay.setData(drivingRouteLines.get(position));
        overlay.addToMap();
        overlay.zoomToSpan();
        
        /**
         * 获取驾车路线信息
         */
        routeLine = drivingRouteLines.get(position);
        LatLng startll = routeLine.getStarting().getLocation();
        startNaviInfo.setLongitude(startll.longitude);
        startNaviInfo.setLatitude(startll.latitude);
        startNaviInfo.setDescription(routeLine.getStarting().getTitle());
        
        LatLng endll = routeLine.getTerminal().getLocation();
        endNaviInfo.setLongitude(endll.longitude);
        endNaviInfo.setLatitude(endll.latitude);
        endNaviInfo.setDescription(routeLine.getTerminal().getTitle());
        
		/**
		 * 显示时间、路程
		 */
		String time = CommonUtil.getFormatTime(routeLine.getDuration());
		String distance = CommonUtil.getFormatDistance(routeLine.getDistance());
		textView_route_timeAndDistance.setText(time + "/" + distance);
	}
	
	private void updateTransitionRoute(int position){
    	mBaiduMap.clear();
    	nodeIndex = -1;
    	
        TransitRouteOverlay overlay = new TransitRouteOverlay(mBaiduMap);
        
        mBaiduMap.setOnMarkerClickListener(overlay);
        overlay.setData(transitRouteLines.get(position));
        overlay.addToMap();
        overlay.zoomToSpan();
        /**
         * 获取驾车路线信息
         */
        routeLine = transitRouteLines.get(position);
        
        LatLng startll = routeLine.getStarting().getLocation();
        startNaviInfo.setLongitude(startll.longitude);
        startNaviInfo.setLatitude(startll.latitude);
        startNaviInfo.setDescription(routeLine.getStarting().getTitle());
        
        //busLineSearch.searchBusLine(new BusLineSearchOption().city("深圳").uid(routeLine.getStarting().getUid()));
        
        LatLng endll = routeLine.getTerminal().getLocation();
        endNaviInfo.setLongitude(endll.longitude);
        endNaviInfo.setLatitude(endll.latitude);
        endNaviInfo.setDescription(routeLine.getTerminal().getTitle());
        
        /**
		 * 显示时间、路程
		 */
        String time = CommonUtil.getFormatTime(routeLine.getDuration());
		String distance = CommonUtil.getFormatDistance(routeLine.getDistance());
		textView_route_timeAndDistance.setText(time + "/" + distance);
	}
	
	private void updateWalkingRoute(int position){
    	mBaiduMap.clear();
    	nodeIndex = -1;
    	
    	WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaiduMap);
        
        mBaiduMap.setOnMarkerClickListener(overlay);
        overlay.setData(walkingRouteLines.get(position));
        overlay.addToMap();
        overlay.zoomToSpan();
        
        /**
         * 获取驾车路线信息
         */
        routeLine = walkingRouteLines.get(position);
        LatLng startll = routeLine.getStarting().getLocation();
        startNaviInfo.setLongitude(startll.longitude);
        startNaviInfo.setLatitude(startll.latitude);
        startNaviInfo.setDescription(routeLine.getStarting().getTitle());
        
        LatLng endll = routeLine.getTerminal().getLocation();
        endNaviInfo.setLongitude(endll.longitude);
        endNaviInfo.setLatitude(endll.latitude);
        endNaviInfo.setDescription(routeLine.getTerminal().getTitle());
        
        /**
		 * 显示时间、路程
		 */
		String time = CommonUtil.getFormatTime(routeLine.getDuration());
		String distance = CommonUtil.getFormatDistance(routeLine.getDistance());
		textView_route_timeAndDistance.setText(time + "/"+ distance);
	}
	
	private void setRoutingState(boolean isRouting){
		   button_route_navigate.setEnabled(!isRouting);
		   button_route_lines.setEnabled(!isRouting);
		   imageBtn_route_locate.setEnabled(!isRouting);
	}
	
	private void setRouteErrorState(){
		setRoutingState(false);
		textView_route_timeAndDistance.setText("");
		button_route_lines.setText(String.format(getString(R.string.map_route_lines), "0"));
		popupRouteLines = null;
		button_route_navigate.setTag(false);
	}
	/**
	 * 显示有歧义的地点的建议信息列表
	 * @param poiInfos
	 * @param title
	 * @param isStart
	 * @param action
	 */
	private void showSuggestionListView(final List<PoiInfo> poiInfos,String title,final boolean isStart,final String action){
		ListView listView = (ListView)LayoutInflater.from(mContext).inflate(R.layout.listview_route_suggest, null, false);
    	String[] from = {"name","address"};
    	int[] to = {R.id.textView_route_suggest_poi_name,R.id.textView_route_suggest_poi_addr};
    	
    	List<Map<String,String>> mData= new ArrayList<Map<String,String>>();;  
    	for(PoiInfo poiInfo : poiInfos){
    		 Map<String,String> item = new HashMap<String,String>();      
        	 item.put("name", poiInfo.name);
        	 
        	 String address = poiInfo.address;
        	 if(TextUtils.isEmpty(address))address = "暂无信息";
        	 item.put("address", address); 
        	 //Log.e(getTAG(), poiInfo.name+","+poiInfo.address+","+poiInfo.city+","+poiInfo.uid+","+poiInfo.phoneNum);
        	 mData.add(item); 
    	}
    	SimpleAdapter adapter = new SimpleAdapter(mContext, mData, R.layout.item_route_suggest, from, to);
    	listView.setAdapter(adapter);
    	
    	final AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle(title).setView(listView).setCancelable(false).create();
    	listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent,
					View view, int position, long id) {
				// TODO Auto-generated method stub
				if(isStart)stNode = PlanNode.withLocation(poiInfos.get(position).location);
				else enNode = PlanNode.withLocation(poiInfos.get(position).location);
				
				if(ROUTE_RESULT_TRANSIT.equals(action))routeSearch.transitSearch((new TransitRoutePlanOption()).from(stNode).city(SharedPreferenceUtils.getCurrentCity(mContext))
	                    .to(enNode));
				if(ROUTE_RESULT_DRIVE.equals(action))routeSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));
				if(ROUTE_RESULT_WALK.equals(action))routeSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode).to(enNode));
			    
				dialog.dismiss();
			}
		});
    	dialog.show();
	}
	
    /**
     * 显示title
     * @param flag 是否显示title
     * @param title title内容
     */
	private void setTitleVisibility(boolean flag,String title){
		if(flag){
            linearLayout_route_title_load.setVisibility(View.GONE);
            textView_route_title.setVisibility(View.VISIBLE);
            textView_route_title.setText(title);
		}else{
	        linearLayout_route_title_load.setVisibility(View.VISIBLE);
	        textView_route_title.setVisibility(View.GONE);
		}
	}
	

	/**
     * 节点浏览示例
     * @param v
     */
    public void nodeClick(View v) {
        if (routeLine == null || routeLine.getAllStep() == null) {
            return;
        }
        if (nodeIndex == -1 && v.getId() == R.id.imageView_route_pre) {
        	return;
        }
        //设置节点索引
        if (v.getId() == R.id.imageView_route_next) {
            if (nodeIndex < routeLine.getAllStep().size() - 1) {
            	nodeIndex++;
            } else {
            	return;
            }
        } else if (v.getId() == R.id.imageView_route_pre) {
        	if (nodeIndex > 0) {
        		nodeIndex--;
        	} else {
            	return;
            }
        }
        //获取节结果信息
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = routeLine.getAllStep().get(nodeIndex);
        if (step instanceof DrivingRouteLine.DrivingStep) {
            nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrance().getLocation();
            nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
        } else if (step instanceof WalkingRouteLine.WalkingStep) {
            nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrance().getLocation();
            nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
        } else if (step instanceof TransitRouteLine.TransitStep) {
            nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance().getLocation();
            nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();

        }

        if (nodeLocation == null || nodeTitle == null) {
            return;
        }
        //移动节点至中心
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(nodeLocation,MAP_BASE_ZOOM+2));
        
        BaiduMapUtil.setPopupOverlay(mContext, mBaiduMap, nodeTitle, nodeLocation);
    }
    
	@Override
	protected void onPause() {
		super.onPause();
		if(mapView!=null)mapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mapView!=null)mapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mapView!=null)mapView.onDestroy();
		if(routeSearch!=null)routeSearch.destroy();
	}
	
	
	/**
	 * -------------------------------------------    导航相关类和函数                -------------------------------------------
	 */
	
	/**
	 * 初始化导航引擎文件存储路径
	 * @return
	 */
	private boolean initDirs() {
		mSDCardPath = getSdcardDir();
		if (mSDCardPath == null) {
			return false;
		}
		File f = new File(mSDCardPath, APP_FOLDER_NAME);
		if (!f.exists()) {
			try {
				f.mkdir();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 获取sd卡路径
	 * @return sd卡路径
	 */
	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}
	
	/**
	 * 初始化导航引擎
	 */
	private void initNavi() {
		BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new NaviInitListener() {
			@Override
			public void onAuthResult(int status, String msg) {
				final String authinfo;
				if (0 == status) {
					authinfo = getString(R.string.map_route_navi_key_correct);
				} else {
					authinfo = getString(R.string.map_route_navi_key_incorrect) + ", "+msg;
				}
				MapActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						//Toast.makeText(MapActivity.this, authinfo, Toast.LENGTH_LONG).show();
						Log.e(getTAG(), authinfo);
					}
				});
			}

			public void initSuccess() {
				showTip(getString(R.string.map_route_navi_init_succeed));
			}

			public void initStart() {
				//Toast.makeText(MapActivity.this, getString(R.string.map_route_navi_start), Toast.LENGTH_SHORT).show();
			}

			public void initFailed() {
				showTip(getString(R.string.map_route_navi_init_fail));
			}

		},  mTTSCallback/* null mTTSCallback */);
	}
	
	/**
	 * 根据规划路线开始导航
	 * @param coType 坐标类型
	 */
	private boolean routePlanToNavigate(NavigationInfo startInfo,NavigationInfo endInfo,CoordinateType coType) {
		BNRoutePlanNode sNode = null;
		BNRoutePlanNode eNode = null;
		switch (coType) {
			case GCJ02: {
				sNode = new BNRoutePlanNode(startInfo.getLongitude(), startInfo.getLatitude(), startInfo.getPlace(), startInfo.getDescription(), coType);
				eNode = new BNRoutePlanNode(endInfo.getLongitude(), endInfo.getLatitude(), endInfo.getPlace(), endInfo.getDescription(), coType);
				break;
			}
			case WGS84: {
				sNode = new BNRoutePlanNode(startInfo.getLongitude(), startInfo.getLatitude(), startInfo.getPlace(), startInfo.getDescription(), coType);
				eNode = new BNRoutePlanNode(endInfo.getLongitude(), endInfo.getLatitude(), endInfo.getPlace(), endInfo.getDescription(), coType);
				break;
			}
			case BD09_MC: {
				sNode = new BNRoutePlanNode(startInfo.getLongitude(), startInfo.getLatitude(), startInfo.getPlace(), startInfo.getDescription(), coType);
				eNode = new BNRoutePlanNode(endInfo.getLongitude(), endInfo.getLatitude(), endInfo.getPlace(), endInfo.getDescription(), coType);
				break;
			}
			case BD09LL: {
				sNode = new BNRoutePlanNode(startInfo.getLongitude(), startInfo.getLatitude(), startInfo.getPlace(), startInfo.getDescription(), coType);
				eNode = new BNRoutePlanNode(endInfo.getLongitude(), endInfo.getLatitude(), endInfo.getPlace(), endInfo.getDescription(), coType);
				break;
			}
			default:
				break;
			}
		    if(sNode == null){
		    	showTip(getString(R.string.map_route_navi_startNode_is_null));
		    	return false;
		    }
            if(eNode == null){
            	showTip(getString(R.string.map_route_navi_endNode_is_null));
		    	return false;
		    }
			List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
			list.add(sNode);
			list.add(eNode);
			return BaiduNaviManager.getInstance().launchNavigator(MapActivity.this, list, RoutePlanPreference.ROUTE_PLAN_MOD_RECOMMEND, true, new BNRoutePlanListener(sNode));
	}
	
	/**
	 * 路径规划监听者
	 * @author 邓耀宁
	 */
	public class BNRoutePlanListener implements RoutePlanListener {

		private BNRoutePlanNode mBNRoutePlanNode = null;

		public BNRoutePlanListener(BNRoutePlanNode node) {
			mBNRoutePlanNode = node;
		}

		@Override
		public void onJumpToNavigator() {
			/**
			 * 设置途径点以及resetEndNode会回调该接口
			 */
			Intent intent = new Intent(MapActivity.this, NavigationActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(BN_ROUTE_PLAN__NODE, (BNRoutePlanNode) mBNRoutePlanNode);
			intent.putExtras(bundle);
			startActivity(intent);
			
		}

		@Override
		public void onRoutePlanFailed() {
			// TODO Auto-generated method stub
			Toast.makeText(MapActivity.this, getString(R.string.map_route_navi_fail), Toast.LENGTH_SHORT).show();
		}
	}
	
	private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {

		@Override
		public void stopTTS() {
			// TODO Auto-generated method stub
			Log.e("test_TTS", "stopTTS");
		}

		@Override
		public void resumeTTS() {
			// TODO Auto-generated method stub
			Log.e("test_TTS", "resumeTTS");
		}

		@Override
		public void releaseTTSPlayer() {
			// TODO Auto-generated method stub
			Log.e("test_TTS", "releaseTTSPlayer");
		}

		@Override
		public int playTTSText(String speech, int bPreempt) {
			// TODO Auto-generated method stub
			Log.e("test_TTS", "playTTSText" + "_" + speech + "_" + bPreempt);
            
			return 1;
		}

		@Override
		public void phoneHangUp() {
			// TODO Auto-generated method stub
			Log.e("test_TTS", "phoneHangUp");
		}

		@Override
		public void phoneCalling() {
			// TODO Auto-generated method stub
			Log.e("test_TTS", "phoneCalling");
		}

		@Override
		public void pauseTTS() {
			// TODO Auto-generated method stub
			Log.e("test_TTS", "pauseTTS");
		}

		@Override
		public void initTTSPlayer() {
			// TODO Auto-generated method stub
			Log.e("test_TTS", "initTTSPlayer");
		}

		@Override
		public int getTTSState() {
			// TODO Auto-generated method stub
			Log.e("test_TTS", "getTTSState");
			return 1;
		}
	};


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageBtn_route_locate:
			LatLng ptStart = new LatLng(startNaviInfo.getLatitude(), startNaviInfo.getLongitude());
	        LatLng ptEnd = new LatLng(endNaviInfo.getLatitude(), endNaviInfo.getLongitude());

	        // 构建 route搜索参数
	        RouteParaOption para = new RouteParaOption()
	                .startPoint(ptStart)
	                .endPoint(ptEnd);
	        try {
	            BaiduMapRoutePlan.openBaiduMapWalkingRoute(para, mContext);
	        } catch (Exception e) {
	            e.printStackTrace();
	            showTip("百度地图打开失败!");
	        }
			break;

		default:
			break;
		}
	}

	@Override
	public void onViewChange(Message msg) {
		// TODO Auto-generated method stub
		
	}

}
