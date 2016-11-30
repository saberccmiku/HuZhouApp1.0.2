package com.geekband.huzhouapp.vo.pojo;

/**
 * 
 * 内容表
 * 
 */
public class ContentTable extends BaseTable
{
	private static final long serialVersionUID = -6349511649093732638L;
	
	public static final String TABLE_NAME = "CONTENTS";
	/** 信息的contentId */
	public static final String FIELD_NEWSID = "NEWSID";
	/** 内容 */
	public static final String FIELD_SUBSTANCE = "SUBSTANCE";
	/** 序号 */
	public static final String FIELD_ORDERNUM = "ORDERNUM";
	/** 内容表区分 */
	public static final String FIELD_DIVI = "DIVI";
	
	public ContentTable()
	{
		initTable();
	}
	
	private void initTable()
	{
		setTableName(TABLE_NAME);
	}
}
