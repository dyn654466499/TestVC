package com.dyn.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify.TransformFilter;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Toast;

import com.dyn.voicecontrol.R;

/**
 * 公共的工具类
 * @author 邓耀宁
 *
 */
public class CommonUtil {
	/** 
	 * 验证手机格式 
	 * @return 如果符合手机格式，返回true；否则返回false
	 */  
	public static boolean isPhoneNum(String phoneNumber) {  
	    /* 
	    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188 
	    联通：130、131、132、152、155、156、185、186 
	    电信：133、153、180、189、（1349卫通） 
	    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9 
	    */  
	    String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。  
	    if (TextUtils.isEmpty(phoneNumber)) return false;  
	    else return phoneNumber.matches(telRegex);  
	   } 
	
	/**
	 * 自定义middle()方法：取出语义理解返回的文本结果中的特定结果
	 * 
	 * @param input
	 *            （字符串结果）,index（ 截取开始的字符串位置，以1开始）,endPos（截取结束的字符串位置）
	 */
	public static String middle(String input, int index, int count) {
		if (input.isEmpty()) {
			return "";
		}
		count = (count > input.length() - index + 1) ? input.length()
				- index + 1 : count;
		return input.substring(index - 1, index + count - 1);
	}

	/**
	 * 判断是否text中是否含有小写字母
	 * @param text
	 * @return
	 */
	public static boolean isHaveLowerCase(String text) {
		return Pattern.compile("(?i)[a-z]").matcher(text).find();
	}
	
	/**
	 * 设置字母为大写
	 * @param text
	 * @return
	 */
	public static String setUpperCase(String text) {
		if (isHaveLowerCase(text)) {
			
			text = text.toUpperCase(Locale.getDefault());
		}
		return text;
	}

	/**
	 * 格式化时间
	 * @param time
	 * @return
	 */
	public static String getFormatDuration(String time){
		  String dur = null;
		  if(time!=null)
		  {		
			double time_=Double.parseDouble(time);
			int second=(int)time_%60;	
			int minute=(int)time_/60%60;
			int hour=(int)time_/3600;	
			String s= (second<10)?"0"+String.valueOf(second):String.valueOf(second);
			String m=(minute<10)?"0"+String.valueOf(minute):String.valueOf(minute);
			String h=(hour<10)?"0"+String.valueOf(hour):String.valueOf(hour);
			dur=h+":"+m+":"+s;										
		  }
		    return dur;
	}
	
