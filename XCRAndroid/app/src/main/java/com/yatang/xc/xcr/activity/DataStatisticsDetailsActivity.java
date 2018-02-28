package com.yatang.xc.xcr.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.DataStatisticsDetailsAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.views.PressTextView;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.widget.recyclevew.LoadMoreRecyclerView;
import org.jocerly.jcannotation.widget.swiperefreshlayout.JCSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 排行榜详情页面
 * Created by dengjiang on 2017/7/3.
 */
@ContentView(R.layout.activity_data_statistics_details)
public class DataStatisticsDetailsActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.textFilter)
    private TextView textFilter;
    @BindView(id = R.id.textOrder, click = true)
    private PressTextView textOrder;
    @BindView(id = R.id.recyclerView)
    private LoadMoreRecyclerView recyclerView;
    @BindView(id = R.id.swipeLayout)
    private JCSwipeRefreshLayout mSwipeLayout;

    private boolean order = false;//true表示升序，false表示降序
    private String type = "1";//1表示销量排行,２表示利润排行
    private int pageIndex = 1;
    private String ScreenStatue = "1";//筛选的类型
    private String StartDate = "";//开始时间
    private String EndDate = "";//结束时间
    private DataStatisticsDetailsAdapter adapter;
    private List<ConcurrentHashMap<String, String>> listData;//销量排行数据

    @Override
    protected void onResume() {
        super.onResume();
        if ("1".equals(type)) {
            MobclickAgent.onPageStart("商品销量排行榜");
        } else {
            MobclickAgent.onPageStart("商品利润排行榜");
        }
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ("1".equals(type)) {
            MobclickAgent.onPageEnd("商品销量排行榜");
        } else {
            MobclickAgent.onPageEnd("商品利润排行榜");
        }
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        listData = new ArrayList<>();
        mSwipeLayout.setOnRefreshListener(refreshlistener);  //设置下拉刷新监听器
        //初始化 销量排行列表view
        mSwipeLayout.setColorSchemeResources(R.color.red);
        setOrderText();
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            textFilter.setText(bundle.getString("textFilter"));
            type = bundle.getString("Type");
            ScreenStatue = bundle.getString("ScreenStatue");
            StartDate = bundle.getString("StartDate");
            EndDate = bundle.getString("EndDate");
        }
        adapter = new DataStatisticsDetailsAdapter(getApplicationContext(), listData, type);
        adapter.setCanLoadMore(true);  //设置可以上拉加载，如果设置为false则不能上拉加载
        LinearLayoutManager layoutManager = new LinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.addItemDecoration(new DividerItemDecoration(aty, DividerItemDecoration.VERTICAL_LIST,
//                (int) getResources().getDimension(R.dimen.pad1_px), colorGap));
        recyclerView.setRecyclerAdapter(adapter);
        recyclerView.setLoadMoreListener(loadMoreListener);    //设置上拉加载监听器
        if ("1".equals(type)) {
            textTitle.setText("商品销量排行榜");
        } else {
            textTitle.setText("商品利润排行榜");
        }
        getDataList(false, false, order, type);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.textOrder:
                order = !order;
                pageIndex = 1;
                setOrderText();
                getDataList(true, false, order, type);
                break;
        }
    }
    private void setOrderText(){
        if (order) {
            textOrder.setText("升序");
            textOrder.setCompoundDrawablesWithIntrinsicBounds(R.drawable.imgup, 0, 0, 0);
        } else {
            textOrder.setText("降序");
            textOrder.setCompoundDrawablesWithIntrinsicBounds(R.drawable.imgdown, 0, 0, 0);
        }
    }
    /**
     * 获取数据
     *
     * @param order 1:升序,2:降序
     * @param type  1: 商品销量排行 2:商品利润排行
     */
    private void getDataList(final boolean isShowDialog, final boolean isShowToast, boolean order, String type) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("ScreenStatue", ScreenStatue);
        params.put("StartDate", StartDate);
        params.put("EndDate", EndDate);
        params.put("StatisticsType", type);
        params.put("Sort", order ? 1 : 0);
        params.put("PageIndex", pageIndex);
        params.put("PageSize", Constants.PageSize);
        httpRequestService.doRequestData(aty, "User/StatisticsDetialList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setRefreshing(false);  //下拉刷新结束
                }
                if (Constants.M00.equals(resultParam.resultId)) {
                    if (pageIndex > 1 && resultParam.listData.size() < 1) {//没有更多数据了
                        pageIndex--;
                        //没有更多数据了
                        adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FINISH);
                    } else {
                        if (pageIndex == 1) {
                            if (isShowToast) {
                                toast("刷新成功");
                            }
                            listData.clear();
                            adapter.setLoadingDefualt();
                        }
                        listData.addAll(resultParam.listData);
                        if (listData.size() > 0) {
                            adapter.notifyDataSetChanged();
                            adapter.finishLoad(BaseRecyclerViewAdapter.STATE_SUCCESS);
                            if (pageIndex == 1 && listData.size() < Constants.PageSize) {// 没有更多数据了
                                adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FINISH);
                            }
                        }
                    }
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
                    pageIndex = 1;
                    getDataList(false, true, order, type);
                }
            }, Constants.RefreshTime);
        }
    };

    /**
     * 加载更多
     */
    LoadMoreRecyclerView.PTLoadMoreListener loadMoreListener = new LoadMoreRecyclerView.PTLoadMoreListener() {
        @Override
        public void loadMore() {
            pageIndex++;
            getDataList(false, false, order, type);
        }
    };

    /**
     * 加载更多
     */
    DataStatisticsDetailsAdapter.OnItemClickListener onItemClickListener = new DataStatisticsDetailsAdapter.OnItemClickListener() {
        @Override
        public void OnFooterClick() {
            recyclerView.startLoadMore();
        }
    };
}
