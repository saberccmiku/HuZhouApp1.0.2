package com.geekband.huzhouapp.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/4/13
 */
public class FileUtils {

    public static String getCurrentTimeStr(){
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }
    public static String getCurrentTimeStr(String dateType){
        return new SimpleDateFormat(dateType).format(new Date());
    }

    //获取手机屏幕尺寸，单位是px
    public static int[] getPhoneSize(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        return new int[]{w_screen,h_screen};
    }
    //获取手机屏幕宽度，单位是dip
    public static int getScreenWidth(Context context){
        return pxToDip(context,getPhoneSize(context)[0]);
    }
    //获取手机屏幕高度，单位是dip
    public static int getScreenHeight(Context context){
        return pxToDip(context,getPhoneSize(context)[1]);
    }

    //px转dip
    public static int pxToDip (Context context,int px){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(px/scale+0.5f);
    }
    //dip转px
    public static int dipToPx (Context context,int dip){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dip*scale+0.5f);
    }
}