	/**
	 * 判断手机是否连接网络
	 * @param context
	 * @return
	 */
    public static boolean isConnectingToInternet(Context context){
        ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null)
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null)
                  for (int i = 0; i < info.length; i++)
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
 
          }
          return false;
    }
    
    /** 
     * 格式化字节数
     * @param size 
     * @return 格式化后的数据
     */  
    public static String getFormatTrafficSize(long size) {  
    	long kiloByte = size / 1024;  
        if (kiloByte < 1) {  
        	return "没有使用流量";
        }  
  
        long megaByte = kiloByte / 1024;  
        if (megaByte < 1) {  
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));  
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)  
                    .toPlainString() + "KB";  
        }  
  
        long gigaByte = megaByte / 1024;  
        if (gigaByte < 1) {  
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));  
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)  
                    .toPlainString() + "MB";  
        }  
  
        long teraBytes = gigaByte / 1024;  
        if (teraBytes < 1) {  
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));  
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)  
                    .toPlainString() + "GB";  
        }  
        
        BigDecimal result4 = new BigDecimal(teraBytes);  
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()  
                + "TB";  
    }
    
    /**
     * 读取文件数据  
     * @param fileName
     * @return
     * @throws IOException
     */
    public String readFileData(String fileName) throws IOException{   
      String res="";   
      try{   
    	     File file = new File(fileName);
             FileInputStream fis = new FileInputStream(file);   
             int length = fis.available();   
             byte [] buffer = new byte[length];   
             fis.read(buffer);       
             res = EncodingUtils.getString(buffer, "UTF-8");   
             fis.close();       
         }   
         catch(Exception e){   
             e.printStackTrace();   
         }   
         return res;   
    }  
    
    /**
     * 格式化手机号码(135*****7710)
     * @param phoneNum
     * @return 
     */
    public static String formatPhoneNum(String phoneNum){
        String target = phoneNum.substring(3, 7);
        phoneNum = phoneNum.replace(target, "*****");
    	return phoneNum;
    }
    
    /**
     * 根据参数改变指定文本的部分字体大小，颜色。
     * @param text 需要处理的整体文本
     * @param start 指定文本的开始位置
     * @param end 指定文本的结束位置
     * @param color 字体颜色
     * @param size 字体大小
     * @return 处理后的文本
     */
    public static SpannableString setFontType(String text, int start, int end, int color, int size)
	  {
	    SpannableString spannableString = new SpannableString(text);
		spannableString.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		spannableString.setSpan(new AbsoluteSizeSpan(size,true), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
	    return spannableString;
	  }
    
    /**
	 * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
	 * 
	 * @return 应用程序是/否获取Root权限
	 */
	public static boolean upgradeRootPermission(String pkgCodePath) {
		Process process = null;
		DataOutputStream os = null;
		try {
			String cmd = "chmod 777 " + pkgCodePath;
			process = Runtime.getRuntime().exec("su"); // 切换到root帐号
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
		return true;
	}
	
	/**
	 * 格式化时间
	 * @param time
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String getFormatDate(long time){  
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
        Date date=new Date(time); 
        //SimpleDateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault()).format(date);
        return format.format(date);  
    }
	
	/**
	 * 格式化时间
	 * @param time
	 * @return HH:mm:ss
	 */
	public static String getFormatTime(long time){  
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
        Date date=new Date(time); 
        //SimpleDateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault()).format(date);
        return format.format(date);  
    }

	/**
	 * 格式化时间
	 * @param time
	 * @return HH小时mm分钟
	 */
	public static String getFormatTime(int time){ 
       String str_time = String.valueOf(time/3600)+"小时"+
    		             ((time/60)%60 < 10 ? "0"+String.valueOf((time/60)%60):String.valueOf((time/60)%60))+"分钟";
       return str_time;
    }
	
	/**
	 * 格式化时间
	 * @param time
	 * @return HH小时mm分钟
	 */
	public static String getFormatDistance(int distance){
		DecimalFormat decimalFormat=new DecimalFormat(".0");
		String str_distance = "";
		if(distance<1000){
			str_distance = "0"+decimalFormat.format((float)distance/1000f) + "公里";
		}else{
			str_distance = decimalFormat.format((float)distance/1000f) + "公里";
		}
        return str_distance;
    }
	
	/**
	 * 判断是否有SD卡
	 * @return
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 取本地的apk进行发送
	 * @param context
	 */
	public static void sendAPKFromLocal(Context context) {
		PackageManager pm = context.getPackageManager();
		ApplicationInfo aInfo = null;
		try {
			aInfo = pm.getApplicationInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(context, "发送apk出错", Toast.LENGTH_LONG).show();
			return;
		}
		String apkArchive = aInfo.sourceDir;
		Intent shareMyAppIntent = new Intent(Intent.ACTION_SEND);
		shareMyAppIntent.setType("*/*");// text/plain
		shareMyAppIntent.putExtra(Intent.EXTRA_SUBJECT,
				context.getString(R.string.extra_subject));
		shareMyAppIntent.putExtra(Intent.EXTRA_TEXT,
				context.getString(R.string.extra_text));

		File file = new File(apkArchive);
		Uri uri = Uri.fromFile(file);
		shareMyAppIntent.putExtra(Intent.EXTRA_STREAM, uri);
		shareMyAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(Intent.createChooser(shareMyAppIntent, context.getString(R.string.local_app_name)));
	}
	
	public static float dipToPixels(Context context, float dipValue) {
	    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
	    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
	}
	
	/**
	 * 获取版本名
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
	    return getPackageInfo(context).versionName;
	}
	 
	/**
	 * 获取版本号
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
	    return getPackageInfo(context).versionCode;
	}
	 
	private static PackageInfo getPackageInfo(Context context) {
	    PackageInfo pi = null;
	 
	    try {
	        PackageManager pm = context.getPackageManager();
	        pi = pm.getPackageInfo(context.getPackageName(),
	                PackageManager.GET_CONFIGURATIONS);
	 
	        return pi;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	 
	    return pi;
	}
	
	/**
	 * 获取版本号
	 * @param context
	 * @param packageName
	 * @return 
	 */
	public static String getVersionName(Context context,String packageName) {
		PackageInfo pi = null;
	    try {
	        PackageManager pm = context.getPackageManager();
	        pi = pm.getPackageInfo(packageName,PackageManager.GET_CONFIGURATIONS);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return pi.versionName;
	}
	
	/**
	 * 去除特殊字符或将所有中文标号替换为英文标号
	 * @param str
	 * @return
	 */
	public static String stringFilter(String str) {
		str = str.replaceAll("【", "[").replaceAll("】", "]")
				.replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
		String regEx = "[『』]"; // 清除掉特殊字符
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	/***
	 * 半角转换为全角
	 * 
	 * @param input
	 * @return
	 */
	public static String ToSBC(String input) {
		if (!TextUtils.isEmpty(input)) {
			char c[] = input.toCharArray();
			for (int i = 0; i < c.length; i++) {
				if (c[i] == ' ') {
					c[i] = '\u3000';
				} else if (c[i] < '\177') {
					c[i] = (char) (c[i] + 65248);
				}
			}
			return new String(c);
		}
		return "";
	}
	
	public static final String makeUrl(String url, String[] prefixes,
            Matcher m, TransformFilter filter) {
        if (filter != null) {
            url = filter.transformUrl(m, url);
        }

        boolean hasPrefix = false;
        
        for (int i = 0; i < prefixes.length; i++) {
            if (url.regionMatches(true, 0, prefixes[i], 0,
                                  prefixes[i].length())) {
                hasPrefix = true;

                // Fix capitalization if necessary
                if (!url.regionMatches(false, 0, prefixes[i], 0,
                                       prefixes[i].length())) {
                    url = prefixes[i] + url.substring(prefixes[i].length());
                }

                break;
            }
        }

        if (!hasPrefix) {
            url = prefixes[0] + url;
        }

        return url;
    }
	
    /** 
     * 密度转换像素 
     **/
    public int Dp2Px(Context context, float dp) { 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int) (dp * scale + 0.5f); 
    } 
    
    /** 
     * 像素转换密度 
     **/ 
    public int Px2Dp(Context context, float px) { 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int) (px / scale + 0.5f); 
    } 
}
