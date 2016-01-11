package com.dyn.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dyn.beans.WeatherInfo;
import com.dyn.voicecontrol.R;

public class WeekWeatherAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext = null;
	private List<WeatherInfo> info;

	public WeekWeatherAdapter(Context context,List<WeatherInfo> info){
		mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.info = info;
		//(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return info.size();
	}

	@Override
	public WeatherInfo getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();

			int w = parent.getWidth() - parent.getPaddingLeft()
					- parent.getPaddingRight();
			int h = parent.getHeight();

			RelativeLayout itemlayout = (RelativeLayout) mInflater.inflate(
					R.layout.item_weather_weekly, parent, false);
			LayoutParams params = new LayoutParams(w / 5,
					LayoutParams.MATCH_PARENT);
			itemlayout.setLayoutParams(params);

			int wt = itemlayout.getMeasuredWidth();

			holder.layout = (RelativeLayout) itemlayout
					.findViewById(R.id.week_item_rl);

			holder.textWeek = (TextView) itemlayout
					.findViewById(R.id.week_item_week);
			holder.textDate = (TextView) itemlayout
					.findViewById(R.id.week_item_date);
			holder.textNongli = (TextView) itemlayout
					.findViewById(R.id.week_item_nongli);
			holder.imgWeather = (ImageView) itemlayout
					.findViewById(R.id.weather_icon);
			holder.textTemperature = (TextView) itemlayout
					.findViewById(R.id.week_item_temperature);
			holder.textWeather = (TextView) itemlayout
					.findViewById(R.id.week_item_weather);
			convertView = itemlayout;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position < info.size()) {
			WeatherInfo weatherInfo = info.get(position);
			if (position == 0)
				holder.textWeek.setText(mContext.getString(R.string.today));
			else
				holder.textWeek.setText(String.format(
						mContext.getString(R.string.week),
						weatherInfo.getWeek()));
			holder.textDate.setText(weatherInfo.getDate());
			holder.textNongli.setText(weatherInfo.getNongli());
			holder.textTemperature.setText(String.format(
					mContext.getString(R.string.temperature),
					weatherInfo.getTemperatureLow())
					+ "/"
					+ String.format(mContext.getString(R.string.temperature),
							weatherInfo.getTemperatureHight()));
			// holder.textWeather.setText(w.weather);
			int length = weatherInfo.getWeather().length();
			if (length > 15 && length <= 18) {
				holder.textWeather.setTextSize(12);
			} else if (length > 18 && length <= 21) {
				holder.textWeather.setTextSize(11);
			} else if (length > 21) {
				holder.textWeather.setTextSize(9);
			}
			holder.textWeather.setText(weatherInfo.getWeather());
			holder.imgWeather.setImageDrawable(mContext.getResources().getDrawable(getDrawableID(weatherInfo.getWeatherID())));

		}

		return convertView;
	}
    
	private int getDrawableID(String weatherID){
		switch (Integer.valueOf(weatherID)) {
		case 0:
			//晴
			return R.drawable.weather32;
		case 1:
			//多云
			return R.drawable.weather26;
		case 2:
			//阴
			return R.drawable.weather28;
		case 3:
			//阵雨
			return R.drawable.weather39;
		case 4:
			//雷阵雨
			return R.drawable.weather37;
		case 5:
			//雷阵雨伴有冰雹
			return R.drawable.weather38;
		case 6:
			//雨夹雪
			return R.drawable.weather5;
		case 7:
			//小雨
			return R.drawable.weather9;
		case 8:
			//中雨
			return R.drawable.weather11;
		case 9:
			//大雨
			return R.drawable.weather12;
		case 10:
			//暴雨
			return R.drawable.weather3;
		case 11:
			//大暴雨
			return R.drawable.weather3;
		case 12:
			//特大暴雨
			return R.drawable.weather3;
		case 13:
			//阵雪
			return R.drawable.weather41;
		case 14:
			//小雪
			return R.drawable.weather13;
		case 15:
			//中雪
			return R.drawable.weather14;
		case 16:
			//大雪
			return R.drawable.weather16;
		case 17:
			//暴雪
			return R.drawable.weather18;
		case 18:
			//雾
			return R.drawable.weather21;
		case 19:
			//冻雨
			return R.drawable.weather10;
		case 20:
			//沙尘暴
			return R.drawable.weather21;
		case 21:
			//小到中雨
			return R.drawable.weather11;
		case 22:
			//中到大雨
			return R.drawable.weather12;
		case 23:
			//大到暴雨
			return R.drawable.weather3;
		case 24:
			//暴雨到大暴雨
			return R.drawable.weather3;
		case 25:
			//大暴雨到特大暴雨
			return R.drawable.weather3;
		case 26:
			//小雪到中雪
			return R.drawable.weather14;
		case 27:
			//中雪到大雪
			return R.drawable.weather16;
		case 28:
			//大雪到暴雪
			return R.drawable.weather18;
		case 29:
			//浮尘
			return R.drawable.weather23;
		case 30:
			//扬沙
			return R.drawable.weather21;
		case 33:
			//霾
			return R.drawable.weather19;

		default:
			return R.drawable.weather44;
		}
	}
	
	public static class ViewHolder{
		public RelativeLayout layout;
		public TextView textWeek;
		public TextView textDate;
		public TextView textNongli;
        public ImageView imgWeather;
        public TextView textWeather;
        public TextView textTemperature;
	}
}