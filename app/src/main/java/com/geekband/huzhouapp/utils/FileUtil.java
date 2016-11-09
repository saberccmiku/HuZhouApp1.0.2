package com.geekband.huzhouapp.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FileUtil {


    /**
     * 将Bitmap 图片保存到本地路径，并返回路径
     * mType    资源类型，参照  MultimediaContentType 枚举，根据此类型，保存时可自动归类
     *
     * @param fileName 文件名称
     * @param bitmap   图片
     * @return
     */
    //1
    public static String saveFile(String directoryName, String fileName, Bitmap bitmap) {
        return saveFile(directoryName, "", fileName, bitmap);
    }

    //2
    public static String saveFile(String directoryName, String filePath, String fileName, Bitmap bitmap) {
        byte[] bytes = bitmapToBytes(bitmap);
        return saveFile(directoryName, filePath, fileName, bytes);
    }

    public static byte[] bitmapToBytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 创建文件
     *
     * @param filePath 上级文件路径名
     * @param fileName 文件名称
     * @param bytes    流
     * @return
     */
    //3
    public static String saveFile(String directoryName, String filePath, String fileName, byte[] bytes) {
        String fileFullName = "";
        FileOutputStream fos = null;
        String dateFolder = new SimpleDateFormat("yyyyMMdd", Locale.CHINA)
                .format(new Date());
        try {
            String suffix = "";
            if (filePath == null || filePath.trim().length() == 0) {
                filePath = Environment.getExternalStorageDirectory() + "/HZGA/" + directoryName + "/" + dateFolder + "/";
            }
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            File fullFile = new File(filePath, fileName + suffix);
            fileFullName = fullFile.getPath();
            fos = new FileOutputStream(new File(filePath, fileName + suffix));
            fos.write(bytes);
        } catch (Exception e) {
            fileFullName = "";
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    fileFullName = "";
                }
            }
        }
        return fileFullName;
    }

    /**
     * 获取本地图片路径
     *
     * @param context
     * @return
     */
    public static ArrayList<String> getLocalImagePath(Context context) {
        ArrayList<String> imagePathList = new ArrayList<>();
        String[] images = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA
        };
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, images, null, null, null
        );
        if (cursor != null && cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
//                System.out.println("这是测试分割线：");
//                System.out.println("图片ID"+cursor.getString(0)); // 图片ID
//                System.out.println("图片文件名"+cursor.getString(1)); // 图片文件名
//                System.out.println(cursor.getString(2)); // 图片绝对路径
                String path = cursor.getString(2);
                if (!path.contains("/storage/emulated/0/Pictures/Screenshots/img")) {
                    imagePathList.add(path);
                }
            }
            cursor.close();
        }
        return imagePathList;
    }

    public static File saveFile(String typeFile, String fileName) {
        String filePath = Environment.getExternalStorageDirectory() + "/" + Constants.ROOT_DIRECTORY_NAME + "/" + typeFile + "/";
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        File fullFile = new File(file, fileName);
        if (fullFile.exists()) {
            fullFile.delete();
        }
        return fullFile;
    }

    //通过网络图片的Url转成Bitmap并存放在本地
    public static Bitmap url2Bitmap(String imageUrl) {

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            int max = conn.getContentLength();
            InputStream is = conn.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int len;

            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            byte[] result = baos.toByteArray();

//            return BitmapFactory.decodeStream(is);
            BitmapFactory.Options options =new BitmapFactory.Options();
//            options.inSampleSize = 2;
            return BitmapFactory.decodeByteArray(result, 0, result.length);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    //通过网络图片的Url转成byte[]并存放在本地
    public static byte[] url2Byte(String imageUrl) {

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            int max = conn.getContentLength();
            InputStream is = conn.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int len;

            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    //获取网络图片流
    public static byte[] getNetImageByUrl(String imageUrl){
        URL url = null;
        InputStream is = null;
        try {
            url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            int max = conn.getContentLength();
            is = conn.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = inputStreamToByte(is);
        return Base64.encode(data,Base64.DEFAULT);
    }

    // 输入流转Byte
    public static byte[] inputStreamToByte(InputStream is) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (is != null) {
            byte data[] = new byte[2048];
            int count;
            try {

                while (((count = is.read(data)) != -1)) {
                    baos.write(data, 0, count);
                }
                baos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return baos.toByteArray();
    }


    //删除文件夹及其子文件
    public static boolean deleteFile(File file) {
        if (file.exists()) {                    //判断文件是否存在
            if (file.isFile()) {                    //判断是否是文件
                return file.delete();                       //delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) {              //否则如果它是一个目录
                File files[] = file.listFiles();               //声明目录下所有的文件 files[];
                for (File file1 : files) {            //遍历目录下所有的文件
                   deleteFile(file1); //把每个文件 用这个方法进行迭代
                }
            }
            return file.delete();
        } else {
            return false;
        }
    }

    //下载网络图片到本地
    public static ArrayList<String> loadImageByUrl(String directoryName, ArrayList<String> imageUrls) {
        ArrayList<String> localFileUrls = new ArrayList<>();
        byte[] data = null;
        for (String fileUrl : imageUrls) {
            data = FileUtil.url2Byte(fileUrl);
            //截取文件名
            int first = fileUrl.lastIndexOf("/");
            int second = fileUrl.substring(0, first).lastIndexOf("/");
            String fileName = fileUrl.substring(second + 1, first);
            if (data!=null) {
                //saveFile(String directoryName, String filePath, String fileName, byte[] bytes)
                String localFileUrl = FileUtil.saveFile(directoryName,"", fileName, data);
//                            System.out.println("fileUrl源文件地址:"+fileUrl);
//                            System.out.println("bitmap装换流:" + bitmap);
//                            System.out.println("localFileUrl本地文件地址:"+localFileUrl);
                localFileUrls.add(localFileUrl);
            }

        }
        return localFileUrls;
    }

    //清除HZGA目录下的文件
    public static boolean clearFolder(String directoryName) {
        String filePath = Environment.getExternalStorageDirectory() + "/" + Constants.ROOT_DIRECTORY_NAME + "/" + directoryName;
        deleteFile(new File(filePath));
        return false;
    }
}
