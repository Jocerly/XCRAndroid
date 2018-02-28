package com.yatang.xc.xcr.uitls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 序列化,反序列化
 * @author Jocerly
 */
public class SerializUtil {
	/**
	 * 序列化
	 * 
	 * @param obj
	 */
	public static String enSerializ(Object obj) {
		String str="";
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			// 需要一个文件输出流和对象输出流；文件输出流用于将字节输出到文件，对象输出流用于将对象输出为字节
			ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream);
			out.writeObject(obj.toString());
			str=new String(Base64.encode(byteArrayOutputStream.toByteArray()));
			out.close();
			byteArrayOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 反序列化
	 * @return
	 */
	public static Object deSerializ(String str) {
		Object obj=null;
		try {
			ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(Base64.decode(str.getBytes()));
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			obj=objectInputStream.readObject();
			byteArrayInputStream.close();
			objectInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
}
