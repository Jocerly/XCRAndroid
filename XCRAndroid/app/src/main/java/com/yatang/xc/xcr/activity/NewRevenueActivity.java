package com.yatang.xc.xcr.activity;

import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.ActionBarManager;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.db.RevenueBaseEn;
import com.yatang.xc.xcr.db.RevenueEntity;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.uitls.ToolCacheUtil;
import com.yatang.xc.xcr.views.CardView.CardItem;
import com.yatang.xc.xcr.views.CardView.CardPagerAdapter;
import com.yatang.xc.xcr.views.CardView.ShadowTransformer;
import com.yatang.xc.xcr.views.RevenueColorView;
import com.yatang.xc.xcr.views.wireframe.HomeDiagram;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.widget.recyclevew.MyScrollview;
import org.jocerly.jcannotation.widget.swiperefreshlayout.JCSwipeRefreshLayout;

import java.util.Calendar;
import java.util.Date;

/**
 * 门店收入activity
 * Created by zengxiaowen on 2017/7/17.
 */

@ContentView(R.layout.activity_newrevenue)
public class NewRevenueActivity extends BaseActivity {

    @BindView(id = R.id.swipeLayout)
    private JCSwipeRefreshLayout mSwipeLayout;
    @BindView(id = R.id.scrollView)
    private MyScrollview scrollView;

    @BindView(id = R.id.viewPager)
    private ViewPager mViewPager;
    @BindView(id = R.id.radioGroup)
    private RadioGroup radioGroup;

    // 门店收入分布
    @BindView(id = R.id.textProfitValue)
    private TextView textProfitValue;//利润
    @BindView(id = R.id.textZhifubao)
    private TextSwitcher textZhifubao;
    @BindView(id = R.id.textWeixin)
    private TextSwitcher textWeixin;
    @BindView(id = R.id.textRevenueCard)
    private TextSwitcher textCouponCash;
    @BindView(id = R.id.textRevenueCash)
    private TextSwitcher textRevenueCash;
    @BindView(id = R.id.colorView1)
    private RevenueColorView colorView1;
    @BindView(id = R.id.colorView2)
    private RevenueColorView colorView2;
    @BindView(id = R.id.colorView3)
    private RevenueColorView colorView3;
    @BindView(id = R.id.colorView4)
    private RevenueColorView colorView4;

    // 外卖收入分布
    @BindView(id = R.id.textGoods)
    private TextSwitcher textGoods;
    @BindView(id = R.id.textPeis)
    private TextSwitcher textPeis;
    @BindView(id = R.id.textYou)
    private TextSwitcher textYou;
    @BindView(id = R.id.colorView5)
    private RevenueColorView colorView5;
    @BindView(id = R.id.colorView6)
    private RevenueColorView colorView6;
    @BindView(id = R.id.colorView7)
    private RevenueColorView colorView7;
    @BindView(id = R.id.textProfit)
    private TextView textProfit;

    @BindView(id = R.id.linear)
    private RelativeLayout linear;  //折线图

    @BindView(id = R.id.mend)
    private LinearLayout mend; // 门店收入分布
    @BindView(id = R.id.waim)
    private LinearLayout waim; // 外卖收入分布

    private ShadowTransformer mCardShadowTransformer;
    private CardPagerAdapter mCardAdapter;

    private TextView[] textViews; //标题+右侧按钮

    public static final int MEND = 1;  // 门店
    public static final int WAIM = 2;  // 外卖

    private static final String MENDFILENAME = "/Reven.txt";  // 门店收入缓存地址
    private static final String WAIMFILENAME = "/OutDetial.txt";  // 门店外卖缓存地址

    private int Type = MEND;

    private RevenueBaseEn baseEn; // 页面数据

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("店铺收入");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("店铺收入");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        int color = getResources().getColor(R.color.green);

        mSwipeLayout.setColorSchemeResources(R.color.green);
        mSwipeLayout.setOnRefreshListener(refreshlistener);  //设置下拉刷新监听器
        scrollView.getViewTreeObserver().addOnScrollChangedListener(myScrollChangedListener);

        textViews = ActionBarManager.initBackToolbar(this, "门店收入", "外送收入", color);
        textZhifubao.setFactory(new ViewFactoryImp());
        setSwitcherViewAnim(textZhifubao);
        textWeixin.setFactory(new ViewFactoryImp());
        setSwitcherViewAnim(textWeixin);
        textCouponCash.setFactory(new ViewFactoryImp());
        setSwitcherViewAnim(textCouponCash);
        textRevenueCash.setFactory(new ViewFactoryImp());
        setSwitcherViewAnim(textRevenueCash);
        textGoods.setFactory(new ViewFactoryImp());
        setSwitcherViewAnim(textGoods);
        textPeis.setFactory(new ViewFactoryImp());
        setSwitcherViewAnim(textPeis);
        textYou.setFactory(new ViewFactoryImp());
        setSwitcherViewAnim(textYou);

