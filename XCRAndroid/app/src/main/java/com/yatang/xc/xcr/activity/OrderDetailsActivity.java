package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.OrderDetailsAdapter;
import com.yatang.xc.xcr.adapter.OrderDiscountAdapter;
import com.yatang.xc.xcr.adapter.OrderDiscountLAdapter;
import com.yatang.xc.xcr.adapter.OrderListAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.DataAnalyze;
import com.yatang.xc.xcr.uitls.ResultParam;
import com.yatang.xc.xcr.views.PressTextView;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.DividerItemDecoration;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;
import org.jocerly.jcannotation.widget.listView.CustomerListView;
import org.jocerly.jcannotation.widget.recyclevew.FullyLinearLayoutManager;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订单详情
 * Created by DengJiang on 2017/6/9.
 */
@ContentView(R.layout.activity_order_details)
public class OrderDetailsActivity extends BaseActivity {
    public static final String TAG = "OrderDetailsActivity";
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.text_GoodCount)
    private TextView text_GoodCount;
    @BindView(id = R.id.textGoodCount)
    private TextView textGoodCount;
    @BindView(id = R.id.text_GoodPrice)
    private TextView text_GoodPrice;
    @BindView(id = R.id.btn_returntip, click = true)
    private PressTextView Btn_returntip;
    @BindView(id = R.id.textGoodPrice)
    private TextView textGoodPrice;
    @BindView(id = R.id.text_DeliveryFee)
    private TextView text_DeliveryFee;
    @BindView(id = R.id.deliveryFee)
    private TextView deliveryFee;
    @BindView(id = R.id.relayReceivePrice)
    private RelativeLayout relayReceivePrice;
    @BindView(id = R.id.relayDeliveryFee)
    private RelativeLayout relayDeliveryFee;
    @BindView(id = R.id.textReceivePrice)
    private TextView textReceivePrice;
    @BindView(id = R.id.text_ReceivePrice)
    private TextView text_ReceivePrice;
    @BindView(id = R.id.relayReturnOrderNumber)
    private RelativeLayout relayReturnOrderNumber;
    @BindView(id = R.id.textReturnOrderNumber)
    private TextView textReturnOrderNumber;
    @BindView(id = R.id.relayRemarks)
    private RelativeLayout relayRemarks;
    @BindView(id = R.id.textRemarks)
    private TextView textRemarks;
    @BindView(id = R.id.relayPayType)
    private RelativeLayout relayPayType;
    @BindView(id = R.id.textPayType)
    private TextView textPayType;
    @BindView(id = R.id.textName)
    private TextView textName;
    @BindView(id = R.id.textPhone)
    private TextView textPhone;
    @BindView(id = R.id.textAddress)
    private TextView textAddress;
    @BindView(id = R.id.text_OrderNO)
    private TextView text_OrderNO;
    @BindView(id = R.id.textOrderNO)
    private TextView textOrderNO;
    @BindView(id = R.id.text_Time)
    private TextView text_Time;
    @BindView(id = R.id.textTime)
    private TextView textTime;
    @BindView(id = R.id.text_sortNo)
    private TextView text_sortNo;
    @BindView(id = R.id.text_status)
    private TextView text_status;
    @BindView(id = R.id.textDeliveryType)
    private TextView textDeliveryType;
    @BindView(id = R.id.btn3)
    private TextView btn3;
    @BindView(id = R.id.btn2)
    private TextView btn2;
    @BindView(id = R.id.btn1)
    private TextView btn1;
    @BindView(id = R.id.btnLookUpReturnOrder, click = true)
    private TextView btnLookUpReturnOrder;
    @BindView(id = R.id.llButton)
    private LinearLayout llButton;
    @BindView(id = R.id.lineName)
    private LinearLayout lineName;
    @BindView(id = R.id.lineAddress)
    private LinearLayout lineAddress;

    @BindView(id = R.id.relayRefuse)
    private RelativeLayout relayRefuse;

    @BindView(id = R.id.textRefuseReason)
    private TextView textRefuseReason;
    @BindView(id = R.id.textReason)
    private TextView textReason;
    @BindView(id = R.id.listDiscount)
    private CustomerListView listDiscount;
    private OrderDiscountLAdapter discountAdapter;
    private List<ConcurrentHashMap<String, String>> listData_Discount = new ArrayList<>();

    private String OrderNo;
    private String CancelId;

    private OrderDetailsAdapter adapter;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();

    private boolean needRefresh = false;
    private NomalDialog returnedDialog;
    private NomalDialog returnTipDialog;
    private String returnTip;

    @Override
    protected void onResume() {
        super.onResume();
        if (!StringUtils.isEmpty(CancelId)) {
            MobclickAgent.onPageStart("退货订单详情");
        } else {
            MobclickAgent.onPageStart("订单详情");
        }
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!StringUtils.isEmpty(CancelId)) {
            MobclickAgent.onPageEnd("退货订单详情");
        } else {
            MobclickAgent.onPageEnd("订单详情");
        }
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("订单详情");
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            OrderNo = bundle.getString("OrderNo", "");
            CancelId = bundle.getString("CancelId", "");
        }
        adapter = new OrderDetailsAdapter(aty, listData);
        FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(aty, DividerItemDecoration.VERTICAL_LIST,
                (int) getResources().getDimension(R.dimen.pad1_px), colorGap));
        recyclerView.setAdapter(adapter);

        listData_Discount = new ArrayList<>();
        discountAdapter = new OrderDiscountLAdapter(aty, listData_Discount);
