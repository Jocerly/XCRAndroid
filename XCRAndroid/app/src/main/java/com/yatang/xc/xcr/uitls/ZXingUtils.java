package com.yatang.xc.xcr.uitls;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

/**
 * 生成条形码和二维码的工具
 * Created by Jocerly on 2017/10/19.
 */

public class ZXingUtils {
    /**
     * 黑点颜色
     */
    private static final int BLACK = 0xFF000000;
    /**
     * 白色
     */
    private static final int WHITE = 0xFFFFFFFF;
    /**
     * 正方形二维码宽度
     */
    private static final int CODE_WIDTH = 582;
    /**
     * LOGO宽度值,最大不能大于二维码20%宽度值,大于可能会导致二维码信息失效
     */
    private static final int LOGO_WIDTH_MAX = CODE_WIDTH / 5;
    /**
     * LOGO宽度值,最小不能小于二维码10%宽度值,小于影响Logo与二维码的整体搭配
     */
    private static final int LOGO_WIDTH_MIN = CODE_WIDTH / 10;

    /**
     * 生成二维码 要转换的地址或字符串,可以是中文
     *
     * @param url
     * @return
     */
    public static Bitmap createQRImage(String url) {
        try {
            // 判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, CODE_WIDTH, CODE_WIDTH, hints);
            int[] pixels = new int[CODE_WIDTH * CODE_WIDTH];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < CODE_WIDTH; y++) {
                for (int x = 0; x < CODE_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * CODE_WIDTH + x] = BLACK;
                    } else {
                        pixels[y * CODE_WIDTH + x] = WHITE;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(CODE_WIDTH, CODE_WIDTH, Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, CODE_WIDTH, 0, 0, CODE_WIDTH, CODE_WIDTH);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成带logo二维码 要转换的地址或字符串,可以是中文
     *
     * @param url
     * @param logoBitmap
     * @return
     */
    public static Bitmap createQRLogoImage(String url, Bitmap logoBitmap) {
        try {
            // 判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            int logoWidth = logoBitmap.getWidth();
            int logoHeight = logoBitmap.getHeight();
            // 将logo图片按martix设置的信息缩放
            Matrix m = new Matrix();
            /*
             * 给的源码是,由于CSDN上传的资源不能改动，这里注意改一下
             * float sx = (float) 2*logoHaleWidth / logoWidth;
             * float sy = (float) 2*logoHaleHeight / logoHeight;
             */
            float sx = (float) LOGO_WIDTH_MAX / logoWidth;
            float sy = (float) LOGO_WIDTH_MAX / logoHeight;
            m.setScale(sx, sy);// 设置缩放信息
            Bitmap newLogoBitmap = Bitmap.createBitmap(logoBitmap, 0, 0, logoWidth, logoHeight, m, false);
            int newLogoWidth = newLogoBitmap.getWidth();
            int newLogoHeight = newLogoBitmap.getHeight();

            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);//设置容错级别,H为最高
            hints.put(EncodeHintType.MAX_SIZE, LOGO_WIDTH_MAX);// 设置图片的最大值
            hints.put(EncodeHintType.MIN_SIZE, LOGO_WIDTH_MIN);// 设置图片的最小值
            hints.put(EncodeHintType.MARGIN, 2);//设置白色边距值
            // 图像数据转换，使用了矩阵转换
            BitMatrix matrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, CODE_WIDTH, CODE_WIDTH, hints);
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int halfW = width / 2;
            int halfH = height / 2;
            int[] pixels = new int[width * height];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < width; y++) {
                for (int x = 0; x < height; x++) {
                   /*
                 * 取值范围,可以画图理解下
                 * halfW + newLogoWidth / 2 - (halfW - newLogoWidth / 2) = newLogoWidth
                 * halfH + newLogoHeight / 2 - (halfH - newLogoHeight) = newLogoHeight
                 */
                    if (x > halfW - newLogoWidth / 2 && x < halfW + newLogoWidth / 2
                            && y > halfH - newLogoHeight / 2 && y < halfH + newLogoHeight / 2) {// 该位置用于存放图片信息
                    /*
                     *  记录图片每个像素信息
                     *  halfW - newLogoWidth / 2 < x < halfW + newLogoWidth / 2
                     *  --> 0 < x - halfW + newLogoWidth / 2 < newLogoWidth
                     *   halfH - newLogoHeight / 2  < y < halfH + newLogoHeight / 2
                     *   -->0 < y - halfH + newLogoHeight / 2 < newLogoHeight
                     *   刚好取值newLogoBitmap。getPixel(0-newLogoWidth,0-newLogoHeight);
                     */
                        pixels[y * width + x] = newLogoBitmap.getPixel(
                                x - halfW + newLogoWidth / 2, y - halfH + newLogoHeight / 2);
                    } else {
                        pixels[y * width + x] = matrix.get(x, y) ? BLACK : WHITE;// 设置信息
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将两个Bitmap合并成一个
     *
     * @param first     大图
     * @param second    小图
     * @param fromPoint 第二个Bitmap开始绘制的起始位置（相对于第一个Bitmap）
     * @return
     */
    public static Bitmap mixtureBitmap(Bitmap first, Bitmap second, PointF fromPoint) {
        if (first == null || second == null || fromPoint == null) {
            return null;
        }
        Bitmap newBitmap = Bitmap.createBitmap(
                first.getWidth(),
                first.getHeight(), Config.ARGB_4444);
        Canvas cv = new Canvas(newBitmap);
        cv.drawBitmap(first, 0, 0, null);
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();

        return newBitmap;
    }
}
