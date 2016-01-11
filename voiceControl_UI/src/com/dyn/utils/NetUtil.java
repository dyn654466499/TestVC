package com.dyn.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * 网络工具类
 * @author 邓耀宁 （The module was passed by my testing.）
 */
@SuppressLint("NewApi")
public class NetUtil {
	public static String proxyHost = "10.16.240.155";
	public static String proxyPort = "80";
	private static int timeout = 5;
	public static String PHPSESSID = "";

	private final static String ping_passTag = "5 packets transmitted, 5 received, 0% packet loss";

	/**
	 * post访问并返回结果，此方法会阻塞线程。
	 * @param url
	 * @return the content which you post or null.
	 */
	public static String HttpPost(final String url,
			final HashMap<String, String> params_map) {
		/**
		 * 设置代理
		 */
		//System.setProperty("http.proxySet", "true");
		//System.setProperty("http.proxyHost", proxyHost);
		//System.setProperty("http.proxyPort", proxyPort);
		FutureTask<String> task = new FutureTask<String>(
				new Callable<String>() {
					@Override
					public String call() throws IOException,
							ClientProtocolException, Exception {
						HttpClient httpClient = new DefaultHttpClient();
						HttpPost post = new HttpPost(url);
						post.addHeader("Content-type",
								"application/x-www-form-urlencoded");
						post.addHeader("Connection", "close");

						if (params_map != null && params_map.size() > 0) {
							List<NameValuePair> params = new ArrayList<NameValuePair>();
                            //写法上比KeySet复杂一点，但是效率却高一倍
							Iterator<Entry<String, String>> iterator = params_map
									.entrySet().iterator();
							while (iterator.hasNext()) {
								Entry<String, String> entry = iterator.next();
								params.add(new BasicNameValuePair(entry
										.getKey(), entry.getValue()));
							}
							post.setEntity(new UrlEncodedFormEntity(params,
									HTTP.UTF_8));
						}

						if ("" != PHPSESSID) {
							post.setHeader("Cookie", "PHPSESSID=" + PHPSESSID);
						}

						HttpResponse response = httpClient.execute(post);

						if (response.getStatusLine().getStatusCode() == 200) {
							List<Cookie> cookies = ((AbstractHttpClient) httpClient)
									.getCookieStore().getCookies();
							setPHPSESSID(cookies);
							HttpEntity entity = response.getEntity();
							String content = EntityUtils.toString(entity,
									"utf-8");
							return content;
						}
						for(Header head:response.getAllHeaders()){
							Log.e("response header", head.getName()+":"+head.getValue()+"\n");
						}
						Log.e("response code", ""+response.getStatusLine().getStatusCode());
						return "Http connect time out";
					}

				});
		new Thread(task).start();

		try {
			// ���ó�ʱ
			return task.get(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * get访问，并返回网络访问的结果，此方法会阻塞线程。
	 * @param url
	 * @return the content which you get or null.
	 */
	public static String HttpGet(final String url,
			final HashMap<String, String> params_map) {
		/**
		 * 设置代理
		 */
		//System.setProperty("http.proxySet", "true");
		//System.setProperty("http.proxyHost", proxyHost);
		//System.setProperty("http.proxyPort", proxyPort);
		FutureTask<String> task = new FutureTask<String>(
				new Callable<String>() {

					@Override
					public String call() throws IOException,
							ClientProtocolException, Exception {

						HttpClient httpClient = new DefaultHttpClient();
						HttpGet get = null;

						if (params_map != null && params_map.size() > 0) {
							String params = "?";
							Iterator<Entry<String, String>> iterator = params_map
									.entrySet().iterator();
							while (iterator.hasNext()) {
								Entry<String, String> entry = iterator.next();
								params += entry.getKey() + "=";
								params += URLEncoder.encode(entry.getValue(),
										"utf-8");
								params += "&";
							}
							params = params.substring(0, params.length() - 1);
							get = new HttpGet(url + params);
						} else {
							get = new HttpGet(url);
						}
						get.addHeader("Content-type",
								"application/x-www-form-urlencoded");
						get.addHeader("Connection", "close");
						if ("" != PHPSESSID) {
							get.setHeader("Cookie", "PHPSESSID=" + PHPSESSID);
						}
						HttpResponse response = httpClient.execute(get);
						if (response.getStatusLine().getStatusCode() == 200) {
							List<Cookie> cookies = ((AbstractHttpClient) httpClient)
									.getCookieStore().getCookies();
							setPHPSESSID(cookies);

							HttpEntity entity = response.getEntity();
							String content = EntityUtils.toString(entity,
									"utf-8");
							return content;
						}
						return null;
					}

				});
		new Thread(task).start();
		try {
			return task.get(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * 判断是否能ping通某个IP地址。
	 * @param ip
	 * @return
	 */
	public static boolean ping(final String ip) {
		synchronized (NetUtil.class) {
			FutureTask<Boolean> task = new FutureTask<Boolean>(
					new Callable<Boolean>() {

						@Override
						public Boolean call() throws IOException, Exception {
							Process p = Runtime.getRuntime().exec(
									"ping -c 5 " + ip);
							InputStream inStream = p.getInputStream();
							if (inStream != null) {
								int code = -1;
								char ch = 0;
								StringBuffer buffer = new StringBuffer();
								while ((code = inStream.read()) != -1) {
									ch = (char) code;
									buffer.append(ch);
								}
								inStream.close();
								String resultinfo = buffer.toString();
								if (resultinfo.contains(ping_passTag)) {
									return true;
								}
							}
							return false;
						}

					});
			new Thread(task).start();

			try {
				return task.get(10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}

			return false;
		}
	}

	/**
	 * 设置网络访问超时时间。
	 * @param timeout
	 * 
	 *            You can set the time out example for 10;The range of time out
	 *            is 5 to 30 seconds;
	 */
	public static void setTimeout(int timeout) {
		if (timeout < 5) {
			timeout = 5;
		}
		if (timeout > 30) {
			timeout = 30;
		}
		NetUtil.timeout = timeout;
	}

	public static void setPHPSESSID(List<Cookie> cookies) {
		for (int i = 0; i < cookies.size(); i++) {
			// �����Ƕ�ȡCookie['PHPSESSID']��ֵ���ھ�̬�����У���֤ÿ�ζ���ͬһ��ֵ
			if ("PHPSESSID".equals(cookies.get(i).getName())) {
				PHPSESSID = cookies.get(i).getValue();
				Log.e("PHPSESSID", "PHPSESSID = " + PHPSESSID);
				break;
			}
		}
	}

	
	/**
     * 获取Android本机IP地址
     * @return
     */
	public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }

    /**
     * 获取Android本机MAC
     * @return MAC地址
     */
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    } 
    
    /**
     * 判断是否连接网络。
     * @param context
     * @return
     */
    public static boolean isConnectedToNet(Context context){
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
     * 判断wifi是否连接
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}
    
  /**
   * 将map型转为请求参数型
   * @param data
   * @return
   */
    public static String urlencode(Map<String, ?> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, ?> i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    
    
}
