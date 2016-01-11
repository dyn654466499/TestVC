package com.dyn.activities;

import static com.dyn.consts.ControlState.MODEL_WEATHER_QUERY;
import static com.dyn.consts.ControlState.VIEW_WEATHER_QUERY;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dyn.adapters.WeekWeatherAdapter;
import com.dyn.beans.WeatherInfo;
import com.dyn.customview.HorizontalListView;
import com.dyn.customview.TextPromptView;
import com.dyn.interfaces.Commands;
import com.dyn.models.WeatherModel;
import com.dyn.utils.DialogUtils;
import com.dyn.voicecontrol.R;

/**
 * 显示天气的activity
 * @author 邓耀宁
 *
 */
public class WeatherActivity extends BaseActivity{
    
    private TextView todayDate;
	private TextView todayCity;
	private TextView todayTime;
	
	private TextView todayWeatherText;
	private TextView todayTemperature;
	
	private TextView todayWindDirect;
	private TextView todayWind;
	private TextView todayHumidity;
	
	private HorizontalListView weeklyHorizonList;
	private WeekWeatherAdapter mWeekWeatherAdapter;
	
	private LinearLayout promptLayout;
	private TaskPromptMove taskPromptMove;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_weather);
        //设置全屏
        getWindow().setFlags(
        		WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		/**
		 * 
		 */
		setModelDelegate(new WeatherModel(handler,this));
		setViewChangeListener(this);
		
		//todayDate = (TextView) this.findViewById(R.id.current_date);
        todayTime = (TextView) this.findViewById(R.id.current_time);
        todayCity = (TextView) this.findViewById(R.id.today_cityName);
        todayWeatherText = (TextView) this.findViewById(R.id.today_weather_text);
        todayTemperature = (TextView) this.findViewById(R.id.today_weather_temperature);
        todayWindDirect = (TextView) this.findViewById(R.id.textView_weather_windDirection);
        todayWind = (TextView) this.findViewById(R.id.textView_weather_wind);
        todayHumidity = (TextView) this.findViewById(R.id.humidity);
        
        promptLayout = (LinearLayout) this.findViewById(R.id.div_prompt);
        weeklyHorizonList = (HorizontalListView) this.findViewById(R.id.horizon_list_weekly);
        weeklyHorizonList.setOnClickListener(this);
        ArrayList<String> list = getIntent().getStringArrayListExtra("voiceResult");
        if(list == null){
        	 notifyModelChange(MODEL_WEATHER_QUERY);
        }else{
        	 Message msg = new Message();
             msg.what = MODEL_WEATHER_QUERY;
             msg.obj = list;
     		 notifyModelChange(msg);
        }
        
	}

	@Override
	public void onViewChange(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case VIEW_WEATHER_QUERY:
			Object obj = msg.obj;
			int searchIndex  = msg.arg1;
			
			if(obj instanceof List){
			List<WeatherInfo> info = (List<WeatherInfo>) obj;
			if(info.get(0).isRealTime()){
		    WeatherInfo todayInfo = info.get(0);
		    //todayDate.setText(todayInfo.getDate());
		    todayTime.setText(todayInfo.getTime());
		    todayCity.setText(todayInfo.getCityname());
		    todayWeatherText.setText(todayInfo.getWeather());
		    todayTemperature.setText(String.format(getString(R.string.temperature), todayInfo.getTemperatureHight()));
		    todayWindDirect.setText(todayInfo.getWindDirection());
		    todayWind.setText(todayInfo.getWind());
		    todayHumidity.setText(todayInfo.getHumidity()+"%");
		    info.remove(0);	
			}
	        mWeekWeatherAdapter = new WeekWeatherAdapter(weeklyHorizonList.getContext(),info);
	        weeklyHorizonList.setAdapter(mWeekWeatherAdapter);
	        /**
	         * 为了保证美观，将列表定为5个item。
	         */
	        taskPromptMove = new TaskPromptMove(searchIndex, 5 ,(weeklyHorizonList.getWidth() 
					                                            - weeklyHorizonList.getPaddingLeft()
					                                            - weeklyHorizonList.getPaddingRight())/5);
	        handler.post(taskPromptMove);
			}else if(obj instanceof String){
				DialogUtils.showDialog(WeatherActivity.this, "错误", (String)obj, new Commands() {
					
					@Override
					public void executeCommand() {
						// TODO Auto-generated method stub
						finish();
					}
				});
			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(taskPromptMove!=null)handler.post(taskPromptMove);
	}

	@Override
	protected void onStop() {
		if(taskPromptMove!=null)taskPromptMove.remove();
		super.onStop();
	}
	
	private class TaskPromptMove implements Runnable{
		private int searchIndex = 0;
		private int wholeIndex = 0;
		private int itemWidth = 0;
		public TaskPromptMove(int searchIndex, int wholeIndex, int width) {
			super();
			this.searchIndex = searchIndex;
			this.wholeIndex = wholeIndex;
			itemWidth = width;
		}

		private TextPromptView textPromptView = null;
		
		public void remove(){
			if(textPromptView != null){
				textPromptView.clearAnimation();
				promptLayout.removeView(textPromptView);
			}
		}
		
		@Override
		public void run() {
			remove();
			if(wholeIndex <= 0 ){
				return;
			}
			
			float from_x = 0;
			float to_x = 0;

			int promptWidth = itemWidth;
			
			textPromptView = new TextPromptView(WeatherActivity.this);
			textPromptView.setText("您要查询的天气.");
			promptLayout.addView(textPromptView,
					new LayoutParams(itemWidth, LayoutParams.WRAP_CONTENT));
			
			from_x = -promptWidth;
			/**
			 * 查询第几天的天气
			 */
			to_x = itemWidth * searchIndex;
			TranslateAnimation traAnimation = new TranslateAnimation(
					from_x, to_x, 0, 0);
			traAnimation.setDuration(1000);
			AlphaAnimation alphaAnimation=new AlphaAnimation(0f,1.0f);
			alphaAnimation.setDuration(300);
			AnimationSet animationSet=new AnimationSet(true);
			animationSet.addAnimation(traAnimation);
			animationSet.addAnimation(alphaAnimation);
			animationSet.setFillAfter(true);
			animationSet.setFillEnabled(true);
			
			textPromptView.startAnimation(animationSet);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if(taskPromptMove!=null)handler.post(taskPromptMove);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.horizon_list_weekly:
			
			break;

		default:
			break;
		}
	};
	
	
}
