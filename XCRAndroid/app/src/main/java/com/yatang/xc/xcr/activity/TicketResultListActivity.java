package com.yatang.xc.xcr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.TicketListAdapter;
import com.yatang.xc.xcr.config.Constants;
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
 * 小票搜索结果页面
 * Created by Jocerly on 2017/6/30.
 */
@ContentView(R.layout.activity_ticket_result_list)
public class TicketResultListActivity extends BaseActivity{
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;

    @BindView(id = R.id.textDate)
    private TextView textDate;
    @BindView(id = R.id.textAllPrice)
    private TextView textAllPrice;

    @BindView(id = R.id.llSearch, click = true)
    private RelativeLayout llSearch;
    @BindView(id = R.id.text_key)
    private TextView text_key;
    @BindView(id = R.id.text_search)
    private TextView text_search;
    @BindView(id = R.id.imagePwdClear, click = true)
    private ImageView imagePwdClear;

    @BindView(id = R.id.swipeLayout)
    private JCSwipeRefreshLayout mSwipeLayout;
    @BindView(id = R.id.textNoData)
    private TextView textNoData;

    @BindView(id = R.id.mRecyclerView)
    private LoadMoreRecyclerView mRecyclerView;
    private TicketListAdapter adapter;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();

    private SearchDialog searchDialog;
    private String search = "";
    private int pageIndex = 1;
    private int pageSize = Constants.PageSize;
    private boolean isChoice = false;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("小票搜索结果页");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("小票搜索结果页");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        mSwipeLayout.setColorSchemeResources(R.color.red);
        mSwipeLayout.setOnRefreshListener(refreshlistener);  //设置下拉刷新监听器
    }

    @Override
    public void initData() {
        searchDialog = new SearchDialog(aty);
        searchDialog.setOnSearchDialogClickLinster(onSearchDialogClickLinster);
        searchDialog = new SearchDialog(aty);
        searchDialog.setOnSearchDialogClickLinster(onSearchDialogClickLinster);

        mRecyclerView.setLayoutManager(aty, 0);  //设置LayoutManager和分隔符
        mRecyclerView.initDecoration(aty, (int) getResources().getDimension(R.dimen.pad1_px), colorGap);
        mRecyclerView.setLoadMoreListener(loadMoreListener);    //设置上拉加载监听器

        adapter = new TicketListAdapter(aty, listData);
        adapter.setCanLoadMore(true);  //设置可以上拉加载，如果设置为false则不能上拉加载
        adapter.setOnItemclickLister(onItemclickLister);
        mRecyclerView.setRecyclerAdapter(adapter); //RecyclerView绑定Adapter

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isChoice = bundle.getBoolean("isChoice", false);
            search = bundle.getString("msg");
            updateAndSearch(search);
        }
        detachLayout();
    }

    JCSwipeRefreshLayout.OnRefreshListener refreshlistener = new JCSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pageIndex = 1;
                    getTicketList(false, true);
                }
            }, Constants.RefreshTime);
        }
    };

    LoadMoreRecyclerView.PTLoadMoreListener loadMoreListener = new LoadMoreRecyclerView.PTLoadMoreListener() {
        @Override
        public void loadMore() {
            pageIndex++;
            getTicketList(false, false);
        }
    };

    /**
     * 单选回调
     */
    TicketListAdapter.OnItemclickLister onItemclickLister = new TicketListAdapter.OnItemclickLister() {
        @Override
        public void OnFooterClick() {
            mRecyclerView.startLoadMore();
        }

        @Override
        public void OnItemClick(String id ,String ticketId) {
            Bundle bundle = new Bundle();
            bundle.putString("TicketNo", id);
            bundle.putString("TicketId",ticketId);
            skipActivity(aty, TicketDetialActivity.class, bundle);
        }
    };

    /**
     * 获取数据
     */
    private void getTicketList(final boolean isShowDialog, final boolean isShowToast) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("Date", "");
        params.put("TicketTypes", "");
        params.put("Search", search);
        params.put("StartTime", "");
        params.put("EndTime", "");
        params.put("PageIndex", pageIndex);
        params.put("PageSize", pageSize);
        httpRequestService.doRequestData(aty, "User/TicketList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

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
                            adapter.setKey(search);
                            adapter.notifyDataSetChanged();
                            adapter.finishLoad(BaseRecyclerViewAdapter.STATE_SUCCESS);
                            if (pageIndex == 1 && listData.size() < Constants.PageSize) {// 没有更多数据了
                                adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FINISH);
                            }
                            textNoData.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            textNoData.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        }
                    }
                    textDate.setText(resultParam.mapData.get("TicketDate"));
                    String temp = Common.formatTosepara(resultParam.mapData.get("TransactionAllValue"), 3, 2);
                    if (!StringUtils.isEmpty(temp)) {
                        textAllPrice.setText(temp);
                    }
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    //如果请求下一页数据失败 pageIndex 减一
                    if(pageIndex > 1) {
                        pageIndex = pageIndex--;
                    }
                    adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FAILURE);
                }
            }
        });
    }

    /**
     * 搜索完成后刷新
     *
     * @param msg
     */
    private void updateAndSearch(String msg) {
        pageIndex = 1;
        search = msg;
        if (StringUtils.isEmpty(search)) {
            search = "";
            text_key.setText("");
            text_search.setVisibility(View.VISIBLE);
            imagePwdClear.setVisibility(View.GONE);
        } else {
            text_key.setText(search);
            text_search.setVisibility(View.INVISIBLE);
            imagePwdClear.setVisibility(View.VISIBLE);
        }

        getTicketList(true, false);
    }

    SearchDialog.OnSearchDialogClickLinster onSearchDialogClickLinster = new SearchDialog.OnSearchDialogClickLinster() {
        @Override
        public void OK(String msg) {
            updateAndSearch(msg);
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnRight:
                pageIndex = 1;
                getTicketList(true, false);
                break;
            case R.id.llSearch:
            case R.id.imagePwdClear:
                searchDialog.show(2);
                break;
        }
    }
}
