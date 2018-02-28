package com.yatang.xc.xcr.adapter;

import java.util.List;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by dengjiang on 2017/4/26.
 */

public class TaskListAdapter extends PagerAdapter {
    List<RecyclerView> viewLists;

    public TaskListAdapter(List<RecyclerView> lists)
    {
        viewLists = lists;
    }

    //获得size
    @Override
    public int getCount() {
        return viewLists.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    //销毁Item
    @Override
    public void destroyItem(View view, int position, Object object)
    {
        ((ViewPager) view).removeView(viewLists.get(position));
    }

    //实例化Item
    @Override
    public Object instantiateItem(View view, int position)
    {
        ((ViewPager) view).addView(viewLists.get(position), 0);
        return viewLists.get(position);
    }
}
