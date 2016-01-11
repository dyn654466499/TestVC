package com.dyn.utils;
import java.util.ArrayList;
import com.dyn.voicecontrol.R;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/**
 * @author 邓耀宁
 * 该类的一些方法用来判断语音中心返回的结果中有没有该应用的关键字，同时也排除其他应用的类似的关键字。
 */
public class SpeechContentHandleUtil {

	private static final String TAG = SpeechContentHandleUtil.class.getName();
	private static ArrayList<String> myList,otherList;
	
	public static void initData(Context context){
		Resources res = context.getResources();
		
		if(otherList==null){
		Log.v(TAG, "otherList new!");	
		otherList = new ArrayList<String>();
		otherList.add(res.getString(R.string.CCTV));
		otherList.add(res.getString(R.string.channel));
		otherList.add(res.getString(R.string.TV));
		otherList.add(res.getString(R.string.station));
		}
		
		if(myList==null){
			Log.v(TAG, "myList new!");
			myList = new ArrayList<String>();
			
			myList.add(res.getString(R.string.open));
			myList.add(res.getString(R.string.launch));
			myList.add(res.getString(R.string.login));
			myList.add(res.getString(R.string.exit));
			myList.add(res.getString(R.string.close));
			myList.add(res.getString(R.string.returnToHome));
			myList.add(res.getString(R.string.uninstall));
		}	
		
	}

	public static boolean isMyOperation(String text) {
		
		for (String list : myList) {
			if (text.contains(list))
				return true;
		}

		return false;
	}
	
    public static boolean isOtherOperation(String text){
		for(String list: otherList){
			if(text.contains(list))return true;
		}
		return false;
		
	}
}
