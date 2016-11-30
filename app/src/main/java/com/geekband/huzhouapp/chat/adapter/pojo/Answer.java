package com.geekband.huzhouapp.chat.adapter.pojo;

import com.geekband.huzhouapp.vo.pojo.ReplyTable;
import com.geekband.huzhouapp.vo.pojo.UserTable;

import java.io.Serializable;

public class Answer implements Serializable
{
	private static final long serialVersionUID = 5401325250201181328L;
	
	//回答者信息
	private UserTable replierInfo;
	//回答信息
	private ReplyTable answerInfo;
	
	public Answer(UserTable replierInfo, ReplyTable answerInfo)
	{
		setReplierInfo(replierInfo);
		setAnswerInfo(answerInfo);
	}
	
	public UserTable getReplierInfo()
	{
		return replierInfo;
	}
	public void setReplierInfo(UserTable replierInfo)
	{
		this.replierInfo = replierInfo;
	}
	public ReplyTable getAnswerInfo()
	{
		return answerInfo;
	}
	public void setAnswerInfo(ReplyTable answerInfo)
	{
		this.answerInfo = answerInfo;
	}
}
