package org.jocerly.jcannotation.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import org.jocerly.jcannotation.ui.ViewInject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * sdk操作类
 *
 * @author Administrator
 */
public class SDCardUtils {

    /**
     * SD目录
     */
    public static final String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    /**
     * 文件夹目录
     */
    public static final String DIR_PATH = SD_PATH + File.separator + "XCR";
    /**
     * DCIM目录
     */
    public static final String DCIM_PATH = SD_PATH + File.separator + "DCIM/Camera";


    /**
     * 判断SDK是否存在
     *
     * @return
     */
    public static boolean ifExistSDCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    /**
     * 创建文件
     *
     * @param fileName
     */
    public static File createFile(String fileName) {
        createDir(DIR_PATH);
        if (StringUtils.isEmpty(fileName)) {// 如果名字为空。不执行
            return null;
        }
        try {
            String fileFullName = DIR_PATH + File.separator + fileName;//文件的完整路径
            File file = new File(fileFullName);//
            if (file.exists()) {//判断文件夹是否存在
                file.delete();
            }
            file.createNewFile();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建文件
     *
     * @param fileName
     */
    public static File createFile(String dirPath, String fileName) {
        createDir(dirPath);
        if (StringUtils.isEmpty(fileName)) {// 如果名字为空。不执行
            return null;
        }
        try {
            String fileFullName = dirPath + File.separator + fileName;//文件的完整路径
            File file = new File(fileFullName);//
            if (file.exists()) {//判断文件夹是否存在
                file.delete();
            }
            file.createNewFile();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建文件夹
     *
     * @param dirPath
     */
    public static void createDir(String dirPath) {
        if (!ifExistSDCard()) {
            return;
        }
        if (StringUtils.isEmpty(dirPath)) {// 如果名字为空。不执行
            return;
        }
        try {
            String[] dirs = dirPath.split(File.separator);// 分解文件夹
            String dirName = "";// 文件夹路径
            for (String dir : dirs) {// 便利文件夹数组
                dirName = dirName + File.separator + dir;
                File file = new File(dirName);//
                if (!file.exists()) {// 判断文件夹是否存在
                    file.mkdir();
                }
                file = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存图片
     *
     * @param bitmap
     * @param picName
     */
    public static boolean saveData(Context mContext, Bitmap bitmap, String path, String picName) {
        if (!ifExistSDCard()) {
            ViewInject.toast(mContext, "SD卡不存在,保存失败！");
            return false;
        }
        createDir(path);
        File file = new File(path);
        File imageFile = new File(file, picName);

        try {
            if (imageFile.exists()) {
                imageFile.delete();
            }
            imageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            //保存到相册后，需要执行广播，通知相册更新
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(imageFile);
            intent.setData(uri);
            mContext.sendBroadcast(intent);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据名字得到图片
     *
     * @param picName
     * @return
     */
    public static Bitmap getBitmap(String picName) {
        File imageFile = new File(DIR_PATH, picName);
        if (imageFile.exists()) {
            try {
                return BitmapFactory.decodeStream(new FileInputStream(SDCardUtils.getFileFullName(picName)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 得到文件信息信息
     *
     * @return
     */
    public static File getFile(String fileName) {
        try {
            return new File(fileName);
        } catch (Exception e) {

        }
        return null;
    }


    /**
     * 得到文件信息信息
     *
     * @return
     */
    public static File getFileByFullPath(String fileName) {
        try {
            return new File(DIR_PATH + File.separator + fileName);
        } catch (Exception e) {

        }
        return null;
    }

    public static String getFileFullName(String fileName) {
        return DIR_PATH + File.separator + fileName;
    }

    /**
     * 删除文件
     *
     * @param dirPath
     * @param fileName
     * @return
     */
    public static boolean deleteFie(String dirPath, String fileName) {
        File file = new File(dirPath, fileName);
        return file.delete();
    }

    /**
     * 删除文件
     *
     * @param fileName
     * @return
     */
    public static boolean deleteFie(String fileName) {
        File file = new File(DIR_PATH, fileName);
        return file.delete();
    }

    /**
     * 得到sdcard的路径
     *
     * @return
     */
    public static String getSDCardPath() {
        if (ifExistSDCard()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    /**
     * 解压缩
     *
     * @param mInput
     * @param mOutput
     * @return extractedSize
     */
    public static long unZip(File mInput, File mOutput) {
        long extractedSize = 0L;
        Enumeration<ZipEntry> entries;
        ZipFile zip = null;
        try {
            zip = new ZipFile(mInput);
            long uncompressedSize = getOriginalSize(zip);
//            publishProgress(0, (int) uncompressedSize);

            entries = (Enumeration<ZipEntry>) zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                File destination = new File(mOutput, entry.getName());
                if (!destination.getParentFile().exists()) {
                    destination.getParentFile().mkdirs();
                }
                ProgressReportingOutputStream outStream = new ProgressReportingOutputStream(destination);
                extractedSize += copy(zip.getInputStream(entry), outStream);
                outStream.close();
            }
        } catch (ZipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                zip.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return extractedSize;
    }

    private static long getOriginalSize(ZipFile file) {
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) file.entries();
        long originalSize = 0l;
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getSize() >= 0) {
                originalSize += entry.getSize();
            }
        }
        return originalSize;
    }

    private static final class ProgressReportingOutputStream extends FileOutputStream {
        public ProgressReportingOutputStream(File file)
                throws FileNotFoundException {
            super(file);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void write(byte[] buffer, int byteOffset, int byteCount)
                throws IOException {
            // TODO Auto-generated method stub
            super.write(buffer, byteOffset, byteCount);
        }
    }

    private static int copy(InputStream input, OutputStream output) {
        byte[] buffer = new byte[1024 * 8];
        BufferedInputStream in = new BufferedInputStream(input, 1024 * 8);
        BufferedOutputStream out = new BufferedOutputStream(output, 1024 * 8);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, 1024 * 8)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * 读取文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String readTextFile(File file) throws IOException {
        String text = null;
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            text = readTextInputStream(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return text;
    }

    /**
     * 从流中读取文件
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String readTextInputStream(InputStream is) throws IOException {
        StringBuffer strbuffer = new StringBuffer();
        String line;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            while ((line = reader.readLine()) != null) {
                strbuffer.append(line).append("\r\n");
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return strbuffer.toString();
    }

    /**
     * 将文本内容写入文件
     *
     * @param file
     * @param str
     * @throws IOException
     */
    public static void writeTextFile(File file, String str) throws IOException {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new FileOutputStream(file));
            out.write(str.getBytes());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

}
