package com.yatang.xc.xcr.views.CardView;

import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;


/**
 * Created by zengxiaowen on 2017/7/17.
 */
public class ShadowTransformer implements ViewPager.OnPageChangeListener, ViewPager.PageTransformer {

    private ViewPager mViewPager;
    private CardAdapter mAdapter;
    private float mLastOffset;
    private boolean mScalingEnabled =true;

    public ShadowTransformer(ViewPager viewPager, CardAdapter adapter) {
        mViewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
        mAdapter = adapter;
    }

    public void enableScaling(boolean enable) {
        mScalingEnabled = enable;
        if (!enable) {
            // shrink main card
            View currentCard = mAdapter.getCardViewAt(mViewPager.getCurrentItem());
            if (currentCard != null) {
                currentCard.animate().scaleY(1);
                currentCard.animate().scaleX(1);
            }

        }else{
            // grow main card

            List<View> views = mAdapter.getCardView();

            for (int i=0; i<views.size();i++){
                if (i!=mViewPager.getCurrentItem()){
                    views.get(i).animate().scaleY(0.9f);
                    views.get(i).animate().scaleY(0.9f);
                }
            }

//            View currentCard = mAdapter.getCardViewAt(mViewPager.getCurrentItem());
//            if (currentCard != null) {
//                currentCard.animate().scaleY(1.1f);
//                currentCard.animate().scaleX(1.1f);
//            }
        }

    }

    @Override
    public void transformPage(View page, float position) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int realCurrentPosition;
        int nextPosition;
        float realOffset;
        boolean goingLeft = mLastOffset > positionOffset;  //左划

        // If we're going backwards, onPageScrolled receives the last position
        // instead of the current one
        if (goingLeft) {
            realCurrentPosition = position + 1;
            nextPosition = position;
            realOffset = 1 - positionOffset;
        } else {
            nextPosition = position + 1;
            realCurrentPosition = position;
            realOffset = positionOffset;
        }

        // Avoid crash on overscroll
        if (nextPosition > mAdapter.getCount() - 1
                || realCurrentPosition > mAdapter.getCount() - 1) {
            return;
        }

        View currentCard = mAdapter.getCardViewAt(realCurrentPosition);

        // This might be null if a fragment is being used
        // and the views weren't created yet
        if (currentCard != null) {
            if (mScalingEnabled) {
//                currentCard.setScaleX((float) (0.9 + 0.1 * (1 - realOffset)));
                currentCard.setScaleY((float) (0.9 + 0.1 * (1 - realOffset)));
            }
//            currentCard.setCardElevation((baseElevation + baseElevation
//                    * (CardAdapter.MAX_ELEVATION_FACTOR - 1) * (1 - realOffset)));
        }

        View nextCard = mAdapter.getCardViewAt(nextPosition);

        // We might be scrolling fast enough so that the next (or previous) card
        // was already destroyed or a fragment might not have been created yet
        if (nextCard != null) {
            if (mScalingEnabled) {
//                nextCard.setScaleX((float) (0.9 + 0.1 * (realOffset)));
                nextCard.setScaleY((float) (0.9 + 0.1 * (realOffset)));
            }
//            nextCard.setCardElevation((baseElevation + baseElevation
//                    * (CardAdapter.MAX_ELEVATION_FACTOR - 1) * (realOffset)));
        }

        mLastOffset = positionOffset;
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
