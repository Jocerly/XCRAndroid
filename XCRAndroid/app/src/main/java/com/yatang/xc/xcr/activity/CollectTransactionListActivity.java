package com.yatang.xc.xcr.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.CollectTransactionListAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.widget.recyclevew.LoadMoreRecyclerView;
import org.jocerly.jcannotation.widget.swiperefreshlayout.JCSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/** 收钱码流水
 * Created by lusha on 2017/10/11.
 */
@ContentView(R.layout.activity_collect_transaction_list)
public class CollectTransactionListActivity extends BaseActivity {
    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight)
    private TextView btnRight;
    @BindView(id = R.id.swipeLayout)
    private JCSwipeRefreshLayout swipeLayout;
    @BindView(id = R.id.recyclerView)
    private LoadMoreRecyclerView recyclerView;
    @BindView(id = R.id.textNoData)
    private TextView textNoData;

    List<ConcurrentHashMap<String, String>> listData;
    private int pageIndex = 1;
    private int PageSize = Constants.PageSize;
    private CollectTransactionListAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("收钱码流水");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("收钱码流水");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("收钱码流水");
        swipeLayout.setColorSchemeResources(R.color.red);
    }

    @Override
    public void initData() {
        listData = new ArrayList<>();
        swipeLayout.setOnRefreshListener(refreshListener);//设置下拉刷新监听器
        recyclerView.setLayoutManager(aty, 0);  //设置LayoutManager和分隔符
//      recyclerView.initDecoration(aty, (int) getResources().getDimension(R.dimen.pad1_px), colorGap);
        recyclerView.setLoadMoreListener(loadMoreListener);//设置上拉加载监听器
        adapter = new CollectTransactionListAdapter(aty, listData);
        adapter.setCanLoadMore(true);//设置可以上拉加载，如果设置为false则不能上拉加载
        adapter.setOnItemClickLinster(onItemClickLinster);
        recyclerView.setRecyclerAdapter(adapter); //RecyclerView绑定Adapter
        getCollectTranction(true, false);
    }

    JCSwipeRefreshLayout.OnRefreshListener refreshListener = new JCSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pageIndex = 1;
                    getCollectTranction(false, true);
                }
            }, Constants.RefreshTime);
        }
    };

    LoadMoreRecyclerView.PTLoadMoreListener loadMoreListener = new LoadMoreRecyclerView.PTLoadMoreListener() {
        @Override
        public void loadMore() {
            pageIndex++;
            getCollectTranction(false, false);

        }
    };

    CollectTransactionListAdapter.OnItemClickLinster onItemClickLinster = new CollectTransactionListAdapter.OnItemClickLinster() {
        @Override
        public void OnItemClick(ConcurrentHashMap<String, String> map) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("mapData", map);
            showActivity(aty, CollectTransactionDetialListActivity.class, bundle);
        }

        @Override
        public void OnFooterClick() {
            recyclerView.startLoadMore();
        }
    };

    /**
     * 获取收钱码流水
     * @param isShowDialog
     * @param isShowToast
     */
    public void getCollectTranction(final boolean isShowDialog, final boolean isShowToast){
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("PageIndex", pageIndex);
        params.put("PageSize", PageSize);
        httpRequestService.doRequestData(aty, "User/NewCollectTransactionList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (swipeLayout.isRefreshing()) {
                    swipeLayout.setRefreshing(false);//下拉刷新结束
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
                            textNoData.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            adapter.finishLoad(BaseRecyclerViewAdapter.STATE_SUCCESS);
                            if (pageIndex == 1 && listData.size() < Constants.PageSize) {//没有更多数据了
                                adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FINISH);
                            }
                        } else {
                            textNoData.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                    }
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    if (pageIndex > 1) {
                        //如果请求下一页数据失败 pageIndex 减一
                        pageIndex = pageIndex--;
                    }
                    adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FAILURE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLeft:
                onBackPressed();
                break;
        }
    }
}
