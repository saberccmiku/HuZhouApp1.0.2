package com.chat.adapter.pojo;

import com.database.pojo.EnquiryTable;
import com.database.pojo.UserTable;
import com.geekband.huzhouapp.utils.Constants;
import com.oa.util.TimeUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 问题
 *
 */
public class Question implements Serializable, Comparable<Question>
{
	private static final long serialVersionUID = 108060571825664663L;

	//提问者信息
	private UserTable askerInfo;
	//提问信息
	private EnquiryTable askInfo;
	//回答信息列表
	private List<Answer> answerInfoList = new ArrayList<>();

	public Question(UserTable askerInfo, EnquiryTable askInfo, List<Answer> answerInfoList)
	{
		setAskerInfo(askerInfo);
		setAskInfo(askInfo);
		if(answerInfoList!=null) getAnswerInfoList().addAll(answerInfoList);
	}

	public UserTable getAskerInfo()
	{
		return askerInfo;
	}
	public void setAskerInfo(UserTable askerInfo)
	{
		this.askerInfo = askerInfo;
	}
	public EnquiryTable getAskInfo()
	{
		return askInfo;
	}
	public void setAskInfo(EnquiryTable askInfo)
	{
		this.askInfo = askInfo;
	}
	/**
	 *
	 * @return
	 */
	public List<Answer> getAnswerInfoList()
	{
		return answerInfoList;
	}

	/**
	 * 比较两个问题的发布时间早晚(新旧)
	 * @return 1：当前问题更新；0：新旧程度相同；-1：当前问题更旧；
	 */
	@Override
	public int compareTo(Question another)
	{
		try
		{
			long thisTime = TimeUtils.strToMsec(getAskInfo().getField(EnquiryTable.FIELD_APPLYTIME), Constants.TIME_FORMAT); //当前对象的时间值
			long thatTime = TimeUtils.strToMsec(another.getAskInfo().getField(EnquiryTable.FIELD_APPLYTIME), Constants.TIME_FORMAT); //传入对象的时间值

			if(thisTime>thatTime) return 1; //传入的问题比当前问题更晚发布(当前问题要新一些)
			if(thisTime==thatTime) return 0; //传入的问题与当前问题同时发布
			if(thisTime<thatTime) return -1; //传入的问题比当前问题更早发布(当前问题要旧一些)
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		return 0;
	}
}
