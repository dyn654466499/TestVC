package com.dyn.activities;

import com.dyn.voicecontrol.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ShowHelpActivity extends BaseActivity {

	private WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_help);
		webView = (WebView)findViewById(R.id.webView_showHelp);
		String path = "";
		try {
			path = getFilesDir().getPath()+"/help.html";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		webView.getSettings().setDefaultTextEncodingName("utf-8");
		webView.getSettings().setDefaultFontSize(20);	
		if("".equals(path)){
			webView.loadDataWithBaseURL(null,"暂无内容", "text/html", "utf-8", null);
		}
		else webView.loadUrl("file://"+path);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_help, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		/**
		 * 销毁webView。
		 */
		if(webView!=null){
		   final ViewGroup viewGroup = (ViewGroup)webView.getParent();
		   if(viewGroup!=null){
			   viewGroup.removeView(webView);
		   }
		webView.removeAllViews();
		webView.destroy();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onViewChange(Message msg) {
		// TODO Auto-generated method stub
		
	}
}
