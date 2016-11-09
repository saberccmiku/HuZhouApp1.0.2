package com.database.pojo;

/**
 * 
 * 应用版本表
 * 
 */
public class AppVersionTable extends BaseTable
{
	private static final long serialVersionUID = 9148095715030693221L;
	
	public static final String TABLE_NAME = "APP_VERSION";
	/** 版本码 */
	public static final String FIELD_VC = "APP_VC";
	/** 版本名 */
	public static final String FIELD_VN = "APP_VN";
	/** 版本详情 */
	public static final String FIELD_DETAIL = "CONTENT";
	/** 版本发布时间 */
	public static final String FIELD_TIME = "T_TIME";
	
	public AppVersionTable()
	{
		initTable();
	}
	
	private void initTable()
	{
		setTableName(TABLE_NAME);
	}
}
