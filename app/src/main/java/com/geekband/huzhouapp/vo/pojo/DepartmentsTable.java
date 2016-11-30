package com.geekband.huzhouapp.vo.pojo;

/**
 * Created by Administrator on 2016/6/30
 */
public class DepartmentsTable extends BaseTable {
    private static final long serialVersionUID = 1L;

    public static final String TABLE_NAME = "DEPARTMENTS";
    /** 部门名称 */
    public static final String FIELD_DEPARTMENTNAME= "DEPARTMENTNAME";
    /** 部门权限 */
    public static final String FIELD_DEPARTMENTRIGHT = "DEPARTMENTRIGHT";
    /** 部门描述 */
    public static final String FIELD_DEPARTMENTDESCRIPTION = "DEPARTMENTDESCRIPTION";
    /** 级别 */
    public static final String FIELD_C_LEVEL = "C_LEVEL";
    /** 父类id */
    public static final String FIELD_C_PARENTID = "C_PARENTID";

    public DepartmentsTable()
    {
        initTable();
    }

    private void initTable()
    {
        setTableName(TABLE_NAME);
    }

}
