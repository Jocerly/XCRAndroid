package com.yatang.xc.xcr.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.TransactionAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.dialog.ScreenDialog;
import com.yatang.xc.xcr.dialog.SearchDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.widget.recyclevew.LoadMoreRecyclerView;
import org.jocerly.jcannotation.widget.swiperefreshlayout.JCSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 交易流水Activity
 * Created by Jocerly on 2017/3/8.
 */
@ContentView(R.layout.activity_transaction)
public class TransactionActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.text_search)
    private TextView text_search;
    @BindView(id = R.id.llSearch, click = true)
    private RelativeLayout llSearch;

    @BindView(id = R.id.textStoreSerialNum)
    private TextView textStoreSerialNum;
    @BindView(id = R.id.textAllPrice)
    private TextView textAllPrice;

    @BindView(id = R.id.llPost, click = true)
    private LinearLayout llPost;
    @BindView(id = R.id.btnPost, click = true)
    private TextView btnPost;

    @BindView(id = R.id.swipeLayout)
    private JCSwipeRefreshLayout mSwipeLayout;
    @BindView(id = R.id.mRecyclerView)
    private LoadMoreRecyclerView mRecyclerView;

    private TransactionAdapter adapter;
    private List<ConcurrentHashMap<String, String>> listData;
    private int pageIndex = 1;
    private int pageSize = Constants.PageSize;
    private ScreenDialog dialog;
    private SearchDialog searchDialog;
    private String startDate = "";
    private String endDate = "";
    private NomalDialog nomalDialog;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("日交易流水");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("日交易流水");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("日交易流水");
        btnRight.setText("筛选");
        textStoreSerialNum.setText(MyApplication.instance.StoreSerialNoDefault);
        mSwipeLayout.setColorSchemeResources(R.color.red);
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            startDate = bundle.getString("StartDate", "");
            endDate = bundle.getString("EndDate", "");
        }

        listData = new ArrayList<>();
        dialog = new ScreenDialog(aty);
        dialog.setOnScreenDialogClickLinster(onScreenDialogClickLinster);
        searchDialog = new SearchDialog(aty);
        searchDialog.setOnSearchDialogClickLinster(onSearchDialogClickLinster);
        nomalDialog = new NomalDialog(aty);
        nomalDialog.setOnNoamlLickListener(onNoamlLickListener);

        mSwipeLayout.setOnRefreshListener(refreshlistener);  //设置下拉刷新监听器
        mRecyclerView.setLayoutManager(aty, 0);  //设置LayoutManager和分隔符
        mRecyclerView.initDecoration(aty, (int) getResources().getDimension(R.dimen.pad1_px), colorGap);
        mRecyclerView.setLoadMoreListener(loadMoreListener);    //设置上拉加载监听器

        adapter = new TransactionAdapter(aty, listData);
        adapter.setCanLoadMore(true);  //设置可以上拉加载，如果设置为false则不能上拉加载
        adapter.setOnItemclickLister(onItemclickLister);
        mRecyclerView.setRecyclerAdapter(adapter); //RecyclerView绑定Adapter

        getTransactionList(true, false);
    }

    JCSwipeRefreshLayout.OnRefreshListener refreshlistener = new JCSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pageIndex = 1;
                    getTransactionList(false, true);
                }
            }, Constants.RefreshTime);
        }
    };
    LoadMoreRecyclerView.PTLoadMoreListener loadMoreListener = new LoadMoreRecyclerView.PTLoadMoreListener() {
        @Override
        public void loadMore() {
            pageIndex++;
            getTransactionList(false, false);
        }
    };

    TransactionAdapter.OnItemclickLister onItemclickLister = new TransactionAdapter.OnItemclickLister() {
        @Override
        public void OnFooterClick() {
            mRecyclerView.startLoadMore();
        }

        @Override
        public void OnItemClick(String date) {
            Bundle bundle = new Bundle();
            bundle.putString("Date", date);
            skipActivity(aty, TicketListActivity.class, bundle);
        }
    };

    SearchDialog.OnSearchDialogClickLinster onSearchDialogClickLinster = new SearchDialog.OnSearchDialogClickLinster() {
        @Override
        public void OK(String msg) {
            Bundle bundle = new Bundle();
            bundle.putString("msg", msg);
            skipActivity(aty, TicketResultListActivity.class, bundle);
        }
    };

    NomalDialog.OnNoamlLickListener onNoamlLickListener = new NomalDialog.OnNoamlLickListener() {
        @Override
        public void onOkClick() {

        }
    };

    /**
     * 获取数据
     */
    private void getTransactionList(final boolean isShowDialog, final boolean isShowToast) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("StartDate", startDate);
        params.put("EndDate", endDate);
        params.put("PageIndex", pageIndex);
        params.put("PageSize", pageSize);
        httpRequestService.doRequestData(aty, "User/TransactionList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setRefreshing(false);  //下拉刷新结束
                }
                if (Constants.M00.equals(resultParam.resultId)) {
                    if (!StringUtils.isEmpty(resultParam.mapData.get("TransactionAllValue"))) {
                        String temp = Common.formatTosepara(resultParam.mapData.get("TransactionAllValue"), 3, 2);
                        if (!StringUtils.isEmpty(temp)) {
                            textAllPrice.setText(temp);
                        }
                    }
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
                        adapter.notifyDataSetChanged();
                        adapter.finishLoad(BaseRecyclerViewAdapter.STATE_SUCCESS);
                        if (pageIndex == 1 && listData.size() < Constants.PageSize) {// 没有更多数据了
                            adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FINISH);
                        }
                    }
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    //如果请求下一页数据失败 pageIndex 减一
                    if (pageIndex > 1) {
                        pageIndex = pageIndex--;
                    }
                    adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FAILURE);
                }
            }
        });
    }

    ScreenDialog.OnScreenDialogClickLinster onScreenDialogClickLinster = new ScreenDialog.OnScreenDialogClickLinster() {
        @Override
        public void OK(String startTime, String endTime, ArrayList<ConcurrentHashMap<String, String>> listDataTmp, ArrayList<ConcurrentHashMap<String, String>> list2) {
            pageIndex = 1;
            startDate = startTime;
            endDate = endTime;
            getTransactionList(true, false);
        }
    };

    /**
     * 获取过账数据
     */
    public void getTicketPost() {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        httpRequestService.doRequestData(aty, "User/TicketPost", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    nomalDialog.showClose("共过账" + resultParam.mapData.get("AllNum") + "张销售小票" + "\n"
                            + "成功" + resultParam.mapData.get("SucNum") + "张 ，"
                            + "失败" + resultParam.mapData.get("ErrorNum") + "张", "确定");
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
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnRight:
                dialog.show(1, startDate, endDate);
                break;
            case R.id.llSearch:
                searchDialog.show(2);
                break;
            case R.id.btnPost:
                showActivity(aty, CollectTransactionListActivity.class);
                break;
        }
    }
}
