package com.useful.utils.encrypt;

import java.io.UnsupportedEncodingException;

import android.text.TextUtils;

/**
 * 简单加密/解密
 * @author xueyy
 *
 */
public class SimpleDES {

	/**
	 * 明文加密
	 * @param input 明文
	 * @return 密文
	 */
	public static String getEncString(String input){
		if(TextUtils.isEmpty(input)) return "";
		String output = "";
		output = MyBase64.encode(input.getBytes());
		return output;
	}
	
	/**
	 * 密文解密
	 * @param input 密文
	 * @return 明文
	 */
	public static String getDecString(String input){
		if(TextUtils.isEmpty(input)) return "";
		String output = "";
		byte[] encStr = null;
		try {
			encStr = MyBase64.decode(input);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		output = new String(encStr);
		return output;
	}
}
