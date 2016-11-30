package com.geekband.huzhouapp.vo.pojo;

/**
 * 
 * WXZJ-回答表 
 * 
 */
public class ReplyTable extends BaseTable
{
	private static final long serialVersionUID = -2156497104803188215L;
	
	public static final String TABLE_NAME = "REPLY";
	/** 回复评论编号 */
	public static final String FIELD_REPLYTONO = "REPLYTONO";
	/** 考试选项内容 */
	public static final String FIELD_CONTENT = "CONTENT";
	/** 时间（教育训练） */
	public static final String FIELD_T_TIME = "T_TIME";
	/** 用户编号 */
	public static final String FIELD_USERNO = "USERNO";
	
	public ReplyTable()
	{
		initTable();
	}
	
	private void initTable()
	{
		setTableName(TABLE_NAME);
	}
}
