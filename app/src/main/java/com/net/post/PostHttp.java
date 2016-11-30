package com.net.post;

import com.geekband.huzhouapp.vo.pojo.DataSetList;
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

import javax.xml.parsers.SAXParserFactory;

/**
 * @author Administrator
 * @date 2013-01-14 上午08:28:51
 * @category 针对程序中对web的数据请求
 */
public class PostHttp {

    public static DataSetList PostXML(String contents) throws Exception {
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

        OutputStream os = httpurlconnection.getOutputStream();
        os.write(contents.getBytes());
        try {
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        os.close();
        InputStream stream = httpurlconnection.getInputStream();
//       String str = convertStreamToString(stream);
//        System.out.println("********** request str:"+str);
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        XMLReader reader = saxParserFactory.newSAXParser().getXMLReader();

        XMLContentHandlerForList myExampleHandler = new XMLContentHandlerForList();
        reader.setContentHandler(myExampleHandler);
        //reader.parse(new InputSource(new StringReader(str)));
        reader.parse(new InputSource(stream));
        parsedExampleDataSet = myExampleHandler.dataSet;

        //IOUtils.writeStringToFile(str, Environment.getExternalStorageDirectory()+"/xmlData.txt");
        //IOUtils.writeStringToFile(contents, Environment.getExternalStorageDirectory()+"/xmlStr.txt");

        // } catch (Exception e) {
        // e.printStackTrace();
        // } finally {
        if (httpurlconnection != null) httpurlconnection.disconnect();
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
