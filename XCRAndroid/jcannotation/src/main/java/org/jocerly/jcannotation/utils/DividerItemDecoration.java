package org.jocerly.jcannotation.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 分割线
 * @author asus
 *
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration{
	public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
	public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    private Paint mPaint;
    private Drawable mDivider;
    private int mDividerHeight = 10;
    private int mOrientation;

    /**
     * 自定义高度分割线
     * @param context
     * @param orientation
     * @param height
     */
    public DividerItemDecoration(Context context, int orientation, int height){
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        mDividerHeight = height;
        a.recycle();
        setOrientation(orientation);
    }
    
    public DividerItemDecoration(Context context, int orientation){
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);
    }
    
    /**
     * 自定义分割线
     *
     * @param context
     * @param orientation 列表方向
     * @param height 高度
     * @param dividerColor 分割线颜色
     */
    public DividerItemDecoration(Context context, int orientation, int height, int dividerColor) {
    	final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        mDividerHeight = height;
        
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	mPaint.setColor(dividerColor);
    	mPaint.setStyle(Paint.Style.FILL);
        a.recycle();
        setOrientation(orientation);
    }
    
    /**
     * 自定义分割线
     *
     * @param context
     * @param orientation 列表方向
     * @param height 高度
     * @param drawableId 分割线图片
     * @param dividerColor 分割线颜色
     */
    public DividerItemDecoration(Context context, int orientation, int height, int drawableId, int dividerColor) {
    	final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(drawableId);
        mDividerHeight = height;
        
        if (dividerColor != 0) {
        	mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        	mPaint.setColor(dividerColor);
        	mPaint.setStyle(Paint.Style.FILL);
		}
        
        a.recycle();
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }

    }

    /**
     * 塑形分割线
     */
    public void drawVertical(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView v = new RecyclerView(parent.getContext());
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDividerHeight;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
            if (mPaint != null) {
            	canvas.drawRect(left, top, right, bottom, mPaint);
			}
        }
    }

    /**
     * 横向分割线
     */
    public void drawHorizontal(Canvas canvas, RecyclerView parent){
        final int top = parent.getPaddingTop();
        int bottom = 0;

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            bottom = child.getHeight();
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDividerHeight;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
            if (mPaint != null) {
            	canvas.drawRect(left, top, right, bottom, mPaint);
			}
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mDividerHeight);
        } else {
            outRect.set(0, 0, mDividerHeight, 0);
        }
    }
}