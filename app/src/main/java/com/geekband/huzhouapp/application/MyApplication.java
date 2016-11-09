package com.geekband.huzhouapp.application;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import com.geekband.huzhouapp.utils.Constants;
import com.lidroid.xutils.DbUtils;

/**
 * Created by Administrator on 2016/5/15
 */
public class MyApplication extends Application {

    public static DbUtils sDbUtils;
    public static SharedPreferences sSharedPreferences;
    public static NotificationCompat.Builder mBuilder;
    public static NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sDbUtils = DbUtils.create(getApplicationContext(), Constants.NEWS_TABLE);
        sSharedPreferences = getSharedPreferences(Constants.AUTO_LOGIN,MODE_PRIVATE);
        //初始化通知栏
        initService();
        initNotify();
    }

    /**
     * 初始化要用到的系统服务
     */
    private void initService() {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 初始化通知栏
     */
    private void initNotify() {
        mBuilder = new NotificationCompat.Builder(this);
    }
}
