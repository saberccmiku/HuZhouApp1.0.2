package com.geekband.huzhouapp.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.application.MyApplication;
import com.geekband.huzhouapp.nav.ReceiveBlessListActivity;
import com.geekband.huzhouapp.utils.Constants;
import com.geekband.huzhouapp.vo.DynamicNews;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/11
 */
public class MessageInformationReceiver extends BroadcastReceiver {

    private Context mContext;
    /**
     * Notification的ID
     */
    int notifyId = 103;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        //Log.i(MessageInformationReceiver.class.getSimpleName() + "目前：", intent.getAction());
        if (intent.getAction().equals(Constants.MESSAGE_BROADCAST)){
            String msg= intent.getStringExtra("giftMessage");
            ArrayList<DynamicNews> dynamicNewses = intent.getParcelableArrayListExtra("gifts");
            showIntentActivityNotify(msg, "系统通知", "好友祝福",dynamicNewses);
        }

    }

    /**
     * 显示通知栏点击跳转到指定Activity
     */
    public void showIntentActivityNotify(String msg,String contentTile,String ticker,ArrayList<DynamicNews> dynamicNewses ) {
        // Notification.FLAG_ONGOING_EVENT --设置常驻 Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
//		notification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
        MyApplication.mBuilder.setSmallIcon(R.drawable.app_icon_message) //设置通知  消息  图标
                .setContentTitle(contentTile)
                .setContentText(msg)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setTicker(ticker);
        //点击的意图ACTION是跳转到Intent
        Intent resultIntent = new Intent();
        resultIntent.setAction("messageToActivity");
        resultIntent.setClass(mContext, ReceiveBlessListActivity.class);
        Bundle messageBundle = new Bundle();
        messageBundle.putParcelableArrayList("giftMessageToActivity", dynamicNewses);
        resultIntent.putExtras(messageBundle);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 3, resultIntent, 0);
        MyApplication.mBuilder.setContentIntent(pendingIntent);
        MyApplication.mNotificationManager.notify(notifyId, MyApplication.mBuilder.build());
    }

}
