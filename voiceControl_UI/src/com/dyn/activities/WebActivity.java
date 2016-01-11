package com.dyn.activities;

import static com.dyn.consts.Constants.*;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dyn.customview.ProgressWebView;
import com.dyn.customview.ProgressWebView.WebViewListener;
import com.dyn.utils.CommonUtil;
import com.dyn.voicecontrol.R;

public class WebActivity extends BaseActivity{
    private ImageButton imageBtn_web_back;
    private ImageButton imageBtn_web_refresh;
    private ProgressWebView webView;
    private TextView textView_web_title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_web);
		imageBtn_web_back = (ImageButton)findViewById(R.id.ImageBtn_web_back);
		imageBtn_web_back.setOnClickListener(this);
		
		imageBtn_web_refresh = (ImageButton)findViewById(R.id.imageBtn_web_refresh);
		imageBtn_web_refresh.setOnClickListener(this);
		
		textView_web_title = (TextView)findViewById(R.id.textView_web_title);
		
		webView = (ProgressWebView)findViewById(R.id.webView_web_content);
		WebSettings setting = webView.getSettings();  
		setting.setJavaScriptEnabled(true);//支持js  
		setting.setDefaultTextEncodingName("GBK");//设置字符编码  
		webView.setScrollBarStyle(0);//滚动条风格，为0指滚动条不占用空间，直接覆盖在网页上  
		setting.setJavaScriptCanOpenWindowsAutomatically(true);  
		setting.setUseWideViewPort(true);//关键点  
		  
		setting.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);//支持内容从新布局 
		      
		setting.setDisplayZoomControls(false);  
		setting.setJavaScriptEnabled(true); // 设置支持javascript脚本  
		setting.setAllowFileAccess(true); // 允许访问文件  
		setting.setBuiltInZoomControls(true); // 设置显示缩放按钮  
		setting.setSupportZoom(true); // 支持缩放  
		//支持缩放
		webView.setInitialScale(35);
		webView.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				//view.loadUrl(url);
				return false;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

			@Override
			public void onScaleChanged(WebView view, float oldScale,
					float newScale) {
				// TODO Auto-generated method stub
				super.onScaleChanged(view, oldScale, newScale);
			}
			
		});
		//设置缩放比例
		webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		
		setting.setLoadWithOverviewMode(true);  
		setting.setDatabaseEnabled(true);
		setting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK) ;//关闭webview中缓存
		setting.setNeedInitialFocus(true) ;//当webview调用requestFocus时为webview设置节点
		setting.setJavaScriptCanOpenWindowsAutomatically(true) ;//支持通过JS打开新窗口
		setting.setLoadsImagesAutomatically(true) ;//支持自动加载图片
		setting.setDomStorageEnabled(true);
		
		String url = getIntent().getStringExtra(WEB_URL);
		if(!TextUtils.isEmpty(url)){
		    webView.setDownloadListener(new DownloadListener() {
		            @Override
		            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
		                if (url != null && url.startsWith("http://")){
		                	Log.e(getTAG(), "url = "+url+",userAgent="+userAgent+",contentDisposition="+
		                    contentDisposition+",mimetype="+mimetype+",length="+CommonUtil.getFormatTrafficSize(contentLength));
		                    
		                	Uri uri = Uri.parse(url);  
		                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
		                    startActivity(intent);  
		                }
		            }
		            
		        });
		    webView.setWebViewListener(new WebViewListener() {
				private boolean isProgressChanging = true;
				@Override
				public void onReceivedTitle(WebView view, String title) {
					// TODO Auto-generated method stub
					textView_web_title.setText(title);
				}

				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					// TODO Auto-generated method stub
					
						if (newProgress == 100) {
							imageBtn_web_refresh
									.setImageDrawable(getResources()
											.getDrawable(
													R.drawable.icon_web_refresh));
							imageBtn_web_refresh
									.setOnClickListener(WebActivity.this);
							isProgressChanging = true;
						} else {
							if (isProgressChanging) {
							imageBtn_web_refresh
									.setImageDrawable(getResources()
											.getDrawable(
													R.drawable.abc_ic_clear_disabled));
							imageBtn_web_refresh
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											webView.stopLoading();
										}
									});
							isProgressChanging = false;
						   }
					   } 
				}
			});
			webView.loadUrl(url);
		}
				
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ImageBtn_web_back:
			finish();
			break;

		case R.id.imageBtn_web_refresh:
			webView.reload();
			break;
			
		default:
			break;
		}
	}

	@Override
	public void onViewChange(Message msg) {
		// TODO Auto-generated method stub
		
	}
	
     @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {   
	         if((keyCode==KeyEvent.KEYCODE_BACK)&&webView.canGoBack()){   
	         webView.goBack();   
	         return true;   
	         }   
	        return super.onKeyDown(keyCode, event);   
	    }  
	
	

}
