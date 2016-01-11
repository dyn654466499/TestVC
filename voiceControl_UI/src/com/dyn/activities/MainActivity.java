package com.dyn.activities;

import static com.dyn.consts.Constants.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import taobe.tec.jcc.JChineseConvertor;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.dyn.ResideMenu.ResideMenu;
import com.dyn.ResideMenu.ResideMenuItem;
import com.dyn.adapters.ChatMsgViewAdapter;
import com.dyn.beans.ChatMsgEntity;
import com.dyn.customview.CustomToast;
import com.dyn.models.MainModel;
import com.dyn.utils.AutoLoadingUtils;
import com.dyn.utils.BaiduMapUtil;
import com.dyn.utils.CommonUtil;
import com.dyn.utils.DataHoldUtil;
import com.dyn.utils.ImageUtil;
import com.dyn.utils.JsonUtil;
import com.dyn.utils.UnpackSoUtil;
import com.dyn.voicecontrol.MyApplication;
import com.dyn.voicecontrol.R;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.pushclient.PushManager;
import com.library.decrawso.DecRawso;
/**
 * 显示地图的activity
 * @author 邓耀宁
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends BaseActivity{
    // 天安门坐标
    double mLat1 = 39.915291;
    double mLon1 = 116.403857;
    // 百度大厦坐标
    double mLat2 = 40.056858;
    double mLon2 = 116.308194;
    
	private final String TAG = getTAG();
	/**
	 * 保存操作应用的action
	 */
	private String appAction = "";
	/**
	 * 语音按钮
	 */
	private ImageButton imageBtn_main_speech;
	/**
	 * 包管理器
	 */
	private PackageManager pm;
	/**
	 * 保存第三方应用信息的map
	 */
	private Map<String, String> appInfoMap;
	/**
	 * 保存联系人的map
	 */
	private Map<String, String> contactsMap;
	
	private Context mContext;
	
	/**
	 * 退出时间记录，用于按两次返回键。
	 */
	private long exitTime;
	/**
	 *  语音合成对象
	 */
	private SpeechSynthesizer mSpeechSynthesizer;
	/**
	 *  语义理解对象（语音到语义）。
	 */
	private SpeechUnderstander mSpeechUnderstander;
	/**
	 * 语义理解对象（文本到语义）。
	 */
	private TextUnderstander mTextUnderstander;	
    /**
     * 对话列表
     */
	private ListView chatListView;
	/**
	 * 对话数据适配器
	 */
	private ChatMsgViewAdapter mAdapter;
	/**
	 * 对话数据
	 */
	private List<ChatMsgEntity> mDataArrays;
	/**
	 * 保存聊天记录
	 */
	private SharedPreferences sharePreference_chat;
	/**
	 * 设置聊天记录的位置
	 */
	private int chatPosition = 0;

	/**
	 * 菜单选项码
	 */
	private final static int menu_delete = 0;
	private final static int menu_queryApp = 1;
	private final static int menu_setHeadImage = 2;
	private final static int menu_help = 3;
	private final static int menu_detail = 4;
	private final static int menu_share = 5;

	private static final String USER_HEAD = "userHead";
	private static final String ROBOT_HEAD = "robotHead";
	/**
	 * 请求码 
	 */
	private static final int USER_IMAGE_REQUEST_CODE = 0;
	private static final int ROBOT_IMAGE_REQUEST_CODE = 1;
	private static final int USER_RESULT_REQUEST_CODE = 2;
	private static final int ROBOT_RESULT_REQUEST_CODE = 3;
	private static final int CAMERA_REQUEST_CODE = 4;
	private static final int FILE_REQUEST_CODE = 5;
	public static final int SEARCH_TEXT = 6;
	/**
	 * 用户头像
	 */
	private Drawable user_Drawable;
	/**
	 * 语音助手头像
	 */
	private Drawable robot_Drawable;
	/**
	 * 侧滑菜单
	 */
	private ResideMenu resideMenu;
	/**
	 * 侧滑菜单item
	 */
	private ArrayList<ResideMenuItem> menuItems;
	/**
	 * 消息通知管理者
	 */
	private NotificationManager nManager;
	private boolean isSetNotification = false;
	private static final int NOTIFICATION_ID = 0x123;
	
    private InstalledReceiver receiver;
	/**
	 * 监听百度地图初始化
	 */
    private BaiduMapSDKReceiver mapReceiver;
    private boolean isMapKeyCorrect = false; 
    
    private CustomToast toast_speech;
    private ImageView imageView_toast_voice;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		DecRawso.NewInstance(this,mHandler,true);
		
