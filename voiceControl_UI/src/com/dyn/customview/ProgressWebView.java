package com.dyn.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.dyn.voicecontrol.R;

/**
 * 带进度条的WebView
 * @author 农民伯伯
 * @see http://www.cnblogs.com/over140/archive/2013/03/07/2947721.html
 * 
 */
@SuppressWarnings("deprecation")
public class ProgressWebView extends WebView {

	public interface WebViewListener{
		public void onReceivedTitle(WebView view, String title);
		public void onProgressChanged(WebView view, int newProgress);
	}
	
    private ProgressBar progressbar;
    private String title;
    private WebViewListener webViewListener;
    

    public void setWebViewListener(WebViewListener webViewListener) {
		this.webViewListener = webViewListener;
	}



	public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 6, 0, 0));
        progressbar.setProgressDrawable(getResources().getDrawable(R.drawable.webview_progress));
        addView(progressbar);
        setWebChromeClient(new WebChromeClient());
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                progressbar.setVisibility(GONE);
            } else {
                if (progressbar.getVisibility() == GONE)progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);
            }
            if(webViewListener!=null)webViewListener.onProgressChanged(view, newProgress);
        }

		@Override
		public void onReceivedTitle(WebView view, String title) {
			// TODO Auto-generated method stub
			super.onReceivedTitle(view, title);
			if(webViewListener!=null)webViewListener.onReceivedTitle(view, title);
		}

        
    }



	@Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
