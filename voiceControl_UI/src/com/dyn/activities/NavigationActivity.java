package com.dyn.activities;

import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNRouteGuideManager.OnNavigationListener;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;

import static com.dyn.consts.Constants.*;

public class NavigationActivity extends BaseActivity{
    /**
     * 	算路节点
     */
	private BNRoutePlanNode mBNRoutePlanNode = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		View view = BNRouteGuideManager.getInstance().onCreate(this, new OnNavigationListener() {
			
			@Override
			public void onNaviGuideEnd() {
				finish();
			}
			
			@Override
			public void notifyOtherAction(int actionType, int arg1, int arg2, Object obj) {
				Log.e("NavigationActivity_notifyOtherAction",
						"actionType:" + actionType + "arg1:" + arg1 + "arg2:" + arg2 + "obj:" + obj.toString());
			}
			
		});
		if ( view != null ) {
			setContentView(view);
		}
		Intent intent = getIntent();
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
			    mBNRoutePlanNode = (BNRoutePlanNode) bundle.getSerializable(BN_ROUTE_PLAN__NODE);
			}
		}
	}

	@Override
	protected void onResume() {
		BNRouteGuideManager.getInstance().onResume();
		super.onResume();
//		if(hd != null){
//			hd.sendEmptyMessageAtTime(MSG_SHOW,2000);
//		}
	}
	
	protected void onPause() {
		super.onPause();
		BNRouteGuideManager.getInstance().onPause();
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		BNRouteGuideManager.getInstance().onDestroy();
	}
	
	@Override
	protected void onStop() {
	    BNRouteGuideManager.getInstance().onStop();
	    super.onStop();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		BNRouteGuideManager.getInstance().onBackPressed(false);
	}
	
	@Override
	public void onConfigurationChanged(android.content.res.Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		BNRouteGuideManager.getInstance().onConfigurationChanged(newConfig);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onViewChange(Message msg) {
		// TODO Auto-generated method stub
		
	};
}
