package com.net.post;

import com.geekband.huzhouapp.vo.pojo.DataSetList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * @author wf
 * @date 2014-4-18
 * @category 解析服务器端返回的文档列表xml文件
 */
public class XmlParseForDocList extends DefaultHandler {
	private String temp = "";
	private String tagName;
	public DataSetList dataSet = new DataSetList();
	private String CurrentDocumentID, CurrentDocumentSize, CurrentMimeType, CurrentDocumentType;

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
		if ("YOUNGDOCUMENT".equals(tagName)) {
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
			// dataSet.DOCUMENTID.add(temp);
			CurrentDocumentID = temp;
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
		} else if ("SIZE".equals(tagName)) {
			dataSet.size.add(temp);
			CurrentDocumentSize = temp;
			if ("FILE".equals(CurrentDocumentType)) {
				dataSet.docSize.add(CurrentDocumentSize);
			} else if ("NOTES".equals(CurrentDocumentType)) {
				int difference = dataSet.docSize.size() - (dataSet.commSize.size() + 1);// VALUE为空时处理
				if (difference > 0) {
					for (int i = 0; i < difference; i++) {
						dataSet.commSize.add("");
					}
				}
				dataSet.commSize.add(CurrentDocumentSize);
			}
			temp = "";
		} else if ("MIMETYPE".equals(tagName)) {
			dataSet.mimeType.add(temp);
			CurrentMimeType = temp;
			if ("FILE".equals(CurrentDocumentType)) {
				dataSet.docMimeType.add(CurrentMimeType);
			} else if ("NOTES".equals(CurrentDocumentType)) {
				int difference = dataSet.docMimeType.size() - (dataSet.commMimeType.size() + 1);// VALUE为空时处理
				if (difference > 0) {
					for (int i = 0; i < difference; i++) {
						dataSet.commMimeType.add("");
					}
				}
				dataSet.commMimeType.add(CurrentMimeType);
			}
			temp = "";
		} else if ("DOCUMENTTYPENAME".equals(tagName)) {
			CurrentDocumentType = temp;
			if ("FILE".equals(temp)) {
				dataSet.DOCUMENTID.add(CurrentDocumentID);
				dataSet.DOCUMENTTYPE.add(temp);
			} else if ("NOTES".equals(temp)) {
				int difference = dataSet.DOCUMENTID.size() - (dataSet.COMMENTID.size() + 1);// VALUE为空时处理
				if (difference > 0) {
					for (int i = 0; i < difference; i++) {
						dataSet.COMMENTID.add("");
						dataSet.DOCUMENTTYPE.add("");
					}
				}
				dataSet.COMMENTID.add(CurrentDocumentID);
				dataSet.DOCUMENTTYPE.add(temp);
			}
			// dataSet.DOCUMENTTYPE.add(temp);
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
		difference = dataSet.DOCUMENTID.size() - (dataSet.COMMENTID.size() + 1);// VALUE为空时处理
		if (difference > 0) {
			for (int i = 0; i < difference; i++) {
				dataSet.COMMENTID.add("");
				dataSet.DOCUMENTTYPE.add("");
			}
		}
		difference = dataSet.docMimeType.size() - (dataSet.commMimeType.size() + 1);// VALUE为空时处理
		if (difference > 0) {
			for (int i = 0; i < difference; i++) {
				dataSet.commMimeType.add("");
			}
		}
		difference = dataSet.docSize.size() - (dataSet.commSize.size() + 1);// VALUE为空时处理
		if (difference > 0) {
			for (int i = 0; i < difference; i++) {
				dataSet.commSize.add("");
			}
		}
		super.endDocument();
	}

}