//        FullyLinearLayoutManager layoutManager1 = new FullyLinearLayoutManager(aty);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        listDiscount.setLayoutManager(layoutManager1);
//        listDiscount.addItemDecoration(new DividerItemDecoration(aty, DividerItemDecoration.VERTICAL_LIST,
//                0, colorGap));
        listDiscount.setAdapter(discountAdapter);
        needRefresh = false;
        initDetails();
    }

    @Override
    public void finish() {
        if (needRefresh) {
            setResult(RESULT_OK);
            needRefresh = false;
        }
        super.finish();
    }

    public void getReturnDetails(final boolean isShowDialog, final boolean isShowToast) {
        {
            params.clear();
            params.put("UserId", MyApplication.instance.UserId);
            params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
            params.put("Token", MyApplication.instance.Token);
            params.put("CancelId", CancelId);
            httpRequestService.doRequestData(aty, "User/OrderReturnDetial", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

                @Override
                public void onRequestCallBack(ResultParam resultParam) {
                    if (Constants.M00.equals(resultParam.resultId)) {
                        //更新 商品列表数据
                        listData.clear();
                        listData.addAll(resultParam.listData);
                        JCLoger.debug(listData.size() + "");
                        adapter.notifyDataSetChanged();

                        //更新退货原因
                        ConcurrentHashMap<String, String> mapData = resultParam.mapData;
                        if (StringUtils.isEmpty(mapData.get("ReturnReason"))) {
                            relayRefuse.setVisibility(View.GONE);
                        } else {
                            relayRefuse.setVisibility(View.VISIBLE);
                            textReason.setText(mapData.get("ReturnReason"));
                        }
                        setReturnDetails(mapData);
                        updateButton(mapData);
                    } else if (Constants.M01.equals(resultParam.resultId)) {
                        toast(R.string.accout_out);
                        doEmpLoginOut();
                    } else {
                        toast(resultParam.message);
                    }
                }
            });
        }
    }

    /**
     * 获取订单详情
     */
    public void getOrderDetails(final boolean isShowDialog, final boolean isShowToast) {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("OrderNo", OrderNo);
        params.put("CancelId", CancelId);
        httpRequestService.doRequestData(aty, "User/OrderDetail", isShowDialog, params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    //更新 商品列表数据
                    listData.clear();
                    listData.addAll(resultParam.listData);
                    JCLoger.debug(listData.size() + "");
                    adapter.notifyDataSetChanged();

                    //更新取消原因UI
                    ConcurrentHashMap<String, String> mapData = resultParam.mapData;
                    if (StringUtils.isEmpty(mapData.get("CancleOrderMsg"))) {
                        relayRefuse.setVisibility(View.GONE);
                    } else {
                        relayRefuse.setVisibility(View.VISIBLE);
                        textReason.setText(mapData.get("CancleOrderMsg"));
                    }
                    setOrderDetails(mapData);
                    updateButton(mapData);
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    /**
     * 更新退货单详情的详情信息
     */
    private void setReturnDetails(ConcurrentHashMap<String, String> mapData) {
        String tip = mapData.get("ReturnToast");
        returnTip = StringUtils.isEmpty(tip) ? "退货金额等于退货商品总金额减去优惠金额（优惠金额分摊到每一件商品）" : tip;
        text_sortNo.setText("#" + mapData.get("SortNo"));
        textGoodCount.setText(mapData.get("ReturnNum"));
        textGoodPrice.setText("￥" + Common.formatFloat(mapData.get("GoodAllValue")));
        textReceivePrice.setText("￥" + Common.formatFloat(mapData.get("ReturnValue")));
        setDiscountData(mapData.get("DiscountList"));
        textName.setText(mapData.get("AccountName"));
        textPhone.setText(mapData.get("Phone"));
        textAddress.setText(mapData.get("Address"));
        textOrderNO.setText(mapData.get("CancelId"));
        textReturnOrderNumber.setText(mapData.get("OrderNo"));
        if (OrderListAdapter.DELIVER_TYPE_CUSTOM.equals(mapData.get("DeliveryCode"))) {
            lineName.setVisibility(View.GONE);
            lineAddress.setVisibility(View.GONE);
        } else {
            lineName.setVisibility(View.VISIBLE);
            lineAddress.setVisibility(View.VISIBLE);
        }
        String time = Common.stampToDate(mapData.get("OrderReturnTime"), "MM.dd HH:mm");
        String timedetails = Common.stampToDate(mapData.get("OrderReturnTime"), "yyyy-MM-dd HH:mm:ss");
        textTime.setText(timedetails);
        textDeliveryType.setText("申请时间: " + time);

    }

    /**
     * 更新订单的详情信息
     */
    private void setOrderDetails(ConcurrentHashMap<String, String> mapData) {
        text_sortNo.setText("#" + mapData.get("SortNo"));
        JCLoger.debug("setOrderDetails==SortNo=" + mapData.get("SortNo"));
        textGoodCount.setText(mapData.get("TotalNum"));
        textGoodPrice.setText("￥" + Common.formatFloat(mapData.get("GoodAllValue")));
        deliveryFee.setText("￥" + Common.formatFloat(mapData.get("DeliveryFee")));
//        discount.setText("￥" + Common.formatFloat(mapData.get("Discount")));
        setDiscountData(mapData.get("DiscountList"));
        textReceivePrice.setText("￥" + Common.formatFloat(mapData.get("PaidUpValue")));
        setPayType(textPayType, mapData.get("PayType"));
        textName.setText(mapData.get("AccountName"));
        textPhone.setText(mapData.get("Phone"));
        textAddress.setText(mapData.get("Address"));
        textOrderNO.setText(mapData.get("OrderNo"));
        if (StringUtils.isEmpty(mapData.get("Remarks"))) {
            relayRemarks.setVisibility(View.GONE);
        } else {
            textRemarks.setText(mapData.get("Remarks"));
            relayRemarks.setVisibility(View.VISIBLE);
        }
        setDeliverType(lineName, lineAddress, textDeliveryType, mapData.get("DeliveryCode"));
        String time = Common.stampToDate(mapData.get("CreateOrderTime"), "yyyy-MM-dd HH:mm:ss");
        textTime.setText(time);

    }

    /**
     * 更新下部操作按钮
     */
    private void updateButton(ConcurrentHashMap<String, String> mapData) {
        String orderStatus = mapData.get("OrderStatue");
        String deliveryType = mapData.get("DeliveryCode");
        JCLoger.debug("getOrderDetails orderStatus=" + orderStatus + " deliveryType= " + deliveryType);
        switch (orderStatus) {
            case OrderListAdapter.STATUS_TOACCEPT:
                //待接单
                btn1.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);
                btn3.setVisibility(View.VISIBLE);
                llButton.setVisibility(View.VISIBLE);
                btn1.setText("拒绝");
                btn2.setText("联系客户");
                btn3.setText("接单");
                break;
            case OrderListAdapter.STATUS_TODELIVER:
                //待发货
                btn1.setVisibility(View.GONE);
                btn2.setVisibility(View.VISIBLE);
                btn3.setVisibility(View.VISIBLE);
                llButton.setVisibility(View.VISIBLE);
                btn2.setText("联系客户");
                btn3.setText("确认发货");
                if (OrderListAdapter.DELIVER_TYPE_FENGNIAO.equals(deliveryType)) {
                    //蜂鸟配送
                    btn3.setVisibility(View.GONE);
                } else if (OrderListAdapter.DELIVER_TYPE_CUSTOM.equals(deliveryType)) {
                    //用户自提
                    btn3.setText("确认完成");
//                    btn2.setVisibility(View.GONE);
                }
                break;
            case OrderListAdapter.STATUS_DELIVERRED:
                //已发货
                btn1.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);
                btn3.setVisibility(View.VISIBLE);
                llButton.setVisibility(View.VISIBLE);
                btn3.setText("配送完成");
                btn2.setText("联系客户");
                btn1.setText("客户拒收");
                if (OrderListAdapter.DELIVER_TYPE_FENGNIAO.equals(deliveryType)) {
//                    btn1.setVisibility(View.GONE);
                    btn3.setText("配送信息");
                }
                break;
            case OrderListAdapter.STATUS_RETURNING_GOODS:
                btn1.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);
                btn3.setVisibility(View.VISIBLE);
                llButton.setVisibility(View.VISIBLE);
                btn3.setText("确认退货");
                btn2.setText("联系客户");
                btn1.setText("拒绝");
                break;
            default:
                btn3.setVisibility(View.GONE);
                btn2.setVisibility(View.GONE);
                btn1.setVisibility(View.GONE);
                llButton.setVisibility(View.GONE);
                break;
        }
        text_status.setText(OrderListAdapter.MAPSTATUS.get(orderStatus));
        btn3.setOnClickListener(new MClickListener(orderStatus, mapData, deliveryType));
        btn2.setOnClickListener(new MClickListener(orderStatus, mapData, deliveryType));
        btn1.setOnClickListener(new MClickListener(orderStatus, mapData, deliveryType));
    }

    /**
     * 设置页面UI
     * 获取数据
     */
    private void initDetails() {
        if (!StringUtils.isEmpty(CancelId)) {
            //退货详情，
            initReturnUI(true);
            getReturnDetails(true, true);
        } else {
            initReturnUI(false);
            getOrderDetails(true, true);
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
                    needRefresh = true;
                    initDetails();
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    /**
     * 设置支付方式
     */
    private void setPayType(TextView textView, String type) {
        switch (type) {
            case "10":
                textView.setText("支付宝");
                break;
            case "11":
                textView.setText("微信");
                break;
            default:
                textView.setText("支付宝");
                break;
        }
    }

    private void setDiscountData(String json) {
        List<ConcurrentHashMap<String, String>> list_data = getListData(json);
        if (list_data != null && list_data.size() > 0) {
            listData_Discount.clear();
            listData_Discount.addAll(list_data);
            listDiscount.setVisibility(View.VISIBLE);
            discountAdapter.notifyDataSetChanged();
        } else {
            listDiscount.setVisibility(View.GONE);
        }
    }

    /**
     * 解析列表数据
     */
    private List<ConcurrentHashMap<String, String>> getListData(String json) {
        List<ConcurrentHashMap<String, String>> listdata = new ArrayList<>();
        if (StringUtils.isEmpty(json)) {
            return listdata;
        }
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                ConcurrentHashMap<String, String> childmap = DataAnalyze.doAnalyzeJsonArray(jsonArray.getJSONObject(i));
                listdata.add(childmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return listdata;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnLookUpReturnOrder:
                String orderNO = textReturnOrderNumber.getText().toString().trim();
                if (!StringUtils.isEmpty(orderNO)) {
                    //查看原订单
                    Bundle bundle = new Bundle();
                    bundle.putString("OrderNo", orderNO);
                    bundle.putString("CancelId", "");
                    skipActivity(aty, OrderDetailsActivity.class, bundle);
                }
                break;
            case R.id.btn_returntip:
                if (returnTipDialog == null) {
                    returnTipDialog = new NomalDialog(aty);
                }
                returnTipDialog.showClose(returnTip, "我知道了");
                break;
        }
    }

    public class MClickListener implements View.OnClickListener {
        private String OrderType;
        private String deliveryType;
        ConcurrentHashMap<String, String> mapData;

        public MClickListener(String OrderType, ConcurrentHashMap<String, String> mapData, String deliveryType) {
            this.OrderType = OrderType;
            this.mapData = mapData;
            this.deliveryType = deliveryType;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn3:

                    if (OrderListAdapter.STATUS_TOACCEPT.equals(OrderType)) {
                        //选择了接单按钮
                        JCLoger.debug("Accept order click");
                        setOrderStatus(true, true, OrderNo, "", OrderManagementActivity.ACTION_ACCEPT, "");
                    } else if (OrderListAdapter.STATUS_TODELIVER.equals(OrderType)) {
                        if (OrderListAdapter.DELIVER_TYPE_CUSTOM.equals(deliveryType)) {
                            JCLoger.debug("Custom finish");
                            //客户自提 确认完成
                            setOrderStatus(true, true, OrderNo, "", OrderManagementActivity.ACTION_FINISH, "");
                        } else {
                            //选择了发货
                            JCLoger.debug("Delivered confirm click");
                            setOrderStatus(true, true, OrderNo, "", OrderManagementActivity.ACTION_DELIVERRED, "");
                        }
                    } else if (OrderListAdapter.STATUS_DELIVERRED.equals(OrderType)) {
                        if (OrderListAdapter.DELIVER_TYPE_FENGNIAO.equals(deliveryType)) {
                            //配送信息
                            MobclickAgent.onEvent(aty, "Order_DistributeMsg");
                            Bundle b = new Bundle();
                            b.putString(DeliverInfoActivity.NAME, mapData.get("DistributorName"));
                            b.putString(DeliverInfoActivity.PHONE, mapData.get("DistributorPhone"));
                            b.putString(DeliverInfoActivity.SENDTIME, mapData.get("DeliveryTime"));
                            b.putString(DeliverInfoActivity.REACHTIME, mapData.get("SucTime"));
                            b.putString(DeliverInfoActivity.ADDRESS, mapData.get("Address"));
                            skipActivity(aty, DeliverInfoActivity.class, b);
                        } else {
                            //选择了配送完成
                            JCLoger.debug("Finished click");
                            setOrderStatus(true, true, OrderNo, "", OrderManagementActivity.ACTION_FINISH, "");
                        }

                    } else if (OrderListAdapter.STATUS_RETURNING_GOODS.equals(OrderType)) {
                        //选择了确认退货
                        JCLoger.debug("Returned confirm click");
                        if (returnedDialog == null) {
                            returnedDialog = new NomalDialog(aty);
                            returnedDialog.setOnNoamlLickListener(new NomalDialog.OnNoamlLickListener() {
                                @Override
                                public void onOkClick() {
                                    setOrderStatus(true, true, "", CancelId, OrderManagementActivity.ACTION_CONFIRM_RETURN, "");
                                }
                            });
                        }
                        returnedDialog.show("请确认商品已退回店铺,同\n时退款现金已交到客户手中", "取消", "确认");
                    }
                    break;
                case R.id.btn2:
                    JCLoger.debug("Connect custom click");
                    //联系用户
                    MobclickAgent.onEvent(aty, "Order_Call");
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mapData.get("Phone")));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                case R.id.btn1:
                    if (OrderListAdapter.STATUS_DELIVERRED.equals(OrderType)) {
                        //客户拒收
                        JCLoger.debug("Custom refused click");
                        Bundle b = new Bundle();
                        b.putString("Type", RefuseOrderActivity.REFUSE_TYPE_CUSTOM);
                        skipActivityForResult(aty, RefuseOrderActivity.class, b, OrderManagementActivity.CUSTOM_REFUSE_CODE);
                    } else if (OrderListAdapter.STATUS_TOACCEPT.equals(OrderType)) {
                        //商家拒接
                        JCLoger.debug("Reject Order click");
                        Bundle b = new Bundle();
                        b.putString("Type", RefuseOrderActivity.REFUSE_TYPE_BUSINESS);
                        skipActivityForResult(aty, RefuseOrderActivity.class, b, OrderManagementActivity.REFUSE_CODE);
                    } else if (OrderListAdapter.STATUS_RETURNING_GOODS.equals(OrderType)) {
                        //拒绝退货
                        JCLoger.debug("Reject Return click");
                        Bundle b = new Bundle();
                        b.putString("Type", RefuseOrderActivity.REFUSE_TYPE_RETURN_GOODS);
                        skipActivityForResult(aty, RefuseOrderActivity.class, b, OrderManagementActivity.REFUSE_RETURN_CODE);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OrderManagementActivity.REFUSE_CODE) {
            if (resultCode == RESULT_OK) {
                JCLoger.debug("onActivityResult refuse REFUSE_CODE");
                String refuseType = data.getExtras().getString("refuseType");
                setOrderStatus(true, true, OrderNo, "", OrderManagementActivity.ACTION_REFUSE, refuseType);
            }
        } else if (requestCode == OrderManagementActivity.CUSTOM_REFUSE_CODE) {
            if (resultCode == RESULT_OK) {
                JCLoger.debug("onActivityResult refuse CUSTOM_REFUSE_CODE");
                String refuseType = data.getExtras().getString("refuseType");
                setOrderStatus(true, true, OrderNo, "", OrderManagementActivity.ACTION_CUSTOM_REFUSE, refuseType);
            }

        } else if (requestCode == OrderManagementActivity.REFUSE_RETURN_CODE) {
            if (resultCode == RESULT_OK) {
                JCLoger.debug("onActivityResult refuse REFUSE_RETURN_CODE");
                String refuseType = data.getExtras().getString("refuseType");
                setOrderStatus(true, true, "", CancelId, OrderManagementActivity.ACTION_REFUSE_RETURN, refuseType);
            }
        }
    }

    public void initReturnUI(boolean isReturnOrder) {
        if (isReturnOrder) {
            textRefuseReason.setText("退货原因:");
            textDeliveryType.setTextColor(getResources().getColor(R.color.black));
            text_GoodCount.setText("退货件数");
            Btn_returntip.setVisibility(View.VISIBLE);
            text_OrderNO.setText("退货单号");
            text_Time.setText("申请时间");
            text_ReceivePrice.setText("退货金额");
            relayDeliveryFee.setVisibility(View.GONE);
            relayPayType.setVisibility(View.GONE);
            relayRemarks.setVisibility(View.GONE);
            relayReturnOrderNumber.setVisibility(View.VISIBLE);
            btnLookUpReturnOrder.setVisibility(View.VISIBLE);
        } else {
            textRefuseReason.setText("取消原因:");
            textDeliveryType.setTextColor(getResources().getColor(R.color.red));
            text_GoodCount.setText("总件数");
            Btn_returntip.setVisibility(View.GONE);
            text_OrderNO.setText("订单号");
            text_Time.setText("下单时间");
            text_DeliveryFee.setText("配送费");
            text_ReceivePrice.setText("实收金额");
            relayDeliveryFee.setVisibility(View.VISIBLE);
            relayReceivePrice.setVisibility(View.VISIBLE);
            relayPayType.setVisibility(View.VISIBLE);
            relayRemarks.setVisibility(View.VISIBLE);
            relayReturnOrderNumber.setVisibility(View.GONE);
            btnLookUpReturnOrder.setVisibility(View.GONE);
        }
    }

    private void setDeliverType(LinearLayout lineName, LinearLayout lineAddress, TextView textView, String deliverType) {
        if (StringUtils.isEmpty(deliverType)) {
            deliverType = OrderListAdapter.DELIVER_TYPE_CUSTOM;
        }
        switch (deliverType) {
            case OrderListAdapter.DELIVER_TYPE_BUSINESS:
                textView.setText("送货上门(商家配送)");
                lineName.setVisibility(View.VISIBLE);
                lineAddress.setVisibility(View.VISIBLE);
                break;
            case OrderListAdapter.DELIVER_TYPE_CUSTOM:
                textView.setText("客户自提");
                lineName.setVisibility(View.GONE);
                lineAddress.setVisibility(View.GONE);
                break;
            case OrderListAdapter.DELIVER_TYPE_FENGNIAO:
                textView.setText("送货上门(蜂鸟配送)");
                lineName.setVisibility(View.VISIBLE);
                lineAddress.setVisibility(View.VISIBLE);
                break;
        }
    }
}
