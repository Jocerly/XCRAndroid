package com.yatang.xc.xcr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.TicketListAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.ScreenDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.widget.recyclevew.LoadMoreRecyclerView;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 小票列表
 * Created by Jocerly on 2017/5/17.
 */
@ContentView(R.layout.activity_ticket_list)
public class TicketListActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.textDate)
    private TextView textDate;
    @BindView(id = R.id.textAllPrice)
    private TextView textAllPrice;

    @BindView(id = R.id.mRecyclerView)
    private LoadMoreRecyclerView mRecyclerView;
    private TicketListAdapter adapter;
    private List<ConcurrentHashMap<String, String>> listData;

    private int pageIndex = 1;
    private int pageSize = Constants.PageSize;
    private int maxPage = 0;
    private ScreenDialog dialog;
    private String startTime = "";
    private String endTime = "";
    private String Date;
    private ArrayList<ConcurrentHashMap<String, String>> ticketTypes;
    private ArrayList<ConcurrentHashMap<String, String>> payTypes;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("小票");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("小票");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("小票");
        btnRight.setText("筛选");
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Date = bundle.getString("Date", "");
        }
        textDate.setText(Date);

        ticketTypes = new ArrayList<>();
        payTypes = new ArrayList<>();
        listData = new ArrayList<>();
        dialog = new ScreenDialog(aty);
        dialog.setOnScreenDialogClickLinster(onScreenDialogClickLinster);

        mRecyclerView.setLayoutManager(aty, 0);  //设置LayoutManager和分隔符
        mRecyclerView.initDecoration(aty, (int) getResources().getDimension(R.dimen.pad1_px), colorGap);
        mRecyclerView.setLoadMoreListener(loadMoreListener);    //设置上拉加载监听器

        adapter = new TicketListAdapter(aty, listData);
        adapter.setCanLoadMore(true);  //设置可以上拉加载，如果设置为false则不能上拉加载
        adapter.setOnItemclickLister(onItemclickLister);
        mRecyclerView.setRecyclerAdapter(adapter); //RecyclerView绑定Adapter

        getTicketList(new JSONArray(ticketTypes), new JSONArray(payTypes),true, false);
    }

    LoadMoreRecyclerView.PTLoadMoreListener loadMoreListener = new LoadMoreRecyclerView.PTLoadMoreListener() {
        @Override
        public void loadMore() {
            pageIndex++;
            getTicketList(new JSONArray(ticketTypes), new JSONArray(payTypes),false, false);
        }
    };

    TicketListAdapter.OnItemclickLister onItemclickLister = new TicketListAdapter.OnItemclickLister() {
        @Override
        public void OnFooterClick() {
            mRecyclerView.startLoadMore();
        }

        @Override
        public void OnItemClick(String id, String ticketId) {
            Bundle bundle = new Bundle();
            bundle.putString("TicketNo", id);
            bundle.putString("TicketId", ticketId);
            skipActivity(aty, TicketDetialActivity.class, bundle);
        }
    };

    /**
     * 获取数据
     */
    private void getTicketList(JSONArray ticketTypes, JSONArray payTypes, final boolean isShowDialog, final boolean isShowToast) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("Date", Date);
        params.put("TicketTypes", ticketTypes);
        params.put("PayTypes", payTypes);
        params.put("Search", "");
        params.put("StartTime", startTime);
        params.put("EndTime", endTime);
        params.put("PageIndex", pageIndex);
        params.put("PageSize", pageSize);
        httpRequestService.doRequestData(aty, "User/TicketList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
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
                        adapter.notifyDataSetChanged();
                        adapter.finishLoad(BaseRecyclerViewAdapter.STATE_SUCCESS);
                        if (pageIndex == 1 && listData.size() < Constants.PageSize) {// 没有更多数据了
                            adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FINISH);
                        }
                    }
                    if (!StringUtils.isEmpty(resultParam.mapData.get("TransactionAllValue"))) {
                        String temp = Common.formatTosepara(resultParam.mapData.get("TransactionAllValue"), 3, 2);
                        if (!StringUtils.isEmpty(temp)) {
                            textAllPrice.setText(temp);
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
        public void OK(String start, String end, ArrayList<ConcurrentHashMap<String, String>> list, ArrayList<ConcurrentHashMap<String, String>> list2) {
            pageIndex = 1;
            startTime = start;
            endTime = end;
            ticketTypes = list;
            payTypes = list2;
            getTicketList(new JSONArray(ticketTypes), new JSONArray(payTypes),true, false);
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
                dialog.show(3, startTime, endTime);
                break;
        }
    }
}
