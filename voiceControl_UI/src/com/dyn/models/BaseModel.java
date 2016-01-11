package com.dyn.models;

import android.os.Handler;
import android.os.Message;

/**
 * model抽象类，负责计算view的业务逻辑请求，并通过controler通知view改变其状态
 * @author 邓耀宁
 *
 */
public abstract class BaseModel {
	/**
	 * controler的handler
	 */
	protected Handler handler;
	/**
	 * 绑定controler的handler
	 */
	public BaseModel(Handler handler) {
		this.handler = handler;
	}
	/**
	 * model业务逻辑计算的主要函数
	 * @param changeState
	 */
   public abstract void changeModelState(final int changeState);
    /**
     * model业务逻辑计算的主要函数，通过Message传入参数
     * @param changeStateMessage
     */
   public abstract void changeModelState(final Message changeStateMessage);
   /**
    * 获取当前类名
    * @return 类名
    */
   protected String getTAG(){
	   return this.getClass().getName();
   }
}
