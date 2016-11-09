package com.geekband.huzhouapp.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.application.MyApplication;
import com.geekband.huzhouapp.fragment.news.GradeActivity;
import com.geekband.huzhouapp.utils.Constants;

/**
 * Created by Administrator on 2016/6/2
 */
public class GradeBroadcastReceiver extends BroadcastReceiver {
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;

        //Log.i(GradeBroadcastReceiver.class.getSimpleName() + "目前：", intent.getAction());
        if (intent.getAction().equals(Constants.GRADE_BROADCAST)) {
            //Log.i(GradeBroadcastReceiver.class.getSimpleName() + "关注：", "学分通知是否启动");
            String msg1 = intent.getStringExtra("gradeMessage");
            // System.out.println("msg1:"+msg1);
            showIntentActivityNotify(msg1, "系统通知", "学分通知");
        }
    }


    /**
     * 清除当前创建的通知栏
     */

    /**
     * Notification的ID
     */
    int notifyId = 100;

    public  void  clearNotify(int notifyId) {
        MyApplication.mNotificationManager.cancel(notifyId);//删除一个特定的通知ID对应的通知
//		mNotification.cancel(getResources().getString(R.string.app_name));
    }

    /**
     * 清除所有通知栏
     */
    public void clearAllNotify() {
        MyApplication.mNotificationManager.cancelAll();// 删除你发的所有通知
    }

    /**
     * @获取默认的pendingIntent,为了防止2.3及以下版本报错
     * @flags属性: 在顶部常驻:Notification.FLAG_ONGOING_EVENT
     * 点击去除： Notification.FLAG_AUTO_CANCEL
     */
    public PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 2, new Intent(), flags);
        return pendingIntent;
    }

    /**
     * 显示通知栏点击跳转到指定Activity
     */
    public void showIntentActivityNotify(String msg,String contentTile,String ticker ) {
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
        Intent resultIntent = new Intent(mContext, GradeActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 2, resultIntent, 0);
        MyApplication.mBuilder.setContentIntent(pendingIntent);
        MyApplication.mNotificationManager.notify(notifyId, MyApplication.mBuilder.build());
    }

}
