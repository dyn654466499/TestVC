package com.dyn.recievers;

import com.dyn.activities.MainActivity;
import com.iflytek.pushclient.PushReceiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class IflytekPushReceiver extends PushReceiver{
	private static final String TAG = IflytekPushReceiver.class.getName();
	/**
     * 调用PushManager.startWork后，sdk将对 push server发起绑定请求，这个过程是异步
     * 的。绑定请求的结果通过onBind返回。
     */
    @Override
    protected void onBind(Context context, String did, String appId, int errorCode) {
        Log.d(TAG, "onBind | did = " + did + ",appId = " + appId + ", errorCode = " + errorCode);
    }

    /**
     * 调用PushManager.stopWork解绑回调，sdk将对该应用进行解绑，该应用的消息将被丢弃
     * @param context
     * @param did
     * @param appId
     * @param errorCode
     */
    @Override
    protected void onUnBind(Context context, String did, String appId, int errorCode) {
        Log.d(TAG, "onUnBind | did = " + did + ",appId = " + appId + ", errorCode = " + errorCode);
    }

    /**
     * 接收透传消息的函数。
     * @param context
     * @param msgId   消息的id
     * @param content 透传消息的内容，由各应用自己解析
     */
    @Override
    protected void onMessage(Context context, String msgId, byte[] content) {
        Log.d(TAG, "onMessage | msgId = " + msgId + ", content = " + new String(content));
    }

    /**
     * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
     * @param context
     * @param messageId
     * @param title
     * @param content
     * @param extraContent
     */
    @Override
    protected void onClickNotification(Context context, String messageId, String title, String content, String extraContent) {
        Log.d(TAG, "onClickNotification | title = " + title + ", content = " + content + ", extraContent = " + extraContent);
        
    }
}
