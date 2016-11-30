package com.geekband.huzhouapp.chat.application;

import android.app.Application;
import android.content.Intent;
import cn.jpush.android.api.JPushInterface;

public class ToolApplication extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		
	    sendBroadcast(new Intent("whyd.APP_STATE_START"));
		
		JPushInterface.setDebugMode(true);
	    JPushInterface.init(this);
	}
}
