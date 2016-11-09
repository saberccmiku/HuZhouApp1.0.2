package com.chat.adapter.pojo;

import java.io.Serializable;

import com.database.pojo.ReplyTable;
import com.database.pojo.UserTable;


public class Message implements Serializable
{
	private static final long serialVersionUID = 5922976569956649690L;
	
	//消息发送者信息
	private UserTable messageSenderInfo;
	//消息信息
	private ReplyTable messageInfo;
	//消息类型
	private int messageType;
	
	public Message(UserTable messageSenderInfo, ReplyTable messageInfo, int messageType)
	{
		setMessageSenderInfo(messageSenderInfo);
		setMessageInfo(messageInfo);
		setMessageType(messageType);
	}
	
	public UserTable getMessageSenderInfo()
	{
		return messageSenderInfo;
	}
	public void setMessageSenderInfo(UserTable messageSenderInfo)
	{
		this.messageSenderInfo = messageSenderInfo;
	}
	public ReplyTable getMessageInfo()
	{
		return messageInfo;
	}
	public void setMessageInfo(ReplyTable messageInfo)
	{
		this.messageInfo = messageInfo;
	}
	public int getMessageType()
	{
		return messageType;
	}
	public void setMessageType(int messageType)
	{
		this.messageType = messageType;
	}

	public static final int TYPE_SEND_TEXT = 0;
	public static final int TYPE_RECEIVE_TEXT = 1;
}
