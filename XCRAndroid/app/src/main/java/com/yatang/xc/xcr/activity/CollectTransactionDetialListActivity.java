package com.yatang.xc.xcr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.CollectTransactionDetialListAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.widget.recyclevew.LoadMoreRecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**日收钱码流水
 * Created by lusha on 2017/10/30.
 */
@ContentView(R.layout.activity_ticket_list)
public class CollectTransactionDetialListActivity extends BaseActivity {
    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight)
    private TextView btnRight;
    @BindView(id = R.id.textCount)
    private TextView textCount;
    @BindView(id = R.id.textDate)
    private TextView textDate;
    @BindView(id = R.id.textAllPrice)
    private TextView textAllPrice;
    @BindView(id = R.id.mRecyclerView)
    private LoadMoreRecyclerView mRecyclerView;

    private String time;
    private int pageIndex = 1;
    private int PageSize = Constants.PageSize;
    List<ConcurrentHashMap<String,String>> listData = new ArrayList<>();
    private CollectTransactionDetialListAdapter adapter;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void initWidget() {
       btnRight.setVisibility(View.GONE);
       textCount.setVisibility(View.VISIBLE);
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            HashMap<String, String> mapData = (HashMap<String, String>) bundle.getSerializable("mapData");
            time = Common.stampToDate(simpleDateFormat, mapData.get("Time"));
            textTitle.setText(Common.isToday(time) ? "今日" : time);
            textAllPrice.setText("￥" + Common.formatTosepara(mapData.get("CollectTransactionDaily"), 3, 2));
        }
        mRecyclerView.setLayoutManager(aty, 0);  //设置LayoutManager和分隔符
        mRecyclerView.initDecoration(aty, (int) getResources().getDimension(R.dimen.pad1_px), colorGap);
        mRecyclerView.setLoadMoreListener(loadMoreListener);    //设置上拉加载监听器
        adapter = new CollectTransactionDetialListAdapter(aty, listData);
        adapter.setCanLoadMore(true); //设置可以上拉加载，如果设置为false则不能上拉加载
        adapter.setOnItemClickLinster(onItemClickLinster);
        mRecyclerView.setRecyclerAdapter(adapter); //RecyclerView绑定adapter
        getCollectTransactionDetialList(true, false);
    }

    CollectTransactionDetialListAdapter.OnItemClickLinster onItemClickLinster = new CollectTransactionDetialListAdapter.OnItemClickLinster() {
        @Override
        public void OnFooterClick() {
            mRecyclerView.startLoadMore();
        }
    };


    LoadMoreRecyclerView.PTLoadMoreListener loadMoreListener = new LoadMoreRecyclerView.PTLoadMoreListener() {
        @Override
        public void loadMore() {
            pageIndex++;
            getCollectTransactionDetialList(false, false);
        }
    };

    /**
     * 获取收钱码交易详情
     */
    public void getCollectTransactionDetialList(final boolean isShowDialog, final boolean isShowToast){
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("PageIndex", pageIndex);
        params.put("PageSize", PageSize);
        params.put("Date", time);
        httpRequestService.doRequestData(aty, "User/CollectTransactionDetialList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)){
                    if (pageIndex > 1 && resultParam.listData.size() < 1) {//没有更多数据
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
                        if (!StringUtils.isEmpty(resultParam.mapData.get("Count"))) {
                            textDate.setText(resultParam.mapData.get("Count"));
                        }
                        adapter.notifyDataSetChanged();
                        adapter.finishLoad(BaseRecyclerViewAdapter.STATE_SUCCESS);
                        if (pageIndex == 1 && resultParam.listData.size() < Constants.PageSize) {//没有更多数据了
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
