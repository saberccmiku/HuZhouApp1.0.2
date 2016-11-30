package com.geekband.huzhouapp.vo.pojo;

/**
 * 
 * 相册表
 * 
 */
public class AlbumTable extends BaseTable
{
	private static final long serialVersionUID = -3989103512893324425L;
	
	public static final String TABLE_NAME = "ALBUM";
	/** 用户的contentId */
	public static final String FIELD_USERID = "USERID";
	/** 名称 */
	public static final String FIELD_NAME = "NAME";
	/** 描述 */
	public static final String FIELD_DESCRIPTION = "DESCRIPTION";
	/** 发布时间 */
	public static final String FIELD_DATETIME = "DATETIME";
	/** 序号 */
	public static final String FIELD_ORDERNUM = "ORDERNUM";
	
	public AlbumTable()
	{
		initTable();
	}
	
	private void initTable()
	{
		setTableName(TABLE_NAME);
	}
}
