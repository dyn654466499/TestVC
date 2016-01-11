package com.dyn.activities;

import static com.dyn.consts.Constants.INSTALLED;
import static com.dyn.consts.Constants.LOAD_MORE_DATA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.dyn.adapters.ListAppsAdapter;
import com.dyn.utils.AutoLoadingUtils;
import com.dyn.utils.CommonUtil;
import com.dyn.utils.NetUtil;
import com.dyn.voicecontrol.R;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingLeftInAnimationAdapter;

public class ListAppsActivity extends BaseActivity{

	public static final String TAG = "ListAppsActivity";
	private ListAppsAdapter adapter;
	private Context mContext;
	ArrayList<HashMap<String, Object>> items;
	private int mPosition = 0;
	private PackageManager pm;
	public static MyHandler mHandler;
	public static final int START = 0;
	public static final int UNINSTALL = 1;
	public static final int SEARCH = 2;
	private View moreView;
	private int lastItem = 0;
    private int count = 0;
    
    private ImageButton imageBtn_listapps_back;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_apps);
		mContext = this;
		
		LinearLayout linearLayout_listApps = (LinearLayout)findViewById(R.id.linearLayout_listApps);
		AutoLoadingUtils.setAutoLoadingView(linearLayout_listApps);
		
		
		new AsyncTask<Void, Integer, ArrayList<HashMap<String, Object>>>(){

			@Override
			protected ArrayList<HashMap<String, Object>> doInBackground(
					Void... params) {
				// TODO Auto-generated method stub
				items = new ArrayList<HashMap<String, Object>>();
				// 得到PackageManager对象
				pm = getPackageManager();
				Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
				mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				// 通过查询，获得所有ResolveInfo对象.
				List<ResolveInfo> resolveInfos = pm
						.queryIntentActivities(mainIntent, 0);
				// 调用系统排序 ， 根据name排序
				// 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
				Collections.sort(resolveInfos,
						new ResolveInfo.DisplayNameComparator(pm));
				for (ResolveInfo reInfo : resolveInfos) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					// 获得该应用程序的包名
					String pkgName = reInfo.activityInfo.packageName;
					// 获得Activity的label名（注意：不是Application的label名）
					String labelName = (String) reInfo.activityInfo.loadLabel(pm);
					if (labelName.contains(" "))
						labelName = labelName.replace(" ", "");
					// 这将会显示所有安装的应用程序，包括系统应用程序
					map.put("icon", reInfo.activityInfo.loadIcon(pm));// 图标
					map.put("appName", labelName);// 应用程序名称
					map.put("version", "版本"+CommonUtil.getVersionName(mContext,pkgName));// 当前版本号
					map.put("packageName", getString(R.string.packageName) + ":"
							+ pkgName);// 应用程序包名
					// 循环读取并存到HashMap中，再增加到ArrayList上，一个HashMap就是一项
					items.add(map);
				}
				return items;
			}

			@Override
			protected void onPostExecute(ArrayList<HashMap<String, Object>> result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				mHandler = new MyHandler(ListAppsActivity.this);
				AutoLoadingUtils.cancelAutoLoadingView();
				
				imageBtn_listapps_back = (ImageButton)findViewById(R.id.imageBtn_listapps_back);
				imageBtn_listapps_back.setOnClickListener(ListAppsActivity.this);
				/**
				 * 参数：Context ArrayList(item的集合) item的layout 包含ArrayList中的HashMap的key的数组
				 * key所对应的值的相应的控件id
				 */
				adapter = new ListAppsAdapter(mContext, items, R.layout.item_list_app,
						new String[] { "icon", "appName", "version","packageName" }, new int[] {
								R.id.icon, R.id.listapp_item_appName, R.id.listapp_item_version,R.id.listapp_item_packageName });
				ListView listApps = (ListView)findViewById(R.id.listView_listApps);
				
				SwingLeftInAnimationAdapter alarmListswingLeftIn = new SwingLeftInAnimationAdapter(
						adapter);
				alarmListswingLeftIn.setListView(listApps);
				listApps.setAdapter(alarmListswingLeftIn);
				//setListAdapter(adapter);
				listApps.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> adapterView,
							View view, int position, long arg3) {
						mPosition = position;
						ListView listview = (ListView) getLayoutInflater().inflate(
								R.layout.listview_apps_operation, null);
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(
								mContext, android.R.layout.simple_list_item_1,
								new String[] { getString(R.string.startApp),
										getString(R.string.uninstallApp),
										getString(R.string.searchApp) });
						listview.setAdapter(adapter);

						final AlertDialog alert =new AlertDialog.Builder(mContext)
								.setTitle(getString(R.string.WhatDoYouDo)).setView(listview).create();
						alert.show();
						
						listview.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
								String packageName = (String) items.get(mPosition).get(
										"packageName");
								packageName = packageName.replace(
										getString(R.string.packageName) + ":", "");
								switch (position) {
								// 启动应用
								case START:
									try {
										Intent startIntent = new Intent(
												Intent.ACTION_MAIN);
										startIntent = pm
												.getLaunchIntentForPackage(packageName);
										startIntent
												.addCategory(Intent.CATEGORY_LAUNCHER);
										startActivity(startIntent);
									} catch (Exception e) {
										e.printStackTrace();
									}
									alert.dismiss();
									break;
								// 卸载应用
								case UNINSTALL:
									try {
										Intent uninstallIntent = new Intent();
										Uri uri = Uri.fromParts("package", packageName,
												null);
										uninstallIntent.setAction(Intent.ACTION_DELETE);
										uninstallIntent.setData(uri); // 这句话最好加上
										uninstallIntent
												.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										startActivity(uninstallIntent);
									} catch (Exception e) {
										e.printStackTrace();
									}
									alert.dismiss();
									break;
								// 搜索应用
								case SEARCH:
									boolean isConnectNetwork = NetUtil.isConnectedToNet(mContext);
									if (isConnectNetwork) {
										String appName = (String) items.get(mPosition)
												.get("appName");
										appName = appName.replace(
												getString(R.string.appName) + ":", "");
										try {
											Intent searchIntent = new Intent();
											searchIntent
													.setAction(Intent.ACTION_WEB_SEARCH);
											searchIntent
													.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
											searchIntent.putExtra(SearchManager.QUERY,
													appName);
											if (searchIntent
													.resolveActivity(getPackageManager()) != null) {
												startActivity(searchIntent);
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
										alert.dismiss();
									} else {
										new AlertDialog.Builder(mContext)
												.setMessage(
														getString(R.string.NetworkConnectionException))
												.create().show();
									}
									break;

								default:
									break;
								}

							}
						});
					}
				});
			}
		}.execute();
		
	}


	 
	static class MyHandler extends Handler {

		ListAppsActivity lActivity;

		public MyHandler(ListAppsActivity lActivity) {
			super();
			this.lActivity = lActivity;
		}

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			switch (msg.what) {
			case INSTALLED:
				// String installedPackage = (String)msg.obj;
				if (lActivity.items != null) {
					lActivity.items.remove(lActivity.mPosition);
					lActivity.adapter.setAppData(lActivity.items);
					lActivity.adapter.notifyDataSetChanged();
				}
				break;

			default:
				break;
			}
		}

	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageBtn_listapps_back:
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
