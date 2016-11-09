package com.net.post;

public class DocInfor
{
	private String contentId;
	private String contentName;

	public DocInfor()
	{
		super();
	}

	public DocInfor(String contentId, String contentName)
	{
		super();
		this.contentId = contentId;
		this.contentName = contentName;
	}

	public String getContentId()
	{
		return contentId;
	}

	public void setContentId(String contentId)
	{
		this.contentId = contentId;
	}

	public String getContentName()
	{
		return contentName;
	}

	public void setContentName(String contentName)
	{
		this.contentName = contentName;
	}

}
