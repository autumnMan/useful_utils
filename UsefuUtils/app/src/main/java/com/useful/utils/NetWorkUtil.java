package com.useful.utils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;


public class NetWorkUtil {
	/** 未知网络 */
	public static final int NETWORK_TYPE_UNKNOWN = 0;
	/** 2G网络 */
	public static final int NETWORK_TYPE_2G = 1;
	/** 3G网络 */
	public static final int NETWORK_TYPE_3G = 2;
	/** 4G网络 */
	public static final int NETWORK_TYPE_4G = 3;
	/** WIFI网络 */
	public static final int NETWORK_TYPE_WIFI = 4;

	public static final int[] NETWORK_2G_DEFINITION = new int[] {
			TelephonyManager.NETWORK_TYPE_GPRS,
			TelephonyManager.NETWORK_TYPE_EDGE,
			TelephonyManager.NETWORK_TYPE_CDMA };
	public static final int[] NETWORK_3G_DEFINITION = new int[] {
			TelephonyManager.NETWORK_TYPE_UMTS,
			TelephonyManager.NETWORK_TYPE_HSDPA,
			TelephonyManager.NETWORK_TYPE_HSUPA,
			TelephonyManager.NETWORK_TYPE_HSPA,
			TelephonyManager.NETWORK_TYPE_EVDO_0,
			TelephonyManager.NETWORK_TYPE_EVDO_A,
			TelephonyManager.NETWORK_TYPE_1xRTT,
			11,// TelephonyManager.NETWORK_TYPE_IDEN
			12,// TelephonyManager.NETWORK_TYPE_EVDO_B
			14,// TelephonyManager.NETWORK_TYPE_EHRPD
			15 // TelephonyManager.NETWORK_TYPE_HSPAP
	};
	public static final int[] NETWORK_4G_DEFINITION = new int[] {
			13 // TelephonyManager.NETWORK_TYPE_LTE
	};

	/**
	 * 检查当前网络状态
	 * 
	 * @param context
	 *            上下文
	 * @return 可用 true 不可用 false
	 **/
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connMgr == null) {
			return false;
		} else {
			NetworkInfo[] info = connMgr.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @param context
	 *            上下文
	 * @return WIFI或CDMA1X或EVDO或EVDO或MOBILE
	 */
	public static String getConnNetworkType(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info == null || !info.isConnected()) {
			return "";
		}
		final int NETWORK_TYPE_EVDO_B = 12; // Level 9
		int type = info.getType();
		int subType = info.getSubtype();
		if (type == ConnectivityManager.TYPE_WIFI) {
			return "WIFI";
		} else if (type == ConnectivityManager.TYPE_MOBILE) {
			switch (subType) {
			case TelephonyManager.NETWORK_TYPE_CDMA:
				return "CDMA1X";
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
			case NETWORK_TYPE_EVDO_B:
				return "EVDO";
			case TelephonyManager.NETWORK_TYPE_GPRS:
				return "GPRS";
			}
		}
		return "MOBILE";
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @param context
	 *            上下文
	 * @return 网络类型 2G/3G/4G/WIFI/UNKNOWN
	 * @see #NETWORK_TYPE_UNKNOWN
	 * @see #NETWORK_TYPE_2G
	 * @see #NETWORK_TYPE_3G
	 * @see #NETWORK_TYPE_4G
	 * @see #NETWORK_TYPE_WIFI
	 */
	public static int getConnNetworkScope(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info == null || !info.isConnected()) {
			return NETWORK_TYPE_UNKNOWN;
		}
		int type = info.getType();
		int subType = info.getSubtype();
		if (type == ConnectivityManager.TYPE_WIFI) {
			return NETWORK_TYPE_WIFI;
		} else if (type == ConnectivityManager.TYPE_MOBILE) {
			return belongToNetworkType(subType);
		}
		return NETWORK_TYPE_UNKNOWN;
	}

	/**
	 * 获取当前ip
	 *
	 *            上下文
	 * @return ip String
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						if (inetAddress instanceof Inet6Address) {
						} else if(inetAddress instanceof Inet4Address){
							return inetAddress.getHostAddress().toString();
						} else {
						}
					}
				}
			}
		} catch (Exception ex) {
		}
		return null;
	}
	private static int belongToNetworkType(int subType) {
		if (doFilter(subType, NETWORK_2G_DEFINITION)) {
			return NETWORK_TYPE_2G;
		} else if (doFilter(subType, NETWORK_3G_DEFINITION)) {
			return NETWORK_TYPE_3G;
		} else if (doFilter(subType, NETWORK_4G_DEFINITION)) {
			return NETWORK_TYPE_4G;
		} else {
			return NETWORK_TYPE_UNKNOWN;
		}
	}

	private static boolean doFilter(int key, int[] data) {
		for (int s : data) {
			if (key == s) {
				return true;
			}
		}
		return false;
	}
}
