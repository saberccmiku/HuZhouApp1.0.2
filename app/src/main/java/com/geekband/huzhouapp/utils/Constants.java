package com.geekband.huzhouapp.utils;

/**
 * Created by Administrator on 2016/5/14
 */
public class Constants {
    //浏览器代理
    public final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36";
    //本地数据库名称
    public final static String NEWS_TABLE = "newsTable.db";
    //是否自动登录标识，contentId
    public final static String AUTO_LOGIN = "autoLogin";
    //用户真实姓名
    public final static String USER_REAL_NAME = "userRealName";
    //裁剪的头像名称
    public final static String AVATAR_IMAGE = "avatarImage";
    //服务器新闻传递标识
    public final static String LOCAL_NEWS_CONTENT = "localNewsContent";
    //用户课程表内容传递标识
    public final static String COURSE_CONTENT = "courseContent";
    //裁剪头像的存储路径
    public static final String AVATAR_URL = "avatarUrl";
    //图片类型
    public static final String IMAGE_TYPE = "image/*";
    public static final String IMAGE_CONTENT_ID = "imageContentId";
    //新闻是否滚动
    public static final String ROLLING = "1";
    public static final String UNROLLING = "0";
    public static final String BIRTHDAY_GIFT = "birthday_gift";

    // 请求返回成功码
    public static String REQUEST_RESULT_SUCCESS = "SUCCESS";
    //通知公告父类id以及序号
    public static final String INFORMATION_PARENT_ID = "A0100020166071447205284156";
    public static final String INFORMATION_SORT = "3";
    public static final String INFORMATION_CONTENT = "information_content";

    //广播
    public static final String GRADE_BROADCAST = "android.intent.action.GRADE_BROADCAST";
    public static final String BIRTHDAY_BROADCAST = "android.intent.action.BIRTHDAY_BROADCAST";
    public static final String MESSAGE_BROADCAST = "android.intent.action.MESSAGE_BROADCAST";
    //记录通知信息日期
    public static final String RECORD_CURRENT_DATE = "isRecordGrade";
    public static final String IS_RECORD_BIRTHDAY = "isRecordBirthday";
    //bs.必修视频 xs.选修视频 bw.必修文档 xw.选修文档
    public static final String BS = "b,s";
    public static final String XS = "x,s";
    public static final String BW = "b,w";
    public static final String XW = "x,w";
    public static final String BS_STR = "必修视频";
    public static final String XS_STR = "选修视频";
    public static final String BW_STR = "必修文档";
    public static final String XW_STR = "选修文档";

    public static final String TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
    public static final String WXZJ_CATEGORIES_ID = "A0100020166081053577090838";

    //创建相册缓存文件名
    public static final String GALLERY_DIRECTORY_NAME = "gallery";
    //创建头像缓存文件名
    public static final String AVATAR_DIRECTORY_NAME = "avatar";
    //应用缓存文件根目录
    public static final String ROOT_DIRECTORY_NAME = "HZGA";

    //通知公告先关参数
    //opinion表"1"审核通过"0"，审核未通过，其他未处理该信息
    public static final String OPINION_PASSED = "1";
    public static final String OPINION_NOT_PASSED = "0";

    //上拉刷新，下拉加载
    public static final int PULL_TO_REFRESH = 1;
    public static final int PULL_TO_LOAD = 2;
    //加载需求意见审核通知
    public static final int REVIEW_INFORMATION = 3;
    //加载需求建议审核结果通知
    public static final int RESULT_INFORMATION = 4;
    //加载学分达标通知
    public static final int GRADE_INFORMATION = 5;
    //加载生日通知
    public static final int BIRTHDAY_INFORMATION = 6;
    //加载生日祝福通知
    public static final int BLESS_INFORMATION = 7;
    //加载完毕
    public static final int LOADED = 8;
    //提交完成
    public static final int COMMITED = 9;
    public static final int UNCOMMIT = 10;

}
