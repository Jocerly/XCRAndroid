package com.yatang.xc.xcr.fragment;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.yatang.plugin.navigation.CordovaPageActivity;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.activity.BrowserActivity;
import com.yatang.xc.xcr.activity.MainActivity;
import com.yatang.xc.xcr.activity.NewRevenueActivity;
import com.yatang.xc.xcr.activity.SignActivity;
import com.yatang.xc.xcr.activity.StartActivity;
import com.yatang.xc.xcr.activity.WebViewActivity;
import com.yatang.xc.xcr.adapter.ADAdapter;
import com.yatang.xc.xcr.adapter.ImageViewHolder;
import com.yatang.xc.xcr.adapter.MainViewFragemntAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.db.ADEntity;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.entity.MainViewEntity;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.DataAnalyze;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.uitls.VersionInfoHelper;
import com.yatang.xc.xcr.views.CustomViewPager;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.utils.DensityUtils;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.widget.listView.CustomerListView;
import org.jocerly.jcannotation.widget.recyclevew.MyScrollview;
import org.jocerly.jcannotation.widget.swiperefreshlayout.JCSwipeRefreshLayout;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 主界面
 * Created by Jocerly on 2017/3/6.
 */

public class MainFragment extends BaseFragment {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textMsgNum)
    private TextView textMsgNum;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    //今日营收
    @BindView(id = R.id.llRevenueNum, click = true)
    private LinearLayout llRevenueNum;
    @BindView(id = R.id.textRevenueNum)
    private TextView textRevenueNum;

    @BindView(id = R.id.swipeLayout)
    private JCSwipeRefreshLayout mSwipeLayout;
    @BindView(id = R.id.scrollView)
    private MyScrollview scrollView;

    @BindView(id = R.id.adBanner)
    private ConvenientBanner adBanner;
    @BindView(id = R.id.recyclerAD)
    private CustomerListView recyclerAD;
    @BindView(id = R.id.mainView)
    private CustomViewPager mainView;
    @BindView(id = R.id.radioGroup)
    private RadioGroup radioGroup;

    private String url;
    private int width;
    private int height;
    private NomalDialog dialog;

    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();//轮播图片数据

    private List<List<MainViewEntity>> mainViewEntities = new ArrayList<>();
    private MainViewFragemntAdapter mainViewFragemntAdapter;
    private ArrayList<MainViewFragment> fragments;
    private ADAdapter adAdapter;
    private List<ADEntity> listData_AD = new ArrayList<>();

    private int mainViewIndex = 0;  //记住MainView的位置

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_main, null);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        mSwipeLayout.setColorSchemeResources(R.color.red);
        mSwipeLayout.setOnRefreshListener(refreshlistener);  //设置下拉刷新监听器
        scrollView.getViewTreeObserver().addOnScrollChangedListener(myScrollChangedListener);

        btnLeft.setText("");
        btnLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.left_menu_back_bg, 0, 0, 0);
        btnRight.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sign_in, 0, 0, 0);
        textTitle.setText(MyApplication.instance.StoreSerialNameDefault);

        addMainDate("0", "0", "0");

        setImageHeight();
        initBannerListener();
        initViewPage();//初始化ViewPage
        initRecyclerAD();//初始化 采购 广告
        if (!Common.isNotificationEnabled(getContext())) {
            showOpenNotificationDialog();
        }
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        for (MainViewFragment mFragment : fragments) {
            if (mFragment == null && childFragment instanceof MainViewFragment) {
                mFragment = (MainViewFragment) childFragment;
                fragments.add(mFragment);
            }
        }
    }

    /**
     * 初始化ViewPage
     */
    private void initViewPage() {
        fragments = new ArrayList<>();
        for (int i = 0; i < mainViewEntities.size(); i++) {
            MainViewFragment fragment = new MainViewFragment(mSwipeLayout, mainViewEntities.get(i));
            fragments.add(fragment);
        }
        mainViewFragemntAdapter = new MainViewFragemntAdapter(getFragmentManager(), fragments);
        mainView.setAdapter(mainViewFragemntAdapter);

        initRadioGroup();
    }

    /**
     * 初始化采购广告UI
     */
    private void initRecyclerAD() {
        adAdapter = new ADAdapter(aty, listData_AD);
        adAdapter.initSize(width, height);
        LinearLayoutManager layoutManager = new LinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerAD.setAdapter(adAdapter);
        adAdapter.setOnItemClickListener(adClickListenner);
    }

    private ADAdapter.OnItemClickListener adClickListenner = new ADAdapter.OnItemClickListener() {

        @Override
        public void itemClick(String AdUrl) {
            MobclickAgent.onEvent(aty, "Home_Recommend");
            Bundle bundle = new Bundle();
            if (AdUrl.startsWith("xcr")) {
                //跳转到 采购页面
                bundle.putString("AdJump", AdUrl);
                //判断文件存在
                skipActivity(aty, VersionInfoHelper.checkWebFileExists() ? CordovaPageActivity.class : StartActivity.class, bundle);
            } else {
                //跳转到常规的页面浏览页面
                bundle.putString("ClassUrl", AdUrl);
                skipActivity(aty, BrowserActivity.class, bundle);
            }
        }
    };

    /**
     * 初始化ViewPage圆点
     */
    private void initRadioGroup() {
        for (int i = 0; i < fragments.size(); i++) {
            RadioButton raBut = new RadioButton(aty);
            raBut.setButtonDrawable(R.drawable.checkbox_main_pageview);
            raBut.setId(i);
            raBut.setPadding(5, 0, 5, 0);
            raBut.setEnabled(false);
            radioGroup.addView(raBut);
        }
        radioGroup.check(0);
        mainView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                radioGroup.check(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 设置主页数据
     */
    private void addMainDate(String taskNum, String orderNum, String manTime) {
        mainViewEntities.clear();
        List<MainViewEntity> entities = new ArrayList<>();
        //第一行
        MainViewEntity en = new MainViewEntity();
        en.setTitle("店铺收入");
        en.setDrawable(R.drawable.revenue);
        en.setTxtNum("0");
        en.setIndex(1);
        entities.add(en);
        en = new MainViewEntity();
        en.setTitle("交易流水");
        en.setDrawable(R.drawable.transaction);
        en.setTxtNum("0");
        en.setIndex(2);
        entities.add(en);
        en = new MainViewEntity();
        en.setTitle("消息");
        en.setDrawable(R.drawable.msg);
        en.setIndex(3);
        en.setTxtNum(Common.getAppInfo(aty, Constants.Preference.MsgNum, "0"));
        entities.add(en);
        //第二行
        en = new MainViewEntity();
        en.setTitle("外送订单");
        en.setDrawable(R.drawable.iconorder);
        en.setTxtNum(orderNum);
        en.setIndex(4);
        entities.add(en);
        en = new MainViewEntity();
        en.setTitle("店铺活动");
        en.setDrawable(R.drawable.iconcoupon);
        en.setTxtNum("0");
        en.setIndex(5);
        entities.add(en);
        en = new MainViewEntity();
        en.setTitle("我要进货");
        en.setDrawable(R.drawable.purchase);
        en.setTxtNum("0");
        en.setIndex(6);
        entities.add(en);
        mainViewEntities.add(entities);

        //第二页
        //第一行
        entities = new ArrayList<>();
        en = new MainViewEntity();
        en.setTitle("外送商品");
        en.setDrawable(R.drawable.ic_wssp);
        en.setTxtNum("0");
        en.setIndex(7);
        entities.add(en);
        en = new MainViewEntity();
        en.setTitle("库存管理");
        en.setDrawable(R.drawable.stock_manager);
        en.setTxtNum("0");
        en.setIndex(8);
        entities.add(en);
        en = new MainViewEntity();
        en.setTitle("门店商品");
        en.setDrawable(R.drawable.goods_manager);
        en.setTxtNum("0");
        en.setIndex(9);
        entities.add(en);
        //第二行
        en = new MainViewEntity();
        en.setTitle("数据统计");
        en.setDrawable(R.drawable.data_statistics);
        en.setTxtNum("0");
        en.setIndex(10);
        entities.add(en);
        en = new MainViewEntity();
        en.setTitle("结算管理");
        en.setDrawable(R.drawable.settlement_manage);
        en.setTxtNum("0");
        en.setIndex(11);
        entities.add(en);
        en = new MainViewEntity();
        en.setTitle("我的任务");
        en.setDrawable(R.drawable.my_task);
        en.setTxtNum(taskNum);
        en.setIndex(12);
        entities.add(en);
        mainViewEntities.add(entities);

        //第三页
        //第一行
        entities = new ArrayList<>();
        en = new MainViewEntity();
        en.setTitle("小超课堂");
        en.setDrawable(R.drawable.clazz);
        en.setTxtNum("0");
        en.setMaxClassTime(manTime);
        en.setIndex(13);
        entities.add(en);
        en = new MainViewEntity();
        en.setTitle("在线客服");
        en.setDrawable(R.drawable.connect);
        en.setTxtNum("0");
        en.setIndex(14);
        entities.add(en);

        mainViewEntities.add(entities);
    }

    /**
     * 没有首页轮播图时，默认给一个空值图片
     */
    private void initImgData() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.put("AdvertisPic", "");
        map.put("AdvertisUrl", "");
        listData.add(map);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
    }

    @Override
    public void onResume() {
        super.onResume();
        textTitle.setText(MyApplication.instance.StoreSerialNameDefault);
        doSignIn(true);
    }

    /**
     * 图片高度设置
     */
    private void setImageHeight() {
        width = (DensityUtils.getScreenW(aty));
        height = width * 22 / 75;
        adBanner.setMinimumHeight(height);
        adBanner.setMinimumWidth(width);
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
     * 下啊刷新
     */
    JCSwipeRefreshLayout.OnRefreshListener refreshlistener = new JCSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(runnable, Constants.RefreshTime);
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            doSignIn(false);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                ((MainActivity) getActivity()).slidingMenuToggle();
                break;
            case R.id.llRevenueNum://今日营收
                MobclickAgent.onEvent(aty, "Home_Income");
                skipActivity(aty, NewRevenueActivity.class);
                break;
            case R.id.btnRight://签到
                skipActivity(aty, SignActivity.class);
                break;
        }
    }

    /**
     * 获取主页数据
     */
    private void doSignIn(final boolean isShowDialog) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        httpRequestService.doRequestData(aty, "User/SignIn", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                //M00：正常，M01：帐号过期重新登录，M02：其他异常信息。
                if (Constants.M00.equals(resultParam.resultId)) {
                    String temp = Common.formatTosepara(resultParam.mapData.get("RevenueNum").toString(), 3, 2);
                    if (!StringUtils.isEmpty(temp)) {
                        textRevenueNum.setText(temp);
                    }
                    //初始化banner广告图片
                    getBannerData(resultParam.mapData.get("AdvertisList"));
                    initBannerData();

                    //初始化采购广告
                    getADData(resultParam.mapData.get("ShortcutFirstList"));
                    doSignIn2(isShowDialog);
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    if (mSwipeLayout.isRefreshing()) {
                        mSwipeLayout.setRefreshing(false);  //下拉刷新结束
                    }
                    toast(R.string.accout_out);
                    ((MainActivity) getActivity()).doEmpLoginOut();
                } else {
                    if (mSwipeLayout.isRefreshing()) {
                        mSwipeLayout.setRefreshing(false);  //下拉刷新结束
                    }
                    toast(resultParam.message);
                }
            }
        });
    }

    /**
     * 获取主页数据2
     */
    private void doSignIn2(final boolean isShowDialog) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        httpRequestService.doRequestData(aty, "User/SignIn2", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setRefreshing(false);  //下拉刷新结束
                }
                //M00：正常，M01：帐号过期重新登录，M02：其他异常信息。
                if (Constants.M00.equals(resultParam.resultId)) {
                    if (!isShowDialog) {
                        toast("刷新成功");
                    }
                    addMainDate(resultParam.mapData.get("TaskNum"), resultParam.mapData.get("OrderNum"), resultParam.mapData.get("MaxClassTime"));

                    fragments = new ArrayList<>();
                    for (int i = 0; i < mainViewEntities.size(); i++) {
                        MainViewFragment fragment = new MainViewFragment(mSwipeLayout, mainViewEntities.get(i));
                        fragments.add(fragment);
                    }

                    mainViewFragemntAdapter.setFragments(fragments);

                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    ((MainActivity) getActivity()).doEmpLoginOut();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    /**
     * 弹出跳转打开弹窗权限的对话框
     */
    private void showOpenNotificationDialog() {
        if (dialog == null) {
            dialog = new NomalDialog(aty);
            dialog.setOnNoamlLickListener(onNoamlLickListene);
        }
        dialog.show("检测到您的系统通知权限未打开\n将影响正常使用", "暂不设置", "立即开启");
    }

    NomalDialog.OnNoamlLickListener onNoamlLickListene = new NomalDialog.OnNoamlLickListener() {
        @Override
        public void onOkClick() {
            Common.requestPermission(aty, Settings.ACTION_SETTINGS);
        }
    };

    /**
     * 初始化 广告轮播监听器
     */
    private void initBannerListener() {
        adBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                doJumpToADPage(position);
            }
        });
    }

    /**
     * 解析banner数据
     */
    private void getBannerData(String json) {
        listData.clear();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                ConcurrentHashMap<String, String> mapdata = DataAnalyze.doAnalyzeJsonArray(jsonArray.getJSONObject(i));
                //添加child
                listData.add(mapdata);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置banner广告数据
     */
    private void initBannerData() {
        if (listData.size() == 0) {
            initImgData();
        }
        adBanner.setPages(new CBViewHolderCreator<ImageViewHolder>() {
            @Override
            public ImageViewHolder createHolder() {
                ImageViewHolder holder = new ImageViewHolder();
                holder.setSize(width, height);
                return holder;
            }
        }, listData);

        if (listData.size() > 1) {
            adBanner.setManualPageable(true);
            adBanner.setCanLoop(true);
            adBanner.startTurning(5000);
            adBanner.setPageIndicator(new int[]{R.drawable.ponit_normal, R.drawable.point_select}); //设置两个点作为指示器
            adBanner.setOnClickListener(null);
        } else {
            adBanner.setCanLoop(false);
            adBanner.setManualPageable(false);
            adBanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doJumpToADPage(0);
                }
            });
        }
    }

    /**
     * 解析采购广告数据数据
     */
    private void getADData(String json) {
        if (StringUtils.isEmpty(json)) {
            //没有广告数据
            recyclerAD.setVisibility(View.GONE);
            return;
        }
        listData_AD = jsonToArrayList(json, ADEntity.class);
        if (listData_AD.size() < 0) {
            //没有广告数据
            recyclerAD.setVisibility(View.GONE);
            return;
        }
        recyclerAD.setVisibility(View.VISIBLE);
        adAdapter.setListData(listData_AD);
        adAdapter.notifyDataSetChanged();
    }

    /**
     * 将Json数组解析成相应的映射对象列表
     *
     * @param json  json字符串
     * @param clazz list的item
     * @return
     */
    public static <T> ArrayList<T> jsonToArrayList(String json, Class<T> clazz) {
        Type type = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();
        ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);

        ArrayList<T> arrayList = new ArrayList<>();
        for (JsonObject jsonObject : jsonObjects) {
            arrayList.add(new Gson().fromJson(jsonObject, clazz));
        }
        return arrayList;
    }

    /**
     * 根据点击项 跳转到广告详情页面
     *
     * @param position 被点击项
     */
    private void doJumpToADPage(int position) {
        MobclickAgent.onEvent(aty, "Home_Carousel");
        if (!MyApplication.instance.isX5Over) {
            toast(R.string.x5_toast);
            return;
        }
        JCLoger.debug("doJumpToADPage:" + position);
        String adUrl = listData.get(position).get("AdvertisUrl");
        if (StringUtils.isEmpty(adUrl)) {
            return;
        }
        String AdvertisTitle = listData.get(position).get("AdvertisTitle");
        if (adUrl.contains("annual.html")) {
            //对年会报名连接做特殊处理
            adUrl += "?token=" + MyApplication.instance.Token + "&userId=" + MyApplication.instance.UserId;
        }
        Bundle bundle = new Bundle();
        bundle.putString("ClassUrl", adUrl);
        bundle.putString("ClassName", StringUtils.isEmpty(AdvertisTitle) ? "精彩活动" : AdvertisTitle);
        skipActivity(aty, WebViewActivity.class, bundle);
    }

    public void setMsgNum() {
        fragments.get(1).setMsgNum();
    }
}
