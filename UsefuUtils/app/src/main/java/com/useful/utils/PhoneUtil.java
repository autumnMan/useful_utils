package com.useful.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


public class PhoneUtil {

	public final static String CHINA_TELECOM = "46003";
	public final static String CHINA_MOBILE = "46000";
	public final static String CHINA_MOBILE2 = "46002";
	public final static String CHINA_UNICOM = "46001";
    private final static String PRO_VERSION = "1.0.1";
	
	/**
	 * 获取sim卡运营商的代号
	 * 
	 * @param context  上下文
	 *           
	 * @return sim卡运营商的代号，取不到返回unknow
	 */
	public static String getSimOperatorName(Context context) {
		TelephonyManager tel = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (tel != null && !"".equals(tel.getSimOperator())) {
			return tel.getSimOperator();
		} else {
			return "unknow";
		}

	}
	/**
	 * 获取sim卡的imsi号
	 * 
	 * @param context  上下文
	 *           
	 * @return sim卡的imsi号
	 */
	public static String getImsi(Context context) {
		TelephonyManager tel = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = "";
		if (tel != null
				&& ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
			imsi = tel.getSubscriberId();
		}
		return imsi;
	}

	/**
	 * 获取设备唯一标示码
	 *
	 * @param context  上下文
	 *
	 * @return 设备的唯一标示
	 */
	public static String getIMEI(Context context) {
		TelephonyManager telephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = "";
		if (telephonyMgr != null
				&& ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
			imei = telephonyMgr.getDeviceId();
		}
		return imei;
	}
	
	/**
	 * 获取设备的型号
	 * @return 设备型号，获取不到则返回“我”
	 */
	public static String getModle() {
		String model = android.os.Build.MODEL;
		if (TextUtils.isEmpty(model)) {
			model = "我";
		}
		return model;
	}
	
	/**
	 * 获取sdk版本
	 * @return sdk版本代号  17、16。。。。
	 */
	public static int getSdkVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}
	/**
	 * 判断是否是电信用户
	 * @param context 上下文
	 * @return
	 */
	public static boolean is189(Context context) {
		if(!judgeSIM(context)){
			return false;
		}
		String telOperator = getSimOperatorName(context);
		if (CHINA_TELECOM.equals(telOperator))
			return true;
		return false;
	}
	/**
	 * 判断是否是联通用户
	 * @param context 上下文
	 * @return
	 */
	public static boolean is186(Context context) {
		if(!judgeSIM(context)){
			return false;
		}
		String telOperator = getSimOperatorName(context);
		if (CHINA_UNICOM.equals(telOperator))
			return true;
		return false;
	}
	/**
	 * 判断是否是移动用户
	 * @param context 上下文
	 * @return
	 */
	public static boolean is188(Context context) {
		if(!judgeSIM(context)){
			return false;
		}
		String telOperator = getSimOperatorName(context);
		if (CHINA_MOBILE.equals(telOperator) || CHINA_MOBILE2.equals(telOperator))
			return true;
		return false;
	}
	/**
	 * 判断sim卡是否可用
	 * @param context 上下文
	 * @return true 可用；false 不可用
	 */
	public static boolean judgeSIM(Context context) {
		boolean flag = true;
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (tm.getSimState() != TelephonyManager.SIM_STATE_READY) {
				flag = false;
			}
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	/**
	 * 检查是否是手机号或者固话
	 * @param phoneNumber 输入号码
	 * @return true 符合 false 不符合
	 */
	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;
		String expression = "(010\\d{8})|(0[2-9]\\d{9})|(13\\d{9})|(14[57]\\d{8})|(15[0-35-9]\\d{8})|(18[0-35-9]\\d{8})";
		CharSequence inputStr = phoneNumber;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}
}
