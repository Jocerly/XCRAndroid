package com.yatang.xc.xcr.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.supchain.uitls.JCLoger;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.ActionBarManager;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.fragment.BaseFragment;
import com.yatang.xc.xcr.fragment.CouponContentFragment;
import com.yatang.xc.xcr.fragment.CouponFragment;
import com.yatang.xc.xcr.fragment.CouponStatisticsFragment;
import com.yatang.xc.xcr.fragment.DiscountContentFragment;
import com.yatang.xc.xcr.fragment.DiscountStatisticsFragment;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动详情页面
 * Created by dengjiang on 2017/10/19.
 */
@ContentView(R.layout.activity_coupondetails)
public class CouponDetailsActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    @BindView(id = R.id.btnRight)
    private TextView btnRight;
    @BindView(id = R.id.radioContent)
    private RadioButton radioContent;
    @BindView(id = R.id.radioStatistics)
    private RadioButton radioStatistics;
    @BindView(id = R.id.viewPager)
    private ViewPager viewPager;
    private NomalDialog dialog;
    private FragmentPagerAdapter mAdapter;
    private List<BaseFragment> fragmentList;
    private String eventID, eventType, status;

    private final String[] titles = new String[]{"活动内容", "效果统计"};

    @Override
    public void initWidget() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            eventID = bundle.getString("EventId", "");
            eventType = bundle.getString("EventType", "");
            status = bundle.getString("Status", "");
        }
        initFragment();
        int color = getResources().getColor(R.color.red);
        ActionBarManager.initBackToolbar(this, "活动详情", "作废", color)[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });
        radioContent.setOnCheckedChangeListener(this);
        radioStatistics.setOnCheckedChangeListener(this);
        updateRadioButton(radioContent, true);
        viewPager.setOffscreenPageLimit(titles.length); //设置缓存
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
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
                        if (!radioContent.isChecked()) {
                            radioContent.setChecked(true);
                        }
                        break;
                    case 1:
                        if (!radioStatistics.isChecked()) {
                            radioStatistics.setChecked(true);
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
        if ("1".equals(eventType)) {
            //初始化优惠券数据
            getCouponDetailsData(true, true);

        } else if ("2".equals(eventType)) {
            //初始化折扣数据
            getDiscountDetailsData(true, true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("活动详情");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("活动详情");
        MobclickAgent.onPause(aty);
    }

    private void initFragment() {
        fragmentList = new ArrayList<>();
        if ("1".equals(eventType)) {
            //初始化优惠券
            fragmentList.add(new CouponContentFragment());
            fragmentList.add(new CouponStatisticsFragment());

        } else if ("2".equals(eventType)) {
            //初始化折扣
            fragmentList.add(new DiscountContentFragment());
            fragmentList.add(new DiscountStatisticsFragment());
        }
    }


    /**
     * 获取数据折扣活动详情
     *
     * @return
     */
    private void getDiscountDetailsData(final boolean isShowDialog, final boolean isShowToast) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("EventStatus", status);
        params.put("EventId", eventID);
        httpRequestService.doRequestData(aty, "User/SpecialPriceEventDetail", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    status = resultParam.mapData.get("EventStatus");
                    if ("3".equals(status)) {
                        btnRight.setVisibility(View.GONE);
                    } else {
                        btnRight.setVisibility(View.VISIBLE);
                    }
                    DiscountContentFragment fragment = (DiscountContentFragment) fragmentList.get(0);
                    fragment.setData(resultParam);
                    DiscountStatisticsFragment couponStatisticsFragment = (DiscountStatisticsFragment) fragmentList.get(1);
                    couponStatisticsFragment.setData(resultParam);
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }


    /**
     * 获取数据优惠券活动
     *
     * @return
     */
    private void getCouponDetailsData(final boolean isShowDialog, final boolean isShowToast) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("EventStatus", status);
        params.put("EventId", eventID);
        httpRequestService.doRequestData(aty, "User/CouponEventDetail", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    status = resultParam.mapData.get("EventStatus");
                    if ("3".equals(status)) {
                        btnRight.setVisibility(View.GONE);
                    } else {
                        btnRight.setVisibility(View.VISIBLE);
                    }
                    CouponContentFragment fragment = (CouponContentFragment) fragmentList.get(0);
                    fragment.setData(resultParam);
                    CouponStatisticsFragment couponStatisticsFragment = (CouponStatisticsFragment) fragmentList.get(1);
                    couponStatisticsFragment.setData(resultParam);
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    private void deleteCoupon(final boolean isShowDialog, final boolean isShowToast) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("Action", "1");
        params.put("EventId", eventID);
        httpRequestService.doRequestData(aty, "User/UpdateEventStatus", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    btnRight.setVisibility(View.GONE);
                    toast("活动已作废成功");
                    setResult(RESULT_OK);
                    finish();
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.radioContent:
                if (b) {
                    viewPager.setCurrentItem(0);
                }
                break;
            case R.id.radioStatistics:
                if (b) {
                    viewPager.setCurrentItem(1);
                }
                break;
        }
        updateRadioButton(compoundButton, b);
    }

    /**
     * 弹出作废确认对话框
     */
    private void showDeleteDialog() {
        if (dialog == null) {
            dialog = new NomalDialog(aty);
            dialog.setOnNoamlLickListener(new NomalDialog.OnNoamlLickListener() {
                @Override
                public void onOkClick() {
                    deleteCoupon(true, true);
                }
            });
        }
        dialog.show("确认作废活动吗？", "取消", "确认");
    }

    /**
     * 更新tab 的状态
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
}
