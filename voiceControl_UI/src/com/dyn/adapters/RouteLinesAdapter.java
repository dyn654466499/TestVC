package com.dyn.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dyn.utils.ImageUtil;
import com.dyn.voicecontrol.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import static com.dyn.consts.Constants.*;

public class RouteLinesAdapter extends BaseAdapter{
    private int click_position = 0;
	private Context context;
    private ArrayList<HashMap<String, String>> data;

	public RouteLinesAdapter(Context context,ArrayList<HashMap<String, String>> data) {
		super();
		this.context = context;
		this.data = data;
	}

	@Override
	public HashMap<String, String> getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}
	
	public int getPosition(String item) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setSelectedPosition(int position){
		click_position = position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_route_lines, parent,false);
			holder.textView_route_lines_num = (TextView)convertView.findViewById(R.id.textView_route_lines_num);
			holder.textView_route_lines_content = (TextView)convertView.findViewById(R.id.textView_route_lines_content);
			holder.textView_route_lines_distance = (TextView)convertView.findViewById(R.id.textView_route_lines_distance);
			holder.textView_route_lines_time = (TextView)convertView.findViewById(R.id.textView_route_lines_time);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.textView_route_lines_num.setText(String.format(
				context.getString(R.string.map_route_lines_details),
				String.valueOf(position+1)));
		
		Drawable d_time = context.getResources().getDrawable(R.drawable.navigation_transit_time);
		Drawable scale_d = ImageUtil.zoomDrawable(d_time, d_time.getIntrinsicWidth()*4/3, d_time.getIntrinsicHeight()*4/3);
		holder.textView_route_lines_time.setCompoundDrawablesWithIntrinsicBounds(scale_d, null, null, null);
		
		Drawable d_distance = context.getResources().getDrawable(R.drawable.navigation_transit_distance);
		scale_d = ImageUtil.zoomDrawable(d_distance, d_distance.getIntrinsicWidth()*4/3, d_distance.getIntrinsicHeight()*4/3);
		holder.textView_route_lines_distance.setCompoundDrawablesWithIntrinsicBounds(scale_d, null, null, null);
		
		holder.textView_route_lines_content.setText(data.get(position).get(HASHMAP_KEY_ROUTELINES_CONTENT));
		holder.textView_route_lines_distance.setText(data.get(position).get(HASHMAP_KEY_ROUTELINES_DISTANCE));
		holder.textView_route_lines_time.setText(data.get(position).get(HASHMAP_KEY_ROUTELINES_TIME));
		if(click_position == position){
			holder.textView_route_lines_num.setTextColor(Color.RED);
			convertView.setBackgroundColor(Color.rgb(244, 244, 244));
		}else{
			holder.textView_route_lines_num.setTextColor(Color.rgb(144, 144,144));
			convertView.setBackgroundColor(Color.WHITE);
		}
		return convertView;
	}
	
	static class ViewHolder{
		TextView 
		textView_route_lines_num,
		textView_route_lines_content,
		textView_route_lines_distance,
		textView_route_lines_time;
	}


}
