package com.yatang.xc.xcr.activity;


import android.view.View;
import android.widget.TextView;

import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.ScanCodeListAdapter;
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

/**
 * 调价记录Activity
 * Created by Jocerly on 2017/3/8.
 */
@ContentView(R.layout.activity_scan_code_list)
public class ScanCodeListActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.swipeLayout)
    private JCSwipeRefreshLayout mSwipeLayout;
    @BindView(id = R.id.mRecyclerView)
    private LoadMoreRecyclerView mRecyclerView;

    private ScanCodeListAdapter adapter;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private int pageIndex = 1;
    private int pageSize = 20;

    @Override
    public void initWidget() {
        textTitle.setText("调价记录");
        btnRight.setVisibility(View.GONE);
        mSwipeLayout.setColorSchemeResources(R.color.red);
    }

    @Override
    public void initData() {
        mSwipeLayout.setOnRefreshListener(refreshlistener);  //设置下拉刷新监听器
        mRecyclerView.setLayoutManager(aty, 0);  //设置LayoutManager和分隔符
        mRecyclerView.initDecoration(aty, (int) getResources().getDimension(R.dimen.pad1_px), colorGap);
        mRecyclerView.setLoadMoreListener(loadMoreListener);    //设置上拉加载监听器

        adapter = new ScanCodeListAdapter(aty, listData);
        adapter.setCanLoadMore(true);  //设置可以上拉加载，如果设置为false则不能上拉加载
        adapter.setOnItemClickLinster(onItemClickLinster);
        mRecyclerView.setRecyclerAdapter(adapter); //RecyclerView绑定Adapter

        getModifyGoodsPriceList(true);
    }

    JCSwipeRefreshLayout.OnRefreshListener refreshlistener = new JCSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pageIndex = 1;
                    getModifyGoodsPriceList(false);
                }
            }, Constants.RefreshTime);
        }
    };
    LoadMoreRecyclerView.PTLoadMoreListener loadMoreListener = new LoadMoreRecyclerView.PTLoadMoreListener() {
        @Override
        public void loadMore() {
            pageIndex++;
            getModifyGoodsPriceList(false);
        }
    };

    ScanCodeListAdapter.OnItemClickLinster onItemClickLinster = new ScanCodeListAdapter.OnItemClickLinster() {
        @Override
        public void OnFooterClick() {
            mRecyclerView.startLoadMore();
        }
    };


    /**
     * 获取数据
     */
    private void getModifyGoodsPriceList(boolean isShowDialog) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("PageIndex", pageIndex);
        params.put("PageSize", pageSize);
        httpRequestService.doRequestData(aty, "User/ModifyGoodsPriceList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

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
                            toast("刷新成功");
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
                    if(pageIndex > 1) {
                        pageIndex = pageIndex--;
                    }
                    adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FAILURE);
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
        }
    }
}
