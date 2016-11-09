package com.geekband.huzhouapp.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.util.List;

/**
 * Created by Administrator on 2016/4/17
 */
public class LinkNet {
    //判断网络连接是否可用
    public static boolean isNetworkAvailable(Context context) {
        //获取连接管理器
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取当前激活的网络对象
        //需要权限  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        if(activeNetworkInfo==null){
            return false;
        }

        //只有wifi和移动网络认为有网
        int type = activeNetworkInfo.getType();
        if (type==ConnectivityManager.TYPE_WIFI){
            return true;
        }else if (type==ConnectivityManager.TYPE_MOBILE) {
            return true;
        }

        return false;
    }

    //判断gps是否打开
    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = ((LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE));
        List<String> accessibleProviders = lm.getProviders(true);
        return accessibleProviders != null && accessibleProviders.size() > 0;
    }

    //判断wifi是否打开
    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    //判断是wifi还是3g网络,用户的体现性在这里了，wifi就可以建议下载或者在线播放。
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }
}