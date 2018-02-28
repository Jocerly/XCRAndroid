package com.yatang.xc.xcr.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.OrderListAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.ScreenDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.widget.recyclevew.LoadMoreRecyclerView;
import org.jocerly.jcannotation.widget.swiperefreshlayout.JCSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 全部订单退货单页面
 * Created by DengJiang on 2017/8/11.
 */
@ContentView(R.layout.activity_all_order)
public class AllOrderActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    public static final String TAG = "AllOrderActivity";

    public static final String TAB_ALL_ORDER = "1";
    public static final String TAB_ALL_RETURN = "2";

    public static final int SEARCH_ALLORDER = 5;
    public static final int SEARCH_ALLRETURN = 6;

    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;

    @BindView(id = R.id.swipeLayout)
    private JCSwipeRefreshLayout mSwipeLayout;
    @BindView(id = R.id.recyclerView)
    private LoadMoreRecyclerView recyclerView;

    @BindView(id = R.id.radioAllOrder, click = true)
    private RadioButton radioAllOrder;
    @BindView(id = R.id.radioReturnOrder, click = true)
    private RadioButton radioReturnOrder;

    @BindView(id = R.id.textNoData)
    private TextView textNoData;

    private ScreenDialog dia_AllOrder;//全部订单筛选对话框
    private ScreenDialog dia_AllReturn;//全部退货单筛选对话框

    private ConcurrentHashMap<String, Integer> pageIndexMap;//保存页码的map
    private ConcurrentHashMap<String, Integer> adapterStatus;//每一个tab的adapter 的状态
    private List<ConcurrentHashMap<String, String>> listData_Order = new ArrayList<>();//全部的订单
    private List<ConcurrentHashMap<String, String>> listData_Return = new ArrayList<>();//全部的退货单
    private ConcurrentHashMap<String, List<ConcurrentHashMap<String, String>>> map_List = new ConcurrentHashMap<>();//保存列表数据的map
    private OrderListAdapter adapter;
    private String tab = TAB_ALL_ORDER;
    private String orDerStartTime = "", orDerEndTime = "";
    private String returnStartTime = "", returnEndTime = "";
    private String orDerStatus = "0", returnStatus = "0";

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("全部订单");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        mSwipeLayout.setColorSchemeResources(R.color.red);
        textTitle.setText("全部订单");
        btnRight.setText("筛选");
        radioAllOrder.setChecked(true);
        radioAllOrder.setOnCheckedChangeListener(this);
        radioReturnOrder.setOnCheckedChangeListener(this);
        updateRadioButton(radioAllOrder, radioAllOrder.isChecked());
        updateRadioButton(radioReturnOrder, radioReturnOrder.isChecked());
        dia_AllOrder = new ScreenDialog(aty);
        dia_AllReturn = new ScreenDialog(aty);
        dia_AllOrder.setOnOrderScreenDialogClickLinster(onOrderScreenDialogClickLinster);
        dia_AllReturn.setOnOrderScreenDialogClickLinster(onOrderScreenDialogClickLinster);
        mSwipeLayout.setOnRefreshListener(refreshlistener);
        detachLayout();//移除 滑动 退出activity
    }

    @Override
    public void initData() {
        pageIndexMap = new ConcurrentHashMap<>();
        pageIndexMap.put(TAB_ALL_ORDER, 1);
        pageIndexMap.put(TAB_ALL_RETURN, 1);//初始化两个tab 分页数据

        adapterStatus = new ConcurrentHashMap<>();
        adapterStatus.put(TAB_ALL_ORDER, BaseRecyclerViewAdapter.STATE_DEFAULT);
        adapterStatus.put(TAB_ALL_RETURN, BaseRecyclerViewAdapter.STATE_DEFAULT);

        map_List.put(TAB_ALL_ORDER, listData_Order);
        map_List.put(TAB_ALL_RETURN, listData_Return);//初始化两个tab list数据

        tab = TAB_ALL_ORDER;//默认进入 第一个 tab
        adapter = new OrderListAdapter(aty, map_List.get(tab), true);
        adapter.setOnItemClickListener(onItemClickListener);
        adapter.setCanLoadMore(true);  //设置可以上拉加载，如果设置为false则不能上拉加载
        recyclerView.setLayoutManager(aty, 0);  //设置LayoutManager和分隔符
        recyclerView.setLoadMoreListener(loadMoreListener);    //设置上拉加载监听器
        recyclerView.setRecyclerAdapter(adapter); //RecyclerView绑定Adapter

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("全部订单");
        MobclickAgent.onResume(aty);
        if (map_List.get(tab).size() <= 0) {
            JCLoger.log(TAG, "onResume refresh:" + tab);
            refreshCurrentTab();
        }
    }

    /**
     * 刷新当前tab
     */
    private void refreshCurrentTab() {
        if (TAB_ALL_ORDER.equals(tab)) {
            getOrderList(true, false, tab, orDerStatus, orDerStartTime, orDerEndTime);
        } else if (TAB_ALL_RETURN.equals(tab)) {
            getOrderList(true, false, tab, returnStatus, returnStartTime, returnEndTime);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.radioAllOrder:
                if (b) {
                    //全部订单
                    tab = TAB_ALL_ORDER;
                }
                break;
            case R.id.radioReturnOrder:
                if (b) {
                    //全部退货单
                    tab = TAB_ALL_RETURN;
                }
                break;
        }
        updateRadioButton(compoundButton, b);
        if (b) {
            adapter.setList(map_List.get(tab), tab.equals(TAB_ALL_RETURN));
            adapter.notifyDataSetChanged();
            adapter.finishLoad(adapterStatus.get(tab));
            if ((map_List.get(tab).size() <= 0)) {
                pageIndexMap.put(tab, 1);
                refreshCurrentTab();
            } else {
                textNoData.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }

    }

    /**
     * 筛选对话框回调
     */
    private ScreenDialog.OnOrderScreenDialogClickLinster onOrderScreenDialogClickLinster = new ScreenDialog.OnOrderScreenDialogClickLinster() {
        @Override
        public void OK(String starte, String end, String status, int type) {
            if (SEARCH_ALLORDER == type) {
                //全部订单
                orDerStartTime = starte;
                orDerEndTime = end;
                orDerStatus = status;
                getOrderList(false, false, tab, orDerStatus, orDerStartTime, orDerEndTime);
            } else if (SEARCH_ALLRETURN == type) {
                //全部退货单
                returnStartTime = starte;
                returnEndTime = end;
                returnStatus = status;
                getOrderList(false, false, tab, returnStatus, returnStartTime, returnEndTime);
            }

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
                //显示筛选对话框
                if (TAB_ALL_ORDER.equals(tab)) {
                    dia_AllOrder.show(SEARCH_ALLORDER);
                } else if (TAB_ALL_RETURN.equals(tab)) {
                    dia_AllReturn.show(SEARCH_ALLRETURN);
                }
                break;
        }
    }

    /**
     * 获取数据
     */
    private void getOrderList(final boolean isShowDialog, final boolean isShowToast, final String currentTab, final String type, final String StartDate, final String EndDate) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        if (TAB_ALL_RETURN.equals(currentTab)) {
            params.put("RefusedType", type);//特殊处理 类型为 TYPE_RETURNING 的为退货单
        } else {
            params.put("OrderType", type);
        }
        params.put("PageIndex", pageIndexMap.get(currentTab));
        params.put("PageSize", Constants.PageSize);
        params.put("StartDate", StartDate);
        params.put("EndDate", EndDate);
        httpRequestService.doRequestData(aty, "User/OrderList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (tab != currentTab) {
                    JCLoger.debug("tab != currentTab return");
                    return;
                }
                if (mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setRefreshing(false);  //下拉刷新结束
                }
                int pageIndex = pageIndexMap.get(currentTab);
                if (Constants.M00.equals(resultParam.resultId)) {
                    if (pageIndex == 1) {
                        if (isShowToast) {
                            toast("刷新成功");
                        }
                    }
                    setList(resultParam.listData, pageIndex < resultParam.totalpage);
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    //如果请求下一页数据失败 pageIndex 减一
                    pageIndex = pageIndex--;
                    pageIndexMap.put(type, pageIndex);
                    adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FAILURE);
                    adapterStatus.put(currentTab, BaseRecyclerViewAdapter.STATE_FAILURE);
                }
            }
        });
    }

    /**
     * 设置list 数据
     *
     * @param listData 根据tab 从服务器获取到的数据
     */
    private void setList(List<ConcurrentHashMap<String, String>> listData, boolean haveMoreData) {
        JCLoger.debug("setList " + pageIndexMap.get(tab) + " " + listData);
        if (pageIndexMap.get(tab) == 1) {
            map_List.get(tab).clear();//如果页数是1，则清除list中之前的数据。
        }
        map_List.get(tab).addAll(listData);
        if (map_List.get(tab).size() > 0) {
            adapter.setList(map_List.get(tab), tab.equals(TAB_ALL_RETURN));
            textNoData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            if (haveMoreData) {
                adapter.finishLoad(BaseRecyclerViewAdapter.STATE_SUCCESS);
                adapterStatus.put(tab, BaseRecyclerViewAdapter.STATE_SUCCESS);
            } else {
                adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FINISH);
                adapterStatus.put(tab, BaseRecyclerViewAdapter.STATE_FINISH);
            }
            adapter.notifyDataSetChanged();
        } else {
            textNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置订单状态
     */
    private void setOrderStatus(final boolean isShowDialog, final boolean isShowToast, final String OrderNo, final String CancelId, final String OrderStatus, String ReasonType) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("OrderNo", OrderNo);
        params.put("Action", OrderStatus);
        params.put("CancelId", CancelId);
        params.put("RefusedMsg", ReasonType);
        httpRequestService.doRequestData(aty, "User/UpdateOrder", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setRefreshing(false);  //下拉刷新结束
                }
                if (Constants.M00.equals(resultParam.resultId)) {
                    //1-接单，2-拒绝接单，4：确认发货、3-配送完成、5：客户拒收（注意：不是订单状态）、6：拒绝退货、7：确认退货、
                    switch (Integer.parseInt(OrderStatus)) {
                        case 1:
                            MobclickAgent.onEvent(aty, "Firm_Order");
                            break;
                        case 2:
                            MobclickAgent.onEvent(aty, "Firm_Order_Refused");
                            break;
                        case 3:
                            MobclickAgent.onEvent(aty, "Firm_Order_Distribute");
                            break;
                        case 4:
                            MobclickAgent.onEvent(aty, "Firm_Order_Deliver");
                            break;
                        case 5:
                            MobclickAgent.onEvent(aty, "Firm_Order_Reject");
                            break;
                        case 6:
                            MobclickAgent.onEvent(aty, "Firm_Order_RejectReturn");
                            break;
                    }
                    toast("提交成功");
                    refreshAll(true);
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    refreshAll(true);
                    toast(resultParam.message);
                }
            }
        });
    }

    /**
     * adapter 事件回调
     */
    OrderListAdapter.OnItemClickListener onItemClickListener = new OrderListAdapter.OnItemClickListener() {

        @Override
        public void itemClick(String OrderNo, String CancelId) {
            //点击了某项进入详情
            JCLoger.log(TAG, "itemClick：" + OrderNo + "  " + CancelId);
            Bundle bundle = new Bundle();
            bundle.putString("OrderNo", OrderNo);
            bundle.putString("CancelId", CancelId);
            skipActivityForResult(aty, OrderDetailsActivity.class, bundle, OrderManagementActivity.DETAILS_CODE);
        }

        @Override
        public void OnFooterClick() {
            //点击了底部item 加载更多
            JCLoger.log(TAG, "OnFooterClick");
            recyclerView.startLoadMore();
        }

        @Override
        public void onRefuseClick(String OrderNo) {
            //点击了拒绝 按钮  进入商家拒绝页面
            JCLoger.log(TAG, "onRefuseClick");
            Bundle b = new Bundle();
            b.putString("OrderNo", OrderNo);
            b.putString("Type", RefuseOrderActivity.REFUSE_TYPE_BUSINESS);
            skipActivityForResult(aty, RefuseOrderActivity.class, b, OrderManagementActivity.REFUSE_CODE);
        }

        @Override
        public void onCustomRefuseClick(String OrderNo) {
            //点击了拒绝 按钮  进入客户拒绝页面
            JCLoger.log(TAG, "onRefuseClick");
            Bundle b = new Bundle();
            b.putString("OrderNo", OrderNo);
            b.putString("Type", RefuseOrderActivity.REFUSE_TYPE_CUSTOM);
            skipActivityForResult(aty, RefuseOrderActivity.class, b, OrderManagementActivity.CUSTOM_REFUSE_CODE);
        }

        @Override
        public void onDeliverredClick(String OrderNo) {
            //选择了确认发货按钮
            JCLoger.log(TAG, "onDeliverredClick");
            setOrderStatus(true, true, OrderNo, "", OrderManagementActivity.ACTION_DELIVERRED, "");

        }

        @Override
        public void onReturnedClick(String cancelId) {
            //选择了确认退货
            JCLoger.log(TAG, "onReturnedClick");
            setOrderStatus(true, true, "", cancelId, OrderManagementActivity.ACTION_CONFIRM_RETURN, "");
        }

        @Override
        public void onRefuseReturnClick(String cancelId) {
            //选择了拒绝退货
            JCLoger.log(TAG, "onRefuseReturnClick");
            Bundle b = new Bundle();
            b.putString("OrderNo", cancelId);
            b.putString("Type", RefuseOrderActivity.REFUSE_TYPE_RETURN_GOODS);
            skipActivityForResult(aty, RefuseOrderActivity.class, b, OrderManagementActivity.REFUSE_RETURN_CODE);
        }

        @Override
        public void onAcceptClick(String OrderNo) {
            //选择了接单按钮
            JCLoger.log(TAG, "onAcceptClick");
            setOrderStatus(true, true, OrderNo, "", OrderManagementActivity.ACTION_ACCEPT, "");
        }

        @Override
        public void onDeliverInfoClick(ConcurrentHashMap<String, String> mapData) {
            //配送信息
            JCLoger.log(TAG, "onDeliverInfoClick");
            Bundle b = new Bundle();
            b.putString(DeliverInfoActivity.NAME, mapData.get("DistributorName"));
            b.putString(DeliverInfoActivity.PHONE, mapData.get("DistributorPhone"));
            b.putString(DeliverInfoActivity.SENDTIME, mapData.get("DeliveryTime"));
            b.putString(DeliverInfoActivity.REACHTIME, mapData.get("SucTime"));
            b.putString(DeliverInfoActivity.ADDRESS, mapData.get("Address"));
            skipActivity(aty, DeliverInfoActivity.class, b);
        }

        @Override
        public void onFinishClick(String OrderNo) {
            //选择了完成按钮
            JCLoger.log(TAG, "onFinishClick");
            setOrderStatus(true, true, OrderNo, "", OrderManagementActivity.ACTION_FINISH, "");
        }
    };

    /**
     * 加载更多
     */
    LoadMoreRecyclerView.PTLoadMoreListener loadMoreListener = new LoadMoreRecyclerView.PTLoadMoreListener() {
        @Override
        public void loadMore() {
            pageIndexMap.put(tab, pageIndexMap.get(tab) + 1);//加载更多同时页码加1
            refreshCurrentTab();
        }
    };

    /**
     * 更新tab 的状态
     */
    private void updateRadioButton(CompoundButton compoundButton, boolean checked) {
        if (checked) {
            compoundButton.setTextColor(getResources().getColor(R.color.red));
        } else {
            compoundButton.setTextColor(getResources().getColor(R.color.text_dark));
        }
    }

    /**
     * 所有tab 刷新
     */
    public void refreshAll(boolean needRefresh) {
        //清空所有的数据
        listData_Order.clear();
        listData_Return.clear();
        //页数全部变为1
        pageIndexMap.put(TAB_ALL_ORDER, 1);
        pageIndexMap.put(TAB_ALL_RETURN, 1);
        //刷新当前tab 的数据
        if (needRefresh) {
            refreshCurrentTab();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OrderManagementActivity.REFUSE_CODE) {
            if (resultCode == RESULT_OK) {
                JCLoger.log(TAG, "onActivityResult refuse ACTION_REFUSE");
                String refuseType = data.getExtras().getString("refuseType");
                String OrderNo = data.getExtras().getString("OrderNo");
                setOrderStatus(true, true, OrderNo, "", OrderManagementActivity.ACTION_REFUSE, refuseType);
            }
        } else if (requestCode == OrderManagementActivity.CUSTOM_REFUSE_CODE) {
            if (resultCode == RESULT_OK) {
                JCLoger.log(TAG, "onActivityResult refuse ACTION_CUSTOM_REFUSE");
                String refuseType = data.getExtras().getString("refuseType");
                String OrderNo = data.getExtras().getString("OrderNo");
                setOrderStatus(true, true, OrderNo, OrderManagementActivity.ACTION_CUSTOM_REFUSE, "", refuseType);
            }

        } else if (requestCode == OrderManagementActivity.REFUSE_RETURN_CODE) {
            if (resultCode == RESULT_OK) {
                JCLoger.log(TAG, "onActivityResult refuse ACTION_REFUSE_RETURN");
                String refuseType = data.getExtras().getString("refuseType");
                String CancelId = data.getExtras().getString("OrderNo");
                setOrderStatus(true, true, "", CancelId, OrderManagementActivity.ACTION_REFUSE_RETURN, refuseType);
            }

        } else if (requestCode == OrderManagementActivity.DETAILS_CODE) {
            if (resultCode == RESULT_OK) {
                JCLoger.log(TAG, "onActivityResult refresh all");
                refreshAll(false);
            }
        }
    }

    /**
     * 下拉刷新
     */
    JCSwipeRefreshLayout.OnRefreshListener refreshlistener = new JCSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pageIndexMap.put(tab, 1);//下拉刷新的时候 将当前tab 的页码回归为1
                    refreshCurrentTab();
                }
            }, Constants.RefreshTime);
        }
    };
}
