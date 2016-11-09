package com.geekband.huzhouapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.AbsListView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapGlobalConfig;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;

import java.io.File;

/**
 * 设置BitmapUtils的图片属性以及缓存路径
 * Created by Administrator on 2016/6/8
 */
public class BitmapHelper {


    /**
     * @param context 上下文
     * @param view    listView或者GridView,如果view不为null，则设置监听事件，静止时加载图片，滚动式不加载
     * @param loading 正在加载图片时显示的样式图,为0时，不设置该属性
     * @param failed  加载图片失败后显示的展示图，为0时，不设置该属性
     * @return bitmapUtils对象
     */
    public static BitmapUtils getBitmapUtils(Context context, AbsListView view, int loading, int failed) {

        File cacheFile = new File(Environment.getExternalStorageDirectory() + "/" + "bitmapCache" + "/");
        if (!cacheFile.exists()) {
            cacheFile.exists();
        }
        String path = cacheFile.getAbsolutePath();
        BitmapUtils sBitmapUtils = new BitmapUtils(context, path, BitmapGlobalConfig.MIN_DISK_CACHE_SIZE);
        sBitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
        sBitmapUtils.configDiskCacheEnabled(true);
        sBitmapUtils.configMemoryCacheEnabled(true);
        if (loading != 0) {
            sBitmapUtils.configDefaultLoadingImage(loading);
        }
        if (failed != 0) {
            sBitmapUtils.configDefaultLoadFailedImage(failed);
        }

        if (view != null) {
            view.setOnScrollListener(new PauseOnScrollListener(sBitmapUtils, false, true));
        }
        return sBitmapUtils;
    }
}
