package com.dyn.activities;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.dyn.interfaces.Commands;
import com.dyn.utils.CommonUtil;
import com.dyn.utils.DialogUtils;
import com.dyn.utils.ImageUtil;
import com.dyn.utils.NetUtil;
import com.dyn.voicecontrol.R;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import static com.dyn.consts.Constants.*;

public class ShareActivity extends BaseActivity {
	private Context mContext;
	private Tencent qq_api;
    private BaseUiListener baseUiListener;
    private IWXAPI wx_api;  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/**
		 * QQsdk初始化
		 */
		qq_api = Tencent.createInstance(QQ_APP_ID, getApplicationContext());
		baseUiListener = new BaseUiListener();
		
		/**
		 * 微信sdk初始化
		 */
		wx_api = WXAPIFactory.createWXAPI(getApplicationContext(), WX_APP_ID, true);  
		wx_api.registerApp(WX_APP_ID); 
		wx_api.handleIntent(getIntent(), new IWXAPIEventHandler() {
			
			@Override
			public void onResp(BaseResp resp) {
				// TODO Auto-generated method stub
				Log.e(getTAG(), "微信返回信息");  
			    String result = "";  
			    switch (resp.errCode) {  
			    case BaseResp.ErrCode.ERR_OK:  
			        result = "wxResponse:errcode_success";  
			        break;  
			    case BaseResp.ErrCode.ERR_USER_CANCEL:  
			        result = "wxResponse:errcode_cancel";  
			        break;  
			    case BaseResp.ErrCode.ERR_AUTH_DENIED:  
			        result = "wxResponse:errcode_deny";  
			        break;  
			    default:  
			        result = "wxResponse:errcode_unknown";  
			        break; 
			    }
			    
			    Toast.makeText(ShareActivity.this, result,Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onReq(BaseReq arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mContext = this;
		setContentView(R.layout.activity_share);
		Button button_share_cancel = (Button) findViewById(R.id.button_share_cancel);
		button_share_cancel.setOnClickListener(this);

		List<ResolveInfo> resolveInfos = getShareApps(this);
		final HashMap<String, String> appMap = new HashMap<String, String>();
		if (null == resolveInfos) {
			finish();
		} else {
			for (ResolveInfo resolveInfo : resolveInfos) {
				appMap.put(resolveInfo.activityInfo.packageName,
						resolveInfo.activityInfo.name);
			}
		}

		GridView gridView_share = (GridView) findViewById(R.id.gridView_share);
		String[] names = { "发送apk", "QQ好友", "微信好友", "朋友圈", "短信", "新浪微博",
				"QQ空间", "复制链接" };
		int[] icons = { R.drawable.bdsocialshare_sendapk,
				R.drawable.bdsocialshare_qqfriend,
				R.drawable.bdsocialshare_weixin_friend,
				R.drawable.bdsocialshare_weixin_timeline,
				R.drawable.bdsocialshare_sms,
				R.drawable.bdsocialshare_sinaweibo,
				R.drawable.bdsocialshare_qqdenglu,
				R.drawable.bdsocialshare_copylink };
		// 新建List
		ArrayList<Map<String, Object>> data_list = new ArrayList<Map<String, Object>>();
		// 获取数据
		for (int i = 0; i < icons.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("imageView_share_item_icon", icons[i]);
			map.put("textView_share_item_name", names[i]);
			data_list.add(map);
		}
		String[] from = { "imageView_share_item_icon",
				"textView_share_item_name" };
		int[] to = { R.id.imageView_share_item_icon,
				R.id.textView_share_item_name };
		final SimpleAdapter adapter = new SimpleAdapter(this, data_list,
				R.layout.item_third_share, from, to);
		gridView_share.setAdapter(adapter);
		gridView_share.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				String shareName = (String) ((Map<String, Object>) adapter
						.getItem(position)).get("textView_share_item_name");
				if ("复制链接".equals(shareName)) {
					if (android.os.Build.VERSION.SDK_INT > 11) {
						android.content.ClipboardManager c = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
						c.setPrimaryClip(ClipData.newPlainText("VoiceControl",
								APK_MOBILE_DOWNLOAD_URL));
					} else {
						android.text.ClipboardManager c = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
						c.setText(APK_MOBILE_DOWNLOAD_URL);
					}
					Toast.makeText(ShareActivity.this, "复制链接成功",
							Toast.LENGTH_SHORT).show();
					finish();
				} else {
					if ("QQ好友".equals(shareName)) {
					    Bundle params = new Bundle();
					    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
					    params.putString(QQShare.SHARE_TO_QQ_TITLE, "快来试试这个语言应用");
					    params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  "");
					    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  APK_MOBILE_DOWNLOAD_URL);
					    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,APK_LOGO_URL);
					    params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "语音控制");
					    qq_api.shareToQQ(ShareActivity.this, params, baseUiListener);
					    finish();
					    return;
					}
					if ("微信好友".equals(shareName)) {
						share2weixin(0);
						finish();
						return;
					}
					if ("朋友圈".equals(shareName)) {
						share2weixin(1);
						finish();
						return;
					}
					if ("短信".equals(shareName)) {
						shareIntent.setComponent(new ComponentName(
								"com.android.contacts", appMap
										.get("com.android.contacts")));
						shareIntent.setType("text/*");
						// 这里就是组织内容了，
						shareIntent.putExtra(Intent.EXTRA_TEXT, APK_MOBILE_DOWNLOAD_URL);
						shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						ShareActivity.this.startActivity(shareIntent);
						finish();
					}
					if ("新浪微博".equals(shareName)) {
						Toast.makeText(ShareActivity.this, "亲，不好意思，新浪微博分享功能暂未实现哦！",Toast.LENGTH_SHORT).show();
					}
					if ("QQ空间".equals(shareName)) {
						Toast.makeText(ShareActivity.this, "马上到QQ空间分享，请稍等...",Toast.LENGTH_SHORT).show();
						Bundle params = new Bundle();
						params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT );
					    params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "语音控制");//必填
					    params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "快来试试这个语言应用");//选填
					    params.putString(QzoneShare.SHARE_TO_QQ_APP_NAME,  "语音控制");
					    params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, APK_MOBILE_DOWNLOAD_URL);//必填
					    
					    ArrayList<String> imageList = new ArrayList<String>();
					    imageList.add(APK_LOGO_URL);
					    params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageList);
					    
					    qq_api.shareToQzone(ShareActivity.this, params, baseUiListener);
					    finish();
						return;
					}
					if ("发送apk".equals(shareName)) {
						// shareIntent.setComponent(new ComponentName(
						// "com.tencent.mobileqq", appMap
						// .get("com.tencent.mobileqq")));
						if (NetUtil.isWifiConnected(mContext)) {
							CommonUtil.sendAPKFromLocal(mContext);
						} else {
							DialogUtils.showDialog(mContext,
									getString(R.string.share_myApp),
									getString(R.string.wifi_message),
									new Commands() {

										@Override
										public void executeCommand() {
											// TODO Auto-generated method stub
											CommonUtil.sendAPKFromLocal(mContext);
										}
									});
						}
						return;
					}

				}
			}
		});
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(0, 0);
	}

	private List<ResolveInfo> getShareApps(Context context) {
		List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/*");
		PackageManager pManager = context.getPackageManager();
		mApps = pManager.queryIntentActivities(intent,
				PackageManager.GET_ACTIVITIES);
		return mApps;
	}

	private class BaseUiListener implements IUiListener {
		
		@Override
		public void onCancel() {
			Toast.makeText(mContext, "取消发送！", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onComplete(Object arg0) {
			// TODO Auto-generated method stub
            Toast.makeText(mContext, "发送成功！", Toast.LENGTH_SHORT).show();
            finish();
		}

		@Override
		public void onError(UiError arg0) {
			// TODO Auto-generated method stub
			Toast.makeText(mContext, "发送失败！", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	      Tencent.onActivityResultData(requestCode, resultCode, data,baseUiListener);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		qq_api.logout(this);
	}
	
	/**
	 * 分享到微信里边的内容，其中flag 1是朋友圈，0是好友， 
     * 分享前判断下是否有安装微信，没有就不提示用户 
	 * @param flag
	 */
	private void share2weixin(int flag) {  
	    // Bitmap bmp = BitmapFactory.decodeResource(getResources(),  
	    // R.drawable.weixin_share);  
	  
	    if (!wx_api.isWXAppInstalled()) {  
	        Toast.makeText(ShareActivity.this, "您还未安装微信客户端",  
	                Toast.LENGTH_SHORT).show();  
	        return;  
	    }  
	  
	    WXWebpageObject webpage = new WXWebpageObject();  
	    webpage.webpageUrl = APK_MOBILE_DOWNLOAD_URL;  
	    WXMediaMessage msg = new WXMediaMessage(webpage);  
	  
	    msg.title = "语音控制";  
	    msg.description = "快来看看这款应用"; 
	    Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.control);  
	    msg.setThumbImage(thumb);  
	    SendMessageToWX.Req req = new SendMessageToWX.Req();  
	    req.transaction = String.valueOf(System.currentTimeMillis());  
	    req.message = msg;  
	    req.scene = flag;
	    wx_api.sendReq(req);  
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_share_cancel:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onViewChange(Message msg) {
		// TODO Auto-generated method stub
		
	}  
}
