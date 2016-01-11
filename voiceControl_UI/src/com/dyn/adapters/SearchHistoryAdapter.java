package com.dyn.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dyn.voicecontrol.R;

public class SearchHistoryAdapter extends ArrayAdapter<String> {
    private ArrayList<String> searchKey; 
    private Context context;
    
	public SearchHistoryAdapter(Context context,ArrayList<String> searchKey) {
		super(context,0);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.searchKey = searchKey;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return searchKey.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return searchKey.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_goods_search_history, null, false);
			holder.textView_search_recordKey = (TextView) convertView
					.findViewById(R.id.textView_search_recordKey);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
        holder.textView_search_recordKey.setText(searchKey.get(position));
		return convertView;
	}

	static class ViewHolder {
		TextView textView_search_recordKey;
	}
}
