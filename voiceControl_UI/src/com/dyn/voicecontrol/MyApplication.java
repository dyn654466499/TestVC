package com.dyn.voicecontrol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.spec.MGF1ParameterSpec;

import android.app.Application;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.dyn.utils.UnpackSoUtil;
import com.iflytek.cloud.SpeechUtility;
import com.library.decrawso.DecRawso;

public class MyApplication extends Application{
    private Handler mHandler;
    private static final int MSG_WRITE_ERROR = 100;
	@Override
	public void onCreate() {
		/**
		 * 将帮助文档写入本地存储
		 */
		String locale = this.getResources().getConfiguration().locale.toString();
		super.onCreate();
		
		InputStream inputstream = null;
		if (locale.contentEquals("zh_TW")) {
            inputstream = this.getResources().openRawResource(R.raw.chinese_traditional);
		} else {
			inputstream = this.getResources().openRawResource(R.raw.chinese_simplified);
		}
		final InputStream inputstream_copy = inputstream;
		final String path = getFilesDir().getPath()+"/help.html";
		new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				byte[] buffer = new byte[1024];
				int count = 0;
				FileOutputStream fos = null;
				try {
					File file = new File(path);
					try {
			              String command = "chmod 777 " + file.getAbsolutePath();
			              Log.i("zyl", "command = " + command);
			              Runtime runtime = Runtime.getRuntime();  
		                  runtime.exec(command);
			             } catch (IOException e) {
			              Log.i("zyl","chmod fail!!!!");
			              e.printStackTrace();
			             }
					fos = new FileOutputStream(file);
					while ((count = inputstream_copy.read(buffer)) > 0) {
						fos.write(buffer, 0, count);
					}
					fos.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					mHandler.obtainMessage(MSG_WRITE_ERROR).sendToTarget();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}.start();
		
	}
}
