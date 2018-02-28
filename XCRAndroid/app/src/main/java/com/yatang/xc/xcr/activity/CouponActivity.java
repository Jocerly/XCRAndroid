package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;

import com.flyco.tablayout.SlidingTabLayout;
import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.supchain.uitls.JCLoger;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.ActionBarManager;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.fragment.CouponFragment;
import com.yatang.xc.xcr.uitls.Common;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 店铺活动首页
 * Created by dengjiang on 2017/10/11.
 */

@ContentView(R.layout.activity_coupon)
public class CouponActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    public static final int CODE_ADD_PROMOTION = 1;
    @BindView(id = R.id.radioNotBegin)
    private RadioButton radioNotBegin;
    @BindView(id = R.id.radioAlive)
    private RadioButton radioAlive;
    @BindView(id = R.id.radioEnd)
    private RadioButton radioEnd;
    @BindView(id = R.id.viewPager)
    private ViewPager viewPager;
    private FragmentPagerAdapter mAdapter;
    private List<CouponFragment> couponFragmentList;
    private final String[] titles = new String[]{"未开始", "进行中", "已结束"};
    private SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.SimpleDateFormat);

    @Override
    public void initWidget() {
        initFragment();
        int color = getResources().getColor(R.color.red);
        ActionBarManager.initBackToolbar(this, "店铺活动", "添加", color)[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipActivityForResult(aty, AddPromotionActivity.class, CODE_ADD_PROMOTION);
            }
        });
        radioNotBegin.setOnCheckedChangeListener(this);
        radioAlive.setOnCheckedChangeListener(this);
        radioEnd.setOnCheckedChangeListener(this);
        updateRadioButton(radioNotBegin, true);
        viewPager.setOffscreenPageLimit(titles.length); //设置缓存
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return couponFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return titles.length;
            }
        };
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        //未开始
                        if (!radioNotBegin.isChecked()) {
                            radioNotBegin.setChecked(true);
                        }
                        break;
                    case 1:
                        //进行中
                        if (!radioAlive.isChecked()) {
                            radioAlive.setChecked(true);
                        }
                        break;
                    case 2:
                        //已结束
                        if (!radioEnd.isChecked()) {
                            radioEnd.setChecked(true);
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("店铺活动");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("店铺活动");
        MobclickAgent.onPause(aty);
    }

    private void initFragment() {
        couponFragmentList = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            Bundle args = new Bundle();
            args.putString("Status", (i + 1) + "");
            CouponFragment fragment = new CouponFragment();
            fragment.setCouponActivity(this);
            fragment.setArguments(args);
            couponFragmentList.add(fragment);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.radioNotBegin:
                if (b) {
                    viewPager.setCurrentItem(0);
                }
                break;
            case R.id.radioAlive:
                if (b) {
                    viewPager.setCurrentItem(1);
                }
                break;
            case R.id.radioEnd:
                if (b) {
                    viewPager.setCurrentItem(2);
                }
                break;
        }
        updateRadioButton(compoundButton, b);
    }

    /**
     * 更新tab button的状态
     *
     * @param compoundButton 更新的控件
     * @param checked        控件的选中状态
     */
    private void updateRadioButton(CompoundButton compoundButton, boolean checked) {
        if (checked) {
            //选中的tab
            compoundButton.setTextColor(getResources().getColor(R.color.red));
        } else {
            //未选中的tab
            compoundButton.setTextColor(getResources().getColor(R.color.text_dark));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CODE_ADD_PROMOTION) {
                //添加优惠券成功
                String startTime = String.valueOf(data.getExtras().get("StartTime"));
                String endTime = dateFormat.format(new Date().getTime());
                JCLoger.debug(startTime + "    ---   " + endTime);
                if (!Common.isCurrentDate(startTime, endTime)) {
                    //开始时间大于 当前时间 属于未开始状态
                    if (viewPager.getCurrentItem() != 0) {
                        viewPager.setCurrentItem(0);
                    }
                    couponFragmentList.get(0).setNeedRefresh(true);
                    JCLoger.debug("Not begin");
                } else {
                    //开始时间小于 当前时间 属于进行中状态
                    if (viewPager.getCurrentItem() != 1) {
                        viewPager.setCurrentItem(1);
                    }
                    couponFragmentList.get(1).setNeedRefresh(true);
                    JCLoger.debug("InProgress");
                }
            } else if (requestCode == CouponFragment.CODE_DETAILS) {
                JCLoger.debug("MY2===CODE_DETAILS");
                for (CouponFragment couponFragment : couponFragmentList) {
                    couponFragment.setNeedRefresh(true);
                }
            }
        }
    }

    /**
     * 更新所有tab的内容
     */
    public void refreshAll() {
        for (CouponFragment couponFragment : couponFragmentList) {
            couponFragment.setNeedRefresh(true);
        }
    }
}
