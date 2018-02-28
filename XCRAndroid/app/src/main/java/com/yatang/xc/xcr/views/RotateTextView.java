package com.yatang.xc.xcr.views;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yatang.xc.xcr.R;

/**
 * 旋转TextView
 *
 * @author Jocerly
 */
public class RotateTextView extends TextView {
    private static final int[] textDegree = new int[]{R.attr.degree, R.attr.transX, R.attr.transY};
    private int degree;
    private int transX;
    private int transY;

    public RotateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, textDegree);
        degree = ta.getInteger(0, 0);
        transX = ta.getDimensionPixelSize(1, 0);
        transY = ta.getDimensionPixelSize(2, 0);
        ta.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.rotate(degree, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        canvas.translate(transX, transY);
        super.onDraw(canvas);
        canvas.restore();
    }
}
