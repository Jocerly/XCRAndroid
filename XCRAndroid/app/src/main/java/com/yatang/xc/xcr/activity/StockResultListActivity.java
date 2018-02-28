package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.StockListAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.SearchDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
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
 * 库存搜索结果
 * Created by Jocerly on 2017/5/28.
 */
@ContentView(R.layout.activity_goods_result_list)
public class StockResultListActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;

    @BindView(id = R.id.llSearch, click = true)
    private RelativeLayout llSearch;
    @BindView(id = R.id.text_search)
    private TextView text_search;
    @BindView(id = R.id.imagePwdClear, click = true)
    private ImageView imagePwdClear;
    @BindView(id = R.id.text_key)
    private TextView text_key;

    @BindView(id = R.id.swipeLayout)
    private JCSwipeRefreshLayout mSwipeLayout;
    @BindView(id = R.id.textNoData)
    private TextView textNoData;

    @BindView(id = R.id.mRecyclerView)
    private LoadMoreRecyclerView mRecyclerView;

    private StockListAdapter adapter;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();

    private SearchDialog searchDialog;
    private String search = "";
    private int pageIndex = 1;
    private int pageSize = Constants.PageSize;
    private String classifyId = "";

    @Override
    public void initWidget() {
        mSwipeLayout.setColorSchemeResources(R.color.red);
        mSwipeLayout.setOnRefreshListener(refreshlistener);  //设置下拉刷新监听器
    }

    @Override
    public void initData() {
        searchDialog = new SearchDialog(aty);
        searchDialog.setOnSearchDialogClickLinster(onSearchDialogClickLinster);

        mRecyclerView.setLayoutManager(aty, 0);  //设置LayoutManager和分隔符
        mRecyclerView.initDecoration(aty, (int) getResources().getDimension(R.dimen.pad1_px), colorGap);
        mRecyclerView.setLoadMoreListener(loadMoreListener);    //设置上拉加载监听器

        adapter = new StockListAdapter(aty, listData);
        adapter.setCanLoadMore(true);  //设置可以上拉加载，如果设置为false则不能上拉加载
        adapter.setOnItemclickLister(onItemclickLister);
        mRecyclerView.setRecyclerAdapter(adapter); //RecyclerView绑定Adapter

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            updateAndSearch(bundle.getString("msg"));
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
                    getStockList(false, true);
                }
            }, Constants.RefreshTime);
        }
    };

    LoadMoreRecyclerView.PTLoadMoreListener loadMoreListener = new LoadMoreRecyclerView.PTLoadMoreListener() {
        @Override
        public void loadMore() {
            pageIndex++;
            getStockList(false, false);
        }
    };

    StockListAdapter.OnItemclickLister onItemclickLister = new StockListAdapter.OnItemclickLister() {
        @Override
        public void OnFooterClick() {
            mRecyclerView.startLoadMore();
        }
    };

    /**
     * 获取数据
     */
    private void getStockList(final boolean isShowDialog, final boolean isShowToast) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("ClassifyId", classifyId);
        params.put("Search", search);
        params.put("PageIndex", pageIndex);
        params.put("PageSize", pageSize);
        httpRequestService.doRequestData(aty, "User/StockList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

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
            text_key.setText("");
            text_search.setVisibility(View.VISIBLE);
            imagePwdClear.setVisibility(View.GONE);
        } else {
            text_key.setText(search);
            text_search.setVisibility(View.INVISIBLE);
            imagePwdClear.setVisibility(View.VISIBLE);
        }
        getStockList(true, false);
    }

    /**
     * 搜索回调
     */
    SearchDialog.OnSearchDialogClickLinster onSearchDialogClickLinster = new SearchDialog.OnSearchDialogClickLinster() {
        @Override
        public void OK(String msg) {
            classifyId = "";
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
                getStockList(true, false);
                break;
            case R.id.llSearch:
                searchDialog.show(1);
                break;
            case R.id.imagePwdClear:
                classifyId = "";
                updateAndSearch("");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.ForResult.SCAN:
                if (resultCode == RESULT_OK && data != null) {
                    String msg = data.getExtras().getString("code");
                    searchDialog.updateDataBase(msg);
                    classifyId = "";
                    updateAndSearch(msg);
                }
                break;
        }
    }
}
