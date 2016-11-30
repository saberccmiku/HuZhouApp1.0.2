package com.geekband.huzhouapp.post;

import com.geekband.huzhouapp.vo.pojo.BaseTable;
import com.geekband.huzhouapp.vo.pojo.DataSetList;
import com.geekband.huzhouapp.vo.pojo.Document;
import com.geekband.huzhouapp.utils.Constants;


public final class DataOperation {
    private DataOperation() {
    }

    /**
     * 向服务器端 插入一条表记录/更新现有的一条记录
     * @param tableData 要 插入/更新 的表记录
     * @param file 附件
     * @return 插入成功的表记录的contentID
     */
    public static boolean insertOrUpdateTable(BaseTable tableData, Document file)
    {
        return insertOrUpdateTable(tableData, new Document[]{file});
    }


    public static boolean insertOrUpdateTable(BaseTable tableData, Document[] file) {
        boolean result = false;
        DataSetList resultData = null;
        try {
            resultData = PostHttp.PostXML(tableData,file);
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

}
