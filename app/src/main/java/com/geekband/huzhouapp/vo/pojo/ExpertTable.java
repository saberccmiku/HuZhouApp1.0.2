package com.geekband.huzhouapp.vo.pojo;

/**
 * 
 * 专家表
 * 
 */
public class ExpertTable extends BaseTable
{
	private static final long serialVersionUID = 8946916737704086334L;
	
	public static final String TABLE_NAME = "A_EXPERT";
	/** 用户编号 */
	public static final String FIELD_USERNO = "USERNO";
	/** 真实姓名 */
	public static final String FIELD_REALNAME = "REALNAME";
	/** 分类ID */
	public static final String FIELD_CATEGORYID = "CATEGORYID";
	/** 个人介绍 */
	public static final String FIELD_INTRODUCTION = "INTRODUCTION";
	/** 用户的contentId */
	public static final String FIELD_USERID = "USERID";
	/** 时间（教育训练） */
	public static final String FIELD_T_TIME = "T_TIME";
	
	public ExpertTable()
	{
		initTable();
	}
	
	private void initTable()
	{
		setTableName(TABLE_NAME);
	}
}
