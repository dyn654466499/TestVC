package com.dyn.activities;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.dyn.interfaces.ViewChangeListener;
import com.dyn.models.BaseModel;

/**
 * 该Activity作为controler的抽象类，负责向model转发view的业务逻辑计算请求，并通知view改变其状态。
 * @author  邓耀宁
 *
 */
public abstract class BaseActivity extends Activity implements OnClickListener,ViewChangeListener{
    
    /**
     * model的代理对象，需要相应的子类设置它（多态）
     */
    private BaseModel modelDelegate;
    /**
     * 与UI线程通信的handler
     */
    protected Handler handler;
    /**
     * view状态监听者
     */
    protected ViewChangeListener viewChangeListener;
    /**
     * 使用线程池
     */
    private ThreadPoolExecutor executor;
    
    protected boolean isActive = false;
    
    private Toast mToast;
    
    /**
     * 设置Model代理
     * @param modelDelegate
     */
	public void setModelDelegate(BaseModel modelDelegate) {
		this.modelDelegate = modelDelegate;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (executor == null)
			executor = new ThreadPoolExecutor(2, 5, 3000, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>(3),
					new ThreadPoolExecutor.DiscardOldestPolicy());

		handler = new Handler(getMainLooper()) {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				viewChangeListener.onViewChange(msg);
			}

		};
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
	}
	
	    /**
	     * 提示框
	     * @param str
	     */
		public void showTip(final String str) {
			if (!TextUtils.isEmpty(str)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mToast.setText(str);
						mToast.show();
					}
				});
			}
		}

		/**
		 * 取消提示框
		 */
		public void cancelShowTip() {
			if (mToast!=null) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mToast.cancel();
					}
				});
			}
		}
		
	/**
	 * 设置view状态监听者
	 * @param viewChangeListener
	 */
    public void setViewChangeListener(ViewChangeListener viewChangeListener) {
		this.viewChangeListener = viewChangeListener;
	}

    /**
     * 需设置model代理后，才能给给model转发请求，通过封装Message传递参数
     * @param changeStateMessage
     */
	public void notifyModelChange(final Message changeStateMessage) {
		// TODO Auto-generated method stub
		/**
		 * 交付给子线程做业务逻辑计算
		 */
		executor.execute(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				modelDelegate.changeModelState(changeStateMessage);
			}
			
		});
	}
	/**
	 * 需设置model代理后，才能给model转发请求
	 * @param changeState
	 */
	public void notifyModelChange(final int changeState) {
		// TODO Auto-generated method stub
		/**
		 * 交付给子线程做业务逻辑计算
		 */
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				modelDelegate.changeModelState(changeState);
			}
		});
	}
	
	/**
	 * 获取当前类名
	 * @return 类名
	 */
    protected String getTAG(){
 	   return this.getClass().getName();
    }
    
    @Override
    protected void onStop() {
            // TODO Auto-generated method stub
            super.onStop();
            if (!isAppOnForeground()) {
                    //app 进入后台
                    isActive = false ;//记录当前已经进入后台
            }
    }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!isActive) {
			// app 从后台唤醒，进入前台
			isActive = true;
		}
	}

    /**
     * 程序是否在前台运行
     * 
     * @return
     */
    public boolean isAppOnForeground() {
            // Returns a list of application processes that are running on the
            // device
            ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            String packageName = getApplicationContext().getPackageName();

            List<RunningAppProcessInfo> appProcesses = activityManager
                            .getRunningAppProcesses();
            if (appProcesses == null)
                    return false;

            for (RunningAppProcessInfo appProcess : appProcesses) {
                    // The name of the process that this object is associated with.
                    if (appProcess.processName.equals(packageName)
                                    && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                            return true;
                    }
            }
            return false;
    }
    
    abstract class ButtonListener implements OnClickListener{};
}
