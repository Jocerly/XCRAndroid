package org.jocerly.jcannotation.utils;

import android.app.ProgressDialog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 文件上传下载请求类
 *
 * @author Administrator
 */
public class FileUploadOrDownLoad {

    /**
     * 下载文件
     *
     * @param url
     * @param pd
     * @return
     * @throws Exception
     */
    public static File doDownLoadFile(String dirPath, String fileName, String url, ProgressDialog pd) throws Exception {
        if (!SDCardUtils.ifExistSDCard()) {//sd卡不存在
            return null;
        }
        File file = SDCardUtils.createFile(dirPath, fileName);
        if (file == null) {//文件创建失败
            return null;
        }
        try {
            HttpURLConnection conn = getHttpURLConnection(url);
            if (pd != null) {
                pd.setMax(conn.getContentLength());
            }

            InputStream is = conn.getInputStream();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                if (pd != null) {
                    total += len;
                    pd.setProgress(total);
                }
            }
            fos.close();
            bis.close();
            is.close();
        } catch (Exception e) {
            throw e;
        }
        return file;
    }

    /**
     * 文件上传
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static String doUploadFile(String path, ByteArrayOutputStream stream, String fileName) throws Exception {
        JCLoger.debug("path------" + path);
        StringBuffer sb = new StringBuffer();
        HttpURLConnection connection;
        try {
            connection = getHttpURLConnection(path);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
//            connection.addRequestProperty("Cookie", "DeviceId=" + deviceId);
            connection.addRequestProperty("FileName", fileName);
            connection.setRequestProperty("Charset", "utf-8");
            connection.setRequestProperty("content-type", "text/html");

            BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream());
            InputStream fileInputStream = new ByteArrayInputStream(stream.toByteArray());
            byte[] bytes = new byte[1024];
            int numReadByte = 0;
            while ((numReadByte = fileInputStream.read(bytes, 0, 1024)) > 0) {
                out.write(bytes, 0, numReadByte);
                JCLoger.debug("-----" + numReadByte);
            }
            out.flush();
            fileInputStream.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                DataInputStream in = new DataInputStream(connection.getInputStream());
                while ((numReadByte = in.read(bytes, 0, 1024)) > 0) {
                    sb.append(new String(bytes, 0, numReadByte, "utf-8"));
                }
                in.close();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            sb.append("{\"Status\":{\"StateValue\":\"99\",\"StateDesc\":\"连接失败,请检查你的网络后重试\"}}");
        }
        return sb.toString();
    }

    /**
     * 获取HttpURLConnection对象
     *
     * @param path 路径
     * @return
     * @throws Exception
     */
    private static HttpURLConnection getHttpURLConnection(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);
        conn.setDoInput(true);
        return conn;
    }
}
