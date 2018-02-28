package com.yatang.xc.xcr.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.PayTypeAdapter;
import com.yatang.xc.xcr.adapter.TicketDetialAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.DataAnalyze;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.DividerItemDecoration;
import org.jocerly.jcannotation.widget.listView.CustomerListView;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 小票详情
 * Created by Jocerly on 2017/5/16.
 */
@ContentView(R.layout.activity_ticket_detial)
public class TicketDetialActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.llTicket)
    private LinearLayout llTicket;
    @BindView(id = R.id.iamgePostingSign)
    private ImageView iamgePostingSign;

    @BindView(id = R.id.textTicketAccount)
    private TextView textTicketAccount;
    @BindView(id = R.id.customerListView)
    private CustomerListView customerListView;
    @BindView(id = R.id.recyclerViewPayType)
    private RecyclerView recyclerViewPayTpe;

    @BindView(id = R.id.textGoodAllValue)
    private TextView textGoodAllValue;
    @BindView(id = R.id.textReceivableValue)
    private TextView textReceivableValue;
    @BindView(id = R.id.textPaidUpValue)
    private TextView textPaidUpValue;
    @BindView(id = R.id.textChangeValue)
    private TextView textChangeValue;

    @BindView(id = R.id.textYatangAssessedValue)
    private TextView textYatangAssessedValue;
    @BindView(id = R.id.textBusinessAssessedValue)
    private TextView textBusinessAssessedValue;
    @BindView(id = R.id.textAllDiscountValue)
    private TextView textAllDiscountValue;
    @BindView(id = R.id.textAllReducteValue)
    private TextView textAllReducteValue;
    @BindView(id = R.id.textProfitLossValue)
    private TextView textProfitLossValue;

    @BindView(id = R.id.textTicketNo)
    private TextView textTicketNo;
    @BindView(id = R.id.textTime)
    private TextView textTime;
    @BindView(id = R.id.textCashegisterNo)
    private TextView textCashegisterNo;
    @BindView(id = R.id.textCashierStaffNo)
    private TextView textCashierStaffNo;
    @BindView(id = R.id.textStoreSerialNo)
    private TextView textStoreSerialNo;

    private TicketDetialAdapter adapter;
    private PayTypeAdapter payTypeAdapter;
    private ArrayList<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private ArrayList<ConcurrentHashMap<String, String>> listData2 = new ArrayList<>();
    private String ticketNo;
    private String TicketId;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("小票明细");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("小票明细");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("小票明细");
        btnRight.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ticketNo = bundle.getString("TicketNo");
            TicketId = bundle.getString("TicketId");
        }
    }

    @Override
    public void initData() {
        adapter = new TicketDetialAdapter(aty, listData);
        customerListView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        payTypeAdapter = new PayTypeAdapter(aty,listData2);
        LinearLayoutManager manager = new LinearLayoutManager(aty);
        recyclerViewPayTpe.setLayoutManager(manager);
        recyclerViewPayTpe.addItemDecoration(new DividerItemDecoration(aty,DividerItemDecoration.VERTICAL_LIST,
                (int)getResources().getDimension(R.dimen.pad1_px), colorGap));
        recyclerViewPayTpe.setAdapter(payTypeAdapter);

        getTicketDetial();

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

    /**
     * 获取小票信息
     */
    public void getTicketDetial() {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("TicketId", TicketId);
        httpRequestService.doRequestData(aty, "User/TicketDetial", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    listData.clear();
                    listData.addAll(resultParam.listData);
                    adapter.notifyDataSetChanged();

                    try {
                        JSONArray jsonArray = new JSONArray(resultParam.mapData.get("listdata2"));
                        for(int i=0; i<jsonArray.length(); i++){
                            ConcurrentHashMap<String, String> childmap = DataAnalyze.doAnalyzeJsonArray(jsonArray.getJSONObject(i));
                            listData2.add(childmap);
                        }
                        payTypeAdapter.notifyDataSetChanged();
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    showData(resultParam.mapData);
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    toast("获取小票数据异常");
                    finish();
                }
            }
        });
    }

    /**
     * 显示数据
     *
     * @param mapData
     */
    private void showData(ConcurrentHashMap<String, String> mapData) {
        textTicketAccount.setText("￥" + Common.formatTosepara( mapData.get("ReceivableValue"),3,2));

        llTicket.setBackgroundResource(getTicketTypeBg(mapData.get("TicketType")));
        iamgePostingSign.setImageResource(getPostingSign(mapData.get("PostingSign")));

        textGoodAllValue.setText("￥" + Common.formatTosepara(mapData.get("GoodAllValue"),3,2));
        textReceivableValue.setText("￥" + Common.formatTosepara(mapData.get("ReceivableValue"),3,2));
        textPaidUpValue.setText("￥" + Common.formatTosepara(mapData.get("PaidUpValue"),3,2));
        textChangeValue.setText("￥" + Common.formatTosepara(mapData.get("ChangeValue"),3,2));

        textYatangAssessedValue.setText("￥" + Common.formatTosepara(mapData.get("YatangAssessedValue"),3,2));
        textBusinessAssessedValue.setText("￥" + Common.formatTosepara(mapData.get("BusinessAssessedValue"),3,2));
        textAllDiscountValue.setText("￥" + Common.formatTosepara(mapData.get("AllDiscountValue"),3,2));
        textAllReducteValue.setText("￥" + Common.formatTosepara(mapData.get("AllReducteValue"),3,2));
        textProfitLossValue.setText("￥" + Common.formatTosepara(mapData.get("ProfitLossValue"),3,2));

        textTicketNo.setText(ticketNo);
        textTime.setText(mapData.get("Time"));
        textCashegisterNo.setText(mapData.get("CashegisterNo"));
        textCashierStaffNo.setText(mapData.get("CashierStaffNo"));
        textStoreSerialNo.setText(MyApplication.instance.StoreSerialNoDefault);

    }

    /**
     * 单据类型
     *
     * @param ticketType 1：销售、3：退货、2：换货
     * @return
     */
    private int getTicketTypeBg(String ticketType) {
        switch (Integer.parseInt(ticketType)) {
            case 1:
                return R.drawable.ticket_sall_out_bg;
            case 3:
                return R.drawable.ticket_return_bg;
            case 2:
                return R.drawable.ticket_change_bg;
        }
        return 0;
    }

    /**
     * 过账标识
     *
     * @param postingSign 0 未过账， 1 过账失败，2 已过账
     * @return
     */
    private int getPostingSign(String postingSign) {
        switch (Integer.parseInt(postingSign)) {
            case 0:
                return R.drawable.posting_no;
            case 1:
                return R.drawable.posting_error;
            case 2:
                return R.drawable.posting_down;
        }
        return 0;
    }
}
