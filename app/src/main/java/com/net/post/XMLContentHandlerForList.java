package com.net.post;

import com.geekband.huzhouapp.vo.pojo.DataSetList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Administrator
 * @date 2012-9-21 下午03:52:16
 * @category 解析服务器端返回的xml文件
 */
public class XMLContentHandlerForList extends DefaultHandler
{
	private List<String> tempDOCUMENTID = new ArrayList<>();
	
	private String temp = "";
	private String tagName;
	public DataSetList dataSet = new DataSetList();
	private boolean isExistDocumentID;// 是否存在文档ID
	int nameCounter = 0;
	int valueCounter = 0;

	@Override
	public void startDocument() throws SAXException
	{

	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		tagName = localName;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		if (tagName.equals("CONTENTTYPENAME"))
		{
			dataSet.type = new String(ch, start, length);
		}
		else if (tagName.equals("CONTENTID"))
		{
			temp = temp + new String(ch, start, length);
		}
		else if (tagName.equals("DOCUMENTID"))
		{
			isExistDocumentID = true;
			temp = temp + new String(ch, start, length);
		}
		else if (tagName.equals("NODETYPENAME"))
		{
			temp = temp + new String(ch, start, length);
		}
		else if (tagName.equals("DESCRIPTION"))
		{
			temp = temp + new String(ch, start, length);
		}
		else if (tagName.equals("NAME"))
		{
			temp = temp + new String(ch, start, length);
		}
		else if (tagName.equals("VALUE"))
		{
			temp = temp + new String(ch, start, length);
		}
		else if (tagName.equals("ERROR"))
		{
			temp = temp + new String(ch, start, length);
		}
		else if (tagName.equals("SUCCESS"))
		{
			temp = temp + new String(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		tagName = localName;
		if (tagName.equals("NAME") || tagName.equals("NODETYPENAME"))
		{
			dataSet.nameList.add(temp);
			temp = "";
			nameCounter++;
		}
		else if (tagName.equals("VALUE") || tagName.equals("NODETYPENAME"))
		{
			int difference = dataSet.nameList.size() - (dataSet.valueList.size() + 1);// VALUE为空时处理
			if (difference > 0)
			{
				for (int i = 0; i < difference; i++)
				{
					dataSet.valueList.add("");
				}
			}
			dataSet.valueList.add(temp);
			temp = "";
			valueCounter++;
		}
		else if (tagName.equals("CONTENTID"))
		{
			dataSet.CONTENTID.add(temp);
			temp = "";
		}
		else if (tagName.equals("DOCUMENTID"))
		{
			tempDOCUMENTID.add(temp);
			//dataSet.DOCUMENTID.add(temp);
			temp = "";
		}
		else if (tagName.equals("DESCRIPTION"))
		{
			dataSet.description.add(temp);
			temp = "";
		}
		else if (tagName.equals("ERROR"))
		{
			// dataSet.ERROR = "ERROR";
			dataSet.ERROR = temp;
			temp = "";
		}
		else if (tagName.equals("SUCCESS"))
		{
			dataSet.SUCCESS = "SUCCESS";
			// dataSet.SUCCESS = temp;

			temp = "";
		}
		else if (tagName.equals("YOUNGCONTENT"))
		{
			// 附件缺失时的处理
			if (!isExistDocumentID)
			{
				//dataSet.DOCUMENTID.add("A00000");
			}
			dataSet.DOCUMENTIDLIST.add(tempDOCUMENTID);
			tempDOCUMENTID = new ArrayList<>();
			isExistDocumentID = false;// 重置
		}
	}

	@Override
	public void endDocument() throws SAXException
	{
		int difference = dataSet.nameList.size() - dataSet.valueList.size();// VALUE为空时处理
		if (difference > 0)
		{
			for (int i = 0; i < difference; i++)
			{
				dataSet.valueList.add("");
			}
		}
	}
}
