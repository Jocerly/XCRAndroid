package com.yatang.xc.xcr.activity;


import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.plugin.navigation.CordovaPageActivity;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.MsgAdapter;
import com.yatang.xc.xcr.config.Constants;
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
 * 消息Activity
 * Created by Jocerly on 2017/3/8.
 */
@ContentView(R.layout.activity_msg)
public class MsgActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.swipeLayout)
    private JCSwipeRefreshLayout mSwipeLayout;
    @BindView(id = R.id.recyclerViewMsg)
    private LoadMoreRecyclerView recyclerViewMsg;
    @BindView(id = R.id.textNoData)
    private TextView textNoData;

    private MsgAdapter adapter;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private int pageIndex = 1;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("消息");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("消息");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancelAll();

        textTitle.setText(R.string.msg);
        btnRight.setVisibility(View.GONE);
        mSwipeLayout.setColorSchemeResources(R.color.red);
    }

    @Override
    public void initData() {
        colorGap = getResources().getColor(R.color.base_bg);
        mSwipeLayout.setOnRefreshListener(refreshlistener);  //设置下拉刷新监听器
        adapter = new MsgAdapter(aty, listData);
        adapter.setOnItemClickListener(onItemClickListener);
        adapter.setCanLoadMore(true);  //设置可以上拉加载，如果设置为false则不能上拉加载
        LinearLayoutManager layoutManager = new LinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerViewMsg.setLayoutManager(aty, 0);  //设置LayoutManager和分隔符
        recyclerViewMsg.initDecoration(aty, (int) getResources().getDimension(R.dimen.pad10), colorGap);
        recyclerViewMsg.setLoadMoreListener(loadMoreListener);    //设置上拉加载监听器
        recyclerViewMsg.setRecyclerAdapter(adapter); //RecyclerView绑定Adapter

        checkHttpData();

        getMsgList(true, false);
    }

    private void checkHttpData() {
        if (StringUtils.isEmpty(MyApplication.instance.UserId) || StringUtils.isEmpty(MyApplication.instance.Token)) {
            MyApplication.instance.UserId = Common.getAppInfo(aty, Constants.Preference.UserId, "");
            MyApplication.instance.StoreSerialNoDefault = Common.getAppInfo(aty, Constants.Preference.StoreSerialNoDefault, "");
            MyApplication.instance.Token = Common.getAppInfo(aty, Constants.Preference.Token, "");
        }
    }

    JCSwipeRefreshLayout.OnRefreshListener refreshlistener = new JCSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pageIndex = 1;
                    getMsgList(false, true);
                }
            }, Constants.RefreshTime);
        }
    };

    LoadMoreRecyclerView.PTLoadMoreListener loadMoreListener = new LoadMoreRecyclerView.PTLoadMoreListener() {
        @Override
        public void loadMore() {
            pageIndex++;
            getMsgList(false, false);
        }
    };

    MsgAdapter.OnItemClickListener onItemClickListener = new MsgAdapter.OnItemClickListener() {
        @Override
        public void itemClick(String url, String title) {
            Bundle bundle = new Bundle();
            bundle.putString("ClassUrl", url);
            bundle.putString("ClassName", title);
            skipActivity(aty, WebViewActivity.class, bundle);
        }

        @Override
        public void OnFooterClick() {
            recyclerViewMsg.startLoadMore();
        }
    };

    /**
     * 获取数据
     */
    private void getMsgList(final boolean isShowDialog, final boolean isShowToast) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("PageIndex", pageIndex);
        params.put("PageSize", Constants.PageSize);
        httpRequestService.doRequestData(aty, "User/MsgList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setRefreshing(false);  //下拉刷新结束
                }
                if (Constants.M00.equals(resultParam.resultId)) {
                    Common.setAppInfo(aty, Constants.Preference.MsgNum, "0");
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
                            textNoData.setVisibility(View.GONE);
                            recyclerViewMsg.setVisibility(View.VISIBLE);
                        } else {
                            textNoData.setVisibility(View.VISIBLE);
                            recyclerViewMsg.setVisibility(View.GONE);
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

    public void refrashData() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancelAll();
        pageIndex = 1;
        getMsgList(true, false);
    }
}
