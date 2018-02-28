package com.yatang.xc.xcr.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.SettlementAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.dialog.ScreenDialog;
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
 * 结算管理Activity
 * Created by Jocerly on 2017/3/8.
 */
@ContentView(R.layout.activity_settlement_manage)
public class SettlementManageActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.textStoreSerialNum)
    private TextView textStoreSerialNum;
    @BindView(id = R.id.textAllPrice)
    private TextView textAllPrice;

    @BindView(id = R.id.swipeLayout)
    private JCSwipeRefreshLayout mSwipeLayout;
    @BindView(id = R.id.mRecyclerView)
    private LoadMoreRecyclerView mRecyclerView;
    @BindView(id = R.id.textNoData)
    private TextView textNoData;
    @BindView(id = R.id.rbSettlement, click = true)
    private RadioButton rbSettlement;
    @BindView(id = R.id.rbCouponList, click = true)
    private RadioButton rbCouponList;

    private SettlementAdapter adapter;
    private List<ConcurrentHashMap<String, String>> listData;
    private int pageIndex = 1;
    private ScreenDialog dialog;
    private String startDate = null;
    private String endDate = null;
    private int type = 1;//1:订单结算、2：优惠券结算
    ArrayList<ConcurrentHashMap<String, String>> listDataTmp;
    private NomalDialog nomalDialog;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("结算管理");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("结算管理");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText(R.string.settlement_manage);
        btnRight.setText("说明");
        mSwipeLayout.setColorSchemeResources(R.color.red);
        textStoreSerialNum.setText(MyApplication.instance.StoreSerialNoDefault);
    }

    @Override
    public void initData() {
        listDataTmp = new ArrayList<>();
        listData = new ArrayList<>();
        dialog = new ScreenDialog(aty);
        dialog.setOnScreenDialogClickLinster(onScreenDialogClickLinster);
        nomalDialog = new NomalDialog(aty);
        nomalDialog.setOnNoamlLickListener(onNoamlLickListener);

        mSwipeLayout.setOnRefreshListener(refreshlistener);  //设置下拉刷新监听器
        mRecyclerView.setLayoutManager(aty, 0);  //设置LayoutManager和分隔符
       // mRecyclerView.initDecoration(aty, (int) getResources().getDimension(R.dimen.pad1_px), colorGap);
        mRecyclerView.setLoadMoreListener(loadMoreListener);    //设置上拉加载监听器

        adapter = new SettlementAdapter(aty, listData);
        adapter.setCanLoadMore(true);  //设置可以上拉加载，如果设置为false则不能上拉加载
        adapter.setOnItemClickLinster(onItemClickLinster);
        mRecyclerView.setRecyclerAdapter(adapter); //RecyclerView绑定Adapter
        adapter.setType(type);
        getSettlementManageList(true, false);
    }

    JCSwipeRefreshLayout.OnRefreshListener refreshlistener = new JCSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pageIndex = 1;
                    if (type == 1){
                        getSettlementManageList(false, true);
                    }else {
                        getSettlementCouponList(false, true);
                    }

                }
            }, Constants.RefreshTime);
        }
    };
    LoadMoreRecyclerView.PTLoadMoreListener loadMoreListener = new LoadMoreRecyclerView.PTLoadMoreListener() {
        @Override
        public void loadMore() {
            pageIndex++;
            if (type == 1){
                getSettlementManageList(false, false);
            }else {
                getSettlementCouponList(false, false);
            }

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
    private void getSettlementManageList(final boolean isShowDialog, final boolean isShowToast) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("PageIndex", pageIndex);
        params.put("PageSize", Constants.PageSize);
        httpRequestService.doRequestData(aty, "User/SettlementManageList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setRefreshing(false);  //下拉刷新结束
                }
                if (Constants.M00.equals(resultParam.resultId)) {
                    String temp = Common.formatTosepara(resultParam.mapData.get("SettlementAllValue"), 3, 2);
                    if (!StringUtils.isEmpty(temp)) {
                        textAllPrice.setText(temp);
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
                        if (listData.size() > 0) {
                            adapter.notifyDataSetChanged();
                            textNoData.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);

                            try {
                                adapter.finishLoad(BaseRecyclerViewAdapter.STATE_SUCCESS);
                            } catch (Exception e) {
                                e.printStackTrace();
                                adapter.finishLoad(BaseRecyclerViewAdapter.STATE_SUCCESS);
                            }

                            if (pageIndex == 1 && listData.size() < Constants.PageSize) {// 没有更多数据了
                                adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FINISH);
                            }
                        }else {
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
     * 获取优惠券结算数据
     * @param isShowDialog
     * @param isShowToast
     */
    private void getSettlementCouponList(final boolean isShowDialog, final boolean isShowToast){
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("PageIndex", pageIndex);
        params.put("PageSize", Constants.PageSize);
        httpRequestService.doRequestData(aty,"User/SettlementCouponList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setRefreshing(false);  //下拉刷新结束
                }
                if (Constants.M00.equals(resultParam.resultId)){
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
                            mRecyclerView.setVisibility(View.VISIBLE);

                            try {
                                adapter.finishLoad(BaseRecyclerViewAdapter.STATE_SUCCESS);
                            } catch (Exception e) {
                                e.printStackTrace();
                                adapter.finishLoad(BaseRecyclerViewAdapter.STATE_SUCCESS);
                            }

                            if (pageIndex == 1 && listData.size() < Constants.PageSize) {// 没有更多数据了
                                adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FINISH);
                            }
                        }else {
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
     * 筛选回调
     */
    ScreenDialog.OnScreenDialogClickLinster onScreenDialogClickLinster = new ScreenDialog.OnScreenDialogClickLinster() {
        @Override
        public void OK(String startTime, String endTime, ArrayList<ConcurrentHashMap<String, String>> list, ArrayList<ConcurrentHashMap<String, String>> list2) {
            pageIndex = 1;
            startDate = startTime;
            endDate = endTime;
            listDataTmp = list;
            getSettlementManageList(true, false);
        }
    };

    SettlementAdapter.OnItemClickLinster onItemClickLinster = new SettlementAdapter.OnItemClickLinster() {
        @Override
        public void itemClickLinster(ConcurrentHashMap<String,String> mapData, int type) {
                Bundle bundle = new Bundle();
                bundle.putInt("type", type);
                bundle.putSerializable("mapData", mapData);
                skipActivityForResult(aty, SettlementManageDetialActivity.class, bundle, 0);
        }

        @Override
        public void OnFooterClick() {
            mRecyclerView.startLoadMore();
        }
    };

    @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 0){
                if (resultCode == RESULT_OK){
                    getSettlementManageList(true, false);
                }
            }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnRight:
                nomalDialog.showTitle("说明",R.string.txt_settlement,"我知道了");
                break;
            case R.id.rbSettlement:
                pageIndex = 1;
                type = 1;
                adapter.setType(type);
                getSettlementManageList(true, false);
                break;
            case R.id.rbCouponList:
                pageIndex = 1;
                type = 2;
                adapter.setType(type);
                getSettlementCouponList(true, false);
        }
    }
}