package com.geekband.huzhouapp.utils;

import android.content.Context;

import com.geekband.huzhouapp.application.MyApplication;
import com.geekband.huzhouapp.vo.NetNewsInfo;
import com.lidroid.xutils.exception.DbException;

import java.util.List;

/**
 * 用来处理本地数据缓存
 */
public class DBUtils {
    public static void newsInfoCache(List<NetNewsInfo> newsInfos){
        try {
            MyApplication.sDbUtils.saveAll(newsInfos);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
