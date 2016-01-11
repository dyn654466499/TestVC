package com.dyn.controlers;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.dyn.models.BaseModel;

/**
 * 该Activity作为controler的抽象类，负责向model转发view的业务逻辑计算请求，并通知view改变其状态。
 * @author  邓耀宁
 *
 */
public abstract class BaseControler{
	/**
	 * 
	 * @author 邓耀宁
	 *
	 */
    public interface ViewChangeListener{
    	public void onViewChange(final Message msg);
    }
    /**
     * controler绑定activity
     */
    protected Activity activity;
    /**
     * model的代理对象，需要相应的子类设置它（多态）
     */
    protected BaseModel modelDelegate;
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
    
	public BaseControler(Activity activity) {
		super();
		if (executor == null)
			executor = new ThreadPoolExecutor(2, 5, 3000, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>(3),
					new ThreadPoolExecutor.DiscardOldestPolicy());

		handler = new Handler(activity.getMainLooper()) {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				viewChangeListener.onViewChange(msg);
			}

		};
		this.activity = activity;
	}
	/**
	 * 设置view状态监听者
	 * @param viewChangeListener
	 */
    public void setViewChangeListener(ViewChangeListener viewChangeListener) {
		this.viewChangeListener = viewChangeListener;
	}

    /**
     * 给model转发请求，通过封装Message传递参数
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
	 * 给model转发请求
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
}
