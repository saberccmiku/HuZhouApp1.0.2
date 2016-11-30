package com.geekband.huzhouapp.vo.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yumignxu
 * @date 2012-11-14 上午08:06:51
 * @category xml文件解析后获得的节点属性值
 */
public class DataSetList implements Serializable
{
	/** 新增的字段，用于弥补之前的DOCUMENTID无法处理 字段附带1个以上的附件 的问题 */
	public List<List<String>> DOCUMENTIDLIST = new ArrayList<List<String>>();

	private static final long serialVersionUID = 1L;
	public Map<String, List<String>> map = new HashMap<String, List<String>>();
	public String type;
	public List<String> description = new ArrayList<String>();
	public List<String> nameList = new ArrayList<String>();
	public List<String> valueList = new ArrayList<String>();
	public List<String> CONTENTID = new ArrayList<String>();
	public List<String> DOCUMENTID = new ArrayList<String>();
	public List<String> COMMENTID = new ArrayList<String>();
	public List<String> NODEID = new ArrayList<String>();
	public List<String> WORKID = new ArrayList<String>();
	public List<String> WORKQUEUENAME = new ArrayList<String>();
	public List<Boolean> isExistDocumentID = new ArrayList<Boolean>();
	public String EXTATTR1;// 拓展属性1
	public String EXTATTR2;
	public String ERROR = "";
	public String SUCCESS = "";

	// MobileOffice
	public String fileName;
	public String lastChangedDate;
	public ArrayList<String> DOCUMENTTYPE = new ArrayList<String>();
	public ArrayList<String> docSize = new ArrayList<String>();
	public ArrayList<String> commSize = new ArrayList<String>();
	public ArrayList<String> size = new ArrayList<String>();
	public ArrayList<String> mimeType = new ArrayList<String>();
	public ArrayList<String> docMimeType = new ArrayList<String>();
	public ArrayList<String> commMimeType = new ArrayList<String>();
	public ArrayList<String> sharer = new ArrayList<String>();
	public ArrayList<String> creater = new ArrayList<String>();
	public ArrayList<String> lastChangedDates = new ArrayList<String>();
	public ArrayList<String> tableName = new ArrayList<String>();

	@Override
	public String toString()
	{
		StringBuffer str = new StringBuffer();

		str.append("map:" + map + "\n");
		str.append("type:" + type + "\n");
		str.append("description:" + description + "\n");
		str.append("nameList:" + nameList + "\n");
		str.append("valueList:" + valueList + "\n");
		str.append("CONTENTID:" + CONTENTID + "\n");
		str.append("DOCUMENTID:" + DOCUMENTID + "\n");
		str.append("COMMENTID:" + COMMENTID + "\n");
		str.append("NODEID:" + NODEID + "\n");
		str.append("WORKID:" + WORKID + "\n");
		str.append("WORKQUEUENAME:" + WORKQUEUENAME + "\n");
		str.append("isExistDocumentID:" + isExistDocumentID + "\n");
		str.append("EXTATTR1:" + EXTATTR1 + "\n");
		str.append("EXTATTR2:" + EXTATTR2 + "\n");
		str.append("ERROR:" + ERROR + "\n");
		str.append("SUCCESS:" + SUCCESS + "\n");

		str.append("fileName:" + fileName + "\n");
		str.append("lastChangedDate:" + lastChangedDate + "\n");
		str.append("DOCUMENTTYPE:" + DOCUMENTTYPE + "\n");
		str.append("docSize:" + docSize + "\n");
		str.append("commSize:" + commSize + "\n");
		str.append("size:" + size + "\n");
		str.append("mimeType:" + mimeType + "\n");
		str.append("docMimeType:" + docMimeType + "\n");
		str.append("commMimeType:" + commMimeType + "\n");
		str.append("sharer:" + sharer + "\n");
		str.append("creater:" + creater + "\n");
		str.append("lastChangedDates:" + lastChangedDates + "\n");
		str.append("tableName:" + tableName + "\n");

		return str.toString();
	}
}
