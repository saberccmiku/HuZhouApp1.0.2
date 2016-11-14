package com.geekband.huzhouapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.geekband.huzhouapp.service.BirthdayBroadcastReceiver;
import com.geekband.huzhouapp.service.GradeBroadcastReceiver;
import com.geekband.huzhouapp.service.MessageInformationReceiver;
import com.geekband.huzhouapp.service.NotificationService;
import com.geekband.huzhouapp.utils.Constants;

/**
 * Created by Administrator on 2016/5/12
 */
public class BaseActivity extends FragmentActivity {

    GradeBroadcastReceiver mGbr;
    private BirthdayBroadcastReceiver mBbr;
    private MessageInformationReceiver mMir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //禁止横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //注册广播
        registerBroadcast();
        //启动通知栏服务
        startService();

    }

    private void registerBroadcast() {

        mGbr = new GradeBroadcastReceiver();
        IntentFilter gradeFilter = new IntentFilter();
        gradeFilter.addAction(Constants.GRADE_BROADCAST);
        registerReceiver(mGbr, gradeFilter);

        mBbr = new BirthdayBroadcastReceiver();
        IntentFilter birthdayFilter = new IntentFilter();
        birthdayFilter.addAction(Constants.BIRTHDAY_BROADCAST);
        registerReceiver(mBbr, birthdayFilter);

        mMir = new MessageInformationReceiver();
        IntentFilter messageFilter = new IntentFilter();
        messageFilter.addAction(Constants.MESSAGE_BROADCAST);
        registerReceiver(mMir, messageFilter);
    }


    public void startService() {
       // Log.i(BaseActivity.class.getSimpleName() + "关注：", "通知栏是否启动");
        startService(new Intent(this, NotificationService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGbr);
        unregisterReceiver(mBbr);
        unregisterReceiver(mMir);
    }

}
