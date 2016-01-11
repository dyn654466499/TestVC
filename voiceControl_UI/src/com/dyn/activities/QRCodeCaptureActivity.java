package com.dyn.activities;

import static com.dyn.consts.Constants.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.util.Linkify.MatchFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dyn.utils.CommonUtil;
import com.dyn.utils.ImageUtil;
import com.dyn.utils.QQUtil;
import com.dyn.voicecontrol.R;
import com.dyn.zxing.LuminanceSource.RGBLuminanceSource;
import com.dyn.zxing.camera.CameraManager;
import com.dyn.zxing.control.AmbientLightManager;
import com.dyn.zxing.control.BeepManager;
import com.dyn.zxing.decode.CaptureActivityHandler;
import com.dyn.zxing.decode.FinishListener;
import com.dyn.zxing.decode.InactivityTimer;
import com.dyn.zxing.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

/**
 * 二维码扫描界面
 * @author 邓耀宁
 */
@SuppressLint("NewApi") 
public final class QRCodeCaptureActivity extends BaseActivity implements SurfaceHolder.Callback {
	
	private ImageButton ImageBtn_capture_back;
	private Button button_capture_turnFlash;
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private Result savedResultToShow;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Collection<BarcodeFormat> decodeFormats;
	private Map<DecodeHintType, ?> decodeHints;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private BeepManager beepManager;
	private AmbientLightManager ambientLightManager;
	
	private boolean isCameraOpen = false;
	
	private String url = "";
	public static final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
	
	private static final int PICK_QRCODE_REQUEST_CODE = 0x0; 
	
