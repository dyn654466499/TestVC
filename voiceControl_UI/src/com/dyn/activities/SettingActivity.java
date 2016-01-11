package com.dyn.activities;

import static com.dyn.consts.Constants.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.dyn.interfaces.Commands;
import com.dyn.utils.DataCacheUtils;
import com.dyn.utils.DialogUtils;
import com.dyn.voicecontrol.R;
import com.iflytek.pushclient.PushManager;

/**
 * 设置界面
 * @author 邓耀宁
 */
public class SettingActivity extends BaseActivity{
    private Button button_setting_clearCache,
    button_setting_evaluate,
    button_setting_inviteFriends,
    button_setting_about,
    button_setting_setNotifications,
    button_setting_voice,
    button_setting_update;
    
    private ImageButton imageBtn_setting_back;
    
    private Context mContext;
    
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		    
			// TODO Auto-generated method stub
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.activity_setting);
			mContext = this;
			
			preferences = getSharedPreferences(PUSH_SETTING, Context.MODE_PRIVATE);
			editor = preferences.edit();
			
			
			button_setting_clearCache = (Button)findViewById(R.id.button_setting_clearCache);
			button_setting_clearCache.setOnClickListener(this);
			/**
			 * 显示当前缓存大小
			 */
			try {
				button_setting_clearCache.setText(
						String.format(getString(R.string.clearCache), 
								DataCacheUtils.getCacheSize(getCacheDir())));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			imageBtn_setting_back = (ImageButton)findViewById(R.id.imageBtn_setting_back);
			imageBtn_setting_back.setOnClickListener(this);
			
			button_setting_evaluate = (Button)findViewById(R.id.button_setting_evaluate);
			button_setting_evaluate.setOnClickListener(this);
			
			button_setting_inviteFriends = (Button)findViewById(R.id.button_setting_inviteFriends);
			button_setting_inviteFriends.setOnClickListener(this);
			
			button_setting_about = (Button)findViewById(R.id.button_setting_about);
			button_setting_about.setOnClickListener(this);
			
			button_setting_setNotifications = (Button)findViewById(R.id.button_setting_setNotifications);
			button_setting_setNotifications.setOnClickListener(this);
			
			button_setting_voice = (Button)findViewById(R.id.button_setting_voice);
			button_setting_voice.setOnClickListener(this);
			
			button_setting_update = (Button)findViewById(R.id.button_setting_update);
			button_setting_update.setOnClickListener(this);
			
			/**
			 * 设置消息提醒功能状态
			 */
			if(preferences.getBoolean(PUSH_ENABLE, true)){
				button_setting_setNotifications.setTag("in_use");
				button_setting_setNotifications.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.component_in_use), null);
			}else{
				button_setting_setNotifications.setTag("off_use");
				button_setting_setNotifications.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.component_off_use), null);
			}
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			/**
			 * 清除缓存
			 */
			case R.id.button_setting_clearCache:
				DialogUtils.showDialog(mContext, "", "确定要清除缓存吗？", new Commands() {
					@Override
					public void executeCommand() {
						// TODO Auto-generated method stub
						DataCacheUtils.cleanInternalCache(mContext);
						try {
							button_setting_clearCache.setText(
									String.format(getString(R.string.clearCache), 
											DataCacheUtils.getCacheSize(getCacheDir())));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				break;
			/**
			 * 语音设置	
			 */
			case R.id.button_setting_voice:
				startActivity(new Intent(mContext,VoiceSettingActivity.class));
				break;
			/**
			 * 结束当前页面	
			 */
			case R.id.imageBtn_setting_back:
				finish();
				break;
			/**
			 * 去相应的应用市场进行评价	
			 */
			case R.id.button_setting_evaluate:
				Uri uri = Uri.parse("market://details?id="+getPackageName());  
				Intent intent = new Intent(Intent.ACTION_VIEW,uri);  
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
				startActivity(intent); 
				break;
			/**
			 * 分享给好友	
			 */
			case R.id.button_setting_inviteFriends:
				startActivity(new Intent(SettingActivity.this,InviteFriendActivity.class));
				
				break;
		    /**
			 * 关于语音控制	
			 */				
			case R.id.button_setting_about:
				startActivity(new Intent(SettingActivity.this,AboutActivity.class));
				break;
			/**
			 * 消息提醒设置	
			 */
			case R.id.button_setting_setNotifications:
				if("in_use".equals((String)button_setting_setNotifications.getTag())){
					/**
					 * 关闭消息提醒
					 */
					button_setting_setNotifications.setTag("off_use");
					button_setting_setNotifications.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.component_off_use), null);
				    if(PushManager.isPushEnabled(getApplicationContext())){
				    	showTip("关闭消息提醒");
				    	PushManager.stopWork(getApplicationContext());
				    	editor.putBoolean(PUSH_ENABLE, false);
						editor.commit();
				    }
				}else{
					/**
					 * 开启消息提醒
					 */
					button_setting_setNotifications.setTag("in_use");
					button_setting_setNotifications.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.component_in_use), null);
						showTip("开启消息提醒");
						PushManager.startWork(getApplicationContext(),getString(R.string.iflyteck_app_id));
						editor.putBoolean(PUSH_ENABLE, true);
						editor.commit();
				}
				break;
			/**
			 * 检查版本	
			 */
			case R.id.button_setting_update:
				showTip("亲，已经是最新版本啦！");
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
