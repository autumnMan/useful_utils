package com.useful.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.text.TextUtils;

public class TimeUtils {
	public final static String LONGEST_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	public final static String LONG_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public final static String SHORT_FORMAT = "yyyy-MM-dd";
	public final static String TIME_FORMAT = "HH:mm:ss";
	public final static String TIME_SHORT_FORMAT = "HH:mm";

	/**
	 * 获取现在时间
	 * 
	 * @return 返回短时间字符串格式yyyy-MM-dd
	 */
	public static String getNowDateShort() {
		return getNowDate(SHORT_FORMAT);
	}

	/**
	 * 获取现在时间
	 * 
	 * @return 返回字符串格式 yyyy-MM-dd HH:mm:ss
	 */
	public static String getNowDateNormal() {
		return getNowDate(LONG_FORMAT);
	}

	/**
	 * 获取现在时间
	 * 
	 * @return 返回字符串格式 yyyy-MM-dd HH:mm:ss.SSS
	 */
	public static String getNowDateLongest() {
		return getNowDate(LONGEST_FORMAT);
	}

	/**
	 * 获取现在时间 小时:分;秒 HH:mm:ss
	 * 
	 * @return
	 */
	public static String getNowTimeShort() {
		return getNowDate(TIME_FORMAT);
	}

	/**
	 * 获取现在时间
	 * 
	 * @param timeFormat
	 *            返回字符串格式
	 */
	public static String getNowDate(String timeFormat) {
		Date currentTime = new Date();
		String dateString = "";
		SimpleDateFormat formater = new SimpleDateFormat(timeFormat);
		dateString = formater.format(currentTime);
		return dateString;
	}

	/**
	 * 将长时间格式(yyyy-MM-dd HH:mm:ss.SSS)字符串转换为时间
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date strToDateLongest(String dateStr) {
		return strToDate(dateStr, LONGEST_FORMAT);
	}

	/**
	 * 将长时间格式(yyyy-MM-dd HH:mm:ss)字符串转换为时间
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date strToDateLong(String dateStr) {
		return strToDate(dateStr, LONG_FORMAT);
	}

	/**
	 * 将短时间格式(yyyy-MM-dd)字符串转换为时间
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date strToDateShort(String dateStr) {
		return strToDate(dateStr, SHORT_FORMAT);
	}

	/**
	 * 将时间格式(HH:mm:ss)字符串转换为时间
	 * 
	 * @param timeStr
	 *            时间 HH:mm:ss
	 * @return
	 */
	public static Date strToTime(String timeStr) {
		return strToDate(timeStr, TIME_FORMAT);
	}

	/**
	 * 将时间格式(HH:mm)字符串转换为时间
	 * 
	 * @return
	 */
	public static Date strToTimeShort(String strDate) {
		return strToDate(strDate, TIME_SHORT_FORMAT);
	}

	/**
	 * 按指定的时间格式字符串转换为时间
	 * 
	 * @param dateStr
	 *            日期字符串
	 * @param timeFormat
	 *            日期格式
	 * @return
	 */
	public static Date strToDate(String dateStr, String timeFormat) {
		Date date = null;
		SimpleDateFormat formater = new SimpleDateFormat(timeFormat);
		try {
			date = formater.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 将时间转换为字符串 yyyy-MM-dd HH:mm:ss.SSS
	 * 
	 * @param date
	 *            时间
	 * @return
	 */
	public static String dateToLongestStr(Date date) {
		return dateToStr(date, LONGEST_FORMAT);
	}

	/**
	 * 将时间转换为字符串 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 *            时间
	 * @return
	 */
	public static String dateToLongStr(Date date) {
		return dateToStr(date, LONG_FORMAT);
	}

	/**
	 * 将时间转换为字符串 yyyy-MM-dd
	 * 
	 * @param date
	 *            时间
	 * @return
	 */
	public static String dateToShortStr(Date date) {
		return dateToStr(date, SHORT_FORMAT);
	}

	/**
	 * 将时间转换为字符串 HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToTimeStr(Date date) {
		return dateToStr(date, TIME_FORMAT);
	}

	/**
	 * 将时间转换为字符串 HH:mm
	 * 
	 * @param date
	 * @return HH:mm
	 */
	public static String dateToShortTimeStr(Date date) {
		return dateToStr(date, TIME_SHORT_FORMAT);
	}

	/**
	 * 按指定的时间格式时间转换为字符串
	 * 
	 * @param date
	 *            时间
	 * @param timeFormat
	 *            时间格式
	 * @return
	 */
	public static String dateToStr(Date date, String timeFormat) {
		SimpleDateFormat formater = new SimpleDateFormat(timeFormat);
		return formater.format(date);
	}

	/**
	 * 把毫秒值转换成时间
	 * 
	 * @param milliseconds
	 *            毫秒
	 * @param timeFormat
	 *            時間格式
	 * @return
	 */
	public static String LongToDateStr(long milliseconds, String timeFormat) {
		return dateToStr(new Date(milliseconds), timeFormat);
	}

	public final static int DAYTIME = 1000 * 60 * 60 * 24;

	/**
	 * 获取星期
	 * 
	 * @param context
	 *            上下文
	 * @return
	 */
	public static String getWeekOfDate(Context context) {
		Date dt = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);

		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;

		if (context.getResources().getConfiguration().locale
				.equals(Locale.CHINA)
				|| context.getResources().getConfiguration().locale
						.equals(Locale.TRADITIONAL_CHINESE)) {
			String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
					"星期六" };
			return weekDays[w];
		} else {
			String[] weekDays = { "Sunday", "Monday", "Tuesday", "Wednesday",
					"Thursday", "Friday", "Saturday" };
			return weekDays[w];
		}

	}

