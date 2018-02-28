package com.yatang.xc.xcr.views.wireframe;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.view.View;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.uitls.Common;

import org.jocerly.jcannotation.utils.JCLoger;

/**
 * 线框图
 * Created by zengxiaowen on 2017/7/17.
 */
public class HomeDiagram extends View {

    private List<Double> milliliter; // 收入
    private List<Double> malliliter; // 利润
    private float tb;
    private float interval_left_right;
    private Paint paint_brokenLine, framPanint ,framPanint2;
    private Paint paint_text; //

    private Bitmap bitmap_point;
    private Path path;
    private Path path2;

    private int blueLineColor = Color.argb(255, 39, 195, 160); //


    private int top = 80; //上间距
    private int bottom = 80; // 下间距
    private float base = 0; // 单位长度
    private int htight; // 折线绘制高度

    public HomeDiagram(Context context, List<Double> milliliter, List<Double> malliliter) {
        super(context);
        init(milliliter, malliliter);
    }

    /**
     * @param milliliter 收入
     * @param malliliter 利润
     */
    public void init(List<Double> milliliter, List<Double> malliliter) {
        if (null == milliliter || milliliter.size() == 0 || null == malliliter || malliliter.size() == 0)
            return;
        this.milliliter = milliliter;
        this.malliliter = malliliter;
        Resources res = getResources();
        tb = res.getDimension(R.dimen.px10);
        interval_left_right = tb * 2.0f;

        paint_brokenLine = new Paint();
        framPanint = new Paint();
        paint_text = new Paint();
        framPanint2 = new Paint();
        path = new Path();
        path2 = new Path();

        framPanint.setAntiAlias(true);
        framPanint.setStrokeWidth(2f);
        framPanint2.setAntiAlias(true);
        framPanint2.setStrokeWidth(2f);

    }

    protected void onDraw(Canvas c) {
        if (null == milliliter || milliliter.size() == 0)
            return;
        htight = getHeight() - top - bottom;
        if (Collections.max(milliliter) != 0) {
            base = (float) (htight / Collections.max(milliliter));
        }
        // 绘制折线
        drawLine(c, milliliter);
        drawBrokenLine(c, malliliter);

        //绘制阴影
        if (Collections.max(milliliter)>0) {
            drawShader(c, milliliter);
        }
        if(Collections.max(malliliter)>0) {
            drawBrokShader(c, malliliter);
        }

        // 绘制图片
        drawBitMap(c,milliliter);
        drawBrokBitmap(c,malliliter);
    }

