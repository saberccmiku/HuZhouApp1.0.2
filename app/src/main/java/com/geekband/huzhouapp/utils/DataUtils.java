package com.geekband.huzhouapp.utils;
import android.os.AsyncTask;
import com.database.dto.DataOperation;
import com.database.pojo.AlbumTable;
import com.database.pojo.BaseTable;
import com.database.pojo.CategoriesTable;
import com.database.pojo.CommonTable;
import com.database.pojo.ContentTable;
import com.database.pojo.CourseTable;
import com.database.pojo.DepartmentsTable;
import com.database.pojo.OpinionTable;
import com.database.pojo.StudyInfoTable;
import com.database.pojo.UserInfoTable;
import com.database.pojo.UserTable;
import com.geekband.huzhouapp.activity.SplashActivity;
import com.geekband.huzhouapp.application.MyApplication;
import com.geekband.huzhouapp.vo.AlbumInfo;
import com.geekband.huzhouapp.vo.BirthdayInfo;
import com.geekband.huzhouapp.vo.CourseInfo;
import com.geekband.huzhouapp.vo.DynamicNews;
import com.geekband.huzhouapp.vo.GradeInfo;
import com.geekband.huzhouapp.vo.NetNewsInfo;
import com.geekband.huzhouapp.vo.UserBaseInfo;
import com.lidroid.xutils.exception.DbException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/25
 */
public class DataUtils {
    /**
     * @return 分页获取本地新闻信息
     */
    public static ArrayList<DynamicNews> getNewsList(String isRolling, int pageSize, int currentPage) {
        ArrayList<DynamicNews> localNewsList = new ArrayList<>();
        //查询分类表
        Map<String, String> newsParentCategory = new HashMap<>();
        newsParentCategory.put(CategoriesTable.FIELD_PARENTID, "A0100020166071445164939060");
        newsParentCategory.put(CategoriesTable.FIELD_SORT, "1");
        //noinspection unchecked
        ArrayList<CategoriesTable> categoriesTables = (ArrayList<CategoriesTable>) DataOperation.
                queryTable(CategoriesTable.TABLE_NAME, newsParentCategory);
        if (categoriesTables != null && categoriesTables.size() != 0) {
            String newsId = categoriesTables.get(0).getContentId();
            //在通用信息表里面匹配新闻
            Map<String, String> newsCategory = new HashMap<>();
            newsCategory.put(CommonTable.FIELD_CATEGORYID, newsId);
            if (isRolling.equals("1")) {
                newsCategory.put(CommonTable.FIELD_ISROLLING, isRolling);
            }
            newsCategory.put(CommonTable.FIELD_ISPASSED, "1");
            newsCategory.put(CommonTable.FIELD_ISPUBLISH, "1");

            //noinspection unchecked
            ArrayList<CommonTable> commonTables = (ArrayList<CommonTable>) DataOperation.queryTable(CommonTable.TABLE_NAME, currentPage, pageSize, newsCategory, CommonTable.FIELD_DATETIME);

            if (commonTables != null) {
                for (int i = 0; i < commonTables.size(); i++) {
                    DynamicNews localNews = new DynamicNews();
                    String title = commonTables.get(i).getField(CommonTable.FIELD_TITLE);
                    String writerId = commonTables.get(i).getField(CommonTable.FIELD_WRITERID);
                    //noinspection unchecked
                    ArrayList<UserTable> writerTables = (ArrayList<UserTable>) DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, writerId);
                    String writer = "";
                    String departmentName = "";
                    if (writerTables != null && writerTables.size() != 0) {
                        writer = writerTables.get(0).getField(UserTable.FIELD_REALNAME);
                        String departmentsId = writerTables.get(0).getField(UserTable.FIELD_DEPARTMENTNO);
                        if (departmentsId != null) {
                            //noinspection unchecked
                            ArrayList<BaseTable> departmentsTables = (ArrayList<BaseTable>) DataOperation.queryTable(DepartmentsTable.TABLE_NAME, DepartmentsTable.CONTENTID, departmentsId);
                            if (departmentsTables != null && departmentsTables.size() != 0) {
                                departmentName = departmentsTables.get(0).getField(DepartmentsTable.FIELD_DEPARTMENTNAME);

                            }
                        }
                    }
                    String date = commonTables.get(i).getField(CommonTable.FIELD_DATETIME).substring(0, 10);
                    String picUrl = null;
                    if (isRolling.equals("0")) {
                        ArrayList<String> picUrls = (ArrayList<String>) commonTables.get(i).getAccessaryFileUrlList();
                        if (picUrls.size() != 0) {
                            picUrl = picUrls.get(0);
                        }
                    } else if (isRolling.equals("1")) {
                        picUrl = "http://" + com.oa.util.Constants.CONNIP + commonTables.get(i).getField(CommonTable.FIELD_PICURL);
                    }
                    String contentID = commonTables.get(i).getContentId();

                    //根据contentId获取新闻内容
                    String content = null;
                    //noinspection unchecked
                    ArrayList<ContentTable> contentTables = (ArrayList<ContentTable>) DataOperation.queryTable(ContentTable.TABLE_NAME, ContentTable.FIELD_NEWSID, contentID);
//                System.out.println("测试新闻内容图片"+contentTables);
                    if (contentTables != null && contentTables.size() != 0) {
                        content = contentTables.get(0).getField(ContentTable.FIELD_SUBSTANCE);
                        //将获取的新闻信息放入本地LocalNews
                    }
                    localNews.setTitle(title);
                    localNews.setWriter(writer);
                    localNews.setDepartmentName(departmentName);
                    localNews.setPicUrl(picUrl);
                    localNews.setDate(date);
                    localNews.setContent(content);
                    localNews.setId(i);
                    localNewsList.add(localNews);
                }
            }
        }

