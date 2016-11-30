package com.geekband.huzhouapp.vo.pojo;

/**
 * 
 * 需求意见表
 * 
 */
public class OpinionTable extends BaseTable
{
	private transient static final long serialVersionUID = -1457340830882406359L;
	
	public transient static final String TABLE_NAME = "OPINION";
	/** 用户的contentId */
	public transient static final String FIELD_USERID = "USERID";
	/** 投稿时间 */
	public transient static final String FIELD_POSTTIME = "POSTTIME";
	/** 审核时间 */
	public transient static final String FIELD_PASSTIME = "PASSTIME";
	/** 是否通过审核 */
	public transient static final String FIELD_ISPASSED = "ISPASSED";
	/** 审核人 */
	public transient static final String FIELD_AUDITOR = "AUDITOR";
	
	public OpinionTable()
	{
		initTable();
	}

	private void initTable()
	{
		setTableName(TABLE_NAME);
	}


}
