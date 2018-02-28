package org.jocerly.jcannotation.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 文件上传实体类
 * @author Administrator
 */
public class FormFile {
	private byte[] data;// 上传文件的数据
	private InputStream inStream;
	private File file;
	
	private String fileName;// 文件名称
	private String parameterName;// 请求参数名称
	private String contentType = "application/octet-stream";// 内容类型

	/**
	 * 上传小文件，把文件数据先读入内存
	 * 
	 * @param fileName
	 * @param data
	 * @param parameterName
	 * @param contentType
	 */
	public FormFile(String fileName, byte[] data, String parameterName,String contentType) {
		this.data = data;
		this.fileName = fileName;
		this.parameterName = parameterName;
		if (contentType != null)
			this.contentType = contentType;
	}

	/**
	 * 上传大文件，一边读文件数据一边上传
	 * 
	 * @param fileName
	 * @param file
	 * @param parameterName
	 * @param contentType
	 */
	public FormFile(String fileName, File file, String parameterName,String contentType) {
		this.fileName = fileName;
		this.parameterName = parameterName;
		this.file = file;
		try {
			this.inStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (contentType != null)
			this.contentType = contentType;
	}

	public File getFile() {
		return file;
	}

	public InputStream getInStream() {
		return inStream;
	}

	public byte[] getData() {
		return data;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