	/**
	 * 获取月份
	 * 
	 * @param context
	 * @return
	 */
	public static String getMonth(Context context) {
		int d[] = TimeUtils.getDate();
		String string = null;
		if (context.getResources().getConfiguration().locale
				.equals(Locale.CHINA)
				|| context.getResources().getConfiguration().locale
						.equals(Locale.TRADITIONAL_CHINESE)) {
			string = String.valueOf(d[1]) + "月";
		}

		else {

			String[] month = { "January", "February", "March", "April", "May",
					"June", "July", "August", "September", "October",
					"November", "December" };
			string = String.valueOf(month[d[1] - 1]);
		}

		return string;

	}

	/**
	 * 获取年月
	 * 
	 * @return
	 */
	public static int[] getDate() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DATE);
		return new int[] { year, month, day };
	}
	/**
	 * 日期比较 
	 * @param d1
	 * @param d2
	 * @return d1》d2 返回 1；d1《d2返回-1；d1=d2返回0
	 */
	public static int compare(Date d1,Date d2){
		long t1 = d1.getTime();
		long t2 = d2.getTime();
		if(t1 > t2){
			return 1;
		}else if(t1 ==t2){
			return 0;
		}else{
			return -1;
		}
	}
	
    public static String formatTimestamp(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        } else {
            String revTime = "";
            try {
                revTime = time.substring(0, 4);
                for (int i = 0; i < 5; i++) {
                    int start = 4 + i * 2;
                    if (start == 8) {
                        revTime = revTime + " " + time.substring(start, start + 2);
                    } else if (start < 8) {
                        revTime = revTime + "-" + time.substring(start, start + 2);
                    } else {
                        revTime = revTime + ":" + time.substring(start, start + 2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return revTime;
        }
    }

	public static String millisToString(long millis) {
		boolean negative = millis < 0L;
		millis = Math.abs(millis);

		millis /= 1000L;
		int sec = (int)(millis % 60L);
		millis /= 60L;
		int min = (int)(millis % 60L);
		millis /= 60L;
		int hours = (int)millis;

		DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(Locale.US);
		format.applyPattern("00");
		String time;

		if (millis > 0L)
			time = (negative ? "-" : "") + hours + ":" + format.format(min) + ":" + format.format(sec);
		else
			time = (negative ? "-" : "") + min + ":" + format.format(sec);

		return time;
	}

	/**
	 * 判断两个日期是否是同一天
	 *
	 * @param date1
	 *            date1
	 * @param date2
	 *            date2
	 * @return
	 */
	public static boolean isSameDate(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);

		boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
				.get(Calendar.YEAR);
		boolean isSameMonth = isSameYear
				&& cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
		boolean isSameDate = isSameMonth
				&& cal1.get(Calendar.DAY_OF_MONTH) == cal2
				.get(Calendar.DAY_OF_MONTH);

		return isSameDate;
	}
}