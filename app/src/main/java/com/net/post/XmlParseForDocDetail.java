package com.net.post;

import com.database.pojo.DataSetList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * @author lb
 * @date 2014-2-25
 * @category 解析服务器端返回的xml文件
 */
public class XmlParseForDocDetail extends DefaultHandler {
	private String temp = "";
	private String tagName;
	public DataSetList dataSet = new DataSetList();

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		tagName = localName;
		super.startElement(uri, localName, qName, attributes);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if ("YOUNGCONTENT".equals(tagName)) {
			dataSet.type = new String(ch, start, length);
		} else if ("LASTCHANGEDDATE".equals(tagName)) {
			temp = temp + new String(ch, start, length);
		} else if ("VALUE".equals(tagName)) {
			temp = temp + new String(ch, start, length);
		} else if ("DOCUMENTID".equals(tagName)) {
			temp = temp + new String(ch, start, length);
		} else if ("CONTENTID".equals(tagName)) {
			temp = temp + new String(ch, start, length);
		} else if ("WORKID".equals(tagName)) {
			temp = temp + new String(ch, start, length);
		} else if ("CONTENTTYPENAME".equals(tagName)) {
			temp = temp + new String(ch, start, length);
		} else if ("WORKQUEUENAME".equals(tagName)) {
			temp = temp + new String(ch, start, length);
		} else if ("NAME".equals(tagName)) {
			temp = temp + new String(ch, start, length);
		} else if ("DOCUMENTTYPENAME".equals(tagName)) {
			temp = temp + new String(ch, start, length);
		} else if ("SIZE".equals(tagName)) {
			temp = temp + new String(ch, start, length);
		} else if ("MIMETYPE".equals(tagName)) {
			temp = temp + new String(ch, start, length);
		} else if (tagName.equals("SUCCESS")) {
			temp = temp + new String(ch, start, length);
		}
		super.characters(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		tagName = localName;
		if ("LASTCHANGEDDATE".equals(tagName)) {
			dataSet.lastChangedDate = temp;
			dataSet.lastChangedDates.add(temp);
			temp = "";
		} else if ("VALUE".equals(tagName)) {
			int difference = dataSet.nameList.size() - (dataSet.valueList.size() + 1);// VALUE为空时处理
			if (difference > 0) {
				for (int i = 0; i < difference; i++) {
					dataSet.valueList.add("");
				}
			}
			dataSet.valueList.add(temp);
			dataSet.fileName = temp;
			temp = "";
		} else if ("DOCUMENTID".equals(tagName)) {
			dataSet.DOCUMENTID.add(temp);
			temp = "";
		} else if ("WORKID".equals(tagName)) {
			dataSet.WORKID.add(temp);
			temp = "";
		} else if ("CONTENTTYPENAME".equals(tagName)) {
			dataSet.tableName.add(temp);
			temp = "";
		} else if ("WORKQUEUENAME".equals(tagName)) {
			dataSet.WORKQUEUENAME.add(temp);
			temp = "";
		} else if ("CONTENTID".equals(tagName)) {
			dataSet.CONTENTID.add(temp);
			temp = "";
		} else if ("NAME".equals(tagName)) {
			dataSet.sharer.add(temp);
			dataSet.nameList.add(temp);
			temp = "";
		} else if ("DOCUMENTTYPENAME".equals(tagName)) {
			dataSet.DOCUMENTTYPE.add(temp);
			temp = "";
		} else if ("SIZE".equals(tagName)) {
			dataSet.size.add(temp);
			temp = "";
		} else if ("MIMETYPE".equals(tagName)) {
			dataSet.mimeType.add(temp);
			temp = "";
		} else if (tagName.equals("SUCCESS")) {
			dataSet.SUCCESS = "SUCCESS";
			temp = "";
		}
		super.endElement(uri, localName, qName);
	}

	@Override
	public void endDocument() throws SAXException {
		int difference = dataSet.nameList.size() - dataSet.valueList.size();// VALUE为空时处理
		if (difference > 0) {
			for (int i = 0; i < difference; i++) {
				dataSet.valueList.add("");
			}
		}
		super.endDocument();
	}

}