//		DecRawso.ConfigureFilter("app_BaiduNaviApplib","libapp_BaiduNaviApplib.so");
//		DecRawso.ConfigureFilter("app_BaiduVIlib","libapp_BaiduVIlib.so");
//		DecRawso.ConfigureFilter("bd_etts","libbd_etts.so");
//		DecRawso.ConfigureFilter("bds","libbds.so");
//		DecRawso.ConfigureFilter("BDSpeechDecoder_V1","libBDSpeechDecoder_V1.so");
//		DecRawso.ConfigureFilter("curl","libcurl.so");
//		DecRawso.ConfigureFilter("gnustl_shared","libgnustl_shared.so");
		
		nManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
		initResideMenu();
		/**
		 * 初始化语音动画toast
		 */
		toast_speech = CustomToast.makeText(mContext, "请开始说话！", TOAST_SHOW_TIME);
		toast_speech.setGravity(Gravity.CENTER, 0, 0);
		LinearLayout toastView = (LinearLayout) toast_speech.getView();
		RelativeLayout layout = (RelativeLayout)LayoutInflater.from(mContext).inflate(R.layout.view_speech, null,false);
		imageView_toast_voice = (ImageView)layout.findViewById(R.id.imageView_view_voice);
		toastView.addView(layout);
		
		/**
         * 开启讯飞推送服务
         */
		SharedPreferences sp = getSharedPreferences(PUSH_SETTING, Context.MODE_PRIVATE);
		if(sp.getBoolean(PUSH_ENABLE, true)){
			PushManager.startWork(getApplicationContext(), getString(R.string.iflyteck_app_id));
		}
		/**
		 * 设置model代理
		 */
		setModelDelegate(new MainModel(handler));
		setViewChangeListener(this);
		/**
		 * 语音按钮
		 */
		imageBtn_main_speech = (ImageButton) findViewById(R.id.imageBtn_main_speech);
		imageBtn_main_speech.setColorFilter(Color.GRAY);
		imageBtn_main_speech.setOnClickListener(this);
		
		initView();
		
		LinearLayout linearLayout_main_chat = (LinearLayout)findViewById(R.id.linearLayout_main_chat);
		AutoLoadingUtils.setAutoLoadingView(linearLayout_main_chat);
		
		new AsyncTask<Void, Integer, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				pm = getPackageManager();
				// HashMap,存放Activity的label属性值和包名的键值对。
				contactsMap = new HashMap<String, String>();
				initContactsMap();
				appInfoMap = getLabelNameAndPackageName();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				AutoLoadingUtils.cancelAutoLoadingView();
				// 获取root权限
				// upgradeRootPermission(getPackageCodePath());
				if (mDataArrays == null) {
					mDataArrays = new ArrayList<ChatMsgEntity>();
				}
				mAdapter = new ChatMsgViewAdapter(mContext, mDataArrays);
				chatListView.setAdapter(mAdapter);
				SwingBottomInAnimationAdapter alarmListswingLeftIn = new SwingBottomInAnimationAdapter(
						mAdapter);
				alarmListswingLeftIn.setListView(chatListView);
				chatListView.setAdapter(alarmListswingLeftIn);
				sharePreference_chat = mContext.getSharedPreferences(CHAT, MODE_PRIVATE);
				initHeadPortrait();

				if (mHandler != null) {
					mHandler.obtainMessage(RECEIVE,setStringID(R.string.knowMore)).sendToTarget();
				}
				isSetNotification = true;
				
				receiver = new InstalledReceiver();
				IntentFilter filter = new IntentFilter();
				filter.addAction("android.intent.action.PACKAGE_ADDED");
				filter.addAction("android.intent.action.PACKAGE_REMOVED");
				filter.addAction("android.intent.action.LOCALE_CHANGED");
				filter.addAction("android.intent.action.CAMERA_BUTTON");
				filter.addDataScheme("package");
				registerReceiver(receiver, filter);
				
				//Log.e(TAG, appInfoMap.get("微信"));
				
				/**
				 * 注册百度地图SDK广播监听者
				 */
				IntentFilter iFilter = new IntentFilter();
				iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
				iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
				iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
				mapReceiver = new BaiduMapSDKReceiver();
				registerReceiver(mapReceiver, iFilter);
			}
		}.execute();
		
		
	}

	/**
	 * 初始化侧滑菜单
	 */
	private void initResideMenu() {
		// attach to current activity;
		resideMenu = new ResideMenu(this);
		resideMenu.setBackground(R.drawable.reside_background);
		// resideMenu.setBackgroundColor(0);
		resideMenu.attachToActivity(this);
		resideMenu.setMenuListener(new ResideMenu.OnMenuListener() {
			@Override
			public void openMenu() {
			}

			@Override
			public void closeMenu() {
			}
		});
		// valid scale factor is between 0.0f and 1.0f. leftmenu'width is
		// 150dip.
		resideMenu.setScaleValue(0.7f);
		// 禁止从右向左滑动
		resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

		menuItems = new ArrayList<ResideMenuItem>();
		String[] Item = new String[] {
				setStringID(R.string.scanQCode),
				setStringID(R.string.title_settings),
				setStringID(R.string.deleteChat), 
				setStringID(R.string.setHeadImage),
				setStringID(R.string.title_listApps),
				};
		int[] icon = new int[] { 
				R.drawable.icon_scan,
				R.drawable.icon_settings,
				R.drawable.icon_delete,
				R.drawable.icon_pick_contact,
				R.drawable.icon_more_apps,
				};
		
		for (int i = 0; i < Item.length; i++) {
			ResideMenuItem item = new ResideMenuItem(mContext, icon[i], Item[i]);
			menuItems.add(item);
			item.setOnClickListener(this);
			resideMenu.addMenuItem(item, ResideMenu.DIRECTION_LEFT);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return resideMenu.dispatchTouchEvent(ev);
	}

	/**
	 * 初始化头像
	 */
	private void initHeadPortrait() {
		String userfilepath = getFilesDir().getPath() + "/" + USER_HEAD
				+ ".jpg";
		File userfile = new File(userfilepath);
		File user_imagefile = new File(userfile, USER_HEAD + ".jpg");

		String robotFilepath = getFilesDir().getPath() + "/" + ROBOT_HEAD
				+ ".jpg";
		File robotFile = new File(robotFilepath);
		File robot_imagefile = new File(robotFile, ROBOT_HEAD + ".jpg");

		Bitmap userImage = BitmapFactory.decodeFile(user_imagefile.getPath());

		user_Drawable = new BitmapDrawable(mContext.getResources(), userImage);

		Bitmap robotImage = BitmapFactory.decodeFile(robot_imagefile.getPath());
		robot_Drawable = new BitmapDrawable(mContext.getResources(), robotImage);
		getMessage(user_Drawable, robot_Drawable);
	}

	private String setStringID(int id) {
		return mContext.getResources().getString(id);
	}

	private Drawable setDrawable(int id) {
		return mContext.getResources().getDrawable(id);
	}

	private void setMessage(String text, boolean isComMsg, String name,
			String date, Drawable headImage) {
		if (text != null && text.length() > 0) {
			String locale = getResources().getConfiguration().locale.toString();
			JChineseConvertor jConvertor = null;
			try {
				jConvertor = JChineseConvertor.getInstance();

			} catch (IOException e) {
				e.printStackTrace();
			}
			if (jConvertor != null) {
				if (locale.contentEquals("zh_TW")) {
					// 转成繁体
					text = jConvertor.s2t(text);
				} else {
					// 转成简体
					text = jConvertor.t2s(text);
				}
			}

			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(date);
			entity.setName(name);
			entity.setMsgType(isComMsg);
			entity.setText(text);
			BitmapDrawable headBitmapDrawable = (BitmapDrawable) headImage;
			if (headBitmapDrawable.getBitmap() != null) {
				entity.setDrawable(headImage);
			} else {
				if (isComMsg) {
					robot_Drawable = setDrawable(R.drawable.assistant);
					entity.setDrawable(setDrawable(R.drawable.assistant));
				} else {
					user_Drawable = setDrawable(R.drawable.me);
					entity.setDrawable(setDrawable(R.drawable.me));
				}
			}

			mDataArrays.add(entity);
			mAdapter.notifyDataSetChanged();

			chatListView.setSelection(chatListView.getCount() - 1);
		}
	}

	private void getMessage(Drawable userDrawable, Drawable robotDrawable) {
		if (sharePreference_chat.getAll().size() > 300) {
			sharePreference_chat.edit().clear();
		} else {

			for (int i = 0; i + 2 < sharePreference_chat.getAll().size(); i = i + 3) {
				String text = sharePreference_chat.getString("" + i, "data is error");
				String date = sharePreference_chat.getString("" + (i + 2), "2014");

				if (sharePreference_chat.getBoolean("" + (i + 1), true) == true) {
					setMessage(text, true, getString(R.string.xiaoyan), date,
							robotDrawable);
				} else
					setMessage(text, false, getString(R.string.me), date,
							userDrawable);
				chatPosition = i + 3;
			}
		}

	}


	public void setUserMessage(String text, String date, Drawable drawable) {
		setMessage(text, false, getString(R.string.me), date, drawable);
		Editor spe = sharePreference_chat.edit();
		spe.putString("" + chatPosition, text);
		spe.putBoolean("" + (++chatPosition), false);
		spe.putString("" + (++chatPosition), date);
		spe.commit();
		chatPosition++;
	}

	public void setMyMessage(String text, String date, Drawable drawable) {
		setMessage(text, true, getString(R.string.xiaoyan), date, drawable);
		Editor spe = sharePreference_chat.edit();
		spe.putString("" + chatPosition, text);
		spe.putBoolean("" + (++chatPosition), true);
		spe.putString("" + (++chatPosition), date);
		spe.commit();
		chatPosition++;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String date = CommonUtil.getFormatDate(System.currentTimeMillis());
			Log.v(TAG, "msg.what = " + msg.what);
			switch (msg.what) {
			case HANDLE_RESULTS:
				// JsonResult中理应包含"rc","text","service","operation"等字符串。
				String[] JsonResult = (String[]) msg.obj;
				if (JsonResult != null) {
					// 0代表操作成功
					if ("0".equals(JsonResult[0]) || "4".equals(JsonResult[0])) {
						handleResult(JsonResult[1]);

					} else if ("1".equals(JsonResult[0])) {
						synthetizeInSilence(getString(R.string.InvalidRequest));

					} else if ("2".equals(JsonResult[0])) {
						synthetizeInSilence(getString(R.string.ServerException));

					} else if ("3".equals(JsonResult[0])) {
						synthetizeInSilence(getString(R.string.SemanticParsingError));

					}

				} else {
					synthetizeInSilence(getString(R.string.RecognizeError));
				}
				break;

			case INSTALLED:
				//String packageName = (String) msg.obj;
				// 打印包名
				//Log.e(TAG, "执行case INSTALLED:" + packageName);
				//ActivityInfo info = pm.getLaunchIntentForPackage(packageName).resolveActivityInfo(pm, 0);
				//String labelName = (String)info.loadLabel(pm);
				appInfoMap = getLabelNameAndPackageName();
				break;

			case SEND:
				String send_var = (String) msg.obj;
				if (user_Drawable != null) {
					setUserMessage(send_var, date,user_Drawable);
				} else
					setUserMessage(send_var, date,
							setDrawable(R.drawable.me));
				break;

			case RECEIVE:
				String receive_var = (String) msg.obj;
				if (robot_Drawable != null) {
					setMyMessage(receive_var, date, robot_Drawable);
				} else
					setMyMessage(receive_var, date,
							setDrawable(R.drawable.assistant));
 				break;
 			/**
	          * 解压so成功
	          */
			case DecRawso.HDL_MSGDECEND:	
					if(!DecRawso.GetInstance().ShowError(getApplicationContext(),msg.arg1))  //if no error, go on
					{				
						//showTip("收到解压完成信息");
						
						SDKInitializer.initialize(getApplicationContext());
						// 应用程序入口处调用,避免手机内存过小，杀死后台进程,造成SpeechUtility对象为null
						// 设置你申请的应用appid
						SpeechUtility.createUtility(getApplicationContext(), "appid="+getString(R.string.iflyteck_app_id));
						
						UnpackSoUtil.loadSystemLibrary();
						// 初始化语音合成对象
						mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(MainActivity.this,
								mTtsInitListener);

						mSpeechUnderstander = SpeechUnderstander.createUnderstander(MainActivity.this,
								speechUnderstanderInitListener);
						
						mTextUnderstander = TextUnderstander.createTextUnderstander(MainActivity.this, mTextUdrInitListener);
					}
					//if you don't use ProgressDialog, you must stop the application by your self, and until this message.
					break;
					
			default:

				break;
			}
		};

	};

	/**
	 * 初始化监听器（语音到语义）。
	 */
	private InitListener speechUnderstanderInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
        		showTip("初始化失败,错误码："+code);
        	}	
		}
	};

	 /**
     * 初始化监听器（文本到语义）。
     */
    private InitListener mTextUdrInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d(TAG, "textUnderstanderListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
        		showTip("初始化失败,错误码："+code);
        	}
		}
    };
    
    private TextUnderstanderListener mTextUnderstanderListener = new TextUnderstanderListener() {
		
		@Override
		public void onResult(final UnderstanderResult result) {
			if (result != null) {
				Log.e(getTAG(),"文本结果="+result
						.getResultString());
				String[] JsonResult = JsonUtil.ResultArray(result
						.getResultString());
				DataHoldUtil.setSpeechJson(result.getResultString());
				if (JsonResult[1] != null) {
					if (mHandler != null) {
						Message.obtain(mHandler, SEND, JsonResult[1]).sendToTarget();
						/**
						 * 延迟1秒处理结果
						 */
						Message msg = mHandler.obtainMessage(HANDLE_RESULTS,
								JsonResult);
						mHandler.sendMessageDelayed(msg, 1000);
					}
				} else {
					if (mHandler != null) {
						Message.obtain(mHandler, RECEIVE, setStringID(R.string.TextIsNull)).sendToTarget();
						return;
					}
				}
				if ("0".equals(JsonResult[0])) {
					String searchName = JsonUtil.parseSearchResults(result
							.getResultString());
					DataHoldUtil.setSearchName(searchName);

					if ("ANSWER".equals(JsonResult[3])) {
						DataHoldUtil.setSmartAnswer(true);
					}

					if ("telephone".equals(JsonResult[2])) {
						if ("CALL".equals(JsonResult[3])) {
							DataHoldUtil.setCallPeople(true);
						}
					}

					if ("schedule".equals(JsonResult[2])) {
						if ("CREATE".equals(JsonResult[3])) {
							DataHoldUtil.setAlarm(true);
						}
					}

					if ("message".equals(JsonResult[2])) {
						if ("SEND".equals(JsonResult[3])) {
							DataHoldUtil.setSend_SMS(true);
						}
					}

					if ("website".equals(JsonResult[2])) {
						if ("OPEN".equals(JsonResult[3])) {
							DataHoldUtil.setOpenWebsite(true);
						}
					}
					
					if ("map".equals(JsonResult[2])) {
						DataHoldUtil.setOpenMap(true);
					}
					
					if ("weather".equals(JsonResult[2])) {
						DataHoldUtil.setWeather(true);
					}
				}

			}
		}
		
		@Override
		public void onError(SpeechError error) {
			// 文本语义不能使用回调错误码14002，请确认您下载sdk时是否勾选语义场景和私有语义的发布
			//showTip("onError Code："	+ error.getErrorCode());
			synthetizeInSilence(error.getErrorDescription());
		}
		
	};
	
	private SpeechUnderstanderListener speechUnderstanderListener = new SpeechUnderstanderListener() {

		@Override
		public void onBeginOfSpeech() {
			toast_speech.setText(setStringID(R.string.startSpeech));
			imageBtn_main_speech.setColorFilter(getResources().getColor(R.color.blue));
			imageBtn_main_speech.setSelected(true);
			toast_speech.show();
		}

		@Override
		public void onEndOfSpeech() {
//			Animation animation = AnimationUtils.loadAnimation(mContext,
//					R.anim.rotate);
//			animation.setInterpolator(new LinearInterpolator());
//			imageBtn_main_speech.setImageDrawable(getResources().getDrawable(
//					R.drawable.waiting));
//			imageBtn_main_speech.startAnimation(animation);
			toast_speech.setText(setStringID(R.string.Recognizing));
			imageBtn_main_speech.setColorFilter(Color.GRAY);
			imageBtn_main_speech.setSelected(false);
			imageView_toast_voice.setImageDrawable(getResources().getDrawable(R.drawable.zero));
		}

		@Override
		public void onError(SpeechError error) {
			cancelShowTip();
			synthetizeInSilence(error.getErrorDescription());
			if(toast_speech!=null)toast_speech.cancel();
			imageBtn_main_speech.setColorFilter(Color.GRAY);
			imageBtn_main_speech.setSelected(false);
		}


		@Override
		public void onResult(UnderstanderResult results) {
			cancelShowTip();
			if(toast_speech!=null)toast_speech.cancel();
			imageBtn_main_speech.setColorFilter(Color.GRAY);
			imageBtn_main_speech.setSelected(false);
			
			if (results != null) {
				Log.e(getTAG(),"语音结果="+results
						.getResultString());
				String[] JsonResult = JsonUtil.ResultArray(results
						.getResultString());
				DataHoldUtil.setSpeechJson(results.getResultString());
				if (JsonResult[1] != null) {
					if (mHandler != null) {
						Message.obtain(mHandler, SEND, JsonResult[1]).sendToTarget();
						/**
						 * 延迟1秒处理结果
						 */
						Message msg = mHandler.obtainMessage(HANDLE_RESULTS,
								JsonResult);
						mHandler.sendMessageDelayed(msg, 1000);
					}
				} else {
					if (mHandler != null) {
						Message.obtain(mHandler, RECEIVE, setStringID(R.string.TextIsNull)).sendToTarget();
						return;
					}
				}
				if ("0".equals(JsonResult[0])) {
					String searchName = JsonUtil.parseSearchResults(results
							.getResultString());
					DataHoldUtil.setSearchName(searchName);

					if ("ANSWER".equals(JsonResult[3])) {
						DataHoldUtil.setSmartAnswer(true);
					}

					if ("telephone".equals(JsonResult[2])) {
						if ("CALL".equals(JsonResult[3])) {
							DataHoldUtil.setCallPeople(true);
						}
					}

					if ("schedule".equals(JsonResult[2])) {
						if ("CREATE".equals(JsonResult[3])) {
							DataHoldUtil.setAlarm(true);
						}
					}

					if ("message".equals(JsonResult[2])) {
						if ("SEND".equals(JsonResult[3])) {
							DataHoldUtil.setSend_SMS(true);
						}
					}

					if ("website".equals(JsonResult[2])) {
						if ("OPEN".equals(JsonResult[3])) {
							DataHoldUtil.setOpenWebsite(true);
						}
					}
					
					if ("map".equals(JsonResult[2])) {
						DataHoldUtil.setOpenMap(true);
					}
					
					if ("weather".equals(JsonResult[2])) {
						DataHoldUtil.setWeather(true);
					}
				}

			}

		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onVolumeChanged(int v, byte[] arg1) {
			// TODO Auto-generated method stub
			
			if (v >= 0 && v <= 5)
				imageView_toast_voice.setImageDrawable(getResources().getDrawable(R.drawable.zero));
			if (v > 5 && v <= 10)
				imageView_toast_voice.setImageDrawable(getResources().getDrawable(R.drawable.one));
			if (v > 10 && v <= 15)
				imageView_toast_voice.setImageDrawable(getResources().getDrawable(R.drawable.two));
			if (v > 15 && v <= 20)
				imageView_toast_voice.setImageDrawable(getResources().getDrawable(R.drawable.three));
			if (v > 20 && v <= 25)
				imageView_toast_voice.setImageDrawable(getResources().getDrawable(R.drawable.four));
			if (v > 25 && v <= 30)
				imageView_toast_voice.setImageDrawable(getResources().getDrawable(R.drawable.five));
			if (v > 30 && v <= 35)
				imageView_toast_voice.setImageDrawable(getResources().getDrawable(R.drawable.six));

		}

	};

	private void startSpeechUnderstand() {
		// 设置参数 为何每次进行语义理解都要设置参数？
		setSpeechParams();
		if (mSpeechUnderstander.isUnderstanding()) {// 开始前检查状态
			mSpeechUnderstander.stopUnderstanding();
		} else {
			int ret = mSpeechUnderstander.startUnderstanding(speechUnderstanderListener);
			if (ret != 0) {
				Log.e(TAG, "语义理解失败,错误码:" + ret);
			} else {
				Log.e(TAG, "请开始说话!! ");
			}
		}
	}
	
	/**
	 * 开始文本语义理解
	 * @param text
	 */
	private void startTextUnderstand(String text) {
		//错误码
		int ret = 0;
		if(mTextUnderstander.isUnderstanding()){
			mTextUnderstander.cancel();
			showTip("取消");
		}else {
			ret = mTextUnderstander.understandText(text, mTextUnderstanderListener);
			if(ret != 0)
			{
				showTip("文本语义理解失败,错误码:"+ ret);
			}
		}
	}

	private void setSpeechParams() {
		mSpeechUnderstander.setParameter(SpeechConstant.PARAMS, null);
//		String locale = getResources().getConfiguration().locale.toString();
//		// "mandarin";
//		Log.v(TAG, locale);
//		if (locale.contentEquals("en_US")) {
//			mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "en_us");
//		} else {
//			// 设置语言
//			mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
//			mSpeechUnderstander.setParameter(SpeechConstant.ACCENT, "mandarin");
//		}
        SharedPreferences sp = getSharedPreferences(VOICE_SETTING, Context.MODE_PRIVATE);
		String lang = sp.getString(VOICE_LANGUAGE, "mandarin");
		if (lang.equals("en_us")) {
			// 设置语言
			mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "en_us");
		}else {
			// 设置语言
			mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mSpeechUnderstander.setParameter(SpeechConstant.ACCENT, lang);
		}
		
		// 设置语音前端点
		mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS, "4000");
		// 设置语音后端点
		mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS, "1000");
		// 设置标点符号
		mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT, "1");
		
		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		//mSpeechUnderstander.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		//mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/sud.wav");
	}

	/**
	 * 初期化监听。
	 */

	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d("", "InitListener init() code = " + code);
			if (code != ErrorCode.SUCCESS)
				showTip(getString(R.string.text_login_fail));
		}
	};

	/**
	 * 初始化联系人
	 */
	private void initContactsMap() {
		ContentResolver cResolver = mContext.getContentResolver();
		Cursor cursor = cResolver.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String name = cursor
						.getString(cursor
								.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
				String number = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				if (TextUtils.isEmpty(number))
					continue;
				contactsMap.put(name, number);

			}
			cursor.close();
		}

	}

	// 第三方应用安装或卸载的广播接收者
   class InstalledReceiver extends BroadcastReceiver {
        private final String TAG = this.getClass().getName();
		@Override
		public void onReceive(Context context, Intent intent) {
			// 监听是否有应用安装或者卸载
			if (intent.getAction().equals(PACKAGE_ADDED)
					|| intent.getAction().equals(PACKAGE_REMOVED)) {
				String packageName = intent.getDataString();
				Log.v(TAG,
						"The installed or uninstalled APP's package name  is "
								+ packageName);
				packageName = packageName.replace("package:", "");
				if (mHandler != null) {
					mHandler.obtainMessage(INSTALLED, packageName).sendToTarget();
				}

				if (ListAppsActivity.mHandler != null) {
					ListAppsActivity.mHandler.obtainMessage(INSTALLED, packageName).sendToTarget();
				}
			}

			if (intent.getAction().equals(
					"android.intent.action.LOCALE_CHANGED")) {
				Log.v(TAG, "android.intent.action.LOCALE_CHANGED");
				if (mHandler != null) {
					mHandler.obtainMessage(INSTALLED).sendToTarget();
				}
			}

			if (intent.getAction()
					.equals("android.intent.action.CAMERA_BUTTON")) {
				Log.v(TAG, "android.intent.action.CAMERA_BUTTON");
				intent.setClassName(context,
						context.getPackageName()+".MainActivity");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}

			if (intent.getAction().equals("com.vps.help")) {
				String locale = context.getResources().getConfiguration().locale
						.toString();
				InputStream inputstream = null;
				if (locale.contentEquals("zh_TW")) {
					inputstream = context.getResources().openRawResource(
							R.raw.chinese_traditional);
				} else {
					inputstream = context.getResources().openRawResource(
							R.raw.chinese_simplified);
				}

				byte[] buffer = new byte[1024];
				int count = 0;
				FileOutputStream fos = null;
				try {
					File file = new File(context.getFilesDir().getPath()
							+ "/help.html");
					try {
						String command = "chmod 777 " + file.getAbsolutePath();
						Log.i("zyl", "command = " + command);
						Runtime runtime = Runtime.getRuntime();
						runtime.exec(command);
					} catch (IOException e) {
						Log.i("zyl", "chmod fail!!!!");
						e.printStackTrace();
					}
					fos = new FileOutputStream(file);
					while ((count = inputstream.read(buffer)) > 0) {
						fos.write(buffer, 0, count);
					}
					fos.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					Toast.makeText(context, "Check your sdcard",
							Toast.LENGTH_LONG).show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 保存label名和包名键值对
	 * @return 
	 */
	private HashMap<String, String> getLabelNameAndPackageName() {
			Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
			mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			// 通过查询，获得所有ResolveInfo对象.
			List<ResolveInfo> resolveInfos = pm
					.queryIntentActivities(mainIntent, 0);
			// 调用系统排序 ， 根据name排序
			// 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
			Collections.sort(resolveInfos,
					new ResolveInfo.DisplayNameComparator(pm));
			HashMap<String, String> map = new HashMap<String, String>();
			for (ResolveInfo reInfo : resolveInfos) {
				// 获得该应用程序的包名
				String pkgName = reInfo.activityInfo.packageName;
				// 获得Activity的label名（注意：不是Application的label名）
				String labelName = (String) reInfo.activityInfo
						.loadLabel(pm);
				if (labelName.contains(" "))
					labelName = labelName.replace(" ", "");
				labelName = CommonUtil.setUpperCase(labelName);
				try {
					// 无论是繁体或是简体，都将其转换成简体字。
					labelName = JChineseConvertor.getInstance().t2s(labelName);
				} catch (IOException e) {
					e.printStackTrace();
				}
				map.put(labelName, pkgName);
				Log.v(TAG, "labelName：" + labelName + " ，packageName："+ pkgName);
			}
		return map;
	}

	/**
	 * 在用户的语音转成文本的时候，截取文本中相应的应用名称。（大多数情况下，我们会说：启动或登录XXX，启动或登录则作为action，
	 * 那么action后面肯定是应用程序名，所以我们只需要对截取action后面的字符串，这个字符串就是应用程序名。否则做出相应的处理）
	 * @param text
	 * @param myResult
	 * @return
	 */
	private String returnResult(String text, String myResult) {
		try {
			int resultIndex = text.indexOf(appAction) + appAction.length() + 1;
			if (resultIndex < text.length()) {
				myResult = CommonUtil.middle(text, resultIndex,
						text.length());
			}
			// 若结果有空格，则将空格去掉
			myResult = myResult.replace(" ", "");
		} catch (Exception e) {
			Log.v(TAG, "执行returnResult的catch：" + myResult);
			synthetizeInSilence(getString(R.string.CatchException));
		}
		return myResult;

	}

	/**
	 * 拨打电话
	 * @param who
	 */
	public void ringUp(String who) {
		if (contactsMap.containsKey(who)) {
			try {
				String number = contactsMap.get(who);
				if (number.contains("-"))
					number = number.replace("-", "");
				String call = String.format(setStringID(R.string.service_call),
						who, number);
				synthetizeInSilence(getString(R.string.ServiceUser) + call);

				Intent intent = new Intent(Intent.ACTION_CALL);

				intent.setData(Uri.parse("tel:" + number));
				startActivity(intent);
			} catch (Exception e) {
				System.out.println(who);
				synthetizeInSilence(getString(R.string.CatchException));
			}
		} else {
			synthetizeInSilence(getString(R.string.HasntTheNumber));
		}
	}

	/**
	 * 发送短信
	 * @param who
	 */
	public void sendSMS(String who) {
		if (contactsMap.containsKey(who)) {
			try {
				String number = contactsMap.get(who);
				if (number.contains("-"))
					number = number.replace("-", "");

				synthetizeInSilence(getString(R.string.ServiceUser)
						+ setStringID(R.string.service_sms));

				Intent smsIntent = new Intent(Intent.ACTION_SENDTO);

				smsIntent.setData(Uri.parse("smsto:" + number));
				startActivity(smsIntent);
			} catch (Exception e) {
				System.out.println(who);
				synthetizeInSilence(getString(R.string.CatchException));
			}
		} else {
			synthetizeInSilence(getString(R.string.HasntTheNumber));
		}
	}

	// 设置闹钟
	private void setAlarmToRemind(Calendar currentTime) {
		int hour = currentTime.get(Calendar.HOUR_OF_DAY);
		int minute = currentTime.get(Calendar.MINUTE) + 1;

		AlarmManager aManager = (AlarmManager) mContext
				.getSystemService(Service.ALARM_SERVICE);
		Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
		intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
		intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
		PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, 0,
				intent, 0);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		// calendar.set(Calendar.SECOND, currentTime.get(Calendar.SECOND)+2);
		if (calendar.after(currentTime)) {
			aManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
					pIntent);
			synthetizeInSilence("已经成功为您设定了"
					+ CommonUtil.getFormatDate(calendar
							.get(Calendar.HOUR_OF_DAY)) + ":"
					+ CommonUtil.getFormatDate(calendar.get(Calendar.MINUTE))
					+ "的闹钟");
		} else
			synthetizeInSilence("您设置了一个过时的闹钟");
	}

	/**
	 * 指定打开一个网站
	 * @param webSiteName 网站名称
	 * @param url 网站url
	 */
	private void openWebSite(String webSiteName, String url) {
		try {
			synthetizeInSilence(getString(R.string.ServiceUser)
					+ setStringID(R.string.open) + webSiteName);

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			startActivity(intent);
		} catch (Exception e) {
			System.out.println(webSiteName);
			synthetizeInSilence(getString(R.string.CatchException));
		}
	}

	/**
	 * 返回主页
	 * @param text
	 */
	public void backToHome(String text) {
		try {
			synthetizeInSilence(getString(R.string.ServiceUser) + text);

			Intent i = new Intent(Intent.ACTION_MAIN);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addCategory(Intent.CATEGORY_HOME);
			startActivity(i);

		} catch (Exception e) {
			System.out.println(text);
			synthetizeInSilence(getString(R.string.CatchException));
		}
	}

	/**
	 * 启动name对应的APP（name是APP的launcher名称）
	 * @param action
	 * @param name
	 */
	public void startAPP(String action, String name) {
		Log.v(TAG, "startAPP's name =" + name + ".");
		if (appInfoMap.containsKey(name)) {
			try {
				synthetizeInSilence(getString(R.string.ServiceUser) + action
						+ name);

				Intent i = new Intent(Intent.ACTION_MAIN);
				i = pm.getLaunchIntentForPackage(appInfoMap.get(name));
				i.addCategory(Intent.CATEGORY_LAUNCHER);

				startActivity(i);
			} catch (Exception e) {
				System.out.println(name);
				synthetizeInSilence(getString(R.string.CatchException));
			}
		} else if (TextUtils.isEmpty(name)) {
			synthetizeInSilence(getString(R.string.DoNotKnow));
		} else {
			synthetizeInSilence(getString(R.string.DoNotInstallAPP));
		}
	}

	// 退出应用
	public void exitAPP(String action, String name) {
		Log.v(TAG, "exitAPP's name =" + name + ".");
		if (appInfoMap.containsKey(name)) {
			try {
				synthetizeInSilence(getString(R.string.ServiceUser) + action
						+ name);

				Intent i = new Intent(Intent.ACTION_MAIN);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addCategory(Intent.CATEGORY_HOME);
				startActivity(i);

				Message msg = mHandler.obtainMessage(KILL_BACKGROUNDPROCESS,
						appInfoMap.get(name));
				mHandler.sendMessageDelayed(msg, 3000);
			} catch (Exception e) {
				System.out.println(name);
				synthetizeInSilence(getString(R.string.CatchException));
			}
		} else if (TextUtils.isEmpty(name)) {
			synthetizeInSilence(getString(R.string.DoNotKnow));
		} else {
			synthetizeInSilence(getString(R.string.DoNotInstallAPP));
		}
	}

	/**
	 * 卸载应用
	 * @param action 卸载、删除等
	 * @param appName 应用名称
	 */
	@SuppressLint("InlinedApi")
	private void uninstallAPP(String action, String appName) {
		Log.v(TAG, "uninstallAPP's name =" + appName + ".");
		if (appInfoMap.containsKey(appName)) {
			try {
				synthetizeInSilence(getString(R.string.ServiceUser) + action
						+ appName);
				Intent intent = new Intent();
				Uri uri = Uri.fromParts("package", appInfoMap.get(appName), null);
				// intent.setAction(Intent.ACTION_DELETE);
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					intent.setAction(Intent.ACTION_DELETE);
				} else {
					intent.setAction(Intent.ACTION_UNINSTALL_PACKAGE);
				}
				intent.setData(uri);
				// 这句话最好加上
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
				Log.v(TAG, "执行uninstallAPP的catch：" + appName);
				synthetizeInSilence(getString(R.string.CatchException));
			}
		} else if (TextUtils.isEmpty(appName)) {
			synthetizeInSilence(getString(R.string.DoNotKnow));
		} else {
			synthetizeInSilence(getString(R.string.DoNotInstallAPP));
		}
	}

	/**
	 * 搜索应用
	 * @param searchName
	 */
	public void searchAPP(String searchName) {
		Log.v(TAG, "searchAPP's name =" + searchName + ".");
		try {
			if (searchName != null && searchName != "") {
				synthetizeInSilence(getString(R.string.ServiceUser)
						+ getString(R.string.search) + searchName);

				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_WEB_SEARCH);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(SearchManager.QUERY, searchName);

				startActivity(intent);

			} else {
				synthetizeInSilence(getString(R.string.DoNotKnow));
			}
		} catch (Exception e) {
			synthetizeInSilence(getString(R.string.SearchFail));
			e.printStackTrace();
		}
	}

	// 判断用户输入的是哪个动作，用action保存这个动作
	private boolean isContainString(String text, String actionString) {
		if (text.contains(actionString)) {
			appAction = actionString;
			return true;
		} else
			return false;
	}

	/**
	 * 处理文本
	 * @param text
	 */
	public void handleResult(String text) {
		if (text != null && text != "") {
			Log.e(getTAG(),"handle result:"+text);
			// 将小写字母设置为大写
			text = CommonUtil.setUpperCase(text);
			// 先把text中的句号去掉
			text = text.replace("。", "");
			String myResult = "";

			// 判断用户说出的动作，再用这个动作结束应用，并合成语音。
			if (isContainString(text, getString(R.string.close))
					|| isContainString(text, getString(R.string.exit))) {
				Log.e(getTAG(),"handle result: exitApp");
				myResult = returnResult(text, myResult);
				exitAPP(appAction, myResult);
			}
			// 判断用户说出的动作，再用这个动作启动应用，并合成语音。
			else if (isContainString(text, getString(R.string.open))
					|| isContainString(text, getString(R.string.launch))
					|| isContainString(text, getString(R.string.login))) {
				if (DataHoldUtil.isOpenWebsite()) {
					Log.e(getTAG(),"handle result: launch:OpenWebsite");
					HashMap<String, String> map = JsonUtil
							.getWebsiteNameAndUrl(DataHoldUtil.getSpeechJson());
					if (map != null) {
						if (map.get("name") != null && map.get("url") != null) {
							openWebSite(map.get("name"), map.get("url"));
						} else {
							synthetizeInSilence(getString(R.string.DoNotKnow));
						}
					}
					DataHoldUtil.setOpenWebsite(false);
				} else {
					Log.e(getTAG(),"handle result: launchApp");
					myResult = returnResult(text, myResult);
					startAPP(appAction, myResult);
				}
			}
			// 卸载应用
			else if (isContainString(text, getString(R.string.uninstall))
					|| isContainString(text, getString(R.string.remove))) {
				Log.e(getTAG(),"handle result: unistallApp");
				myResult = returnResult(text, myResult);
				uninstallAPP(appAction, myResult);
			}

			// 返回主页
			else if (getString(R.string.returnToHome).equals(text)
					|| getString(R.string.returnToHomePage).equals(text)
					|| getString(R.string.backToHome).equals(text)
					|| getString(R.string.backToHomePage).equals(text)) {
				
				backToHome(text);
			}
			// 搜索相关应用
			else if (DataHoldUtil.isAppFlag() || DataHoldUtil.isWebsearchFlag()) {
				Log.e(getTAG(),"handle result: search");
				String searchName = DataHoldUtil.getSearchName();
				if (searchName != null) {
					Log.v(TAG, "The app that is searched is " + searchName);
					searchAPP(searchName);
					DataHoldUtil.setAppFlag(false);
					DataHoldUtil.setWebsearchFlag(false);
				}
			}
			// 智能回答
			else if (DataHoldUtil.isSmartAnswer()) {
				Log.e(getTAG(),"handle result: smartAnswer");
				String answer = JsonUtil.parseSmartAnswer(DataHoldUtil
						.getSpeechJson());
				synthetizeInSilence(answer);
				DataHoldUtil.setSmartAnswer(false);
			}

			// 拨打电话
			else if (DataHoldUtil.isCallPeople()) {
				Log.e(getTAG(),"handle result: callPeople");
				String name = JsonUtil.getName(DataHoldUtil.getSpeechJson());
				ringUp(name);
				DataHoldUtil.setCallPeople(false);
			}

			// 设置闹钟
			else if (DataHoldUtil.isAlarm()) {
				Log.e(getTAG(),"handle result: setAlarm");
				DataHoldUtil.setAlarm(false);
			}

			// 发送短信
			else if (DataHoldUtil.isSend_SMS()) {
				Log.e(getTAG(),"handle result: SMS");
				String name = JsonUtil.getName(DataHoldUtil.getSpeechJson());
				sendSMS(name);
				DataHoldUtil.setSend_SMS(false);
			}

			else if (DataHoldUtil.isOpenWebsite()) {
				Log.e(getTAG(),"handle result: openWebsite");
				HashMap<String, String> map = JsonUtil
						.getWebsiteNameAndUrl(DataHoldUtil.getSpeechJson());
				if (map != null) {
					if (map.get("name") != null && map.get("url") != null) {
						openWebSite(map.get("name"), map.get("url"));
					} else {
						synthetizeInSilence(getString(R.string.DoNotKnow));
					}
				}
				DataHoldUtil.setOpenWebsite(false);
			}

			/**
			 * 解析语音文本为查询天气
			 */
			else if(DataHoldUtil.isWeather()){
				Log.e(getTAG(),"handle result: weather");
					synthetizeInSilence(getString(R.string.ServiceUser)+getString(R.string.query_weather));
					handler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
							ArrayList<String> list = JsonUtil.parseWeatherResult(DataHoldUtil.getSpeechJson());
							Intent intent = new Intent(mContext,WeatherActivity.class);
							intent.putStringArrayListExtra("voiceResult", list);
							mContext.startActivity(intent);
							} catch (JSONException e) {
								synthetizeInSilence(getString(R.string.CatchException));
								DataHoldUtil.setWeather(false);
							}
							DataHoldUtil.setWeather(false);
						}
					}, 1500);
					
			}
			
			/**
			 * 如果是地图服务
			 */
			else if(DataHoldUtil.isOpenMap()){
				Log.e(getTAG(),"handle result: map");

				synthetizeInSilence(getString(R.string.ServiceUser)+"定位，请稍等...");
				String[] jsonArray = JsonUtil.ResultArray(DataHoldUtil.getSpeechJson());
				if("POSITION".equals(jsonArray[3])){
					try {
						ArrayList<String> result = JsonUtil.parseMapPositionResult(DataHoldUtil.getSpeechJson());
						Intent intent = new Intent(mContext,MapActivity.class);
						intent.putStringArrayListExtra(LOCATE_SOMEWHERE, result);
						startActivity(intent);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if("ROUTE".equals(jsonArray[3])){
					try {
						ArrayList<String> result = JsonUtil.parseMapRouteResult(DataHoldUtil.getSpeechJson());
						Intent intent = new Intent(mContext,MapActivity.class);
						intent.putStringArrayListExtra(ROUTE, result);
						startActivity(intent);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				DataHoldUtil.setOpenMap(false);
			}
				
			// else提示不知道
			else {
				Log.e(getTAG(),"handle result: not know");
				synthetizeInSilence(getString(R.string.DoNotKnow));
			}
		} else {
			Log.e(getTAG(),"handle result: parse error");
			synthetizeInSilence(getString(R.string.ParseError));
		}
	}

	/**
	 * 初始化控件
	 */
	public void initView() {
		chatListView = (ListView) findViewById(R.id.listview_main_chat);
		
		ImageButton imageBtn_main_moreMenu = (ImageButton)findViewById(R.id.imageBtn_main_moreMenu);
		Bitmap bm = ImageUtil.rotateBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_nav_more_white), 90);
		imageBtn_main_moreMenu.setImageBitmap(bm);
		imageBtn_main_moreMenu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				resideMenu.openMenu(0);
			}
		});
		
		final ImageButton imageBtn_textOperation = (ImageButton)findViewById(R.id.imageBtn_textOperation);
		imageBtn_textOperation.setColorFilter(Color.GRAY);
		imageBtn_textOperation.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					imageBtn_textOperation.setColorFilter(getResources().getColor(R.color.blue));
					break;
                case MotionEvent.ACTION_UP:
                	imageBtn_textOperation.setColorFilter(Color.GRAY);
                	Intent intent = new Intent(MainActivity.this,SearchActivity.class);
    				startActivityForResult(intent,SEARCH_TEXT);
					break;

				default:
					break;
				}
				return false;
			}
		});
	}

	/**
	 * 显示设置头像选择对话框
	 */
	private void showHeadPortraitSelectDialog() {
		new AlertDialog.Builder(this)
				.setTitle(setStringID(R.string.setHeadImage))
				.setItems(
						new String[] { setStringID(R.string.setUserHead),
								setStringID(R.string.setRobotHead) },
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {

								case USER_IMAGE_REQUEST_CODE:
									Intent userIntent = new Intent();
									userIntent.setType("image/*"); // 设置文件类型
									userIntent
											.setAction(Intent.ACTION_GET_CONTENT);
									startActivityForResult(userIntent,
											USER_IMAGE_REQUEST_CODE);
									break;
								case ROBOT_IMAGE_REQUEST_CODE:
									Intent robotIntent = new Intent();
									robotIntent.setType("image/*"); // 设置文件类型
									robotIntent
											.setAction(Intent.ACTION_GET_CONTENT);
									startActivityForResult(robotIntent,
											ROBOT_IMAGE_REQUEST_CODE);
									break;
									
								case CAMERA_REQUEST_CODE:
									Intent intentFromCapture = new Intent(
											MediaStore.ACTION_IMAGE_CAPTURE);
									// 判断存储卡是否可以用，可用进行存储
									if (CommonUtil.hasSdcard()) {
										intentFromCapture.putExtra(
												MediaStore.EXTRA_OUTPUT,
												Uri.fromFile(new File(mContext
														.getFilesDir()
														.getPath()
														+ "/"
														+ USER_HEAD
														+ ".jpg")));
									}
									startActivityForResult(intentFromCapture,
											CAMERA_REQUEST_CODE);
									
								default:
									break;
								}
							}
						}).setIcon(R.drawable.ic_pick_contact)
				.create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case USER_IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData(), USER_RESULT_REQUEST_CODE);
				break;
			case ROBOT_IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData(), ROBOT_RESULT_REQUEST_CODE);
				break;
			/**
			 * 此方法中注释掉的代码是实现裁剪功能的代码。 实现裁剪必须将上述的 data.putExtra("WHOS_HEAD",
			 * USER_IMAGE_REQUEST_CODE); getImageToView(data); 去掉
			 */
			case USER_RESULT_REQUEST_CODE:
				data.putExtra("WHOS_HEAD", USER_IMAGE_REQUEST_CODE);
				getImageToView(data);
				break;

			case ROBOT_RESULT_REQUEST_CODE:
				data.putExtra("WHOS_HEAD", ROBOT_IMAGE_REQUEST_CODE);
				getImageToView(data);
				break;

			case CAMERA_REQUEST_CODE:

				break;

			case FILE_REQUEST_CODE:
				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				shareIntent.setType("*/*");// text/plain
				shareIntent.putExtra(Intent.EXTRA_SUBJECT,
						getString(R.string.extra_subject));
				shareIntent.putExtra(Intent.EXTRA_TEXT,
						getString(R.string.extra_text));

				shareIntent.putExtra(Intent.EXTRA_STREAM, data.getData());
				shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(shareIntent, getTitle()));
				break;

			case SEARCH_TEXT:
				Log.e(getTAG(), "SEARCH_TEXT");
				String searchKey = data.getStringExtra(SEARCH_KEY);
				startTextUnderstand(searchKey);
				break;
				
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * @param uri
	 */
	private void startPhotoZoom(Uri uri, int whos_head) {
		Intent intent = new Intent(ACTION_CROP);

		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 90);
		intent.putExtra("outputY", 90);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, whos_head);
	}

	

	private static int sampleSize(int width, int target) {
		int result = 1;
		for (int i = 0; i < 10; i++) {
			if (width < target * 2) {
				break;
			}
			width = width / 2;
			result = result * 2;
		}
		return result;
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	@SuppressLint("NewApi")
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = null;
			try {
				// 裁剪时使用
				photo = extras.getParcelable("data");

				/*
				 * Options options = new Options(); options.inJustDecodeBounds =
				 * true; //photo =
				 * MediaStore.Images.Media.getBitmap(mContext.getContentResolver
				 * (), data.getData()); photo =
				 * BitmapFactory.decodeFile(data.getStringExtra
				 * ("path"),options); int size = Math.max(options.outWidth,
				 * options.outHeight); int imageSize = sampleSize(size, 400);
				 * options.inSampleSize = imageSize; options.inDither = false;
				 * options.inPurgeable = true; options.inInputShareable = true;
				 * options.inTempStorage = new byte[1024 * 1024];
				 * options.inJustDecodeBounds = false; photo =
				 * BitmapFactory.decodeFile
				 * (data.getStringExtra("path"),options);
				 */

				// 使用系统自带相册时使用
				// String path = getAbsoluteImagePath(data.getData());
				// int degree = readPictureDegree(path);
				// photo = MainActivity.scaleToFit(photo, photo.getWidth(),
				// photo.getHeight(), degree);

				// 使用本人开发的图片浏览器时使用下列语句
				// int degree = readPictureDegree(data.getStringExtra("path"));

				// 先按比例缩放图片，不旋转
				// photo = MainActivity.scaleToFit(photo, 90, 90, 0);
				/*
				 * int width = photo.getWidth(); // 图片宽度 int height =
				 * photo.getHeight(); // 图片高度 float scale = 0; if(width>height){
				 * // 90.0对应ImageView的长度 scale = (float) (width/90.0); } else {
				 * scale = (float) (height/90.0); } photo =
				 * MainActivity.scaleToFit(photo, dip2px(mContext, width/scale),
				 * dip2px(mContext,height/scale), degree);
				 */
			} catch (Exception e) {
				e.printStackTrace();
			}

			int whos_head = extras.getInt("WHOS_HEAD");
			if (whos_head == USER_IMAGE_REQUEST_CODE) {
				ImageUtil.storeImageToSDcard(photo, USER_HEAD, mContext.getFilesDir()
						.getPath() + "/" + USER_HEAD + ".jpg");
				user_Drawable = new BitmapDrawable(mContext.getResources(),
						photo);
			} else if (whos_head == ROBOT_IMAGE_REQUEST_CODE) {
				ImageUtil.storeImageToSDcard(photo, ROBOT_HEAD, mContext.getFilesDir()
						.getPath() + "/" + ROBOT_HEAD + ".jpg");
				robot_Drawable = new BitmapDrawable(mContext.getResources(),
						photo);
			}

			for (int i = 0; i < mDataArrays.size(); i++) {
				ChatMsgEntity cEntity = mDataArrays.get(i);
				if (cEntity.getMsgType())
					mDataArrays.get(i).setDrawable(robot_Drawable);
				else
					mDataArrays.get(i).setDrawable(user_Drawable);
			}
			mAdapter.notifyDataSetChanged();
		}
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 使用SpeechSynthesizer合成语音，不弹出合成Dialog.
	 * 
	 * @param text
	 */
	private void synthetizeInSilence(String text) {
        Log.e(getTAG(), "文本转语音开始");
		String locale = getResources().getConfiguration().locale.toString();
		JChineseConvertor jConvertor = null;
		try {
			jConvertor = JChineseConvertor.getInstance();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (jConvertor != null) {
			if (locale.contentEquals("zh_TW")) {
				// 转成繁体
				text = jConvertor.s2t(text);
			} else {
				// 转成简体
				text = jConvertor.t2s(text);
			}
		}
		/**
		 * 在语音设置中是否开启了语音合成功能。
		 */
		SharedPreferences sp = getSharedPreferences(VOICE_SETTING, Context.MODE_PRIVATE);
		if(sp.getBoolean(VOICE_ENABLE, true)){
		if (null == mSpeechSynthesizer) {
			// 创建合成对象.
			mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(
					MainActivity.this, mTtsInitListener);
		}
		// 清空参数
		mSpeechSynthesizer.setParameter(SpeechConstant.PARAMS, null);
//		// 根据合成引擎设置相应参数
//		mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE,
//				SpeechConstant.TYPE_CLOUD);
		// 设置在线合成发音人
		mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, sp.getString(VOICE_SPEAKER, "aisxa"));
		// 获取语速
		int speed = 50;
		// 设置语速
		mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, "" + speed);
		// 获取音量.
		int volume = 50;
		// 设置音量
		mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "" + volume);
		// 获取语调
		int pitch = 50;
		// 设置语调
		mSpeechSynthesizer.setParameter(SpeechConstant.PITCH, "" + pitch);

		// 设置播放器音频流类型
		mSpeechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, "3");
		// 设置播放合成音频打断音乐播放，默认为true
		mSpeechSynthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS,String.valueOf(sp.getBoolean(VOICE_INTERRUPTED, true)));
		
		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//		mSpeechSynthesizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//		mSpeechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH,
