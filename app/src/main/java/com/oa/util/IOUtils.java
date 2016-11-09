package com.oa.util;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 
 * IO鎿嶄綔宸ュ叿绫�
 * 
 */
public class IOUtils
{
	private IOUtils(){}
	
	/**
	 * 灏嗘祦杞崲涓哄瓧绗︿覆
	 * @param inputStream
	 * @return
	 */
	public static String streamToString(InputStream inputStream)
	{
		StringBuffer xmlStr = new StringBuffer();
		
		try
		{
			InputStreamReader isr = new InputStreamReader(inputStream);
			BufferedReader br = new BufferedReader(isr);
			String str;
			while((str = br.readLine()) != null)
			{
				xmlStr.append(str);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return xmlStr.toString();
	}
	
	/**
	 * 灏嗘祦杞崲涓哄瓧鑺傛暟缁�
	 * @param inputStream
	 * @return
	 */
	public static byte[] streamToByte(InputStream inputStream)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte data[] = new byte[1024];
		int count;
		try
		{
			while (((count = inputStream.read(data)) != -1))
			{
				baos.write(data, 0, count);
			}
			baos.flush();
		}
		catch (IOException e)
		{
		}
		return baos.toByteArray();
	}
	
	/**
	 * 灏嗘祦杞崲涓�64浣嶇紪鐮佸瓧鑺傛暟缁�
	 * @param inputStream
	 * @return
	 */
	public static byte[] streamTo64Byte(InputStream inputStream)
	{
		return Base64.encode(streamToByte(inputStream),1);
	}
	
	/**
	 * 灏嗘祦杞崲涓�64浣嶇紪鐮佸瓧鑺傛暟缁勫瓧绗︿覆
	 * @param inputStream
	 * @return
	 */
	public static String streamTo64ByteString(InputStream inputStream)
	{
		return new String(streamTo64Byte(inputStream));
	}
	
	/**
	 * 灏嗘枃浠惰浆鎹负64浣嶇紪鐮佸瓧鑺傛暟缁勫瓧绗︿覆
	 * @param file
	 * @return
	 */
	public static String fileTo64ByteString(File file)
	{
		try
		{
			return streamTo64ByteString(new FileInputStream(file));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 鍐欎俊鎭埌鎸囧畾鐨勬枃浠朵腑
	 * @param info 瑕佸啓鍏ョ殑淇℃伅瀛楃涓�
	 * @param path 瑕佸啓鍏ョ殑鐩爣鏂囦欢鐨勮矾寰�
	 */
	public static boolean writeStringToFile(String info, String path)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(path);
			fos.write(info.getBytes());
			fos.flush();
			fos.close();
			
			return true;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
}
