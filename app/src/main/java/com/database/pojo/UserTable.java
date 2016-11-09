package com.database.pojo;


/**
 * 
 * 用户表
 * 
 */
public class UserTable extends BaseTable
{
	private transient static final long serialVersionUID = -7359533823172139550L;
	
	public transient static final String TABLE_NAME = "USERS";
	/** 用户名 */ 
	public transient static final String FIELD_USERNAME = "USERNAME";
	/** 真实姓名 */ 
	public transient static final String FIELD_REALNAME = "REALNAME";
	/** 电话 */ 
	public transient static final String FIELD_TELEPHONE = "TELEPHONE";
	/** 邮箱 */ 
	public transient static final String FIELD_EMAIL = "EMAIL";
	/** 密码 */ 
	public transient static final String FIELD_PASSWORD = "PASSWORD";
	/** 部门编号 */ 
	public transient static final String FIELD_DEPARTMENTNO = "DEPARTMENTNO";
	/** 角色编号 */ 
	public transient static final String FIELD_ROLENO = "ROLENO";
	/** 组编号 */ 
	public transient static final String FIELD_GROUPNO = "GROUPNO";
	/** 用户编号 */ 
	public transient static final String FIELD_USERNUM = "USERNUM";
	/** 警号 */
	public transient static final String FIELD_POLICENUM = "POLICENUM";
	/** 文化程度 */
	public transient static final String FIELD_EDUCATION = "EDUCATION";
	/** 参加工作时间 */
	public transient static final String FIELD_TOWORKTIME = "TOWORKTIME";
	/** 参加公安时间 */
	public transient static final String FIELD_TOPOLICETIME = "TOPOLICETIME";
	/** 警衔 */
	public transient static final String FIELD_RANK = "RANK";
	/** 警务 */
	public transient static final String FIELD_POLICEPOST = "POLICEPOST";
	/** 备注 */
	public transient static final String FIELD_REMARK = "REMARK";
	/** 手机号 */
	public transient static final String FIELD_CELLPHONE = "CELLPHONE";
	/** 身份证号 */
	public transient static final String FIELD_IDCARDNO = "IDCARDNO";
	/** 性别 */
	public transient static final String FIELD_SEX = "SEX";
	/** 图片地址 */
	public transient static final String FIELD_PICURL = "PICURL";
	/** 平台秘钥 */
	public transient static final String FIELD_TOKEN = "TOKEN";


	public UserTable()
	{
		initTable();
	}
	
	private void initTable()
	{
		//创建表名
		setTableName(TABLE_NAME);
	}
	
}
