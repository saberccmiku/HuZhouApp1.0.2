package com.geekband.huzhouapp.vo.pojo;

/**
 * 
 * (信息类通用表)
 * 
 */
public class CommonTable extends BaseTable
{
	private static final long serialVersionUID = -553290983353241736L;
	
	public static final String TABLE_NAME = "N_CONTENT";
	/** 标题 */
	public static final String FIELD_TITLE = "TITLE";
	/** 发布时间 */
	public static final String FIELD_DATETIME = "DATETIME";
	/** 是否置顶 */
	public static final String FIELD_ISTOP = "ISTOP";
	/** 是否滚动 */
	public static final String FIELD_ISROLLING = "ISROLLING";
	/** 是否发布 */
	public static final String FIELD_ISPUBLISH = "ISPUBLISH";
	/** 分类ID */
	public static final String FIELD_CATEGORYID = "CATEGORYID";
	/** 序号 */
	public static final String FIELD_ORDERNUM = "ORDERNUM";
	/** 作者的id */
	public static final String FIELD_WRITERID = "WRITERID";
	/** 政工简报报头 */
	public static final String FIELD_HEADER = "HEADER";
	/** 是否通过审核 */
	public static final String FIELD_ISPASSED = "ISPASSED";
	/** 投稿时间 */
	public static final String FIELD_POSTTIME = "POSTTIME";
	/** 审核时间 */
	public static final String FIELD_PASSTIME = "PASSTIME";
	/** 审核人 */
	public static final String FIELD_AUDITOR = "AUDITOR";
	/** 模块的分值 */
	public static final String FIELD_WEIGHT = "WEIGHT";
	/** 项目标兵图片 */
	public static final String FIELD_PICURL = "PICURL";
	/** 未通过原因 */
	public static final String FIELD_REASON = "REASON";
	
	public CommonTable()
	{
		initTable();
	}
	
	private void initTable()
	{
		setTableName(TABLE_NAME);
	}
}
