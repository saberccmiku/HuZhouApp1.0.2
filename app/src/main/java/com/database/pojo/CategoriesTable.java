package com.database.pojo;

public class CategoriesTable extends BaseTable
{
	private static final long serialVersionUID = 302174130134416724L;
	
	public static final String TABLE_NAME = "CATEGORIES";
	/** 分类名称 */
	public static final String FIELD_NAME = "C_NAME";
	/** 父类ID */
	public static final String FIELD_PARENTID = "C_PARENTID";
	/** 信息通用表父类名称 */
	public static final String FIELD_PARENTNAME = "C_PARENTNAME";
	/** 等级 */
	public static final String FIELD_LEVEL = "C_LEVEL";
	/** 顺序 */
	public static final String FIELD_SORT = "C_SORT";
	/** 模块的分值 */
	public static final String FIELD_WEIGHT = "WEIGHT";
	/** 模块是否可以投稿 */
	public static final String FIELD_ISPOST = "ISPOST";
	/** 区分模块 */
	public static final String FIELD_MENU = "MENU";
	/** 项目标兵图片 */
	public static final String FIELD_PICURL = "PICURL";
	
	public CategoriesTable()
	{
		initTable();
	}
	
	private void initTable()
	{
		setTableName(TABLE_NAME);
	}
}
