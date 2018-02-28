package com.yatang.xc.xcr.views.CardView;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.views.wireframe.HomeDiagram;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 近7天
 * Created by zengxiaowen on 2017/7/17.
 */

public class SplineChartAdapter extends PagerAdapter implements CardAdapter {

    private List<View> mViews;
    private float mBaseElevation;
    private List<List<Double>> mData;
    private Context mContext;

    public SplineChartAdapter(Context mContext) {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
        this.mContext = mContext;
    }

    public void addCardItem(List<Double> item) {
        mViews.add(null);
        mData.add(item);
    }

    public void clear(){
        mViews.clear();
        mData.clear();
    }

    @Override
    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public View getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public List<View> getCardView() {
        return mViews;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.adapter_spline, container, false);
        container.addView(view);
        bind(position, mData.get(position), view);
        View cardView = view.findViewById(R.id.cardView);

        mViews.set(position, cardView);
        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(int position, List<Double> item, View view) {
        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        if (position == 0) {
            titleTextView.setText("近7天营业收入趋势");
        } else {
            titleTextView.setText("近7天营业利润趋势");
        }
        RelativeLayout linear = (RelativeLayout) view.findViewById(R.id.linear);

        List<Double> doubles = new ArrayList<>();
        doubles.add(1.54);
        doubles.add(10.54);
        doubles.add(100.54);
        doubles.add(1000.54);
        doubles.add(100.54);
        doubles.add(1500.54);
        doubles.add(140.54);

//        linear.addView(new HomeDiagram(mContext, item));

        TextView[] textViews = {(TextView)view.findViewById(R.id.day1),(TextView)view.findViewById(R.id.day2),(TextView)view.findViewById(R.id.day3),(TextView)view.findViewById(R.id.day4),(TextView)view.findViewById(R.id.day5),(TextView)view.findViewById(R.id.day6),(TextView)view.findViewById(R.id.day7)};
        Calendar c = Calendar.getInstance();//
        c.setTime(new Date());
        for (int i=7;i>0;i--){
            c.set(Calendar.DATE,c.get(Calendar.DATE)-i);
            int mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
            int mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当日期
            c.setTime(new Date());
            textViews[i-1].setText(mMonth+"."+mDay);
        }
    }
}
