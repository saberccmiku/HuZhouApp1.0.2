package com.oa.util;

import android.os.Environment;

public class Constants {

	//登录系统
	public static String USERID = "docadmin";
	public static String PSWID = "passw0rd";

//	public static final String CONNIP = "172.18.18.73:8974";//lmc本地测试用服务器
	public static final String CONNIP = "192.168.0.14:9080";//lmc本地测试用服务器
	public static final String FILE_PATH = "/IDOC/service/file/";

	public static final String PATH = "/IDOC/WebService";// 服务器路径

	public static final String IMGPATH = "/IDOC/service/file/";
	public static final String imageCachePath = "/XNGA/ImageCache/"; // 图片缓存路径
	public static final String fileCachePath = "/XNGA/FileCache/"; // 文件缓存路径

	public static final String WEB_IP = "http://222.73.156.26:9080/weixinPublicPlatform/";

	// 下载文件的本地路径
	public static String localPath = Environment.getExternalStorageDirectory().getPath() + "/XNGA/files/";
	// 下载APK文件的本地路径
	public static String localAPKPath = Environment.getExternalStorageDirectory().getPath() + "/XNGA/";

	public static String REQUEST_RESULT_SUCCESS = "SUCCESS"; // 请求返回成功码

	public static int PHOTOHRAPH = 1;// 拍照
	public static int PHOTOZOOM = 2; // 缩放
	public static int PHOTORESULT = 3;// 结果
	public static String PHOTOTYPE = "image/*";


}