        return localNewsList;
    }

    /**
     * @param url 新闻地址
     * @return 新闻信息集合
     */
    public static ArrayList<NetNewsInfo> loadNetNews(String url) {
        ArrayList<NetNewsInfo> newsList = new ArrayList<>();
        String userAgent = Constants.USER_AGENT;
        try {
            Document document = Jsoup.connect(url).userAgent(userAgent).timeout(5000).get();
            Elements newsElements = document.select("div.at");
            for (int i = 1; i < newsElements.size(); i++) {
                Elements titleElements = newsElements.get(i).getElementsByTag("h3");
                String newsTitle = titleElements.text();
                Elements picElements = newsElements.get(i).getElementsByTag("img");
                String newsHTML = newsElements.get(i).attr("href");
                String newsPic = picElements.attr("src");
                //消除部分图片乱码
                newsPic = newsPic.substring(0, newsPic.lastIndexOf(".")) + ".jpg";
                NetNewsInfo newsInfo = new NetNewsInfo();
                newsInfo.setNewsTitle(newsTitle);
                newsInfo.setNewsPic(newsPic);
                newsInfo.setNewsHTML(newsHTML);
                newsInfo.setId(i);
                newsList.add(newsInfo);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return newsList;
    }


    /**
     * 保存网络新闻到本地数据库
     */
    public static void saveNetNews() {
        new NetNewsTask().execute();
    }

    static class NetNewsTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            ArrayList<NetNewsInfo> netNewsInfoList = loadNetNews("http://news.sohu.com/photo/");
            if (netNewsInfoList != null) {
                try {
                    MyApplication.sDbUtils.deleteAll(NetNewsInfo.class);
                    MyApplication.sDbUtils.saveAll(netNewsInfoList);
                } catch (DbException e) {
                    e.printStackTrace();//无本地数据
                }
            }
            return null;
        }

    }

    /**
     * 保存基本用户信息
     */

    public static void saveUserBaseInfo(String contentId) {
        new BaseInfoTsk().execute(contentId);
    }

    static class BaseInfoTsk extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            String contentId = params[0];
            UserBaseInfo userBaseInfo = new UserBaseInfo();

            //noinspection unchecked
            ArrayList<UserTable> userTables = (ArrayList<UserTable>) DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, contentId);
            if (userTables != null && userTables.size() != 0) {
                UserTable userTable = userTables.get(0);
                if (userTable != null) {
                    int id = 0;
                    String userName = userTable.getField(UserTable.FIELD_USERNAME);
                    String realName = userTable.getField(UserTable.FIELD_REALNAME);
                    String phoneNum = userTable.getField(UserTable.FIELD_TELEPHONE);
                    String emailAddress = userTable.getField(UserTable.FIELD_EMAIL);
                    String IDcard = userTable.getField(UserTable.FIELD_IDCARDNO);

                    String sex = userTable.getField(UserTable.FIELD_SEX);
                    String policeNum = userTable.getField(UserTable.FIELD_POLICENUM);
                    String education = userTable.getField(UserTable.FIELD_EDUCATION);
                    String toWorkTime = userTable.getField(UserTable.FIELD_TOWORKTIME);
                    String toPoliceTime = userTable.getField(UserTable.FIELD_TOPOLICETIME);
                    String rank = userTable.getField(UserTable.FIELD_RANK);
                    String policePost = userTable.getField(UserTable.FIELD_POLICEPOST);


                    userBaseInfo.setId(id);
                    userBaseInfo.setUserName(userName);
                    userBaseInfo.setContentId(contentId);
                    userBaseInfo.setRealName(realName);
                    userBaseInfo.setPhoneNum(phoneNum);
                    userBaseInfo.setIDcard(IDcard);
                    userBaseInfo.setEmailAddress(emailAddress);
                    userBaseInfo.setSex(sex);
                    userBaseInfo.setPoliceNum(policeNum);
                    userBaseInfo.setEducation(education);
                    userBaseInfo.setToWorkTime(toWorkTime);
                    userBaseInfo.setToPoliceTime(toPoliceTime);
                    userBaseInfo.setRank(rank);
                    userBaseInfo.setPolicePost(policePost);

                    //System.out.println("Data_userBaseInfo:"+userBaseInfo);

                    try {
                        MyApplication.sDbUtils.deleteAll(UserBaseInfo.class);
                        MyApplication.sDbUtils.save(userBaseInfo);

                    } catch (DbException e) {
                        e.printStackTrace();
                    }


                }
            }

            return null;
        }
    }


    /**
     * 保存相册信息
     */
    public static void saveAlbum(String contentId) {
        new AlbumTask().execute(contentId);
    }

    static class AlbumTask extends AsyncTask<String, Integer, Integer> {
        ArrayList<AlbumTable> mAlbumTableList;
        //相册基本信息
        ArrayList<AlbumInfo> mAlbumInfoList;

        @Override
        protected Integer doInBackground(String... params) {
            //noinspection unchecked
            mAlbumTableList = (ArrayList<AlbumTable>) DataOperation.queryTable(AlbumTable.TABLE_NAME, AlbumTable.FIELD_USERID, params[0]);
            //相册信息列表
            mAlbumInfoList = new ArrayList<>();

            if (mAlbumTableList != null) {
                for (int i = 0; i < mAlbumTableList.size(); i++) {
                    //获取单个相册及其名称和所包含的相册数量
                    AlbumTable albumTable = mAlbumTableList.get(i);
                    String albumName = albumTable.getField(AlbumTable.FIELD_NAME);
                    String contentId = albumTable.getContentId();
                    List<String> phoUrlList = albumTable.getAccessaryFileUrlList();
                    int albumCount = phoUrlList.size();
                    AlbumInfo albumInfo = new AlbumInfo();
                    //相册封面默认第一张
                    if (phoUrlList.size() != 0) {//当当前相册没有图片时不予展示
                        albumInfo.setAlbumUrl(phoUrlList.get(0));
                        albumInfo.setAlbumName(albumName);
                        albumInfo.setAlbumCount(albumCount);
                        albumInfo.setContentId(contentId);
                        albumInfo.setId(i);
                        mAlbumInfoList.add(albumInfo);
                    }
                }

                try {
                    MyApplication.sDbUtils.deleteAll(AlbumInfo.class);
                    MyApplication.sDbUtils.saveAll(mAlbumInfoList);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                return 1;//有相册
            } else {
                return 2;//无相册
            }

        }

    }

    /**
     * 查询课程信息
     */
    public static void saveCourse(String contentId) {
        new CourseTask().execute(contentId);
    }

    static class CourseTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            ArrayList<CourseInfo> courseList = new ArrayList<>();
            //noinspection unchecked
            ArrayList<StudyInfoTable> StudyInfoTables = (ArrayList<StudyInfoTable>) DataOperation.queryTable(StudyInfoTable.TABLE_NAME, StudyInfoTable.FIELD_USERNO, params[0]);
            if (StudyInfoTables != null) {
                for (int i = 0; i < StudyInfoTables.size(); i++) {
                    String courseNo = StudyInfoTables.get(i).getField(StudyInfoTable.FIELD_COURSENO);
                    //noinspection unchecked
                    ArrayList<CourseTable> courseTables = (ArrayList<CourseTable>) DataOperation.queryTable(CourseTable.TABLE_NAME, CourseTable.CONTENTID, courseNo);
                    if (courseTables != null && courseTables.size() != 0) {
                        CourseTable courseTable = courseTables.get(0);
                        if (courseTable != null) {
                            CourseInfo courseInfo = new CourseInfo();
                            //课程名
                            String title = courseTable.getField(CourseTable.FIELD_COURSENAME);
                            //选修必修
                            String type = courseTable.getField(CourseTable.FIELD_COURSETYPE);

                            //bs.必修视频 xs.选修视频 bw.必修文档 xw.选修文档
                            String typeStr = "";
                            switch (type) {
                                case Constants.BS:
                                    typeStr = Constants.BS_STR;
                                    break;
                                case Constants.XS:
                                    typeStr = Constants.XS_STR;
                                    break;
                                case Constants.BW:
                                    typeStr = Constants.BW_STR;
                                    break;
                                case Constants.XW:
                                    typeStr = Constants.XW_STR;
                                    break;
                            }

                            //课程简介
                            String intro = courseTable.getField(CourseTable.FIELD_COURSEINTRO);
                            //详细内容
                            String detailed = courseTable.getField(CourseTable.FIELD_DETAILED);
                            //积分
                            String point = courseTable.getField(CourseTable.FIELD_POINT);
                            //学习时长
                            String time = courseTable.getField(CourseTable.FIELD_NEEDTIME);

                            courseInfo.setId(i);
                            courseInfo.setTitle(title);
                            courseInfo.setType(typeStr);
                            courseInfo.setPoint(point);
                            courseInfo.setIntro(intro);
                            courseInfo.setTime(time);
                            courseInfo.setDetailed(detailed);
                            courseList.add(courseInfo);
                        }
                    }
                }
                //保存到本地
                try {
                    MyApplication.sDbUtils.deleteAll(CourseInfo.class);
                    MyApplication.sDbUtils.saveAll(courseList);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    /**
     * 获取个人课程
     */
    public static ArrayList<CourseInfo> bindCourseInfo() {
        ArrayList<CourseInfo> courseList = new ArrayList<>();
        //noinspection unchecked
        ArrayList<StudyInfoTable> StudyInfoTables = (ArrayList<StudyInfoTable>) DataOperation.
                queryTable(StudyInfoTable.TABLE_NAME, StudyInfoTable.FIELD_USERNO
                        , MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN, null));
        if (StudyInfoTables != null) {
            for (int i = 0; i < StudyInfoTables.size(); i++) {
                String courseNo = StudyInfoTables.get(i).getField(StudyInfoTable.FIELD_COURSENO);
                //noinspection unchecked
                ArrayList<CourseTable> courseTables = (ArrayList<CourseTable>) DataOperation.queryTable(CourseTable.TABLE_NAME, CourseTable.CONTENTID, courseNo);
                if (courseTables != null && courseTables.size() != 0) {
                    CourseTable courseTable = courseTables.get(0);
                    if (courseTable != null) {
                        CourseInfo courseInfo = new CourseInfo();
                        //课程名
                        String title = courseTable.getField(CourseTable.FIELD_COURSENAME);
                        //选修必修
                        String type = courseTable.getField(CourseTable.FIELD_COURSETYPE);

                        //bs.必修视频 xs.选修视频 bw.必修文档 xw.选修文档
                        String typeStr = "";
                        switch (type) {
                            case Constants.BS:
                                typeStr = Constants.BS_STR;
                                break;
                            case Constants.XS:
                                typeStr = Constants.XS_STR;
                                break;
                            case Constants.BW:
                                typeStr = Constants.BW_STR;
                                break;
                            case Constants.XW:
                                typeStr = Constants.XW_STR;
                                break;
                        }

                        //课程简介
                        String intro = courseTable.getField(CourseTable.FIELD_COURSEINTRO);
                        //详细内容
                        String detailed = courseTable.getField(CourseTable.FIELD_DETAILED);
                        //积分
                        String point = courseTable.getField(CourseTable.FIELD_POINT);
                        //学习时长
                        String time = courseTable.getField(CourseTable.FIELD_NEEDTIME);

                        courseInfo.setId(i + 1);
                        courseInfo.setTitle(title);
                        courseInfo.setType(typeStr);
                        courseInfo.setPoint(point);
                        courseInfo.setIntro(intro);
                        courseInfo.setTime(time);
                        courseInfo.setDetailed(detailed);
                        courseList.add(courseInfo);
                    }
                }
            }
        }
        return courseList;
    }

    /**
     * 获取发布的课程
     */

    public static ArrayList<CourseTable> getAllCourses() {
        Map<String, String> keyMap = new HashMap<>();
        keyMap.put(CourseTable.FIELD_ISDELETED,"0");//"0"表示不删除
        //noinspection unchecked
        return (ArrayList<CourseTable>) DataOperation.queryTable(CourseTable.TABLE_NAME,keyMap,CourseTable.FIELD_COURSETIME);
    }


    /**
     * 获取生日在当前日期内的所有用户真实姓名及其生日
     */

    public static ArrayList<BirthdayInfo> getBirthdayInfo(int pageSize, int currentPage) {

        ArrayList<BirthdayInfo> arrayList = new ArrayList<>();
//        String sql = "select * from (select e.* ,rownum rn from (" +
//                "select * from USERS where IDCARDNO is not null ) e) where rn>=" + start + " and rn<=" + end + " ";
        String date = FileUtils.getCurrentTimeStr("MMdd");
        String sql = "from(select * from users where IdCardNo like '%" + date + "%')users";
        //noinspection unchecked
        ArrayList<UserTable> userTables = (ArrayList<UserTable>) DataOperation.queryTable(UserTable.TABLE_NAME, sql, currentPage, pageSize);
        if (userTables != null && userTables.size() != 0) {
            for (UserTable userTable : userTables) {
                String userId = userTable.getContentId();
                //真实姓名
                String realName = userTable.getField(UserTable.FIELD_REALNAME);
                //生日
                String idCardNum = userTable.getField(UserInfoTable.FIELD_IDCARDNO);
                String birthdayStr = null;
                if (idCardNum != null && idCardNum.length() == 18) {
                    birthdayStr = idCardNum.substring(10, 14);
                }
                //头像地址
                ArrayList<String> avatarUrls = (ArrayList<String>) userTable.getAccessaryFileUrlList();
                String avatarUrl = null;
                if (avatarUrls != null && avatarUrls.size() != 0) {
                    avatarUrl = avatarUrls.get(0);
                }
                BirthdayInfo birthdayInfo = new BirthdayInfo();
                birthdayInfo.setUserId(userId);
                birthdayInfo.setRealName(realName);
                birthdayInfo.setDate(birthdayStr);
                birthdayInfo.setAvatarImage(avatarUrl);
                arrayList.add(birthdayInfo);

            }
        }
        return arrayList;
    }

    //查询所有今天过生日的用户
    public static ArrayList<BirthdayInfo> getBirthdayInfo() {

        ArrayList<BirthdayInfo> arrayList = new ArrayList<>();
//        String sql = "select * from (select e.* ,rownum rn from (" +
//                "select * from USERS where IDCARDNO is not null ) e) where rn>=" + start + " and rn<=" + end + " ";
        String date = FileUtils.getCurrentTimeStr("MMdd");
        String sql = "from(select * from users where IdCardNo like '%" + date + "%')users";
        //noinspection unchecked
        ArrayList<UserTable> userTables = (ArrayList<UserTable>) DataOperation.queryTable(UserTable.TABLE_NAME, sql);
        if (userTables != null && userTables.size() != 0) {
            for (UserTable userTable : userTables) {
                String userId = userTable.getContentId();
                //真实姓名
                String realName = userTable.getField(UserTable.FIELD_REALNAME);
                //生日
                String idCardNum = userTable.getField(UserInfoTable.FIELD_IDCARDNO);
                String birthdayStr = null;
                if (idCardNum != null && idCardNum.length() == 18) {
                    birthdayStr = idCardNum.substring(10, 14);
                }
                //头像地址
                ArrayList<String> avatarUrls = (ArrayList<String>) userTable.getAccessaryFileUrlList();
                String avatarUrl = null;
                if (avatarUrls != null && avatarUrls.size() != 0) {
                    avatarUrl = avatarUrls.get(0);
                }
                BirthdayInfo birthdayInfo = new BirthdayInfo();
                birthdayInfo.setUserId(userId);
                birthdayInfo.setRealName(realName);
                birthdayInfo.setDate(birthdayStr);
                birthdayInfo.setAvatarImage(avatarUrl);
                arrayList.add(birthdayInfo);

            }
        }
        return arrayList;
    }

    //计算日期间隔
    public static String getDays(String userBirthday) {
        long days = 0;
        try {
            Date date = new Date();
            String currentDateStr = new SimpleDateFormat("MMdd").format(date);
            Date currentDate = new SimpleDateFormat("MMdd").parse(currentDateStr);
            Date birthday = new SimpleDateFormat("MMdd").parse(userBirthday);
            days = (birthday.getTime() - currentDate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(days);
    }


    /**
     * 获取学习信息
     *
     * @param contentId
     * @return
     */
    public static GradeInfo getGrade(String contentId) {
        int id = 0;
        //获取需修学分
        int needScore = DataOperation.queryUserNeedScore(contentId);
        //获取已修学分
        int currentScore = DataOperation.queryUserCurrentScore(contentId);
        //必修课程
        //选修课程
        GradeInfo gradeInfo = new GradeInfo();
        gradeInfo.setNeedGrade(String.valueOf(needScore));
        gradeInfo.setAlreadyGrade(String.valueOf(currentScore));
        gradeInfo.setId(id);

        return gradeInfo;
    }




    //定义一个根据图片url获取InputStream的方法
    public static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024]; // 用数据装
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            out.write(buffer, 0, len);
            //int downedFileLength = len;
        }
        out.close();
        // 关闭流一定要记得。
        return out.toByteArray();
    }

    /**
     * 通知公告
     *
     * @param currentPage 当前页码
     * @param pageSize    当前页面数据条数
     * @return 数据集合
     */
    public static ArrayList<DynamicNews> getInformationInfo(int currentPage, int pageSize) {
        ArrayList<DynamicNews> arrayList = new ArrayList<>();
        //通知公告
        Map<String, String> informationCategory = new HashMap<>();
        informationCategory.put(CategoriesTable.FIELD_PARENTID, Constants.INFORMATION_PARENT_ID);
        informationCategory.put(CategoriesTable.FIELD_SORT, Constants.INFORMATION_SORT);
        //noinspection unchecked
        ArrayList<CategoriesTable> categoriesTables = (ArrayList<CategoriesTable>) DataOperation.queryTable(CategoriesTable.TABLE_NAME, informationCategory);
        if (categoriesTables != null && categoriesTables.size() != 0) {
            String informationId = categoriesTables.get(0).getContentId();
            //System.out.println(informationId);
            //noinspection unchecked
            ArrayList<CommonTable> commonTables = (ArrayList<CommonTable>) DataOperation.queryTable(CommonTable.TABLE_NAME, CommonTable.FIELD_CATEGORYID, currentPage, pageSize, informationId, CommonTable.FIELD_DATETIME);
            //System.out.println(commonTables);
            if (commonTables != null && commonTables.size() != 0) {

                for (int i = 0; i < commonTables.size(); i++) {
                    DynamicNews dynamicNews = new DynamicNews();
                    String title = commonTables.get(i).getField(CommonTable.FIELD_TITLE);
                    String writerId = commonTables.get(i).getField(CommonTable.FIELD_WRITERID);
                    //noinspection unchecked
                    ArrayList<UserTable> writerTables = (ArrayList<UserTable>) DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, writerId);
                    String writer = "";
                    String departmentName = "";
                    if (writerTables != null && writerTables.size() != 0) {
                        writer = writerTables.get(0).getField(UserTable.FIELD_REALNAME);
                        String departmentNo = writerTables.get(0).getField(UserTable.FIELD_DEPARTMENTNO);
                        //noinspection unchecked
                        ArrayList<BaseTable> departmentsTables = (ArrayList<BaseTable>) DataOperation.
                                queryTable(DepartmentsTable.TABLE_NAME, DepartmentsTable.CONTENTID, departmentNo);
                        if (departmentsTables != null && departmentsTables.size() != 0) {
                            departmentName = departmentsTables.get(0).getField(DepartmentsTable.FIELD_DEPARTMENTNAME);
                        }

                        String date = commonTables.get(i).getField(CommonTable.FIELD_DATETIME).substring(0, 10);

                        String contentID = commonTables.get(i).getContentId();

                        //根据contentId获取新闻内容
                        String content = null;
                        //noinspection unchecked
                        ArrayList<ContentTable> contentTables = (ArrayList<ContentTable>) DataOperation.
                                queryTable(ContentTable.TABLE_NAME, ContentTable.FIELD_NEWSID, contentID);
                        if (contentTables != null && contentTables.size() != 0) {
                            content = contentTables.get(0).getField(ContentTable.FIELD_SUBSTANCE);
                        }
                        dynamicNews.setTitle(title);
                        dynamicNews.setWriter(writer);
                        dynamicNews.setDepartmentName(departmentName);
                        dynamicNews.setDate(date);
                        dynamicNews.setContent(content);
                        dynamicNews.setId(i);
                        arrayList.add(dynamicNews);
                    }
                }
            }
        }
        return arrayList;
    }

    /**
     * 获取所有用户信息
     *
     * @param currentPage 当前页码
     * @param pageSize    当前数据条数
     * @return 用户信息合
     */
    public static ArrayList<UserBaseInfo> getUserInfo(int currentPage, int pageSize) {
        ArrayList<UserBaseInfo> userBaseInfos = new ArrayList<>();
        //noinspection unchecked
        ArrayList<UserTable> userTables = (ArrayList<UserTable>) DataOperation.queryTable(UserTable.TABLE_NAME, currentPage, pageSize, (Map) null);
        if (userTables != null && userTables.size() != 0) {
            for (int i = 0; i < userTables.size(); i++) {
                String userId = userTables.get(i).getContentId();
                //noinspection unchecked
                ArrayList<UserInfoTable> userInfoTables = (ArrayList<UserInfoTable>) DataOperation.queryTable(UserInfoTable.TABLE_NAME, UserInfoTable.FIELD_USERID, userId);
                if (userInfoTables != null && userInfoTables.size() != 0) {
                    UserBaseInfo userBaseInfo = new UserBaseInfo();
                    UserInfoTable userInfoTable = userInfoTables.get(0);
                    userBaseInfo.setId(i);
                    String avatarUrl;
                    ArrayList<String> avatarUrls = (ArrayList<String>) userTables.get(i).getAccessaryFileUrlList();
                    if (avatarUrls != null && avatarUrls.size() != 0) {
                        avatarUrl = avatarUrls.get(0);
                    } else {
                        avatarUrl = "";
                    }
                    userBaseInfo.setAvatarUrl(avatarUrl);
                    userBaseInfo.setRealName(userTables.get(i).getField(UserTable.FIELD_REALNAME));
                    userBaseInfo.setSex(userInfoTable.getField(UserInfoTable.FIELD_SEX));
                    userBaseInfo.setPhoneNum(userTables.get(i).getField(UserTable.FIELD_TELEPHONE));

                    userBaseInfos.add(userBaseInfo);
                }
            }
        }
        return userBaseInfos;
    }

    /**
     * 收到的好友祝福
     *
     * @return
     */
    public static ArrayList<DynamicNews> getGiftInfo() {
        ArrayList<DynamicNews> dynamicNewses = new ArrayList<>();
        String userId = MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN, null);
        Map<String, String> map = new HashMap<>();
        map.put("AUDITOR", userId);
        map.put("ISPASSED", "0");
        ArrayList<CommonTable> commonTables = (ArrayList<CommonTable>) DataOperation.queryTable(CommonTable.TABLE_NAME, map, "DATETIME");

        if (commonTables != null && commonTables.size() != 0) {
            for (int i = 0; i < commonTables.size(); i++) {
                String contentId = commonTables.get(i).getContentId();
                String senderId = commonTables.get(i).getField(CommonTable.FIELD_WRITERID);
                String senderName = null;
                String senderPicUrl = null;
                ArrayList<UserTable> userTables = (ArrayList<UserTable>) DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, senderId);


                if (userTables != null && userTables.size() != 0) {
                    senderName = userTables.get(0).getField(UserTable.FIELD_REALNAME);
                    ArrayList<String> senderPicUrls = (ArrayList<String>) userTables.get(0).getAccessaryFileUrlList();
                    if (senderPicUrls != null && senderPicUrls.size() != 0) {
                        senderPicUrl = senderPicUrls.get(0);
                    }
                }

                ArrayList<ContentTable> contentTables = (ArrayList<ContentTable>) DataOperation.queryTable(ContentTable.TABLE_NAME, ContentTable.FIELD_NEWSID, contentId);


                if (contentTables != null && contentTables.size() != 0) {
                    for (int j = 0; j < contentTables.size(); j++) {
                        DynamicNews dynamicNews = new DynamicNews();
                        dynamicNews.setContentId(commonTables.get(j).getContentId());
                        dynamicNews.setWriter(senderName);
                        dynamicNews.setPicUrl(senderPicUrl);
                        dynamicNews.setContent(contentTables.get(j).getField(ContentTable.FIELD_SUBSTANCE));
                        dynamicNewses.add(dynamicNews);
                    }
                }
            }
        }

        return dynamicNewses;
    }

    //需求意见审核通知
    public static ArrayList<?> getToDoOpinions() {
        String userId = MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN, null);
        String sql = "from (select * from opinion where isPassed ='999' and auditor = '" + userId + "' order by postTime DESC) opinion";
        //noinspection unchecked
        return DataOperation.queryTable(OpinionTable.TABLE_NAME, sql);
    }

    //需求建议审核结果通知
    public static ArrayList<?> getOpinionsInformation() {
        String userId = MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN, null);
        String sql = "from (select * from opinion where userId = '" + userId + "'and isPassed !='999' and isPassed is not null order by passTime DESC) opinion";
        return DataOperation.queryTable(OpinionTable.TABLE_NAME, sql);
    }

    //需求意见内容
    public static ArrayList<?> getOpinionContent(OpinionTable opinionTable) {
        String contentId = opinionTable.getContentId();
        return DataOperation.queryTable(ContentTable.TABLE_NAME, ContentTable.FIELD_NEWSID, contentId);
    }

}
