package com.yatang.xc.xcr.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatang.xc.supchain.uitls.JCLoger;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.activity.CouponActivity;
import com.yatang.xc.xcr.activity.CouponDetailsActivity;
import com.yatang.xc.xcr.activity.MainActivity;
import com.yatang.xc.xcr.adapter.CouponListAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.widget.recyclevew.LoadMoreRecyclerView;
import org.jocerly.jcannotation.widget.swiperefreshlayout.JCSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 店铺活动首页Fragment
 * Created by dengjiang on 2017/10/12.
 */

public class CouponFragment extends BaseFragment {
    public static final int CODE_DETAILS = 0;
    private JCSwipeRefreshLayout mSwipeLayout;
    private LoadMoreRecyclerView recyclerView;
    private String status;
    private CouponListAdapter adapter;
    private TextView textNoData; //没有数据
    private List<ConcurrentHashMap<String, String>> listData;
    private int pageIndex;
    private boolean needRefresh = false;
    private CouponActivity couponActivity;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_coupon, null);
        initView(view);
        return view;
    }

    public void setCouponActivity(CouponActivity couponActivity) {
        this.couponActivity = couponActivity;
    }

    /**
     * 初始化
     *
     * @param view
     */
    private void initView(View view) {
        Bundle b = getArguments();
        if (b != null) {
            status = b.getString("Status");
        }
        pageIndex = 1;
        listData = new ArrayList<>();
        adapter = new CouponListAdapter(getActivity(), listData, status);
        adapter.setOnItemClickListener(onItemClickListener);
        adapter.setCanLoadMore(true);  //设置可以上拉加载，如果设置为false则不能上拉加载
        mSwipeLayout = (JCSwipeRefreshLayout) view.findViewById(R.id.swipeLayout);
        recyclerView = (LoadMoreRecyclerView) view.findViewById(R.id.recyclerView);

        textNoData = (TextView) view.findViewById(R.id.textNoData);

        mSwipeLayout.setColorSchemeResources(R.color.red);
        mSwipeLayout.setOnRefreshListener(refreshlistener);  //设置下拉刷新监听器
        recyclerView.setLoadMoreListener(loadMoreListener);
        recyclerView.setLayoutManager(aty, 0);  //设置LayoutManager和分隔符
        recyclerView.setRecyclerAdapter(adapter); //RecyclerView绑定Adapter
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listData.size() <= 0) {
            refreshData(true, false);
        }
        if (needRefresh) {
            refreshData(true, false);
            needRefresh = false;
        }
    }

    private void refreshData(final boolean isShowDialog, final boolean isShowToast) {
        pageIndex = 1;
        getCouponData(isShowDialog, isShowToast);
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    /**
     * 获取优惠券数据
     */
    private void getCouponData(final boolean isShowDialog, final boolean isShowToast) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("EventStatus", status);
        params.put("PageIndex", pageIndex);
        params.put("PageSize", Constants.PageSize);
        httpRequestService.doRequestData(aty, "User/EventList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

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
                            textNoData.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            textNoData.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                    }
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    ((MainActivity) getActivity()).doEmpLoginOut();
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

    CouponListAdapter.OnItemClickListener onItemClickListener = new CouponListAdapter.OnItemClickListener() {
        @Override
        public void itemClick(String eventID, String eventType) {
            Bundle b = new Bundle();
            b.putString("EventId", eventID);
            b.putString("EventType", eventType);
            b.putString("Status", status);
            skipActivityForResult(aty, CouponDetailsActivity.class, b, CODE_DETAILS);
        }

        @Override
        public void OnFooterClick() {
            pageIndex++;
            recyclerView.startLoadMore();
        }
    };

    /**
     * 下拉刷新回调
     */
    JCSwipeRefreshLayout.OnRefreshListener refreshlistener = new JCSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshData(true, true);
                }
            }, Constants.RefreshTime);
        }
    };

    /**
     * 加载更多回调
     */
    LoadMoreRecyclerView.PTLoadMoreListener loadMoreListener = new LoadMoreRecyclerView.PTLoadMoreListener() {
        @Override
        public void loadMore() {
            pageIndex++;
            getCouponData(false, false);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CouponFragment.CODE_DETAILS) {
                couponActivity.refreshAll();
            }
        }
    }
}