//				Environment.getExternalStorageDirectory() + "/msc/tts.wav");
		// 进行语音合成.
		mSpeechSynthesizer.startSpeaking(text, mSynthesizerListener);
		}
		if (mHandler != null) {
			Message msg = mHandler.obtainMessage(RECEIVE, text);
			mHandler.sendMessage(msg);
		}
	}

	// 编写一个类实现语音合成接口
	private SynthesizerListener mSynthesizerListener = new SynthesizerListener() {

		@Override
		public void onSpeakBegin() {

		}

		@Override
		public void onSpeakPaused() {

		}

		@Override
		public void onSpeakResumed() {

		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {

		}

		@Override
		public void onCompleted(SpeechError error) {

		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			// TODO Auto-generated method stub
			
		}

	};

	@Override
	protected void onPause() {
		super.onPause();
		if(PushManager.isPushEnabled(getApplicationContext()))PushManager.onPause(getApplicationContext());
		
		if(toast_speech!=null)toast_speech.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (isActive)
			cancelNotification();
		if(PushManager.isPushEnabled(getApplicationContext()))PushManager.onResume(getApplicationContext());
		
		if(toast_speech!=null)toast_speech.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (isSetNotification&&!isActive)setNotification();
		
		if(mSpeechUnderstander.isUnderstanding()){
			mSpeechUnderstander.stopUnderstanding();
		}
		
		if(!isActive&&mSpeechSynthesizer.isSpeaking()){
			mSpeechSynthesizer.stopSpeaking();
		}
		
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		Log.e(TAG, "onDestroy");
		
		unregisterReceiver(receiver);
		unregisterReceiver(mapReceiver);
		
    	if(mTextUnderstander.isUnderstanding())
    		mTextUnderstander.cancel();
    	mTextUnderstander.destroy();
    	
    	mSpeechSynthesizer.destroy();
    	mSpeechUnderstander.destroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(mContext,
						setStringID(R.string.clickAgainToExitApp),
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				isSetNotification = false;
				cancelNotification();
				finish();
				overridePendingTransition(0, R.anim.down);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

//	// 创建菜单
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		super.onCreateOptionsMenu(menu);
//
//		menu.add(0, menu_queryApp, 0, R.string.queryAllApps).setIcon(
//				android.R.drawable.ic_menu_sort_by_size);
//		menu.add(0, menu_setHeadImage, 0, R.string.setHeadImage).setIcon(
//				android.R.drawable.ic_menu_preferences);
//		menu.add(0, menu_delete, 0, R.string.deleteChat).setIcon(
//				android.R.drawable.ic_menu_delete);
//		menu.add(0, menu_help, 0, R.string.title_help).setIcon(
//				android.R.drawable.ic_menu_help);
//		menu.add(0, menu_detail, 0, R.string.title_about).setIcon(
//				android.R.drawable.ic_menu_info_details);
//		menu.add(0, menu_share, 0, R.string.share).setIcon(
//				android.R.drawable.ic_menu_share);
//
//		// menu.add(0, 6, 0, "分享我的app");
//		return true;
//	}
//
//	/**
//	 * 原生菜单选项处理功能
//	 */
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		super.onOptionsItemSelected(item);
//		switch (item.getItemId()) {
//		case menu_delete:
//			new AlertDialog.Builder(mContext)
//					.setTitle(setStringID(R.string.deleteChat) + "！")
//					.setPositiveButton(setStringID(R.string.sure),
//							new DialogInterface.OnClickListener() {
//
//								@Override
//								public void onClick(DialogInterface arg0,
//										int arg1) {
//									mDataArrays.clear();
//									mAdapter.setmDataArrays(mDataArrays);
//									mAdapter.notifyDataSetChanged();
//									sharePreference_chat.edit().clear();
//									sharePreference_chat.edit().commit();
//									chatPosition = 0;
//								}
//							})
//					.setNegativeButton(setStringID(R.string.cancel),
//							new DialogInterface.OnClickListener() {
//
//								@Override
//								public void onClick(DialogInterface arg0,
//										int arg1) {
//
//								}
//							}).setIcon(android.R.drawable.ic_menu_delete)
//					.setCancelable(false).create().show();
//			break;
//
//		case menu_queryApp:
//			Intent intent = new Intent();
//			intent.setClass(mContext, ListAppsActivity.class);
//			startActivity(intent);
//			break;
//
//		case menu_setHeadImage:
//			if (CommonUtil.hasSdcard()) {
//				showHeadPortraitSelectDialog();
//			} else
//				Toast.makeText(mContext, setStringID(R.string.checkSDcard),
//						Toast.LENGTH_LONG).show();
//			break;
//
//		case menu_help:
//			Intent helpIntent = new Intent(MainActivity.this,
//					ShowHelpActivity.class);
//			startActivity(helpIntent);
//			break;
//
//		case menu_detail:
//			Bitmap bitmap = scaleImage();
//			if (bitmap != null) {
//				BitmapDrawable bDrawable = new BitmapDrawable(
//						mContext.getResources(), bitmap);
//				new AlertDialog.Builder(mContext)
//						.setTitle(setStringID(R.string.aboutAuthorTitle))
//						.setMessage(setStringID(R.string.aboutAuthor))
//						.setIcon(bDrawable).create().show();
//			} else {
//				new AlertDialog.Builder(mContext)
//						.setTitle(setStringID(R.string.aboutAuthorTitle))
//						.setMessage(setStringID(R.string.aboutAuthor)).create()
//						.show();
//			}
//			break;
//
//		case menu_share:
//			if (NetworkUtil.isWifiConnected(mContext)) {
//				showShareDialog();
//			} else {
//				new AlertDialog.Builder(mContext)
//						.setMessage(setStringID(R.string.wifi_message))
//						.setPositiveButton(setStringID(R.string.Continue),
//								new DialogInterface.OnClickListener() {
//
//									@Override
//									public void onClick(DialogInterface arg0,
//											int arg1) {
//										showShareDialog();
//										arg0.dismiss();
//									}
//								})
//						.setNegativeButton(setStringID(R.string.cancel),
//								new DialogInterface.OnClickListener() {
//
//									@Override
//									public void onClick(DialogInterface arg0,
//											int arg1) {
//										arg0.dismiss();
//
//									}
//								}).setCancelable(false).show();
//
//			}
//			break;
//
//		default:
//			break;
//		}
//
//		return true;
//	}

//	private void showShareDialog() {
//		new AlertDialog.Builder(mContext)
//				.setTitle(setStringID(R.string.share))
//				.setItems(
//						new String[] { setStringID(R.string.share_everything),
//								setStringID(R.string.share_myApp) },
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface arg0,
//									int position) {
//								switch (position) {
//								case 0:
//									Intent getContent = new Intent(
//											Intent.ACTION_GET_CONTENT);
//									getContent.setType("*/*");
//									startActivityForResult(getContent,
//											FILE_REQUEST_CODE);
//									break;
//
//								case 1:
//									PackageManager pm = getPackageManager();
//									ApplicationInfo aInfo = null;
//									try {
//										aInfo = pm.getApplicationInfo(
//												getPackageName(), 0);
//									} catch (NameNotFoundException e) {
//
//										e.printStackTrace();
//									}
//									if (aInfo != null) {
//										String apkArchive = aInfo.sourceDir;
//
//										Intent shareMyAppIntent = new Intent(
//												Intent.ACTION_SEND);
//										shareMyAppIntent.setType("*/*");// text/plain
//										shareMyAppIntent
//												.putExtra(
//														Intent.EXTRA_SUBJECT,
//														getString(R.string.extra_subject));
//										shareMyAppIntent.putExtra(
//												Intent.EXTRA_TEXT,
//												getString(R.string.extra_text));
//
//										File file = new File(apkArchive);
//										Uri uri = Uri.fromFile(file);
//										shareMyAppIntent.putExtra(
//												Intent.EXTRA_STREAM, uri);
//										shareMyAppIntent
//												.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//										startActivity(Intent.createChooser(
//												shareMyAppIntent, getTitle()));
//									} else
//										Toast.makeText(mContext, "error",
//												Toast.LENGTH_LONG).show();
//									break;
//								default:
//									break;
//								}
//
//							}
//						}).show();
//	}

	public void setNotification() {
		Intent intent = new Intent(MainActivity.this, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent,
				0);
		Notification notification = new Notification.Builder(mContext)
				.setAutoCancel(true)
				.setTicker(setStringID(R.string.notification_ticker))
				.setSmallIcon(R.drawable.voice)
				.setContentTitle(setStringID(R.string.local_app_name))
				.setContentText(setStringID(R.string.notification_text))
				.setDefaults(Notification.DEFAULT_LIGHTS)
				.setWhen(System.currentTimeMillis()).setContentIntent(pIntent)
				.build();
		nManager.notify(NOTIFICATION_ID, notification);

	}

	public void cancelNotification() {
		nManager.cancel(NOTIFICATION_ID);
	}

	public Bitmap scaleImage() {
		Bitmap photo = null;
		try {
			Options options = new Options();
			// options.inJustDecodeBounds =
			// true使得创建Bitmap时不会申请内存，但是可以得到Bitmap的宽和高。
			options.inJustDecodeBounds = true;
			photo = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.image1, options);
			int size = Math.max(options.outWidth, options.outHeight);
			int imageSize = sampleSize(size, 600);
			options.inSampleSize = imageSize;
			options.inDither = false;
			options.inPurgeable = true;
			options.inInputShareable = true;
			options.inTempStorage = new byte[1024 * 1024];
			// 注意要设置回false，并重新创建Bitmap。
			options.inJustDecodeBounds = false;
			photo = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.image1, options);
			int degree = 0;
			int width = photo.getWidth(); // 图片宽度
			int height = photo.getHeight(); // 图片高度
			float scale = 0;
			if (width > height) {
				scale = (float) (width / 90.0);
			} else {
				scale = (float) (height / 90.0);
			}
			photo = ImageUtil.scaleToFit(photo,
					dip2px(mContext, width / scale),
					dip2px(mContext, height / scale), degree);

		} catch (Exception e) {

			e.printStackTrace();
		}
		return photo;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.v(TAG, "onConfigurationChanged");
	}

	@Override
	public void onViewChange(Message msg) {
		// TODO Auto-generated method stub
		
	}


	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class BaiduMapSDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			Log.e(getTAG(), "baidu map receiver action: " + s);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				Log.e(getTAG(),"百度地图key验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
			} else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
				Log.e(getTAG(),"百度地图key验证成功! 功能可以正常使用");
				isMapKeyCorrect = true;
				BaiduMapUtil.startLocation(mContext,null,false);
			}
			else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Log.e(getTAG(),"百度地图提示：网络出错");
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
			/**
			 * 扫一扫
			 */
			if (v == menuItems.get(0)) {
               startActivity(new Intent(mContext,QRCodeCaptureActivity.class));
			}
			/**
			 * 设置
			 */
			if (v == menuItems.get(1)) {
				Intent intent = new Intent(MainActivity.this,SettingActivity.class);
				startActivity(intent);
			}
			/**
			 * 删除对话记录
			 */
			if (v == menuItems.get(2)) {
				new AlertDialog.Builder(mContext)
						.setTitle(setStringID(R.string.deleteChat) + "?")
						.setPositiveButton(setStringID(R.string.sure),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										mDataArrays.clear();
										mAdapter.setmDataArrays(mDataArrays);
										mAdapter.notifyDataSetChanged();
										Editor editor = sharePreference_chat.edit();
										editor.clear();
										editor.commit();
										chatPosition = 0;
									}
								})
						.setNegativeButton(setStringID(R.string.cancel),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {

									}
								}).setIcon(R.drawable.comment_create_over_del_normal)
						.setCancelable(false).create().show();

			}
			/**
			 * 设置头像
			 */
			if (v == menuItems.get(3)) {
				if (CommonUtil.hasSdcard()) {
					showHeadPortraitSelectDialog();
				} else
					Toast.makeText(mContext, setStringID(R.string.checkSDcard),
							Toast.LENGTH_LONG).show();
			}
			/**
			 * 查看应用
			 */
			if (v == menuItems.get(4)) {
				Intent intent = new Intent();
				intent.setClass(mContext, ListAppsActivity.class);
				startActivity(intent);
			}
			
			
			switch (v.getId()) {
			case R.id.imageBtn_main_speech:
				startSpeechUnderstand();
				break;

			default:
				break;
			}

		}
}
