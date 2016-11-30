package com.geekband.huzhouapp.chat.adapter.pojo;

import java.io.Serializable;

public class QuestionMenuItem implements Serializable
{
	private static final long serialVersionUID = 4469664178406974754L;
	
	//图标
	private int iconId;
	//标题
	private String title;
	
	public QuestionMenuItem(int iconId, String title)
	{
		setIconId(iconId);
		setTitle(title);
	}
	
	public int getIconId()
	{
		return iconId;
	}
	public void setIconId(int iconId)
	{
		this.iconId = iconId;
	}
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
}
