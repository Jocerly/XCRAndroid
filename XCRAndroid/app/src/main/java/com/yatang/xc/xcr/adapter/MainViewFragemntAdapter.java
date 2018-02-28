package com.yatang.xc.xcr.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import com.yatang.xc.xcr.fragment.MainViewFragment;

import java.util.ArrayList;

/**
 * Created by zengxiaowen on 2017/7/28.
 */

public class MainViewFragemntAdapter extends FragmentPagerAdapter {
    private int mChildCount = 0;
    private ArrayList<MainViewFragment> fragments;
    private FragmentManager fm;
    public MainViewFragemntAdapter(FragmentManager fm ,ArrayList<MainViewFragment> fragments) {
        super(fm);
        this.fm = fm;
        this.fragments = fragments;
    }

    public void setFragments(ArrayList<MainViewFragment> fragments) {
        if(this.fragments != null){
            FragmentTransaction ft = fm.beginTransaction();
            for(MainViewFragment f:this.fragments){
                ft.remove(f);
            }
            ft.commit();
            ft=null;
            fm.executePendingTransactions();
        }
        this.fragments = fragments;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return  fragments.size();
    }

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();

        super.notifyDataSetChanged();


    }

    @Override
    public int getItemPosition(Object object) {
        if (mChildCount > 0) {
            mChildCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}
