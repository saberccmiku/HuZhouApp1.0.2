package com.database.pojo;

/**
 * 
 * 课程表
 * 
 */
public class CourseTable extends BaseTable
{
	private transient static final long serialVersionUID = 8904286316307351153L;
	
	public transient static final String TABLE_NAME = "COURSE";
	/** 课程名 */
	public transient static final String FIELD_COURSENAME = "COURSENAME";
	/** 课程类型(1.必修 2.选修) */
	public transient static final String FIELD_COURSETYPE = "COURSETYPE";
	/** 课程简介 */
	public transient static final String FIELD_COURSEINTRO = "COURSEINTRO";
	/** 详细文字 */
	public transient static final String FIELD_DETAILED = "DETAILED";
	/** 排序（越大越靠前） */
	public transient static final String FIELD_SORT = "SORT";
	/** 点击次数 */
	public transient static final String FIELD_CLICKNUM = "CLICKNUM";
	/** 积分 */
	public transient static final String FIELD_POINT = "POINT";
	/** 是否删除 0否 1是 */
	public transient static final String FIELD_ISDELETED = "ISDELETED";
	/** 用户编号 */
	public transient static final String FIELD_USERNO = "USERNO";
	/** 需要学习时长 */
	public transient static final String FIELD_NEEDTIME = "NEEDTIME";
	/** 课程发布时间 */
	public transient static final String FIELD_COURSETIME = "COURSETIME";
	public CourseTable()
	{
		initTable();
	}
	
	private void initTable()
	{
		setTableName(TABLE_NAME);
	}
}
