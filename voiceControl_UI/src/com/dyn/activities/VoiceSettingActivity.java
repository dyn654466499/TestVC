package com.dyn.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.dyn.voicecontrol.R;
import static com.dyn.consts.Constants.*;

/**
 * 语音设置界面
 * @author 邓耀宁
 *
 */
@SuppressLint("NewApi") 
public class VoiceSettingActivity extends BaseActivity {
	private Button button_voicesetting_language;
	private Button button_voicesetting_speaker;
	private Button button_voicesetting_interruptMusic;
	private Button button_voicesetting_voiceEnable;
	
	private ImageButton imageBtn_voicesetting_back;

	private LinearLayout linearLayout_voicesetting_voiceEnable;
	private String[] mCloudVoicersEntries,
	                 mCloudVoicersValues,
	                 mLanguageEntries,
	                 mLanguageValues;
	
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voicesetting);
		preferences = getSharedPreferences(VOICE_SETTING, Context.MODE_PRIVATE);
		editor = preferences.edit();
		
		/**
		 * 云端发音人名称列表
		 */
		mCloudVoicersEntries = getResources().getStringArray(
				R.array.voicer_cloud_entries);
		mCloudVoicersValues = getResources().getStringArray(
				R.array.voicer_cloud_values);
		/**
		 * 语言列表
		 */
		mLanguageEntries = getResources().getStringArray(
				R.array.language_entries);
		mLanguageValues = getResources().getStringArray(
				R.array.language_values);

		button_voicesetting_voiceEnable = (Button)findViewById(R.id.button_voicesetting_voiceEnable); 
		button_voicesetting_language = (Button)findViewById(R.id.button_voicesetting_language);
		button_voicesetting_speaker = (Button)findViewById(R.id.button_voicesetting_speaker);
		button_voicesetting_interruptMusic = (Button)findViewById(R.id.button_voicesetting_interruptMusic);
		
		imageBtn_voicesetting_back = (ImageButton)findViewById(R.id.imageBtn_voicesetting_back);
		
		linearLayout_voicesetting_voiceEnable = (LinearLayout)findViewById(R.id.linearLayout_voicesetting_voiceEnable);
		
		button_voicesetting_voiceEnable.setOnClickListener(this);
		button_voicesetting_language.setOnClickListener(this);
		button_voicesetting_speaker.setOnClickListener(this);
		button_voicesetting_interruptMusic.setOnClickListener(this);
		
		imageBtn_voicesetting_back.setOnClickListener(this);
		/**
		 * 设置语音合成开启状态
		 */
		if(preferences.getBoolean(VOICE_ENABLE, true)){
			button_voicesetting_voiceEnable.setTag("in_use");
			button_voicesetting_voiceEnable.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.component_in_use), null);
			setVoiceEnable(true);
		}else{
			button_voicesetting_voiceEnable.setTag("off_use");
			button_voicesetting_voiceEnable.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.component_off_use), null);
			setVoiceEnable(false);
		}
		/**
		 * 设置音乐打断状态
		 */
		if(preferences.getBoolean(VOICE_INTERRUPTED, true)){
			button_voicesetting_interruptMusic.setTag("in_use");
			button_voicesetting_interruptMusic.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.component_in_use), null);
		}else{
			button_voicesetting_interruptMusic.setTag("off_use");
			button_voicesetting_interruptMusic.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.component_off_use), null);
		}
	}
	
	/**
	 * 根据开启语音合成的状态设置UI
	 * @param enabled
	 */
	private void setVoiceEnable(boolean enabled){
		if(enabled){
			linearLayout_voicesetting_voiceEnable.setVisibility(View.VISIBLE);
		}else{
			linearLayout_voicesetting_voiceEnable.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_voicesetting_voiceEnable:
			if("in_use".equals((String)button_voicesetting_voiceEnable.getTag())){
				/**
				 * 关闭语音合成功能
				 */
				button_voicesetting_voiceEnable.setTag("off_use");
				button_voicesetting_voiceEnable.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.component_off_use), null);
				editor.putBoolean(VOICE_ENABLE, false);
				editor.commit();
				setVoiceEnable(false);
			}else{
				/**
				 * 开启语音合成功能
				 */
				button_voicesetting_voiceEnable.setTag("in_use");
				button_voicesetting_voiceEnable.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.component_in_use), null);
				editor.putBoolean(VOICE_ENABLE, true);
				editor.commit();
				setVoiceEnable(true);
			}
			break;
		
		case R.id.button_voicesetting_language:
			/**
			 * 记录之前已经设置的选项位置
			 */
			int language_seletedNum = 0;
			for(int i = 0 ; i < mLanguageValues.length;i++){
				if(mLanguageValues[i].equals(preferences.getString(VOICE_LANGUAGE, mLanguageValues[0]))){
					language_seletedNum = i;
					break;
				}
			}
			new AlertDialog.Builder(this).setTitle("语言设置选项")
					.setSingleChoiceItems(mLanguageEntries, // 单选框有几项,各是什么名字
							language_seletedNum, // 默认的选项
							new DialogInterface.OnClickListener() { // 点击单选框后的处理
								public void onClick(DialogInterface dialog,int which) { // 点击了哪一项
									editor.putString(VOICE_LANGUAGE, mLanguageValues[which]);
									if(editor.commit())dialog.dismiss();
								}
							}).show();
			break;

		case R.id.button_voicesetting_speaker:
			/**
			 * 记录之前已经设置的选项位置
			 */
			int speaker_seletedNum = 0;
			for(int i = 0 ; i < mCloudVoicersValues.length;i++){
				if(mCloudVoicersValues[i].equals(preferences.getString(VOICE_SPEAKER, mCloudVoicersValues[0]))){
					speaker_seletedNum = i;
					break;
				}
			}
			new AlertDialog.Builder(this).setTitle("在线合成发音人选项")
					.setSingleChoiceItems(mCloudVoicersEntries, // 单选框有几项,各是什么名字
							speaker_seletedNum, // 默认的选项
							new DialogInterface.OnClickListener() { // 点击单选框后的处理
								public void onClick(DialogInterface dialog,int which) { // 点击了哪一项
									editor.putString(VOICE_SPEAKER, mCloudVoicersValues[which]);
									if(editor.commit())dialog.dismiss();
								}
							}).show();
			break;

		case R.id.button_voicesetting_interruptMusic:
			if("in_use".equals((String)button_voicesetting_interruptMusic.getTag())){
				/**
				 * 关闭打断音乐功能
				 */
				button_voicesetting_interruptMusic.setTag("off_use");
				button_voicesetting_interruptMusic.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.component_off_use), null);
				editor.putBoolean(VOICE_INTERRUPTED, false);
				editor.commit();
			}else{
				/**
				 * 开启打断音乐功能
				 */
				button_voicesetting_interruptMusic.setTag("in_use");
				button_voicesetting_interruptMusic.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.component_in_use), null);
				editor.putBoolean(VOICE_INTERRUPTED, true);
				editor.commit();
			}
			break;
			
		case R.id.imageBtn_voicesetting_back:
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
