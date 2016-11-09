package com.oa.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils
{
	private TimeUtils(){}
	
	/**
	 * 把数值类型的时间转换为字符串类型的时间
	 * @param msec 时间毫秒值
	 * @param format 要转换的目标字符串格式
	 * @return
	 */
	public static String msecToStr(int msec, String format)
	{
		String str = new SimpleDateFormat(format).format(new Date(msec));
		
		return str;
	}
	
	/**
	 * 把字符串类型的时间转换成数值类型的时间
	 * @param time 时间字符串
	 * @param format 时间字符串的格式
	 * @return
	 * @throws ParseException
	 */
	public static long strToMsec(String time, String format) throws ParseException
	{
		long msec = new SimpleDateFormat(format).parse(time).getTime();
		
		return msec;
	}
}
