package com.yatang.xc.xcr.activity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.FilterDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.widget.swiperefreshlayout.JCSwipeRefreshLayout;

/**
 * 店铺收入明细Activity
 * Created by Jocerly on 2017/3/8.
 */
@ContentView(R.layout.activity_revenue)
public class RevenueActivity extends BaseActivity {
    @BindView(id = R.id.rlTitle)
    private RelativeLayout rlTitle;
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.swipeLayout)
    private JCSwipeRefreshLayout mSwipeLayout;
    @BindView(id = R.id.rlRevenueCurrentMonth, click = true)
    private RelativeLayout rlRevenueCurrentMonth;
    @BindView(id = R.id.textRevenueAllValue)//总收入
    private TextView textRevenueAllValue;
    @BindView(id = R.id.textRevenueMsg)//总收入描述
    private TextView textRevenueMsg;

    @BindView(id = R.id.scrollView)
    private ScrollView scrollView;
    @BindView(id = R.id.textProfitValue)
    private TextView textProfitValue;//利润
    @BindView(id = R.id.textZhifubao)
    private TextView textZhifubao;
    @BindView(id = R.id.textWeixin)
    private TextView textWeixin;
    @BindView(id = R.id.textRevenueCard)
    private TextView textCouponCash;
    @BindView(id = R.id.textRevenueCash)
    private TextView textRevenueCash;

    //图表
//    @BindView(id = R.id.spChart)
//    private SplineChartView spChart;
    @BindView(id = R.id.img_animator)
    private ImageView img_animator;

//    @BindView(id = R.id.spChart2)
//    private SplineChartView spChart2;
    @BindView(id = R.id.img_animator2)
    private ImageView img_animator2;

    private FilterDialog filterDialog;
    /**
     * 1:今日实时，2：本月实时，3：近7天，4：近30天，5：自定义时间
     */
    private String screenStatue = "2";
    private String startDate = "", endDate = "";

    @Override
    public void initWidget() {
        int color = getResources().getColor(R.color.green);
        rlTitle.setBackgroundColor(color);
        btnRight.setText("筛选");
        textTitle.setText(R.string.revenue);
        mSwipeLayout.setColorSchemeResources(R.color.green);

        filterDialog = new FilterDialog(aty);
        filterDialog.setFilterDialogClickLinster(onFilterDialogClickLinster);
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            screenStatue = bundle.getString("screenStatue", "2");
            showDesData(screenStatue, startDate, endDate);
        }

        scrollView.getViewTreeObserver().addOnScrollChangedListener(myScrollChangedListener);
        mSwipeLayout.setOnRefreshListener(refreshlistener);  //设置下拉刷新监听器
        getRevenueDetial(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAnimator();
    }

    /**
     * 筛选回调
     */
    FilterDialog.OnFilterDialogClickLinster onFilterDialogClickLinster = new FilterDialog.OnFilterDialogClickLinster() {
        @Override
        public void OK(String ScreenStatue, String StartDate, String EndDate) {
            screenStatue = ScreenStatue;
            startDate = StartDate;
            endDate = EndDate;
            showDesData(screenStatue, startDate, endDate);
            getRevenueDetial(true);
        }
    };

    /**
     * 解决scrollView与SwipeRefreshLayout的冲突
     */
    ViewTreeObserver.OnScrollChangedListener myScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {
            mSwipeLayout.setEnabled(scrollView.getScrollY() == 0);
        }
    };

    /**
     * 下啊刷新
     */
    JCSwipeRefreshLayout.OnRefreshListener refreshlistener = new JCSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getRevenueDetial(false);
                }
            }, Constants.RefreshTime);
        }
    };

    /**
     * 获取数据
     */
    private void getRevenueDetial(final boolean isShowDialog) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("ScreenStatue", screenStatue);
        params.put("StartDate", startDate);
        params.put("EndDate", endDate);
        httpRequestService.doRequestData(aty, "User/RevenueDetial", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setRefreshing(false);  //下拉刷新结束
                }
                if (Constants.M00.equals(resultParam.resultId)) {
                    if (!isShowDialog) {
                        toast("刷新成功");
                    }
                    String temp = Common.formatTosepara(resultParam.mapData.get("RevenueAllValue"), 3, 2);
                    if (!StringUtils.isEmpty(temp)) {
                        textRevenueAllValue.setText(temp);
                    }
                    temp = Common.formatTosepara(resultParam.mapData.get("ProfitValue").toString(), 3, 2);//利润
                    if (!StringUtils.isEmpty(temp)) {
                        textProfitValue.setText(temp);
                    }
                    temp = Common.formatTosepara(resultParam.mapData.get("RevenueZhifubao").toString(), 3, 2);
                    if (!StringUtils.isEmpty(temp)) {
                        textZhifubao.setText(temp);
                    }
                    temp = Common.formatTosepara(resultParam.mapData.get("RevenueWeixin").toString(), 3, 2);
                    if (!StringUtils.isEmpty(temp)) {
                        textWeixin.setText(temp);
                    }
                    temp = Common.formatTosepara(resultParam.mapData.get("CouponCash").toString(), 3, 2);
                    if (!StringUtils.isEmpty(temp)) {
                        textCouponCash.setText(temp);
                    }
                    temp = Common.formatTosepara(resultParam.mapData.get("RevenueCash").toString(), 3, 2);
                    if (!StringUtils.isEmpty(temp)) {
                        textRevenueCash.setText(temp);
                    }

//                    setSevenData(resultParam.mapData.get("RevenueSevenDay"), spChart);
//                    setSevenData(resultParam.mapData.get("ProfitSevenDay"), spChart2);
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
     * 文字显示
     *
     * @param statue    1:今日实时，2：本月实时，3：近7天，4：近30天，5：自定义时间
     * @param startDate
     * @param endDate
     */
    private void showDesData(String statue, String startDate, String endDate) {
        switch (Integer.parseInt(statue)) {
            case 1:
                textRevenueMsg.setText("今日实时（元）");
                break;
            case 2:
                textRevenueMsg.setText("本月实时（元）");
                break;
            case 3:
                textRevenueMsg.setText("近7天（元）");
                break;
            case 4:
                textRevenueMsg.setText("近30天（元）");
                break;
            case 5:
                textRevenueMsg.setText(startDate + " 至 " + endDate + "（元）");
                break;
        }
    }

    /**
     * 设置7天折线图
     *
     * @param revenueSevenDay
     * @param spChart
     */
//    private void setSevenData(String revenueSevenDay, SplineChartView spChart) {
//        double maxNum = 0.00;
//        String str = revenueSevenDay.replace("[", "").replace("]", "");
//        String[] arrBill = str.split(",");
//        List<PointD> linePoint1 = new ArrayList<PointD>();
//        for (int i = 0; i < arrBill.length; i++) {
//            double bill = Double.parseDouble(arrBill[i]);
//            linePoint1.add(new PointD(10d * i + 5d, bill));
//            if (bill > maxNum) {
//                maxNum = bill;
//            }
//        }
//        spChart.refreshChart(linePoint1, maxNum);
//    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnRight:
                filterDialog.show(screenStatue, startDate, endDate);
                break;
        }
    }

    private void startAnimator() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        img_animator.setX(0);
        img_animator.invalidate();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img_animator, "translationX", 0f, (float) (screenWidth)).setDuration(2000);
        objectAnimator.start();

        img_animator2.setX(0);
        img_animator2.invalidate();
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(img_animator2, "translationX", 0f, (float) (screenWidth)).setDuration(2000);
        objectAnimator2.start();
    }
}
