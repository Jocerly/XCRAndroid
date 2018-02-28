package org.jocerly.jcannotation.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import org.jocerly.jcannotation.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自适应横向列表
 * Created by Jocerly on 2017/5/22.
 */

public class JCHorizontalScrollView extends HorizontalScrollView {
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public JCHorizontalScrollView(Context context) {
        super(context);
        setHorizontalScrollBarEnabled(false);
    }

    public JCHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);
    }

    public JCHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setHorizontalScrollBarEnabled(false);
    }

    @Override
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        if (paramBoolean) {
            final LinearLayoutCompat linearLayout = (LinearLayoutCompat) getChildAt(0);
            //  getChildAt(postion) ViewGroup 里面的方法 用来获取指定位置的视图，由于下边的setTopic(List<Topic> list)方法中添加的控件是
            // 一个LinearLayoutCompat布局，所以这里获取的就是他了
            int count = linearLayout.getChildCount();//计算一个LinearLayoutCompat布局中子控件的个数
            TextView localTextView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_hor_text, linearLayout, false);
            localTextView.setText("...");//定义一个文本内容为"..."的TextView,作为溢出内容填充
            localTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.itemLast();
                }
            });
            int width = 0;//初始化linearLayout宽度
            for (int i = 0; i < count; i++) {//遍历所用TextView控件，并累加计算当前linearLayout宽度
                width += linearLayout.getChildAt(i).getWidth();
                if (width + 100 > getWidth()) {//如果当前遍历的第i个TextView时候，linearLayout宽度大于父布局宽度
                    linearLayout.removeViews(i - 1, count - i + 1); // 将当前TextView以后的所有TextView移除
                    linearLayout.addView(localTextView);//添加文本内容为"..."的TextView到末尾
                    break;
                }
            }
        }
    }

    /**
     * 阻止左右滑动
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    /**
     * 设置数据
     *
     * @param list
     * @param key  关键字
     */
    public void setListData(List<ConcurrentHashMap<String, String>> list, String key) {
        removeAllViews();
        this.listData = list;
        LinearLayoutCompat linearLayoutCompat = new LinearLayoutCompat(getContext());
        linearLayoutCompat.setOrientation(LinearLayoutCompat.HORIZONTAL);
        Iterator iterator = listData.iterator();
        while (iterator.hasNext()) {
            final ConcurrentHashMap<String, String> mapData = (ConcurrentHashMap<String, String>) iterator.next();
            TextView localTextView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_hor_text, linearLayoutCompat, false);
            localTextView.setText(mapData.get(key));
            localTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.itemNomal(mapData);
                }
            });
            linearLayoutCompat.addView(localTextView);
        }
        addView(linearLayoutCompat);
    }

    public interface OnItemClickListener {
        void itemNomal(ConcurrentHashMap<String, String> mapData);

        void itemLast();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
