package com.geekband.huzhouapp.post;

import com.geekband.huzhouapp.vo.pojo.BaseTable;
import com.geekband.huzhouapp.vo.pojo.DataSetList;
import com.geekband.huzhouapp.vo.pojo.Document;
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
 * 2013-01-14 上午08:28:51
 * 针对程序中对web的数据请求
 */
public class PostHttp {

    public static DataSetList PostXML(BaseTable tableData, Document[] file) throws Exception {
        //数据解析
        String[] filePath = new String[file.length];
        String[] fileType = new String[file.length];
        for (int i = 0; i < file.length; i++) {
            filePath[i] = file[i] == null ? "" : file[i].getPath();
            fileType[i] = file[i] == null ? "" : file[i].getFileType();
        }

        URL url ;
        HttpURLConnection httpurlconnection ;
        DataSetList parsedExampleDataSet = null;
        // try {
        url = new URL("http://" + Constants.CONNIP + Constants.PATH);
        httpurlconnection = (HttpURLConnection) url.openConnection();
        httpurlconnection.setDoOutput(true);
        httpurlconnection.setRequestMethod("POST");
        httpurlconnection.setUseCaches(false);
        httpurlconnection.setConnectTimeout(20000);// 设置连接主机超时为20秒钟
        httpurlconnection.setReadTimeout(25000); // 设置从主机读取数据超时为25秒钟

        OutputStream os ;
        InputStream stream = null;
        try {
            os = httpurlconnection.getOutputStream();
            //第一部分
            os.write(XmlPackage.insertHeaderFileData((HashMap<?, ?>) tableData.getFieldList(),
                    new DocInfor(tableData.getContentId(), tableData.getTableName()), true).getBytes());
            os.flush();
            System.gc();

            //第二部分
            for (int i = 0; i < filePath.length; i++) {
                os.write(XmlPackage.insertContentHeaderFileData(filePath[i]).getBytes());
                os.flush();
                System.gc();

                //拆分附件
                os.write(XmlPackage.insertContentFileData(filePath[i]));
                os.flush();
                System.gc();
                os.write(XmlPackage.insertContentFooterFileData(filePath[i], fileType[i]).getBytes());

                os.flush();
                System.gc();

            }
            //第三部分
            os.write(XmlPackage.insertFooterFileData().getBytes());
            os.flush();
            System.gc();

            os.close();
          stream = httpurlconnection.getInputStream();
//        String str = convertStreamToString(stream);
//        System.out.println("********** request str:" + os.toString());

            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            XMLReader reader = saxParserFactory.newSAXParser().getXMLReader();

            XMLContentHandlerForList myExampleHandler = new XMLContentHandlerForList();
            reader.setContentHandler(myExampleHandler);
//           reader.parse(new InputSource(new StringReader(convertStreamToString(stream))));
            reader.parse(new InputSource(stream));
            parsedExampleDataSet = myExampleHandler.dataSet;

            //IOUtils.writeStringToFile(str, Environment.getExternalStorageDirectory()+"/xmlData.txt");
            //IOUtils.writeStringToFile(contents, Environment.getExternalStorageDirectory()+"/xmlStr.txt");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpurlconnection.disconnect();
            if (stream!=null){
                stream.close();
            }
        }
        return parsedExampleDataSet;
    }

    // 获取文档contentId列表
    public static DataSetList PostDocXML(String contents) throws Exception {
        URL url ;
        HttpURLConnection httpurlconnection;
        DataSetList parsedExampleDataSet ;
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

        httpurlconnection.disconnect();
        return parsedExampleDataSet;
    }

    // 获取文档contentId列表
    public static DataSetList PostDocListXML(String contents) throws Exception {
        URL url ;
        HttpURLConnection httpurlconnection = null;
        DataSetList parsedExampleDataSet = null;
         try {
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

         } catch (Exception e) {
         e.printStackTrace();
         } finally {
        if (httpurlconnection != null) httpurlconnection.disconnect();
         }
        return parsedExampleDataSet;
    }

    /**
     * 在线编码xml获取
     */
    public static DataSetList qrPostXML(String contents) throws Exception {
        URL url ;
        HttpURLConnection httpurlconnection ;
        DataSetList parsedExampleDataSet ;

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


        httpurlconnection.disconnect();

        return parsedExampleDataSet;
    }

    /**
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
        StringBuilder sb = new StringBuilder();
        String line  ;
        try {
            while ((line = reader != null ? reader.readLine() : null) != null) {
                sb.append(line).append("\n");
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
