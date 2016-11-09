package com.database.pojo;

import android.os.Parcel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * 数据库表类
 * 
 */
public class BaseTable implements Serializable
{
	private static final long serialVersionUID = -8683731660085242057L;
	
	public transient static final String CONTENTID = "contentId";
	
	/** id */
	private int id;
	/** 每条表记录的contentId */
	private String contentId;
	/** 每条表记录的附件URL列表 */
	private List<String> accessaryFileUrlList;
	/** 表名 */
	private String tableName;
	/** 当前表记录的所有字段名-字段值映射 */
	private Map<String, String> fieldList;
	
	public BaseTable()
	{
		initVar();
	}

	protected BaseTable(Parcel in) {
		id = in.readInt();
		contentId = in.readString();
		accessaryFileUrlList = in.createStringArrayList();
		tableName = in.readString();
	}


	private void initVar()
	{
		id = 0;
		contentId = "";
		accessaryFileUrlList = new ArrayList<>();
		tableName = "";
		fieldList = new HashMap<>();
	}
	
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getContentId()
	{
		return contentId;
	}

	public void setContentId(String contentId)
	{
		this.contentId = contentId;
	}
	
	public List<String> getAccessaryFileUrlList()
	{
		return accessaryFileUrlList;
	}

	public String getTableName()
	{
		return tableName;
	}

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}
	
	/**
	 * 从当前表记录中获取指定字段的值
	 * @param fieldName
	 * @return
	 */
	public String getField(String fieldName)
	{
		return fieldList.get(fieldName);
	}
	
	/**
	 * 向当前表记录添加一条字段/更新当前表记录中的一条字段
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	public String putField(String fieldName, String fieldValue)
	{
		return fieldList.put(fieldName, fieldValue);
	}
	
	/**
	 * 获取当前表记录的字段名称列表
	 * @return
	 */
	public String[] getFieldNameList()
	{
		Object[] list = fieldList.keySet().toArray();
		String[] fieldNameList = new String[list.length];
		for (int i = 0; i < list.length; i++)
		{
			fieldNameList[i] = (String) list[i];
		}
		return fieldNameList;
	}
	
	/**
	 * 获取当前表记录的字段列表
	 * @return
	 */
	public Map<String, String> getFieldList()
	{
		Map<String, String> backups = new HashMap<>();
		Iterator<?> iterator = fieldList.keySet().iterator();
		while(iterator.hasNext())
		{
			String key = (String) iterator.next();
			String value = fieldList.get(key);
			backups.put(key, value);
		}
		return backups;
	}
	
	/**
	 * 打印当前表记录的所有字段名-字段值键值对
	 */
	@Override
	public String toString()
	{
		StringBuffer str = new StringBuffer();
		str.append(getClass().getName()+":");
		str.append("[");
		str.append("contentId="+contentId+",");
		str.append("accessaryFileList="+accessaryFileUrlList+",");
		str.append("fieldList=[");
		for (int i = 0; i < getFieldList().size(); i++)
		{
			str.append(getFieldNameList()[i]+"="+getField(getFieldNameList()[i])+",");
		}
		str.append("]");
		str.append("]");
		
		return str.toString();
	}
	
	/**
	 * 通过判断两条表记录的contentId是否相同来判断它们是否相同(相等)
	 */
	public boolean equals(Object table)
	{
		try
		{
			if(table instanceof BaseTable) return getContentId().equals(((BaseTable) table).getContentId());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return false;
	}

}