    /**
     * 绘制利润图片
     * @param c
     * @param malliliter
     */
    private void drawBrokBitmap(Canvas c, List<Double> malliliter) {
        bitmap_point = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_spline);
        for (int i =0; i<malliliter.size();i++){
            float x1 = interval_left_right * i + interval_left_right / 2;
            float y1 = malliliter.get(i) < 0 ?  (htight + top)  : (htight - (float) (base * malliliter.get(i)) + top);
            String numTxt = Common.formatFloat(malliliter.get(i) );
            c.drawText(numTxt, x1 - numTxt.length() * 15 / 2, y1 + 50, paint_text);
            c.drawBitmap(bitmap_point,
                    x1 - bitmap_point.getWidth() / 2,
                    y1 - bitmap_point.getHeight() / 2, null);
        }
    }

    /**
     * 绘制收入图片
     * @param c
     * @param milliliter
     */
    private void drawBitMap(Canvas c, List<Double> milliliter) {
        bitmap_point = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_spline2);
        for (int i =0; i<milliliter.size();i++){
            float x1 = interval_left_right * i + interval_left_right / 2;
            float y1 = milliliter.get(i) < 0 ?  (htight + top)  : (htight - (float) (base * milliliter.get(i)) + top);
            String numTxt = Common.formatFloat(milliliter.get(i));
            c.drawText(numTxt, x1 - numTxt.length() * 15 / 2, y1 - 20, paint_text);
            c.drawBitmap(bitmap_point,
                    x1 - bitmap_point.getWidth() / 2,
                    y1 - bitmap_point.getHeight() / 2, null);
        }
    }

    /**
     * 设置7天收入Paint
     */
    private void initLinePaint() {
        paint_brokenLine.setStrokeWidth(3);
        paint_brokenLine.setColor(Color.argb(255, 235, 151, 69));
        paint_brokenLine.setAntiAlias(true);


        paint_text.setTextSize(34);
        paint_text.setColor(Color.parseColor("#333333"));
    }


    /**
     * 设置7天利润Paint
     */
    private void initBrokenLinePaint() {
        paint_brokenLine.setStrokeWidth(3);
        paint_brokenLine.setColor(blueLineColor);
        paint_brokenLine.setAntiAlias(true);

        paint_text.setTextSize(28);
        paint_text.setColor(Color.parseColor("#333333"));
    }

    /**
     * 绘制收入阴影
     *
     * @param c
     * @param milliliter
     */
    private void drawShader(Canvas c, List<Double> milliliter) {
        Shader mShader = new LinearGradient(interval_left_right, htight - (float) (base * Collections.max(milliliter)) + top, interval_left_right, htight + top, new int[]{
                Color.parseColor("#4ceb9745"),
                //Color.argb(45, 235, 151, 69),
                Color.parseColor("#00eb9745")}, null, Shader.TileMode.CLAMP);
        framPanint2.setShader(mShader);
        for (int i = 0; i < milliliter.size() - 1; i++) {
            float x1 = interval_left_right * i + interval_left_right / 2;
            float y1 = milliliter.get(i) < 0 ?  (htight + top)  : (htight - (float) (base * milliliter.get(i)) + top);
            float x2 = interval_left_right * (i + 1) + interval_left_right / 2;
            float y2 = milliliter.get(i + 1) < 0 ?  (htight + top)  : (htight - (float) (base * milliliter.get(i + 1)) + top);
            if (i == 0) {
                path2.moveTo(x1, y1);
            } else {
                path2.lineTo(x1, y1);
            }
            if (i == milliliter.size() - 2) {
                path2.lineTo(x2, y2);
                path2.lineTo(x2, getHeight());
                path2.lineTo(interval_left_right / 2, getHeight());
                path2.close();
                c.drawPath(path2, framPanint2);
            }

        }
    }

    /**
     * 绘制收入折线
     *
     * @param c
     */
    public void drawLine(Canvas c, List<Double> milliliter) {
        initLinePaint();
        interval_left_right = getWidth() / 7;
        for (int i = 0; i < milliliter.size() - 1; i++) {
            float x1 = interval_left_right * i + interval_left_right / 2;
            float y1 = milliliter.get(i) < 0 ? (htight + top) : (htight - (float) (base * milliliter.get(i)) + top);
            float x2 = interval_left_right * (i + 1) + interval_left_right / 2;
            float y2 = milliliter.get(i + 1) < 0 ? (htight + top) : (htight - (float) (base * milliliter.get(i + 1)) + top);
            c.drawLine(x1, y1, x2, y2, paint_brokenLine);
        } }


    /**
     * 绘制利润折线
     *
     * @param c
     * @param milliliter
     */
    private void drawBrokShader(Canvas c, List<Double> milliliter) {
        Shader mShader = new LinearGradient(interval_left_right, htight - (float) (base * Collections.max(milliliter)) + top, interval_left_right, htight + top, new int[]{
                Color.parseColor("#4c41b784"),
                //Color.argb(45, 39, 195, 160),
                Color.parseColor("#0041b784")},null, Shader.TileMode.CLAMP);
        framPanint.setShader(mShader);
        for (int i = 0; i < milliliter.size() - 1; i++) {
            float x1 = interval_left_right * i + interval_left_right / 2;
            float y1 = milliliter.get(i) < 0 ? (htight + top) : (htight - (float) (base * milliliter.get(i)) + top);
            float x2 = interval_left_right * (i + 1) + interval_left_right / 2;
            float y2 = milliliter.get(i + 1) < 0 ? (htight + top) : (htight - (float) (base * milliliter.get(i + 1)) + top);
            if (i == 0) {
                path.moveTo(x1, y1);
            } else {
                path.lineTo(x1, y1);
            }
            if (i == milliliter.size() - 2) {
                path.lineTo(x2, y2);
                path.lineTo(x2, getHeight());
                path.lineTo(interval_left_right / 2, getHeight());
                path.close();
                c.drawPath(path, framPanint);
            }
        }
    }

    /**
     * 绘制利润折线
     *
     * @param c
     */
    public void drawBrokenLine(Canvas c, List<Double> milliliter) {
        initBrokenLinePaint();
        interval_left_right = getWidth() / 7;
        for (int i = 0; i < milliliter.size() - 1; i++) {

            float x1 = interval_left_right * i + interval_left_right / 2;
            float y1 = milliliter.get(i) < 0 ? (htight + top) : (htight - (float) (base * milliliter.get(i)) + top);
            float x2 = interval_left_right * (i + 1) + interval_left_right / 2;
            float y2 = milliliter.get(i + 1) < 0 ? (htight + top) : (htight - (float) (base * milliliter.get(i + 1)) + top);

            c.drawLine(x1, y1, x2, y2, paint_brokenLine);
        }
         }
}
