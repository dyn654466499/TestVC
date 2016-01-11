
package com.dyn.adapters;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dyn.beans.ChatMsgEntity;
import com.dyn.customview.CircleImageView;
import com.dyn.voicecontrol.R;


/**
 * @author 邓耀宁
 *
 */
public class ChatMsgViewAdapter extends BaseAdapter {
	
	public static interface IMsgViewType
	{
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}
	
    //private static final String TAG = ChatMsgViewAdapter.class.getSimpleName();

    private List<ChatMsgEntity> mDataArrays;

    private LayoutInflater mInflater;

    public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> coll) {
        this.mDataArrays = coll;
        mInflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return mDataArrays.size();
    }

    @Override
    public ChatMsgEntity getItem(int position) {
        return mDataArrays.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }

	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
	 	ChatMsgEntity entity = mDataArrays.get(position);
	 	
	 	if (entity.getMsgType())
	 	{
	 		return IMsgViewType.IMVT_COM_MSG;
	 	}else{
	 		return IMsgViewType.IMVT_TO_MSG;
	 	}
	 	
	}

	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}
	
	public void setmDataArrays(List<ChatMsgEntity> coll) {
		this.mDataArrays = coll;
	}
	
	@Override
    @SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent) {
    	
    	ChatMsgEntity entity = mDataArrays.get(position);
    	boolean isComMsg = entity.getMsgType();
    		
    	ViewHolder viewHolder = null;	
	    if (convertView == null)
	    {
	    	viewHolder = new ViewHolder();
	    	  if (isComMsg)
			  {
				  convertView = mInflater.inflate(R.layout.item_chatting_msg_text_left, null);
				  viewHolder.tvImage = (CircleImageView) convertView.findViewById(R.id.iv_robothead);				  
				  //viewHolder.tvImage.setBackground(entity.getDrawable());
			  }else{
				  convertView = mInflater.inflate(R.layout.item_chatting_msg_text_right, null);
				  viewHolder.tvImage = (CircleImageView) convertView.findViewById(R.id.iv_userhead);
				  //viewHolder.tvImage.setBackground(entity.getDrawable());
			  }

	    	  
			  viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			  viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
			  viewHolder.isComMsg = isComMsg;
			  
			  convertView.setTag(viewHolder);
	    }else{
	        viewHolder = (ViewHolder) convertView.getTag();
	    }
	    
	    viewHolder.tvImage.setImageDrawable(entity.getDrawable());
	    //viewHolder.tvImage.setBackground(entity.getDrawable());
	    viewHolder.tvSendTime.setText(entity.getDate());
	    viewHolder.tvContent.setText(entity.getText());
	    return convertView;
    }
    

    static class ViewHolder { 
        public TextView tvSendTime;
        public TextView tvContent;
        public CircleImageView tvImage;
        public boolean isComMsg = true;
    }
}
