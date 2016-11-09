package com.geekband.huzhouapp.utils;

import android.content.Context;

public class DensityUtil
{
	/**
	 * 根据手机的分辨率将px(像素)单位转换为dp单位
	 */
	public static int dipToPx(Context context, float dip)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}
	
	/**
	 * 根据手机的分辨率将dip单位转换为px单位
	 */
	public static int pxToDip(Context context, float px)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}
}
