package com.dyn.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShareAdapter extends BaseAdapter{

	private Context mContext;
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null){
			
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.textView_share_item_name.setText("");
		holder.imageView_share_item_icon.setImageBitmap(null);
		
		return convertView;
	}
	
	static class ViewHolder{
		TextView textView_share_item_name;
		ImageView imageView_share_item_icon;
	}

}