        MobclickAgent.onEvent(aty, "Revenue_In");
        textViews[1].setTag(WAIM);  // 默认显示"外卖收入"
        mend.setVisibility(View.VISIBLE);
        waim.setVisibility(View.GONE);
        textViews[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch ((int) v.getTag()) {
                    case MEND:
                        MobclickAgent.onEvent(aty, "Revenue_In");

                        textViews[0].setText("门店收入");
                        textViews[1].setText("外送收入");
                        mend.setVisibility(View.VISIBLE);
                        waim.setVisibility(View.GONE);
                        textViews[1].setTag(WAIM);
                        Type = MEND;
                        break;
                    case WAIM:
                        MobclickAgent.onEvent(aty, "Revenue_Out");

                        textViews[0].setText("外送收入");
                        textViews[1].setText("门店收入");
                        mend.setVisibility(View.GONE);
                        waim.setVisibility(View.VISIBLE);
                        textViews[1].setTag(MEND);
                        Type = WAIM;
                        break;
                }
                mCardAdapter.setType(Type);
                String fileName = Type == MEND ? MENDFILENAME : WAIMFILENAME;
                String data = null;
                if (data == null) {
                    getClientRest(true, Type);
                } else {
                    JCLoger.debug("读取缓存数据：" + data);
                    setViewDate(data);
                }
            }
        });

        initPageView();
    }

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
     * 下拉刷新
     */
    JCSwipeRefreshLayout.OnRefreshListener refreshlistener = new JCSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getClientRest(false, Type);
                }
            }, Constants.RefreshTime);
        }
    };

    /**
     * 初始化pageview
     */
    private void initPageView() {
        mCardAdapter = new CardPagerAdapter((BaseActivity) aty);
        mCardAdapter.setType(Type);
        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(5);

        // 监听自定义日期
        mCardAdapter.settitOnClickListener(new CardPagerAdapter.titOnClickListener() {
            @Override
            public void ontiClick(RevenueEntity entity, String time) {
                baseEn.getRevenueList().set(5, entity);
                String temp = Common.formatTosepara(entity.getRevenueAllValue() + "", 3, 2);
                mCardAdapter.setCardItem(5, new CardItem(temp, time));
                mCardAdapter.notifyDataSetChanged();
                setRevenueText(entity);
            }
        });
        // 选中放大
        mCardShadowTransformer.enableScaling(true);

        initRadioGroup();

    }

    /**
     * 为TextSwitcher设置动画
     *
     * @param switcher
     */
    private void setSwitcherViewAnim(TextSwitcher switcher) {
        switcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        switcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
    }

    /**
     * ViewSwitcher工厂
     */
    private class ViewFactoryImp implements ViewSwitcher.ViewFactory {

        @Override
        public View makeView() {
            TextView txt = new TextView(aty);
            txt.setTextColor(getResources().getColor(R.color.text_dark));
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(13);
            return txt;
        }
    }

    /**
     * 初始化page圆点
     */
    @SuppressWarnings("ResourceType")
    private void initRadioGroup() {
        for (int i = 0; i < 6; i++) {
            RadioButton raBut = new RadioButton(aty);
            raBut.setButtonDrawable(R.drawable.checkbox_pageview);
            raBut.setPadding(5, 0, 5, 0);
            raBut.setId(i);
            raBut.setEnabled(false);
            radioGroup.addView(raBut);
        }
        radioGroup.check(0);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                radioGroup.check(position);
                if (position == 5 && baseEn.getRevenueList().size() == 5) {
                    setRevenueText(new RevenueEntity());
                } else {
                    setRevenueText(baseEn.getRevenueList().get(position));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        TextView[] textViews = {(TextView) findViewById(R.id.day1), (TextView) findViewById(R.id.day2), (TextView) findViewById(R.id.day3), (TextView) findViewById(R.id.day4), (TextView) findViewById(R.id.day5), (TextView) findViewById(R.id.day6), (TextView) findViewById(R.id.day7)};
        Calendar c = Calendar.getInstance();//
        c.setTime(new Date());
        for (int i = 7; i > 0; i--) {
            c.set(Calendar.DATE, c.get(Calendar.DATE) - i);
            int mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
            int mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当日期
            c.setTime(new Date());
            textViews[i - 1].setText(mMonth + "." + mDay);
        }
        Common.setViewPageHeight(mViewPager, Common.dip2px(aty, 25));
    }

    @Override
    public void initData() {

        String data = null;
        if (data == null) {
            getClientRest(true, Type);
        } else {
            JCLoger.debug("读取缓存数据：" + data);
            setViewDate(data);
        }
//        getClientRest(1);
    }

    /**
     * 获取数据
     * @param isShowDialog
     * @param Type
     */
    private void getClientRest(final boolean isShowDialog, final int Type) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("ScreenStatue", "1"); //筛选条件 1:(昨天、本今日实时、月实时、近7天、近30天），5：自定义时间
        params.put("Type", Type); //查询类型 1：门店、2：线上
        httpRequestService.doRequestData(aty, "User/RevenueOrOutDetial", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    if (mSwipeLayout.isRefreshing()) {
                        mSwipeLayout.setRefreshing(false);  //下拉刷新结束
                    }
                    if (!isShowDialog) {
                        toast("刷新成功");
                    }
                    setViewDate(resultParam.mapData.toString());
                    //设置缓存
                    String fileName = Type == MEND ? MENDFILENAME : WAIMFILENAME;
                    ToolCacheUtil.setUrlCache(resultParam.mapData.toString(), getCacheDir() + fileName);
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
     * 数据布置
     *
     * @param data
     */
    private void setViewDate(String data) {
        Gson gson = new Gson();
        baseEn = gson.fromJson(data, RevenueBaseEn.class);

        baseEn.getRevenueList().add(new RevenueEntity());
        mCardAdapter.clear();
        for (int i = 0; i < baseEn.getRevenueList().size(); i++) {
            String temp = Common.formatTosepara(baseEn.getRevenueList().get(i).getRevenueAllValue() + "", 3, 2);
            mCardAdapter.addCardItem(new CardItem(temp, getTitBar(i)));
        }
        mCardAdapter.notifyDataSetChanged();
        setRevenueText(baseEn.getRevenueList().get(mViewPager.getCurrentItem()));

        JCLoger.debug("解析数据：" + baseEn.toString());
        linear.removeAllViews();
        linear.addView(new HomeDiagram(aty, baseEn.getRevenueSevenDay(), baseEn.getProfitSevenDay()));
    }

    /**
     * 设置柱状图
     *
     * @param ent
     */
    private void setRevenueText(RevenueEntity ent) {
        double allValue = ent.getRevenueAllValue();
        boolean zele = allValue <= 0;
        double temp = ent.getProfitValue();
        textProfitValue.setText(Common.formatTosepara(temp + "", 3, 2));
        textProfit.setText(Common.formatTosepara(temp + "", 3, 2));
        // 支付宝
        temp = ent.getRevenueZhifubao();
        textZhifubao.setText(Common.formatTosepara(temp + "", 3, 2));
        colorView1.setSize(zele ? 0 : (int) Math.rint(temp / allValue * 100));
        // 微信
        temp = ent.getRevenueWeixin();
        textWeixin.setText(Common.formatTosepara(temp + "", 3, 2));
        colorView2.setSize(zele ? 0 : (int) Math.rint(temp / allValue * 100));
        //电子券
        temp = ent.getCouponCash();
        textCouponCash.setText(Common.formatTosepara(temp + "", 3, 2));
        colorView3.setSize(zele ? 0 : (int) Math.rint(temp / allValue * 100));
        // 现金
        temp = ent.getRevenueCash();
        textRevenueCash.setText(Common.formatTosepara(temp + "", 3, 2));
        colorView4.setSize(zele ? 0 : (int) Math.rint(temp / allValue * 100));

        // 商品
        temp = ent.getGoodsNum();
        textGoods.setText(Common.formatTosepara(temp + "", 3, 2));
        colorView5.setSize(zele ? 0 : (int) Math.rint(temp / allValue * 100));
        // 配送费
        temp = ent.getDeliveryFee();
        textPeis.setText(Common.formatTosepara(temp + "", 3, 2));
        colorView6.setSize(zele ? 0 : (int) Math.rint(temp / allValue * 100));
        // 优惠券
        temp = ent.getCoupon();
        textYou.setText(Common.formatTosepara(temp + "", 3, 2));
        colorView7.setSize(zele ? 0 : (int) Math.rint(temp / allValue * 100));
    }

    /**
     * 1:今日实时，2：本月实时，3：近7天，4：近30天，5：自定义时间
     *
     * @param status
     * @return
     */
    private String getTitBar(int status) {
        switch (status) {
            case 0:
                return "今日实时 (元)";
            case 1:
                return "昨天 (元)";
            case 2:
                return "本月实时 (元)";
            case 3:
                return "近7天 (元)";
            case 4:
                return "近30天 (元)";
            case 5:
                return "自定义日期";
            default:
                return "";
        }
    }
}
