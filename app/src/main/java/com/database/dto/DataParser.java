package com.database.dto;

import com.database.pojo.AlbumTable;
import com.database.pojo.AppVersionTable;
import com.database.pojo.BaseTable;
import com.database.pojo.CategoriesTable;
import com.database.pojo.ContentTable;
import com.database.pojo.CourseTable;
import com.database.pojo.DataSetList;
import com.database.pojo.EnquiryTable;
import com.database.pojo.ExpertTable;
import com.database.pojo.CommonTable;
import com.database.pojo.OpinionTable;
import com.database.pojo.ReplyTable;
import com.database.pojo.StudyInfoTable;
import com.database.pojo.StudyScoreTable;
import com.database.pojo.ThumbsTable;
import com.database.pojo.UserInfoTable;
import com.database.pojo.UserTable;
import com.oa.util.Constants;

import java.util.ArrayList;
import java.util.List;

public final class DataParser
{
	private DataParser()
	{
		
	}
	
	public static ArrayList<BaseTable> getTable(DataSetList dataList, String tableName)
	{
		ArrayList<BaseTable> tableDataList = new ArrayList<>();
		if (dataList!=null) {
			String type = dataList.type; //表名
			ArrayList<String> contentIdList = (ArrayList<String>) dataList.CONTENTID; //contentId
			List<List<String>> documentIdList = (List<List<String>>) dataList.DOCUMENTIDLIST; //附件目录
			ArrayList<String> nameList = (ArrayList<String>) dataList.nameList; //字段名
			ArrayList<String> valueList = (ArrayList<String>) dataList.valueList; //字段值

			if (contentIdList.size() == 0) return tableDataList; //若相匹配的结果数为0，则直接返回

			//将数据库表记录数据从DataSetList中解析出来
			BaseTable tableData = null;
			int recordNum = nameList.size() / contentIdList.size(); //记录的总数
			int recordIndex = 0; //当前解析到第几条记录
			for (int i = 0; i < nameList.size(); i++) //循环解析所有的记录
			{
				if (i % recordNum == 0) {
					tableData = newTableInstance(tableName);
					tableData.setTableName(type);
					tableData.setContentId(contentIdList.get(recordIndex));

					for (String documentId : documentIdList.get(recordIndex)) {
						String fileUrl = "http://" + Constants.CONNIP + Constants.FILE_PATH + documentId + "/" + "file";
						tableData.getAccessaryFileUrlList().add(fileUrl);
					}
				} else {
					tableData.putField(nameList.get(i), valueList.get(i));

					if (i % recordNum == recordNum - 1) {
						tableDataList.add(tableData);
						recordIndex++;
					}
				}
			}
		}
		
		return tableDataList;
	}
	
	/**
	 * 根据给定的表类型返回对应的表实例，实例以BaseTable类型保存
	 * @param tableName
	 * @return
	 */
	public static BaseTable newTableInstance(String tableName)
	{
		BaseTable tableData = null;
		
		switch(tableName)
		{
			case AlbumTable.TABLE_NAME: tableData = new AlbumTable(); break;
			case AppVersionTable.TABLE_NAME: tableData = new AppVersionTable(); break;
			case ContentTable.TABLE_NAME: tableData = new ContentTable(); break;
			case CourseTable.TABLE_NAME: tableData = new CourseTable(); break;
			case EnquiryTable.TABLE_NAME: tableData = new EnquiryTable(); break;
			case ExpertTable.TABLE_NAME: tableData = new ExpertTable(); break;
			case CommonTable.TABLE_NAME: tableData = new CommonTable(); break;
			case OpinionTable.TABLE_NAME: tableData = new OpinionTable(); break;
			case ReplyTable.TABLE_NAME: tableData = new ReplyTable(); break;
			case StudyInfoTable.TABLE_NAME: tableData = new StudyInfoTable(); break;
			case StudyScoreTable.TABLE_NAME: tableData = new StudyScoreTable(); break;
			case ThumbsTable.TABLE_NAME: tableData = new ThumbsTable(); break;
			case UserInfoTable.TABLE_NAME: tableData = new UserInfoTable(); break;
			case UserTable.TABLE_NAME: tableData = new UserTable(); break;
			case CategoriesTable.TABLE_NAME: tableData = new CategoriesTable(); break;
			
			default: tableData = new BaseTable(); break;
		}
		
		return tableData;
	}
}
