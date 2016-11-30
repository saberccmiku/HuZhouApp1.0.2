package com.geekband.huzhouapp.vo.pojo;

import java.io.File;

public class Document extends File
{
	private static final long serialVersionUID = 5416346761091489740L;
	
	/*public static final String FILE_TYPE_ = "AIFFAUDIO";
	public static final String FILE_TYPE_ = "ASFVIDEO";
	public static final String FILE_TYPE_ = "AVIVIDEO";
	public static final String FILE_TYPE_ = "BASICAUDIO";
	public static final String FILE_TYPE_ = "BINARY";
	public static final String FILE_TYPE_ = "BITMAP";
	public static final String FILE_TYPE_ = "CSS";
	public static final String FILE_TYPE_ = "DCARFT";
	public static final String FILE_TYPE_ = "DCX";
	public static final String FILE_TYPE_ = "FRAMEMAKER";
	public static final String FILE_TYPE_ = "FREELANCE";
	public static final String FILE_TYPE_ = "G3FAX";
	public static final String FILE_TYPE_ = "GIF";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";
	public static final String FILE_TYPE_ = "";*/
	
	public Document(String path)
	{
		super(path);
	}
	
	public String getSuffix()
	{
		if(getPath().lastIndexOf('.')!=-1)
		{
			return getPath().substring(getPath().lastIndexOf('.')+1);
		}
		else
		{
			return null;
		}
	}
	
	public String getFileType()
	{
		return "JEPG";
		//return "";
	}
}
