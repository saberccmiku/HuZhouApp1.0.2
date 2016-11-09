package com.database.pojo;

/**
 * 
 * 问询表
 * 
 */
public class EnquiryTable extends BaseTable
{
	private static final long serialVersionUID = -7022012299420125655L;
	
	public static final String TABLE_NAME = "ENQUIRY";
	/** 用户编号 */
	public static final String FIELD_USERNO = "USERNO";
	/** 考试选项内容 */
	public static final String FIELD_CONTENT = "CONTENT";
	/** 分类ID */
	public static final String FIELD_CATEGORYID = "CATEGORYID";
	/** 申请时间 */
	public static final String FIELD_APPLYTIME = "APPLYTIME";
	
	public EnquiryTable()
	{
		initTable();
	}
	
	private void initTable()
	{
		setTableName(TABLE_NAME);
	}
}
