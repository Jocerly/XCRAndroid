package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.OrderListAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.widget.recyclevew.LoadMoreRecyclerView;
import org.jocerly.jcannotation.widget.swiperefreshlayout.JCSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订单管理主页面
 * Created by DengJiang on 2017/6/9.
 */
@ContentView(R.layout.activity_order_management)
public class OrderManagementActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    public static final String TAG = "OrderManagementActivity";
    public static final String TYPE_WAIT = "2";//待接单
    public static final String TYPE_DELIVER = "3";//待发货
    public static final String TYPE_DELIVERRED = "31";//已发货
    public static final String TYPE_RETURNING = "201";// 退货中

    public static final String ACTION_ACCEPT = "1";//接单操作
    public static final String ACTION_REFUSE = "2";//拒绝接单
    public static final String ACTION_DELIVERRED = "4";//确认发货
    public static final String ACTION_FINISH = "3";//配送完成
    public static final String ACTION_CUSTOM_REFUSE = "5";//客户拒收
    public static final String ACTION_REFUSE_RETURN = "6";//拒绝退货
    public static final String ACTION_CONFIRM_RETURN = "7";//确认退货

    public static final int REFUSE_CODE = 1;//商家拒单
    public static final int DETAILS_CODE = 2;//详情页面
    public static final int CUSTOM_REFUSE_CODE = 3;//客户拒收
    public static final int REFUSE_RETURN_CODE = 4;//拒绝退货

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

    @BindView(id = R.id.radioDeliverOrder, click = true)
    private RadioButton radioDeliverOrder;

    @BindView(id = R.id.radioWaitingOrder, click = true)
    private RadioButton radioWaitingOrder;

    @BindView(id = R.id.radioDeliverredOrder, click = true)
    private RadioButton radioDeliverredOrder;

    @BindView(id = R.id.radioReturn, click = true)
    private RadioButton radioReturn;

    @BindView(id = R.id.textNoData)
    private TextView textNoData;

    private ConcurrentHashMap<String, Integer> pageIndexMap;//保存页码的map
    private ConcurrentHashMap<String, Integer> adapterStatus;//每一个tab的adapter 的状态
    private List<ConcurrentHashMap<String, String>> listData_wait = new ArrayList<>();//待接单列表数据
    private List<ConcurrentHashMap<String, String>> listData_deliver = new ArrayList<>();//配送中列表数据
    private List<ConcurrentHashMap<String, String>> listData_deliverred = new ArrayList<>();//已发货列表数据
    private List<ConcurrentHashMap<String, String>> listData_return = new ArrayList<>();//退货中
    private ConcurrentHashMap<String, List<ConcurrentHashMap<String, String>>> map_List = new ConcurrentHashMap<>();//保存列表数据的map

    private OrderListAdapter adapter;
    private String tab = TYPE_WAIT;

    private NomalDialog returnedDialog;

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("外送订单");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("外送订单");
        btnRight.setText("全部");
        mSwipeLayout.setColorSchemeResources(R.color.red);
        mSwipeLayout.setOnRefreshListener(refreshlistener);  //设置下拉刷新监听器
        radioWaitingOrder.setOnCheckedChangeListener(OrderManagementActivity.this);
        radioDeliverOrder.setOnCheckedChangeListener(OrderManagementActivity.this);
        radioDeliverredOrder.setOnCheckedChangeListener(OrderManagementActivity.this);
        radioReturn.setOnCheckedChangeListener(OrderManagementActivity.this);

        radioWaitingOrder.setChecked(true);
        updateRadioButton(radioWaitingOrder, true);
        updateRadioButton(radioDeliverOrder, false);
        updateRadioButton(radioDeliverredOrder, false);
        updateRadioButton(radioReturn, false);
        detachLayout();//移除 滑动 退出activity
    }

    @Override
    public void initData() {
        pageIndexMap = new ConcurrentHashMap<>();
        pageIndexMap.put(TYPE_WAIT, 1);
        pageIndexMap.put(TYPE_DELIVER, 1);
        pageIndexMap.put(TYPE_DELIVERRED, 1);
        pageIndexMap.put(TYPE_RETURNING, 1);//初始化4个tab 分页 map 数据

        adapterStatus = new ConcurrentHashMap<>();
        adapterStatus.put(TYPE_WAIT, BaseRecyclerViewAdapter.STATE_DEFAULT);
        adapterStatus.put(TYPE_DELIVER, BaseRecyclerViewAdapter.STATE_DEFAULT);
        adapterStatus.put(TYPE_DELIVERRED, BaseRecyclerViewAdapter.STATE_DEFAULT);
        adapterStatus.put(TYPE_RETURNING, BaseRecyclerViewAdapter.STATE_DEFAULT);


        map_List.put(TYPE_WAIT, listData_wait);
        map_List.put(TYPE_DELIVER, listData_deliver);
        map_List.put(TYPE_DELIVERRED, listData_deliverred);
        map_List.put(TYPE_RETURNING, listData_return);//初始化四个分页数据list

        tab = TYPE_WAIT;//默认进入 第一个 tab
        adapter = new OrderListAdapter(aty, map_List.get(tab), false);
        adapter.setOnItemClickListener(onItemClickListener);
        adapter.setCanLoadMore(true);  //设置可以上拉加载，如果设置为false则不能上拉加载
        recyclerView.setLayoutManager(aty, 0);  //设置LayoutManager和分隔符
        recyclerView.setLoadMoreListener(loadMoreListener);    //设置上拉加载监听器
        recyclerView.setRecyclerAdapter(adapter); //RecyclerView绑定Adapter

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnRight:
                //跳转到全部页面
                skipActivity(aty, AllOrderActivity.class);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("外送订单");
        MobclickAgent.onResume(aty);
        if (map_List.get(tab).size() <= 0) {
            JCLoger.debug("onResume refresh:" + tab);
            getOrderList(false, false, tab);
        }
    }

    /**
     * 设置list 数据
     *
     * @param listData 根据tab 从服务器获取到的数据
     */
    private void setList(List<ConcurrentHashMap<String, String>> listData, boolean haveMoreData) {
        JCLoger.debug("setList tab=" + tab + " " + listData);
        if (pageIndexMap.get(tab) == 1) {
            map_List.get(tab).clear();//如果页数是1，则清除list中之前的数据。
        }
        map_List.get(tab).addAll(listData);
        if (map_List.get(tab).size() > 0) {
            adapter.setList(map_List.get(tab), tab.equals(TYPE_RETURNING));
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
     * 获取数据
     */
    private void getOrderList(final boolean isShowDialog, final boolean isShowToast, final String type) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        if (TYPE_RETURNING.equals(type)) {
            params.put("RefusedType", tab);//特殊处理 类型为 TYPE_RETURNING 的为退货单
        } else {
            params.put("OrderType", tab);
        }
        params.put("PageIndex", pageIndexMap.get(type));
        params.put("PageSize", Constants.PageSize);
        params.put("StartDate", "");
        params.put("EndDate", "");
        httpRequestService.doRequestData(aty, "User/OrderList", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (tab != type) {
                    return;
                }
                if (mSwipeLayout.isRefreshing()) {
                    mSwipeLayout.setRefreshing(false);  //下拉刷新结束
                }
                int pageIndex = pageIndexMap.get(type);
                if (Constants.M00.equals(resultParam.resultId)) {
                    if (!StringUtils.isEmpty(resultParam.mapData.get("NotRecivedNum")) && type != TYPE_RETURNING) {
                        radioWaitingOrder.setText("待接单(" + resultParam.mapData.get("NotRecivedNum") + ")");
                        updateRadioButton(radioWaitingOrder, radioWaitingOrder.isChecked());
                    }

                    if (!StringUtils.isEmpty(resultParam.mapData.get("NotDeliveryNum")) && type != TYPE_RETURNING) {
                        radioDeliverOrder.setText("待发货(" + resultParam.mapData.get("NotDeliveryNum") + ")");
                        updateRadioButton(radioDeliverOrder, radioDeliverOrder.isChecked());
                    }

                    if (!StringUtils.isEmpty(resultParam.mapData.get("ShippedNum")) && type != TYPE_RETURNING) {
                        radioDeliverredOrder.setText("已发货(" + resultParam.mapData.get("ShippedNum") + ")");
                        updateRadioButton(radioDeliverredOrder, radioDeliverredOrder.isChecked());
                    }

                    if (!StringUtils.isEmpty(resultParam.mapData.get("ReturnedNum"))) {
                        radioReturn.setText("退货中(" + resultParam.mapData.get("ReturnedNum") + ")");
                        updateRadioButton(radioReturn, radioReturn.isChecked());
                    }

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
                    JCLoger.debug("MY2==resultParam.resultId=" + resultParam.resultId + "  加载失败");
                    //如果请求下一页数据失败 pageIndex 减一
                    pageIndex--;
                    pageIndexMap.put(type, pageIndex);
                    adapter.finishLoad(BaseRecyclerViewAdapter.STATE_FAILURE);
                    adapterStatus.put(tab, BaseRecyclerViewAdapter.STATE_FAILURE);
                }
            }
        });
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
     * 下拉刷新
     */
    JCSwipeRefreshLayout.OnRefreshListener refreshlistener = new JCSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pageIndexMap.put(tab, 1);//下拉刷新的时候 将当前tab 的页码回归为1
                    getOrderList(false, true, tab);
                }
            }, Constants.RefreshTime);
        }
    };

    /**
     * adapter 事件回调
     */
    OrderListAdapter.OnItemClickListener onItemClickListener = new OrderListAdapter.OnItemClickListener() {

        @Override
        public void itemClick(String OrderNo, String CancelId) {
            //点击了某项进入详情
            JCLoger.debug("itemClick OrderNo=" + OrderNo + " CancelId=" + CancelId);
            Bundle bundle = new Bundle();
            bundle.putString("OrderNo", OrderNo);
            bundle.putString("CancelId", CancelId);
            skipActivityForResult(aty, OrderDetailsActivity.class, bundle, DETAILS_CODE);
        }

        @Override
        public void OnFooterClick() {
            //点击了底部item 加载更多
            JCLoger.debug("OnFooterClick");
            recyclerView.startLoadMore();
        }

        @Override
        public void onRefuseClick(String OrderNo) {
            //点击了拒绝 按钮  进入商家拒绝页面
            JCLoger.debug("onRefuseClick");
            Bundle b = new Bundle();
            b.putString("OrderNo", OrderNo);
            b.putString("Type", RefuseOrderActivity.REFUSE_TYPE_BUSINESS);
            skipActivityForResult(aty, RefuseOrderActivity.class, b, REFUSE_CODE);
        }

        @Override
        public void onCustomRefuseClick(String OrderNo) {
            //点击了拒绝 按钮  进入客户拒绝页面
            JCLoger.debug("onRefuseClick");
            Bundle b = new Bundle();
            b.putString("OrderNo", OrderNo);
            b.putString("Type", RefuseOrderActivity.REFUSE_TYPE_CUSTOM);
            skipActivityForResult(aty, RefuseOrderActivity.class, b, CUSTOM_REFUSE_CODE);
        }

        @Override
        public void onDeliverredClick(String OrderNo) {
            //选择了确认发货按钮
            JCLoger.debug("onDeliverredClick");
            setOrderStatus(true, true, OrderNo, "", ACTION_DELIVERRED, "");

        }

        @Override
        public void onReturnedClick(final String cancelId) {
            //选择了确认退货
            JCLoger.debug("onReturnedClick");
            if (returnedDialog == null) {
                returnedDialog = new NomalDialog(aty);
                returnedDialog.setOnNoamlLickListener(new NomalDialog.OnNoamlLickListener() {
                    @Override
                    public void onOkClick() {
                        setOrderStatus(true, true, "", cancelId, ACTION_CONFIRM_RETURN, "");
                    }
                });
            }
            returnedDialog.show("请确认商品已退回店铺,同\n时退款现金已交到客户手中", "取消", "确认");
        }

        @Override
        public void onRefuseReturnClick(String cancelId) {
            //选择了拒绝退货
            JCLoger.debug("onRefuseReturnClick");
            Bundle b = new Bundle();
            b.putString("OrderNo", cancelId);
            b.putString("Type", RefuseOrderActivity.REFUSE_TYPE_RETURN_GOODS);
            skipActivityForResult(aty, RefuseOrderActivity.class, b, REFUSE_RETURN_CODE);
        }

        @Override
        public void onDeliverInfoClick(ConcurrentHashMap<String, String> mapData) {
            //配送信息
            JCLoger.debug("onDeliverInfoClick");
            MobclickAgent.onEvent(aty, "Order_DistributeMsg");

            Bundle b = new Bundle();
            b.putString(DeliverInfoActivity.NAME, mapData.get("DistributorName"));
            b.putString(DeliverInfoActivity.PHONE, mapData.get("DistributorPhone"));
            b.putString(DeliverInfoActivity.SENDTIME, mapData.get("DeliveryTime"));
            b.putString(DeliverInfoActivity.REACHTIME, mapData.get("SucTime"));
            b.putString(DeliverInfoActivity.ADDRESS, mapData.get("Address"));
            skipActivity(aty, DeliverInfoActivity.class, b);
        }

        @Override
        public void onAcceptClick(String OrderNo) {
            //选择了接单按钮
            JCLoger.debug("onAcceptClick");
            setOrderStatus(true, true, OrderNo, "", ACTION_ACCEPT, "");
        }

        @Override
        public void onFinishClick(String OrderNo) {
            JCLoger.debug("onFinishClick");
            setOrderStatus(true, true, OrderNo, "", ACTION_FINISH, "");
            //选择了完成按钮
        }
    };


    /**
     * 加载更多
     */
    LoadMoreRecyclerView.PTLoadMoreListener loadMoreListener = new LoadMoreRecyclerView.PTLoadMoreListener() {
        @Override
        public void loadMore() {
            pageIndexMap.put(tab, pageIndexMap.get(tab) + 1);//加载更多同时页码加1
            getOrderList(false, false, tab);
        }
    };

    /**
     * 更新tab 的状态
     */
    private void updateRadioButton(CompoundButton compoundButton, boolean checked) {
        if (checked) {
            //选中的tab
            compoundButton.setTextColor(getResources().getColor(R.color.red));
        } else {
            //未选中的tab
            compoundButton.setTextColor(getResources().getColor(R.color.text_dark));
            compoundButton.setText(getSpannableString(compoundButton.getText().toString()));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.radioWaitingOrder:
                if (b) {
                    //等待接单tab
                    tab = TYPE_WAIT;
                }
                break;
            case R.id.radioDeliverOrder:
                if (b) {
                    //待发货tab
                    tab = TYPE_DELIVER;
                }
                break;
            case R.id.radioDeliverredOrder:
                if (b) {
                    //已发货tab
                    tab = TYPE_DELIVERRED;
                }
                break;
            case R.id.radioReturn:
                if (b) {
                    //退货中
                    tab = TYPE_RETURNING;
                }
                break;
            default:
                break;
        }
        updateRadioButton(compoundButton, b);
        if (b) {
            if (map_List.get(tab).size() <= 0) {
                //如果list数据为空 则从服务器获取数据
                JCLoger.debug("refresh tab " + tab);
                pageIndexMap.put(tab, 1);
                getOrderList(true, false, tab);
            } else {
                //如果list数据不为空 则显示对应 的数据
                JCLoger.debug("not refresh tab " + tab);
                textNoData.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setList(map_List.get(tab), tab.equals(TYPE_RETURNING));
                adapter.notifyDataSetChanged();
                adapter.finishLoad(adapterStatus.get(tab));
            }
        }
    }

    /**
     * 设置 string 部分（小括号内部）红色显示
     */
    private SpannableString getSpannableString(String str) {
        SpannableString s = new SpannableString(str);
        if (str.contains("(") && str.contains(")")) {
            int start = str.indexOf("(");
            int end = str.indexOf(")");
            s.setSpan(new ForegroundColorSpan(Color.RED), start + 1, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return s;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REFUSE_CODE) {
            //商家拒单
            if (resultCode == RESULT_OK) {
                JCLoger.debug("onActivityResult refuse order");
                String refuseType = data.getExtras().getString("refuseType");
                String OrderNo = data.getExtras().getString("OrderNo");
                setOrderStatus(true, true, OrderNo, "", ACTION_REFUSE, refuseType);
            }
        } else if (requestCode == CUSTOM_REFUSE_CODE) {
            //客户拒收
            if (resultCode == RESULT_OK) {
                JCLoger.debug("onActivityResult refuse order");
                String refuseType = data.getExtras().getString("refuseType");
                String OrderNo = data.getExtras().getString("OrderNo");
                setOrderStatus(true, true, OrderNo, "", ACTION_CUSTOM_REFUSE, refuseType);
            }

        } else if (requestCode == REFUSE_RETURN_CODE) {
            //拒绝退货
            if (resultCode == RESULT_OK) {
                JCLoger.debug("onActivityResult refuse order");
                String refuseType = data.getExtras().getString("refuseType");
                String CancelId = data.getExtras().getString("OrderNo");
                setOrderStatus(true, true, "", CancelId, ACTION_REFUSE_RETURN, refuseType);
            }

        } else if (requestCode == DETAILS_CODE) {
            //详情页面
            if (resultCode == RESULT_OK) {
                JCLoger.debug("onActivityResult refresh all");
                refreshAll(false);
            }
        }
    }

    /**
     * 所有tab 刷新
     */
    public void refreshAll(boolean needRefresh) {
        //清空所有的数据
        listData_wait.clear();
        listData_deliver.clear();
        listData_deliverred.clear();
        listData_return.clear();
        //页数全部变为1
        pageIndexMap.put(TYPE_WAIT, 1);
        pageIndexMap.put(TYPE_DELIVER, 1);
        pageIndexMap.put(TYPE_DELIVERRED, 1);
        pageIndexMap.put(TYPE_RETURNING, 1);
        //刷新当前tab 的数据
        if (needRefresh) {
            getOrderList(false, false, tab);
        }
    }

    /**
     * 刷新待接单tab
     */
    public void refreshWaitList() {
        //清空待接单数据
        listData_wait.clear();
        //待接单页面页数设为1
        pageIndexMap.put(TYPE_WAIT, 1);
        //如果是待接单tab 则马上刷新
        if (tab == TYPE_WAIT) {
            getOrderList(false, false, tab);
        }
    }
}