	private Context mContext;
	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		mContext = this;
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_qrcode_capture);

		ImageBtn_capture_back = (ImageButton) findViewById(R.id.ImageBtn_capture_back);
		ImageBtn_capture_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				QRCodeCaptureActivity.this.finish();
			}
		});
		button_capture_turnFlash = (Button) findViewById(R.id.button_capture_turnFlash);
		button_capture_turnFlash.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(cameraManager.flashHandler()){
					button_capture_turnFlash.setText("关闭闪光灯");
				}else{
					button_capture_turnFlash.setText("开启闪光灯");
				}
			}
		});

		final ImageButton imageBtn_capture_moreMenu = (ImageButton)findViewById(R.id.imageBtn_capture_moreMenu);
		Bitmap bm = ImageUtil.rotateBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_nav_more_white), 90);
		imageBtn_capture_moreMenu.setImageBitmap(bm);
		imageBtn_capture_moreMenu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PopupWindow popupWindow = createMenuPopupWindow();
				RelativeLayout relativeLayout_capture_title = (RelativeLayout)findViewById(R.id.relativeLayout_capture_title);
				popupWindow.showAsDropDown(relativeLayout_capture_title, 9999, 0);
			}
		});
		
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);
		ambientLightManager = new AmbientLightManager(this);
	}

	/**
	 * 生成菜单popupwindow
	 * @return PopupWindow实例
	 */
	private PopupWindow createMenuPopupWindow(){
		final ListView listView = new ListView(mContext);
	    listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		listView.setBackgroundColor(getResources().getColor(R.color.blue));
		listView.setAlpha(0.8f);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
        
		final PopupWindow popupWindow = new PopupWindow(listView,mScreenWidth/2,LayoutParams.WRAP_CONTENT, false);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.item_textview, new String[]{"将扫一扫添加到桌面","从相册中选取二维码"});
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					addShortcut("扫一扫");
					showTip("添加成功");
					break;
                case 1:
                	 Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); //"android.intent.action.GET_CONTENT"  
                     innerIntent.setType("image/*");  
                     Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");  
                     startActivityForResult(wrapperIntent, PICK_QRCODE_REQUEST_CODE); 
					break;
				default:
					break;
				}
				/**
				 * 延迟popupwindow消失
				 */
				listView.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						popupWindow.dismiss();
					}
				}, 200);
			}
		});
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		/**
		 * 必须设置drawable
		 */
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
//		popupWindow.setTouchInterceptor(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				/**
//				 * 获取listView的矩形区域，判断点击是否在该区域内，若不在，则dismiss。
//				 */
//				Rect rect = new Rect(0, 0, listView.getWidth(),listView.getHeight());
//				if (!rect.contains((int) event.getX(), (int) event.getY())) {
//					popupWindow.dismiss();
//				}
//				return false;
//			}
//		});
		
		return popupWindow;
	}
	
	/**
	 * 添加快捷方式
	 * @param name
	 */
	private void addShortcut(String name) {
        Intent addShortcutIntent = new Intent(ACTION_ADD_SHORTCUT);
        // 不允许重复创建
        addShortcutIntent.putExtra("duplicate", false);// 经测试不是根据快捷方式的名字判断重复的
        // 应该是根据快链的Intent来判断是否重复的,即Intent.EXTRA_SHORTCUT_INTENT字段的value
        // 但是名称不同时，虽然有的手机系统会显示Toast提示重复，仍然会建立快链
        // 屏幕上没有空间时会提示
        // 注意：重复创建的行为MIUI和三星手机上不太一样，小米上似乎不能重复创建快捷方式

        // 名字
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);

        // 图标
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,Intent.ShortcutIconResource.fromContext(mContext,
                        R.drawable.icon_scan));
        
        ComponentName comp = new ComponentName(this, this.getClass().getName());
        Intent test =  new Intent(Intent.ACTION_MAIN);
        test.addCategory("com.dyn.category.captrue");
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, test.setComponent(comp));

        // 发送广播
        sendBroadcast(addShortcutIntent);
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		if(!isCameraOpen)resumeCamera();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(isCameraOpen)pauseCamera();
	}
	
	/**
	 * activity处于活动状态时，改变camera状态
	 */
	private void resumeCamera(){
		cameraManager = new CameraManager(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);

		handler = null;
		resetStatusView();

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		beepManager.updatePrefs();
		ambientLightManager.start(cameraManager);

		inactivityTimer.onResume();

		decodeFormats = null;
		characterSet = null;
		isCameraOpen = true;
    }
	
    /**
     * activity处于暂停状态时，改变camera状态 
     */
	private void pauseCamera(){
    	if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		ambientLightManager.stop();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		isCameraOpen = false;
    }

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		viewfinderView.recycleLineDrawable();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_CAMERA:// ���������
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
		if (handler == null) {
			savedResultToShow = result;
		} else {
			if (result != null) {
				savedResultToShow = result;
			}
			if (savedResultToShow != null) {
				Message message = Message.obtain(handler,
						R.id.decode_succeeded, savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	/**
	 * 处理解码结果
	 * @param rawResult
	 * @param barcode
	 * @param scaleFactor
	 */
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		inactivityTimer.onActivity();
		beepManager.playBeepSoundAndVibrate();

		String msg = rawResult.getText();
		if (TextUtils.isEmpty(msg)) {
			msg = APK_MOBILE_DOWNLOAD_URL;
		}
		showResultDialog(msg);
	}
	
	/**
	 * 根据字符串参数弹出相应的对话框内容
	 * @param result
	 */
	private void showResultDialog(String result){
		if(isCameraOpen)pauseCamera();
		Pattern pattern = Patterns.WEB_URL;
		Matcher m = pattern.matcher(result);
		MatchFilter matchFilter = new MatchFilter() {
	        public final boolean acceptMatch(CharSequence s, int start, int end) {
	            if (start == 0) {
	                return true;
	            }

	            if (s.charAt(start - 1) == '@') {
	                return false;
	            }

	            return true;
	        }
	    };
		 while (m.find()) {
	            int start = m.start();
	            int end = m.end();
	            if (matchFilter == null || matchFilter.acceptMatch(result, start, end)) {
	                url = CommonUtil.makeUrl(m.group(0), new String[] { "http://", "https://", "rtsp://" }, m, null);
	            }
	        }
		final boolean isURLEmpty = TextUtils.isEmpty(url);
		if(!isURLEmpty){
			result = url;
		}
		new AlertDialog.Builder(this).setTitle("扫描结果").setMessage(result).setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				if(!isCameraOpen)resumeCamera();
				url = "";
			}
		}).setPositiveButton("打开网址", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(!isURLEmpty){
					dialog.dismiss();
//					Intent intent = new Intent();  
//					intent.setAction("android.intent.action.VIEW");    
//					intent.setData(Uri.parse(url)); 
//					startActivity(intent);
					
					Intent intent = new Intent(QRCodeCaptureActivity.this,WebActivity.class);     
					intent.putExtra(WEB_URL, url); 
					startActivity(intent);
					
				}else{
					showTip("网址不符合规范！");
				}
			}
		}).setNegativeButton("复制", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
					if (android.os.Build.VERSION.SDK_INT > 11) {
						android.content.ClipboardManager c = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
						c.setPrimaryClip(ClipData.newPlainText("VoiceControl",
								APK_MOBILE_DOWNLOAD_URL));
					} else {
						android.text.ClipboardManager c = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
						c.setText(APK_MOBILE_DOWNLOAD_URL);
					}
					Toast.makeText(QRCodeCaptureActivity.this, "复制成功",Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					finish();
			}
		}).setCancelable(true).create().show();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			return;
		}
		if (cameraManager.isOpen()) {
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats,
						decodeHints, characterSet, cameraManager);
			}
			decodeOrStoreSavedBitmap(null, null);
		} catch (IOException ioe) {
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			displayFrameworkBugMessageAndExit();
		}
	}

	/**
	 * 报错
	 */
	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("未知错误");
		builder.setMessage("可能由于闪光灯正处于开启状态，请关闭后再重新开启扫描功能！");
		builder.setPositiveButton("结束", new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
		resetStatusView();
	}

	private void resetStatusView() {
		viewfinderView.setVisibility(View.VISIBLE);
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	private String photo_path = null;
	@Override 
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);  
        if(resultCode == RESULT_OK){  
            switch(requestCode){  
            case PICK_QRCODE_REQUEST_CODE:  
            	Cursor cursor = null;
            	try {
            		String[] proj = { MediaStore.Images.Media.DATA };  
                    // 获取选中图片的路径  
                    cursor = getContentResolver().query(data.getData(),  
                            proj, null, null, null);  
                    if (cursor.moveToFirst()) {  
                        int column_index = cursor  
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
                        photo_path = cursor.getString(column_index);  
                        if (photo_path == null) {  
                            photo_path = QQUtil.getPath(getApplicationContext(),  
                                    data.getData());  
                        }  
                    }  
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(cursor != null && !cursor.isClosed())cursor.close();
				}
  
				if (!TextUtils.isEmpty(photo_path)) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							Result result = scanningImage(photo_path);
							// String result = decode(photo_path);
							if (result == null) {
								Looper.prepare();
								Toast.makeText(getApplicationContext(),"图片格式有误", Toast.LENGTH_LONG).show();
								Looper.loop();
							} else {
								// 数据返回
								final String recode = recode(result.toString());
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										showResultDialog(recode);
									}
								});
								photo_path = null;
							}
						}
					}).start();
				}
				else showTip("图片路径为空"); 
                break;  
              
            }  
        }  
    } 
	
	/** 
	 * 扫描本地二维码图片的方法 
	 * @param path 
	 * @return 
	 */  
	public Result scanningImage(String path) {  
	    if(TextUtils.isEmpty(path)){  
	        return null;  
	    }  
	    Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();  
	    hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码  
	  
	    BitmapFactory.Options options = new BitmapFactory.Options();  
	    options.inJustDecodeBounds = true; // 先获取原大小  
	    Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);  
	    options.inJustDecodeBounds = false; // 获取新的大小  
	    int sampleSize = (int) (options.outHeight / (float) 200);  
	    if (sampleSize <= 0)  
	        sampleSize = 1;  
	    options.inSampleSize = sampleSize;  
	    scanBitmap = BitmapFactory.decodeFile(path, options);  
	    RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);  
	    BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));  
	    QRCodeReader reader = new QRCodeReader();  
	    try {  
	        return reader.decode(bitmap1, hints);  
	  
	    } catch (NotFoundException e) {  
	        e.printStackTrace();  
	    } catch (ChecksumException e) {  
	        e.printStackTrace();  
	    } catch (FormatException e) {  
	        e.printStackTrace();  
	    }  
	    return null;  
	}  
    
	/**
	 * 处理字符串格式
	 * @param str
	 * @return
	 */
	private String recode(String str) {  
        String formart = "";  
        try {  
            boolean ISO = Charset.forName("ISO-8859-1").newEncoder()  
                    .canEncode(str);  
            if (ISO) {  
                formart = new String(str.getBytes("ISO-8859-1"), "GB2312");  
                Log.i("ISO8859-1", formart);  
            } else {  
                formart = str;  
                Log.i("stringExtra", str);  
            }  
        } catch (UnsupportedEncodingException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        return formart;  
    }  
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onViewChange(Message msg) {
		// TODO Auto-generated method stub
		
	}
}
