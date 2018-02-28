package com.yatang.xc.xcr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.DataStatisticsListAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.FilterDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.DataAnalyze;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.widget.listView.CustomerListView;
import org.jocerly.jcannotation.widget.swiperefreshlayout.JCSwipeRefreshLayout;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据统计
 */
@ContentView(R.layout.activity_data_statistics)
public class DataStatisticsActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;

    @BindView(id = R.id.textFilter, click = true)
    private TextView textFilter;
    @BindView(id = R.id.relaySales, click = true)
    private RelativeLayout relaySales;
    @BindView(id = R.id.relayGain, click = true)
    private RelativeLayout relayGain;
    @BindView(id = R.id.recyclerSales)
    private CustomerListView recyclerSales;
    @BindView(id = R.id.recyclerGain)
    private CustomerListView recyclerGain;
    @BindView(id = R.id.swipeLayout)
    private JCSwipeRefreshLayout mSwipeLayout;
    @BindView(id = R.id.textNoData)
    private TextView textNoData;
    @BindView(id = R.id.lineContent)
    private LinearLayout lineContent;
    private FilterDialog dialog;
    private String ScreenStatue = "1";//筛选的类型 默认是1 今日实时
    private String StartDate = "";//开始时间
    private String EndDate = "";//结束时间
    private DataStatisticsListAdapter adapterSales;
    private DataStatisticsListAdapter adapterGain;
    private List<ConcurrentHashMap<String, String>> listSales;//销量排行数据
    private List<ConcurrentHashMap<String, String>> listGain;//利润排行数据

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("数据统计");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("数据统计");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("数据统计");
        btnRight.setText("筛选");
        dialog = new FilterDialog(DataStatisticsActivity.this);
        dialog.setFilterDialogClickLinster(dialogClickLinster);
        mSwipeLayout.setColorSchemeResources(R.color.red);
        mSwipeLayout.setOnRefreshListener(refreshlistener);  //设置下拉刷新监听器

        listSales = new ArrayList<>();
        listGain = new ArrayList<>();
        adapterSales = new DataStatisticsListAdapter(getApplicationContext(), listSales, 1);
        adapterGain = new DataStatisticsListAdapter(getApplicationContext(), listGain, 2);

        //初始化 销量排行列表view
//        FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(aty);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerSales.setLayoutManager(layoutManager);
//        recyclerSales.addItemDecoration(new DividerItemDecoration(aty, DividerItemDecoration.VERTICAL_LIST,
//                (int) getResources().getDimension(R.dimen.pad1_px), colorGap));
        recyclerSales.setAdapter(adapterSales);

        //初始化 利润排行列表view
//        FullyLinearLayoutManager layoutManager1 = new FullyLinearLayoutManager(aty);
//        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerGain.setLayoutManager(layoutManager1);
//        recyclerGain.addItemDecoration(new DividerItemDecoration(aty, DividerItemDecoration.VERTICAL_LIST,
//                (int) getResources().getDimension(R.dimen.pad1_px), colorGap));
        recyclerGain.setAdapter(adapterGain);
    }

    @Override
    public void initData() {
        getDataList(true);
        textFilter.setText(getFilterText(ScreenStatue));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.relaySales:
                //跳转到 销量 排行 列表
                Bundle bundle = new Bundle();
                bundle.putString("Type", "1");
                bundle.putString("ScreenStatue", ScreenStatue);
                bundle.putString("StartDate", StartDate);
                bundle.putString("EndDate", EndDate);
                bundle.putString("textFilter", textFilter.getText().toString().trim());
                skipActivity(aty, DataStatisticsDetailsActivity.class, bundle);
                break;
            case R.id.relayGain:
                //跳转到 利润 排行 列表
                Bundle bundle1 = new Bundle();
                bundle1.putString("Type", "2");
                bundle1.putString("ScreenStatue", ScreenStatue);
                bundle1.putString("StartDate", StartDate);
                bundle1.putString("EndDate", EndDate);
                bundle1.putString("textFilter", textFilter.getText().toString().trim());
                skipActivity(aty, DataStatisticsDetailsActivity.class, bundle1);
                break;
            case R.id.btnRight:
                //弹出筛选对话框
                dialog.show(ScreenStatue, StartDate, EndDate);
                break;
        }
    }

    /**
     * 获取数据
     */
    private void getDataList(final boolean isShowDialog) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("ScreenStatue", ScreenStatue);
        params.put("StartDate", StartDate);
        params.put("EndDate", EndDate);
        httpRequestService.doRequestData(aty, "User/StatisticsList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setRefreshing(false);  //下拉刷新结束
                }
                if (Constants.M00.equals(resultParam.resultId)) {
                    if (!isShowDialog) {
                        toast("刷新成功");
                    }
                    listSales = getListData(resultParam.mapData.get("MerchandiseSales"));
                    listGain = getListData(resultParam.mapData.get("MerchandiseProfits"));
                    if (listGain.size() <= 0 && listSales.size() <= 0) {
                        textNoData.setVisibility(View.VISIBLE);
                        lineContent.setVisibility(View.GONE);
                    } else {
                        textNoData.setVisibility(View.GONE);
                        lineContent.setVisibility(View.VISIBLE);
                    }
                    adapterSales.setListData(listSales);
                    adapterSales.notifyDataSetChanged();

                    adapterGain.setListData(listGain);
                    adapterGain.notifyDataSetChanged();

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
     * 下拉刷新
     */
    JCSwipeRefreshLayout.OnRefreshListener refreshlistener = new JCSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getDataList(true);
                }
            }, Constants.RefreshTime);
        }
    };

    /**
     * 解析列表数据
     */
    private List<ConcurrentHashMap<String, String>> getListData(String json) {
        List<ConcurrentHashMap<String, String>> listdata = new ArrayList<>();
        if (StringUtils.isEmpty(json)) {
            return listdata;
        }
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                ConcurrentHashMap<String, String> childmap = DataAnalyze.doAnalyzeJsonArray(jsonArray.getJSONObject(i));
                listdata.add(childmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return listdata;
    }

    /**
     * 根据筛选状态显示 筛选 title
     */
    private String getFilterText(String str) {
        String filterText = "";
        switch (str) {
            case "0":
                filterText = "昨天";
                break;
            case "1":
                filterText = "今日实时";
                break;
            case "2":
                filterText = "本月实时";
                break;
            case "3":
                filterText = "近7天";
                break;
            case "4":
                filterText = "近30天";
                break;
            case "5":
                filterText = StartDate + " 至 " + EndDate;
                break;
        }
        return filterText;
    }


    /**
     * 筛选对话框回调
     */
    private FilterDialog.OnFilterDialogClickLinster dialogClickLinster = new FilterDialog.OnFilterDialogClickLinster() {

        @Override
        public void OK(String Statue, String Start, String End) {
            StartDate = Start;
            EndDate = End;
            ScreenStatue = Statue;
            getDataList(true);
            textFilter.setText(getFilterText(ScreenStatue));
        }
    };
}
