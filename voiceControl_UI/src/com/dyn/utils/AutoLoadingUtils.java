package com.dyn.utils;

import java.util.ArrayList;

import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.widget.ImageView;

import com.dyn.voicecontrol.R;

public class AutoLoadingUtils {
	private static View view;
	private static ViewGroup rootView;
	private static ArrayList<View> saveView;
	
	public static void setAutoLoadingView(ViewGroup rootView){
		if(rootView != null){
		AutoLoadingUtils.rootView =rootView;
		view = LayoutInflater.from(rootView.getContext()).inflate(R.layout.autoloading, rootView, false);
		saveView = new ArrayList<View>();
		
		ImageView image = (ImageView) view.findViewById(R.id.imageView_autoLoading);  
		image.setBackgroundResource(R.anim.autoloading);  
        AnimationDrawable anim = (AnimationDrawable) image.getBackground();  
        anim.start();  
        rootView.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
			
			@Override
			public void onChildViewRemoved(View parent, View child) {
				// TODO Auto-generated method stub
				saveView.add(child);
			}
			
			@Override
			public void onChildViewAdded(View parent, View child) {
				// TODO Auto-generated method stub
				
			}
		});
        rootView.removeAllViews();
        rootView.addView(view);
        view.bringToFront();
		}
	}
	
	public static void cancelAutoLoadingView(){
		if(AutoLoadingUtils.rootView != null && view != null){
			AutoLoadingUtils.rootView.removeView(view);
			saveView.remove(view);
			for (View view : saveView) {
				AutoLoadingUtils.rootView.addView(view);
			}
			view = null;
			AutoLoadingUtils.rootView = null;
			saveView = null;
		}
	}
	
	public static void showFailView(){
		if(AutoLoadingUtils.rootView != null){
			 
		}
	}
	
//	public static View getAutoLoadingView(Activity mActivity){
//		view = LayoutInflater.from(mActivity).inflate(R.layout.autoloading, null, false);
//		ImageView image = (ImageView) view.findViewById(R.id.imageView_autoLoading);  
//		image.setBackgroundResource(R.anim.autoloading);  
//        AnimationDrawable anim = (AnimationDrawable) image.getBackground();  
//        anim.start();  
//        view.bringToFront();
//        return view;
//	}
//	
//	public static void cancelAutoLoadingView(ViewGroup rootView){
//		if(rootView != null && view != null){
//			rootView.removeView(view);
//			view = null;
//		}
//	}
//	
//	public static void showFailView(ViewGroup rootView){
//		if(rootView != null && view != null){
//			 
//		}
//	}
}
