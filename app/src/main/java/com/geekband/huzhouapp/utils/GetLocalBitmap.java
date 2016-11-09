package com.geekband.huzhouapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2016/5/25
 */
public class GetLocalBitmap {
    public static Bitmap convertToBitmap(String path, int w, int h) {
        Bitmap bitmap = null;
        if (path.endsWith(".jpg") || path.endsWith(".JPG") ||
                path.endsWith(".jpeg") || path.endsWith(".JPEG") ||
                path.endsWith(".png") || path.endsWith(".PNG")) {
            BitmapFactory.Options mOptions = new BitmapFactory.Options();
            // 设置为true只获取图片大小
            mOptions.inJustDecodeBounds = true;
            mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        //返回为空
//        BitmapFactory.decodeFile(path);
            int width = mOptions.outWidth;
            int height = mOptions.outHeight;
            float scaleWidth = 0.f, scaleHeight = 0.f;
            if (width > w || height > h) {
                //缩放
                scaleWidth = ((float) width) / w;
                scaleHeight = ((float) height) / h;
            }
            mOptions.inJustDecodeBounds = false;
            float scale = Math.max(scaleWidth, scaleHeight);
            mOptions.inSampleSize = (int) scale;
            WeakReference<Bitmap> weak = new WeakReference<>(BitmapFactory.decodeFile(path, mOptions));
            if (weak.get() != null) {
                bitmap = Bitmap.createScaledBitmap(weak.get(), w, h, true);
            }
        }
        return bitmap;
    }

    public static Bitmap convertToBitmap(String path) {
        return BitmapFactory.decodeFile(path);
    }
}
