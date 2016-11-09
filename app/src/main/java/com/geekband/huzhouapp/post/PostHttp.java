package com.geekband.huzhouapp.post;

import com.database.pojo.BaseTable;
import com.database.pojo.DataSetList;
import com.database.pojo.Document;
import com.net.post.DocInfor;
import com.net.post.XMLContentHandlerForList;
import com.net.post.XmlParseForDocDetail;
import com.net.post.XmlParseForDocList;
import com.oa.util.Constants;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.SAXParserFactory;

/**
 * @author Administrator
 * @date 2013-01-14 上午08:28:51
 * @category 针对程序中对web的数据请求
 */
public class PostHttp {

    public static DataSetList PostXML(BaseTable tableData, Document[] file) throws Exception {

        //数据解析
        String xmlStr = "";
        String[] filePath = new String[file.length];
        String[] fileType = new String[file.length];
        for (int i = 0; i < file.length; i++) {
            filePath[i] = file[i] == null ? "" : file[i].getPath();
            fileType[i] = file[i] == null ? "" : file[i].getFileType();
        }

        URL url = null;
        HttpURLConnection httpurlconnection = null;
        DataSetList parsedExampleDataSet = null;
        // try {
        url = new URL("http://" + Constants.CONNIP + Constants.PATH);
        httpurlconnection = (HttpURLConnection) url.openConnection();
        httpurlconnection.setDoOutput(true);
        httpurlconnection.setRequestMethod("POST");
        httpurlconnection.setUseCaches(false);
        httpurlconnection.setConnectTimeout(20000);// 设置连接主机超时为20秒钟
        httpurlconnection.setReadTimeout(25000); // 设置从主机读取数据超时为25秒钟

        OutputStream os = httpurlconnection.getOutputStream();
        //第一部分
        String headerStr = XmlPackage.insertHeaderFileData((HashMap<?, ?>) tableData.getFieldList(),
                new DocInfor(tableData.getContentId(), tableData.getTableName()), true);
        os.write(headerStr.getBytes());
        try {
            os.flush();
            headerStr = null;
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //第二部分
        String contentHeaderStr;
        String contentFooterStr;
        byte[] fileStream;
        for (int i = 0; i < filePath.length; i++) {
            contentHeaderStr = XmlPackage.insertContentHeaderFileData(filePath[i]);
            os.write(contentHeaderStr.getBytes());
            try {
                os.flush();
                contentHeaderStr = null;
                System.gc();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //拆分附件
            int value = 1024;
            fileStream = XmlPackage.insertContentFileData(filePath[i]);
//            int j = fileStream.length / value;
//            for (int k = 0; k < j; k++) {
//                os.write(Arrays.copyOfRange(fileStream, k * value, (k + 1) * value));
//                try {
//                    os.flush();
//                    System.gc();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            if (fileStream.length % value != 0) {
//                os.write(Arrays.copyOfRange(fileStream, j * value, fileStream.length));
//                try {
//                    os.flush();
//                    System.gc();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }

            os.write(fileStream);
            try {
                os.flush();
                fileStream = null;
                System.gc();
            } catch (Exception e) {
                e.printStackTrace();
            }

            contentFooterStr = XmlPackage.insertContentFooterFileData(filePath[i], fileType[i]);
            os.write(contentFooterStr.getBytes());
            try {
                os.flush();
                contentFooterStr = null;
                System.gc();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //第三部分
        String footerStr = XmlPackage.insertFooterFileData();
        os.write(footerStr.getBytes());
        try {
            os.flush();
            footerStr = null;
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        os.close();
        InputStream stream = httpurlconnection.getInputStream();
//        String str = convertStreamToString(stream);
//        System.out.println("********** request str:"+str);
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        XMLReader reader = saxParserFactory.newSAXParser().getXMLReader();

        XMLContentHandlerForList myExampleHandler = new XMLContentHandlerForList();
        reader.setContentHandler(myExampleHandler);
//        reader.parse(new InputSource(new StringReader(convertStreamToString(stream))));
        reader.parse(new InputSource(stream));
        parsedExampleDataSet = myExampleHandler.dataSet;

        //IOUtils.writeStringToFile(str, Environment.getExternalStorageDirectory()+"/xmlData.txt");
        //IOUtils.writeStringToFile(contents, Environment.getExternalStorageDirectory()+"/xmlStr.txt");

        // } catch (Exception e) {
        // e.printStackTrace();
        // } finally {
        httpurlconnection.disconnect();
        stream.close();
        // }
        return parsedExampleDataSet;
    }

    // 获取文档contentId列表
    public static DataSetList PostDocXML(String contents) throws Exception {
        URL url = null;
        HttpURLConnection httpurlconnection = null;
        DataSetList parsedExampleDataSet = null;
        // try {
        url = new URL("http://" + Constants.CONNIP + Constants.PATH);
        httpurlconnection = (HttpURLConnection) url.openConnection();
        httpurlconnection.setDoOutput(true);
        httpurlconnection.setRequestMethod("POST");
        httpurlconnection.setConnectTimeout(20000);// 设置连接主机超时为20秒钟
        httpurlconnection.setReadTimeout(25000); // 设置从主机读取数据超时为25秒钟
        httpurlconnection.getOutputStream().write(contents.getBytes());
        try {
            httpurlconnection.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        httpurlconnection.getOutputStream().close();
        InputStream stream = httpurlconnection.getInputStream();
        String str = convertStreamToString(stream);


        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        XMLReader reader = saxParserFactory.newSAXParser().getXMLReader();

        XmlParseForDocDetail myExampleHandler = new XmlParseForDocDetail();
        reader.setContentHandler(myExampleHandler);
        reader.parse(new InputSource(new StringReader(str)));
        parsedExampleDataSet = myExampleHandler.dataSet;

        if (httpurlconnection != null) httpurlconnection.disconnect();
        return parsedExampleDataSet;
    }

    // 获取文档contentId列表
    public static DataSetList PostDocListXML(String contents) throws Exception {
        URL url = null;
        HttpURLConnection httpurlconnection = null;
        DataSetList parsedExampleDataSet = null;
        // try {
        url = new URL("http://" + Constants.CONNIP + Constants.PATH);
        httpurlconnection = (HttpURLConnection) url.openConnection();
        httpurlconnection.setDoOutput(true);
        httpurlconnection.setRequestMethod("POST");
        httpurlconnection.setConnectTimeout(20000);// 设置连接主机超时为20秒钟
        httpurlconnection.setReadTimeout(25000); // 设置从主机读取数据超时为25秒钟
        httpurlconnection.getOutputStream().write(contents.getBytes());
        try {
            httpurlconnection.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        httpurlconnection.getOutputStream().close();
        InputStream stream = httpurlconnection.getInputStream();
        String str = convertStreamToString(stream);


        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        XMLReader reader = saxParserFactory.newSAXParser().getXMLReader();

        XmlParseForDocList myExampleHandler = new XmlParseForDocList();
        reader.setContentHandler(myExampleHandler);
        reader.parse(new InputSource(new StringReader(str)));
        parsedExampleDataSet = myExampleHandler.dataSet;

        // } catch (Exception e) {
        // e.printStackTrace();
        // } finally {
        if (httpurlconnection != null) httpurlconnection.disconnect();
        // }
        return parsedExampleDataSet;
    }

    /**
     * 在线编码xml获取
     *
     * @param contents
     * @return
     */
    public static DataSetList qrPostXML(String contents) throws Exception {
        URL url = null;
        HttpURLConnection httpurlconnection = null;
        DataSetList parsedExampleDataSet = null;

        url = new URL("http://" + Constants.CONNIP + "/IDOC/service.jsp");// QR请求地址

        httpurlconnection = (HttpURLConnection) url.openConnection();
        httpurlconnection.setDoOutput(true);
        httpurlconnection.setRequestMethod("POST");
        httpurlconnection.setConnectTimeout(600000);// 设置请求时间为6分钟

        httpurlconnection.getOutputStream().write(contents.getBytes());

        httpurlconnection.getOutputStream().flush();


        httpurlconnection.getOutputStream().close();
        InputStream stream = httpurlconnection.getInputStream();
        String str = convertStreamToString(stream);


        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        XMLReader reader = saxParserFactory.newSAXParser().getXMLReader();

        XmlParseForDocDetail myExampleHandler = new XmlParseForDocDetail();
        reader.setContentHandler(myExampleHandler);
        reader.parse(new InputSource(new StringReader(str)));
        parsedExampleDataSet = myExampleHandler.dataSet;


        if (httpurlconnection != null) httpurlconnection.disconnect();

        return parsedExampleDataSet;
    }

    /**
     * @param is
     * @return String
     * @throws IOException
     */
    public static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = null;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(is, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        StringBuffer sb = new StringBuffer();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
