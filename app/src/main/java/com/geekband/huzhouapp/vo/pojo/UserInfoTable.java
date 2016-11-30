package com.geekband.huzhouapp.vo.pojo;


/**
 * 
 * 用户个人信息表
 * 
 */
public class UserInfoTable extends BaseTable
{
	private static final long serialVersionUID = -3345484609132375843L;
	
	public static final String TABLE_NAME = "USERINFO";
	/** 性别 */
	public static final String FIELD_SEX = "SEX";
	/** 地址 */
	public static final String FIELD_ADDRESS = "ADDRESS";
	/** 出生日期 */
	public static final String FIELD_BIRTHDAY = "BIRTHDAY";
	/** 用户编号 */
	public static final String FIELD_USERID = "USERID";
	/** 身份证号 */
	public static final String FIELD_IDCARDNO = "IDCARDNO";
	
	public UserInfoTable()
	{
		initTable();
	}
	
	private void initTable()
	{
		setTableName(TABLE_NAME);
	}
}
