package com.geekband.huzhouapp.utils;

import com.geekband.huzhouapp.vo.pojo.AppVersionTable;
import com.geekband.huzhouapp.vo.pojo.BaseTable;
import com.geekband.huzhouapp.vo.pojo.CourseTable;
import com.geekband.huzhouapp.vo.pojo.DataSetList;
import com.geekband.huzhouapp.vo.pojo.Document;
import com.geekband.huzhouapp.vo.pojo.StudyInfoTable;
import com.geekband.huzhouapp.vo.pojo.StudyScoreTable;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class DataOperation {
    private DataOperation() {
    }

    /**
     * 获取用户已修学分
     *
     * @return
     * @throws Exception
     */
    public static int queryUserCurrentScore(String contentId) {
        int score = -1;
        List<StudyInfoTable> studyInfoTableList = (List<StudyInfoTable>) queryTable(StudyInfoTable.TABLE_NAME, StudyInfoTable.FIELD_USERNO, contentId);
        if (studyInfoTableList != null) {
            for (StudyInfoTable studyInfoTable : studyInfoTableList) {
                if (studyInfoTable != null) {
                    CourseTable courseTable = (CourseTable) queryTable(CourseTable.TABLE_NAME, CourseTable.CONTENTID, studyInfoTable.getField(StudyInfoTable.FIELD_COURSENO)).get(0);
                    if (courseTable != null) {
                        score += Integer.parseInt(courseTable.getField(CourseTable.FIELD_POINT));
                    }
                }
            }
        }

        return score;
    }

    /**
     * 获取用户当前需修学分
     *
     * @param userNo
     * @return
     * @throws Exception
     */
    public static int queryUserNeedScore(String userNo) {
        int needScore = -1;
        long currentTime = System.currentTimeMillis();

        ArrayList<StudyScoreTable> studyScoreDataList = (ArrayList<StudyScoreTable>) queryTable(StudyScoreTable.TABLE_NAME, StudyScoreTable.FIELD_USERNO, userNo);
        if (studyScoreDataList != null) {
            for (StudyScoreTable studyScoreTable : studyScoreDataList) {
                if (studyScoreTable != null) {
                    //按照服务器端的时间格式 格式化字符串 得到int格式的时间(这里的格式化方式需要手动与服务器端保持同步)
                    String timePeriod = studyScoreTable.getField(StudyScoreTable.FIELD_TIMEPERIOD);
                    String startTimeStr = timePeriod.substring(0, 10);
                    String endTimeStr = timePeriod.substring(11, 21);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        long startTime = sdf.parse(startTimeStr).getTime();
                        long endTime = sdf.parse(endTimeStr).getTime();

                        if (currentTime > startTime && currentTime < endTime) {
                            needScore = Integer.parseInt(studyScoreTable.getField(StudyScoreTable.FIELD_NEEDSCORE));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return needScore;
    }

    /**
     * 应用程序是否有新版本发布
     *
     * @param currentVersionCode 程序当前版本码
     * @return 为null时，表示无新版本发布；不为null时，返回值则为当前最新版本相关数据
     */
    public static AppVersionTable isHasUpdate(int currentVersionCode) {
        try {
            int newestVersionCode = -1; //服务器当前最新版本的版本码

            //获取服务器当前最新版本码
            ArrayList<AppVersionTable> versionList = (ArrayList<AppVersionTable>) queryTable(AppVersionTable.TABLE_NAME);
            int newestVersionIndex = 0;
            try {
                newestVersionCode = Integer.parseInt(versionList.get(newestVersionIndex).getField(AppVersionTable.FIELD_VC));
            } catch (NumberFormatException e) //(NumberFormatException异常不影响最新版本判断的继续进行)
            {
                e.printStackTrace();
            }
            for (int i = 0; i < versionList.size(); i++) {
                try {
                    int versionCode = Integer.parseInt(versionList.get(i).getField(AppVersionTable.FIELD_VC));
                    if (newestVersionCode < versionCode) {
                        newestVersionCode = versionCode;
                        newestVersionIndex = i;
                    }
                    ;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            //判断当前版本是否为最新版本
            if (currentVersionCode < newestVersionCode) {
                return versionList.get(newestVersionIndex); //若当前版本小于最新版本，则返回最新版本
            }
        } catch (Exception e) //(被该处catch捕获的任何异常都会导致最新版本判断无法正常进行)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 根据表名，获取所有表记录；用于无需查询条件的查询
     *
     * @param tableName
     * @return
     * @throws Exception
     */
    public static ArrayList<?> queryTable(String tableName) {
        return queryTable(tableName, -1, -1);
    }

    public static ArrayList<?> queryTable(String tableName, int currentPage, int pageSize) {
        return queryTable(tableName, currentPage, pageSize, (HashMap<String, String>) null);
    }

    /**
     * 根据表名，获取符合条件的表记录；用于以单个字段为查询条件的简单查询
     *
     * @param tableName
     * @param fieldName
     * @param fieldValue
     * @return
     * @throws Exception
     */
    public static ArrayList<?> queryTable(String tableName, String fieldName, String fieldValue) {
        return queryTable(tableName, fieldName, -1, -1, fieldValue);
    }

    public static ArrayList<?> queryTable(String tableName, String fieldName, String fieldValue,String order) {
        return queryTable(tableName, fieldName, -1, -1, fieldValue,order);
    }

    public static ArrayList<?> queryTable(String tableName, String fieldName, int currentPage, int pageSize, String fieldValue) {
        HashMap<String, String> fieldList = new HashMap<>();
        fieldList.put(fieldName, fieldValue);
        return queryTable(tableName, currentPage, pageSize, fieldList);
    }

    public static ArrayList<?> queryTable(String tableName, String fieldName, int currentPage, int pageSize, String fieldValue,String order) {
        HashMap<String, String> fieldList = new HashMap<>();
        fieldList.put(fieldName, fieldValue);
        return queryTable(tableName, currentPage, pageSize, fieldList,order);
    }

    /**
     * 根据表名，获取符合条件的表记录；用于以多个字段为查询条件的简单查询
     *
     * @param tableName
     * @param fieldList
     * @return
     * @throws Exception
     */
    public static ArrayList<?> queryTable(String tableName, Map<String, String> fieldList) {
        return queryTable(tableName, -1, -1, fieldList);
    }
    public static ArrayList<?> queryTable(String tableName, Map<String, String> fieldList,String orderStr) {
        return queryTable(tableName, -1, -1, fieldList,orderStr);
    }

//new
    public static ArrayList<?> queryTable(String tableName, int currentPage, int pageSize, Map<String, String> fieldList) {
        StringBuilder sqlStr = new StringBuilder();

        sqlStr.append("from (select * from " + tableName);
        if (fieldList != null && fieldList.keySet() != null) {
            Iterator<String> iterator = fieldList.keySet().iterator();
            List<String> keyList = new ArrayList<>();
            while (iterator.hasNext()) {
                keyList.add(iterator.next());
            }
            for (int i = 0; i < keyList.size(); i++) {
                if (i == 0) sqlStr.append(" where ");
                String key = keyList.get(i);
                String value = fieldList.get(key);
                sqlStr.append(key + "='" + value + "'");
                if (keyList.size() >= 2 && i != keyList.size() - 1) sqlStr.append(" AND ");
            }
        }
        sqlStr.append(" ) " + tableName);
        //System.out.println("最原始的sql："+sqlStr.toString());
        return queryTable(tableName, sqlStr.toString(), currentPage, pageSize);
    }

    //升序排序
    public static ArrayList<?> queryTable(String tableName, int currentPage, int pageSize, Map<String, String> fieldList,String orderStr) {
        StringBuilder sqlStr = new StringBuilder();

        sqlStr.append("from (select * from " + tableName);
        if (fieldList != null && fieldList.keySet() != null) {
            Iterator<String> iterator = fieldList.keySet().iterator();
            List<String> keyList = new ArrayList<>();
            while (iterator.hasNext()) {
                keyList.add(iterator.next());
            }
            for (int i = 0; i < keyList.size(); i++) {
                if (i == 0) sqlStr.append(" where ");
                String key = keyList.get(i);
                String value = fieldList.get(key);
                sqlStr.append(key + "='" + value + "'");
                if (keyList.size() >= 2 && i != keyList.size() - 1) sqlStr.append(" AND ");
            }
        }
        if (orderStr!=null){
            sqlStr.append( "order by "+orderStr+" "+"DESC");
        }
        sqlStr.append(" ) " + tableName);

        return queryTable(tableName, sqlStr.toString(), currentPage, pageSize);
    }


    /**
     * 根据表名，执行sql语句查询表记录；
     *
     * @param tableName
     * @param sqlStr
     * @return
     * @throws Exception
     */
    public static ArrayList<?> queryTable(String tableName, String sqlStr) {
        return queryTable(tableName, sqlStr, -1, -1);
    }



    public static ArrayList<?> queryTable(String tableName, String sqlStr, int currentPage, int pageSize) {
        return queryTable(
                sqlStr,
                pageSize == -1 ? "" : String.valueOf(pageSize),
                "",
                "",
                "SEARCHYOUNGCONTENT",
                new DocInfor("", tableName),
                true,
                false,
                currentPage*pageSize == -1 ? "" : String.valueOf(((currentPage-1)*pageSize)));
    }

    /**
     * 查询指定的表，取offset(默认从第1个开始取)到num(默认取结果总数)之间的结果
     *
     * @param sqlStr            sql语句
     * @param num               取结果数量
     * @param orderBy           排序
     * @param fieldNameList     字段名列表
     * @param command           指令
     * @param docInfor          表名和表记录contentId
     * @param isSimpleSearch
     * @param noTincludedocInfo
     * @param offset            取结果起始位置
     * @return
     * @throws Exception
     */
    public static ArrayList<?> queryTable(String sqlStr, String num, String orderBy, String fieldNameList, String command, DocInfor docInfor, boolean isSimpleSearch, boolean noTincludedocInfo, String offset) {
        DataSetList resultData = null;
        ArrayList<BaseTable> tableDataList = new ArrayList<BaseTable>();

        String xmlStr = XmlPackage.packageSelect(sqlStr, num, orderBy, fieldNameList, command, docInfor, isSimpleSearch, noTincludedocInfo, offset);
        try {
            resultData = PostHttp.PostXML(xmlStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tableDataList = DataParser.getTable(resultData, docInfor.getContentName());

        return tableDataList;
    }



    /**
     * 向服务器端 插入一条表记录/更新现有的一条记录
     *
     * @param tableData 要 插入/更新 的表记录
     * @return 插入成功的表记录的contentId
     * @throws Exception
     */

    public static boolean insertOrUpdateTable(BaseTable tableData)
    {
        boolean result = false;

        String xmlStr = "";

        xmlStr = XmlPackage.packageForSaveOrUpdate(
                (HashMap<?, ?>) tableData.getFieldList(),
                new DocInfor(tableData.getContentId(), tableData.getTableName()),  //当该表记录的contentId对应数据库中的一条已有的表记录时，更新该条记录；不对应已有记录时，添加新记录
                false);

        DataSetList resultData = null;
        try {
            resultData = PostHttp.PostXML(xmlStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(Constants.REQUEST_RESULT_SUCCESS.equals(resultData.SUCCESS))
        {
            if(resultData.CONTENTID!=null && resultData.CONTENTID.size()!=0)
            {
                tableData.setContentId(resultData.CONTENTID.get(0));
            }
            result = true;
        }

        return result;
    }

    /**
     * 向服务器端 插入一条表记录/更新现有的一条记录
     * @param tableData 要 插入/更新 的表记录
     * @param file 附件
     * @return 插入成功的表记录的contentId
     * @throws Exception
     */
    public static boolean insertOrUpdateTable(BaseTable tableData, Document file)
    {
        return insertOrUpdateTable(tableData, new Document[]{file});
    }


    public static boolean insertOrUpdateTable(BaseTable tableData, Document[] file) {
        boolean result = false;

        String xmlStr = "";

        String[] filePath = new String[file.length];
        String[] fileType = new String[file.length];
        for (int i = 0; i < file.length; i++) {
            filePath[i] = file[i] == null ? "" : file[i].getPath();
            fileType[i] = file[i] == null ? "" : file[i].getFileType();
        }

        xmlStr = XmlPackage.packageForInsertFileData(
                (HashMap<?, ?>) tableData.getFieldList(),
                new DocInfor(tableData.getContentId(), tableData.getTableName()),
                true,
                filePath,
                fileType);
        DataSetList resultData = null;
        try {
            resultData = PostHttp.PostXML(xmlStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (resultData!=null) {
            if (Constants.REQUEST_RESULT_SUCCESS.equals(resultData.SUCCESS)) {
                if (resultData.CONTENTID != null && resultData.CONTENTID.size() != 0) {
                    tableData.setContentId(resultData.CONTENTID.get(0));
                }
                result = true;
            }
        }

        return result;
    }

    /**
     * 删除服务器上的一条表记录
     *
     * @param tableData
     * @return 删除成功或失败
     * @throws Exception
     */
    public static boolean deleteTable(BaseTable tableData) {
        boolean result = false;

        String xmlStr = XmlPackage.packageDelete(tableData.getContentId());

        DataSetList resultData = null;
        try {
            resultData = PostHttp.PostXML(xmlStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Constants.REQUEST_RESULT_SUCCESS.equals(resultData.SUCCESS)) result = true;

        return result;
    }
}
