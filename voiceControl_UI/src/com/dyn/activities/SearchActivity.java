package com.dyn.activities;

import static com.dyn.consts.Constants.SEARCH_KEY;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.dyn.adapters.SearchHistoryAdapter;
import com.dyn.customview.CustomEditText;
import com.dyn.interfaces.Commands;
import com.dyn.utils.DialogUtils;
import com.dyn.utils.SharedPreferenceUtils;
import com.dyn.voicecontrol.R;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) 
public class SearchActivity extends BaseActivity{
public static final int VOICE_RECOGNITION_REQUEST_CODE = 0;
	private CustomEditText editText_search_goods;
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search);
		
		editText_search_goods = (CustomEditText)findViewById(R.id.editText_search_goods);
//		editText_search_goods.setText(getIntent().getStringExtra("searchKey"));
//		/**
//		 * 将光标移到最后，方便修改文字内容。
//		 */
//		CharSequence charSequence = editText_search_goods.getText();
//		if (charSequence instanceof Spannable) {
//			Spannable spanText = (Spannable) charSequence;
//			Selection.setSelection(spanText, charSequence.length());
//		}
		/**
		 * 需要delay一下才能弹出输入法。
		 */
		editText_search_goods.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				InputMethodManager inputMethodManager = (InputMethodManager) mContext
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.showSoftInput(editText_search_goods, InputMethodManager.SHOW_FORCED);
			}
		}, 100);
		
		final Button button_search = (Button)findViewById(R.id.button_search);
		button_search.setOnClickListener(this);
		
		Button button_search_clearHistory = (Button)findViewById(R.id.button_search_clearHistory);
		
		/**
		 * ----------------------------------------    热门搜索列表           -------------------------------------------------
		 */
		GridView gridView = (GridView)findViewById(R.id.gridView_search_topSearch);
        //新建List
		final ArrayList<Map<String, Object>> data_list = new ArrayList<Map<String, Object>>();
		String[] keyNames = getResources().getStringArray(R.array.searchTop);
        //获取数据
        for(int i=0;i<8;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("text", keyNames[i]);
            data_list.add(map);
        }
        //新建适配器
        String [] from ={"text"};
        int [] to = {R.id.textView_search_topList};
        SimpleAdapter simpleAdapter = new SimpleAdapter(mContext, data_list, R.layout.item_goods_search_top, from, to);
        gridView.setAdapter(simpleAdapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				// TODO Auto-generated method stub
				editText_search_goods.setText((String) data_list.get(position).get("text"));
				button_search.callOnClick();
			}
		});
        
        /**
         * -------------------------------------------    搜索历史记录列表              --------------------------------------------
         */
        final ArrayList<String> searchKey = SharedPreferenceUtils.getSearchHistory(mContext);
        LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout_search_history);
		if(searchKey != null){
			ListView listView = (ListView)findViewById(R.id.listView_search_history);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position,
						long arg3) {
					// TODO Auto-generated method stub
					editText_search_goods.setText(searchKey.get(position));
					button_search.callOnClick();
				}
			});
			SearchHistoryAdapter adapter = new SearchHistoryAdapter(mContext, searchKey);
			listView.setAdapter(adapter);
			
			button_search_clearHistory.setOnClickListener(this);
		}else{
			layout.setVisibility(View.INVISIBLE);
		}
		
	}
	
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch (view.getId()) {
			case R.id.button_search:
				/**
				 * 将搜索关键字回调给主界面，并设置搜索历史记录
				 */
				Intent intent = new Intent();
				if(editText_search_goods.getText().length() == 0){
					Toast.makeText(mContext, "请输入您想说的话", Toast.LENGTH_SHORT).show();
				}else{
					String searchKey = editText_search_goods.getText().toString();
					intent.putExtra(SEARCH_KEY, searchKey); 
					SharedPreferenceUtils.setSearchHistory(mContext, searchKey);
					setResult(Activity.RESULT_OK, intent);
					finish();
					overridePendingTransition(0, R.anim.activity_down);
				}
				break;
				
			case R.id.button_search_clearHistory:
				DialogUtils.showDialog(mContext, "", getString(R.string.isSureClearHistory), new Commands() {
					@Override
					public void executeCommand() {
						// TODO Auto-generated method stub
						/**
						 * 清除历史记录并隐藏
						 */
						SharedPreferenceUtils.clearSearchHistory(mContext);
						LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout_search_history);
						layout.setVisibility(View.INVISIBLE);
					}
				});
				break;
			default:
				break;
			}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == MainActivity.SEARCH_TEXT  
                && resultCode == Activity.RESULT_OK) { 
//			/**
//			 * 将输入结果回调给主界面，同时设置搜索历史记录
//			 */
//            String text= editText_search_goods.getText().toString();  
//            SharedPreferenceUtils.setSearchHistory(mContext, text);
//			Intent intent = new Intent();
//		    intent.putExtra("text", text); 
//			setResult(Activity.RESULT_OK, intent);
//			finish();
//			overridePendingTransition(0, R.anim.activity_down);
        }  
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
			overridePendingTransition(0, R.anim.activity_down);
			return true;
		}
		return false;
	}

	@Override
	public void onViewChange(Message msg) {
		// TODO Auto-generated method stub
		
	}
	
}
