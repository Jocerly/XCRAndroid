package com.yatang.xc.xcr.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.utils.JCLoger;

/**
 * 矩形色块
 * Created by zengxiaowen on 2017/7/17.
 */

public class RevenueColorView extends View {

    private Paint mPaint;  //色块
    private Paint mPaintTxt;  //字体
    private int opcity; //底部颜色
    private int opc; //占比颜色
    private int size; //占比
    private int mPaintTimes = 0;  //上次占比
    private int sd = 5; //设置动画速度

    private int width;
    private int height;

    private int sizeText = 48; //字体大小
    private int textJian = 10; //字体间距


    public RevenueColorView(@NonNull Context context) {
        super(context);
    }

    public RevenueColorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaintTxt = new Paint();
        mPaintTxt.setAntiAlias(true);
        mPaintTxt.setStyle(Paint.Style.FILL);
        mPaintTxt.setTextSize(sizeText);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RevenueColorView);
        opcity = array.getColor(R.styleable.RevenueColorView_opcity_color, getResources().getColor(R.color.white));
        opc = array.getColor(R.styleable.RevenueColorView_color, getResources().getColor(R.color.white));
        size = array.getInteger(R.styleable.RevenueColorView__size, 0);
        setBackgroundColor(opcity);
        mPaint.setColor(opc);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        width = getWidth();
        height = getHeight();

        mPaintTimes = mPaintTimes + sd;
        int opHeight = height * mPaintTimes / 100;


        if (Math.abs(size - mPaintTimes) > Math.abs(sd)) {

            canvas.drawRect(0, height - opHeight, width, height, mPaint);

            String sTxt = mPaintTimes + "%";
            if (opHeight < sizeText + textJian) {
                mPaintTxt.setColor(opc);
                if (size < 10) {
                    canvas.drawText(sTxt, width / 5 + 15, height - 10, mPaintTxt);
                } else {
                    canvas.drawText(sTxt, width / 5, height - 10, mPaintTxt);
                }
            } else {
                mPaintTxt.setColor(Color.parseColor("#ffffffff"));
                if (size == 100) {
                    canvas.drawText(sTxt, width / 5 - 15, height - opHeight / 2 + sizeText / 2 - textJian / 2, mPaintTxt);
                } else {
                    canvas.drawText(sTxt, width / 5, height - opHeight / 2 + sizeText / 2 - textJian / 2, mPaintTxt);
                }
            }
            invalidate(); //实现动画的关键点
        } else {
            mPaintTimes = size;
            JCLoger.debug("mPaintTimes:" + mPaintTimes);
            opHeight = height * mPaintTimes / 100;

            canvas.drawRect(0, height - opHeight, width, height, mPaint);

            String sTxt = mPaintTimes + "%";
            if (opHeight < sizeText + textJian) {
                mPaintTxt.setColor(opc);
                if (size < 10) {
                    canvas.drawText(sTxt, width / 5 + 15, height - opHeight - 10, mPaintTxt);
                } else {
                    canvas.drawText(sTxt, width / 5, height - opHeight - 10, mPaintTxt);
                }
            } else {
                mPaintTxt.setColor(Color.parseColor("#ffffffff"));
                if (size == 100) {
                    canvas.drawText(sTxt, width / 5 - 15, height - opHeight / 2 + sizeText / 2 - textJian / 2, mPaintTxt);
                } else {
                    canvas.drawText(sTxt, width / 5, height - opHeight / 2 + sizeText / 2 - textJian / 2, mPaintTxt);
                }
            }

        }
    }

    public void setSize(int size) {
        this.size = size;
        if (mPaintTimes > size) {
            sd = -5;
        } else {
            sd = 5;
        }
        invalidate();
    }

}
