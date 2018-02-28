package com.yatang.xc.xcr.activity;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.adapter.SettlementManageDetialAdapter;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 结算管理明细Activity
 * Created by Jocerly on 2017/3/8.
 */
@ContentView(R.layout.activity_settlement_manage_detial)
public class SettlementManageDetialActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;
    @BindView(id = R.id.textSettlementMsg)
    private TextView textSettlementMsg;
    @BindView(id = R.id.textType)
    private TextView textType;
    @BindView(id = R.id.llErrorMsg)
    private LinearLayout llErrorMsg;
    @BindView(id = R.id.textErrorMsg)
    private TextView textErrorMsg;
    @BindView(id = R.id.isFinished, click = true)
    private TextView textIsFinished;
    @BindView(id = R.id.textSettlementTime)
    private TextView textSettlementTime;
    @BindView(id = R.id.llSettlementTime)
    private LinearLayout llSettlementTime;
    @BindView(id = R.id.textSettlementDetialAllValue)
    private TextView textSettlementDetialAllValue;
    @BindView(id = R.id.textSettlementRefuseDate)
    private TextView textSettlementRefuseDate;
    @BindView(id = R.id.textSettlementSucDate)
    private TextView textSettlementSucDate;
    @BindView(id = R.id.textSettlementRefuseValue)
    private TextView textSettlementRefuseValue;
    @BindView(id = R.id.textSettlementSucValue)
    private TextView textSettlementSucValue;
    @BindView(id = R.id.textTime)
    private TextView textTime;

    private SettlementManageDetialAdapter adapter;
    private List<ConcurrentHashMap<String, String>> listData = new ArrayList<>();
    private String id;
    /**
     * 0：未到账，2：结算完成，3：结算失败
     */
    private String statue;
    /**
     * 0：待确定、1：已确定
     */
    private String isFinished;

    /**
     * 1:订单结算、2：优惠券结算
     */
    private int type;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("明细");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("明细");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("明细");
        btnRight.setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getInt("type");
            HashMap<String, String> mapData = (HashMap<String, String>) bundle.getSerializable("mapData");
            if (mapData != null) {
                getSettlement(mapData);
            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(aty);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);


    }

    /**
     * 获取数据
     */
    private void getSettlementDetialList() {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", MyApplication.instance.StoreSerialNoDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("SettlementId", id);
        httpRequestService.doRequestData(aty, "User/SettlementDetialList", params, new HttpRequestService.IHttpRequestCallback() {

            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    textErrorMsg.setText(resultParam.mapData.get("SettlementErrorMsg"));
                    textSettlementMsg.setText(resultParam.mapData.get("SettlementTime"));
                    listData.clear();
                    listData.addAll(resultParam.listData);
                    adapter.notifyDataSetChanged();
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
     * 设置结算详细
     * @param mapData
     */
    private void getSettlement(HashMap<String, String> mapData){
        id = mapData.get("SettlementId");
        statue = mapData.get("SettlementStatue");
        double Allvalue = Double.parseDouble(mapData.get("SettlementSucValue")) + Double.parseDouble(mapData.get("SettlementRefuseValue"));
        textSettlementDetialAllValue.setText("￥" + Common.formatTosepara(String.valueOf(Allvalue), 3, 2));
        textErrorMsg.setText(mapData.get("SettlementErrorMsg"));
        textSettlementTime.setText(mapData.get("SettlementTime"));
        textSettlementRefuseDate.setText(mapData.get("Date"));
        textSettlementSucDate.setText(mapData.get("Date"));
        if ("0".equals(mapData.get("SettlementRefuseValue")) || StringUtils.isEmpty(mapData.get("SettlementRefuseValue"))){
            textSettlementRefuseValue.setText("￥" + Common.formatTosepara(mapData.get("SettlementRefuseValue"), 3, 2));
        }else {
            textSettlementRefuseValue.setText("￥-" + Common.formatTosepara(mapData.get("SettlementRefuseValue"), 3, 2));
        }
        textSettlementSucValue.setText("￥" + Common.formatTosepara(mapData.get("SettlementSucValue"), 3, 2));
        isFinished = mapData.get("IsFinished");
        switch (Integer.parseInt(statue)) {
            case 0:
                switch (type){
                    case 1:
                        switch (Integer.parseInt(isFinished)){
                            case 0:
                                textType.setText("未确认");
                                llErrorMsg.setVisibility(View.GONE);
                                llSettlementTime.setVisibility(View.GONE);
                                textIsFinished.setVisibility(View.VISIBLE);
                                break;
                            case 1:
                                textType.setText("未结算");
                                llErrorMsg.setVisibility(View.GONE);
                                llSettlementTime.setVisibility(View.VISIBLE);
                                textIsFinished.setVisibility(View.GONE);
                                textTime.setText("确认时间：");
                                break;
                        }
                        break;
                    case 2:
                        textType.setText("未结算");
                        llErrorMsg.setVisibility(View.GONE);
                        llSettlementTime.setVisibility(View.GONE);
                        textIsFinished.setVisibility(View.GONE);
                        break;
                }

                break;
            case 2:
                textType.setText("已结算");
                llErrorMsg.setVisibility(View.GONE);
                llSettlementTime.setVisibility(View.VISIBLE);
                textIsFinished.setVisibility(View.GONE);
                textTime.setText("结算时间：");
                break;
            case 3:
                textType.setText("结算失败");
                llErrorMsg.setVisibility(View.VISIBLE);
                llSettlementTime.setVisibility(View.VISIBLE);
                textIsFinished.setVisibility(View.GONE);
                textTime.setText("结算时间：");
                break;
        }
}
    /**
     * 确认结算
     */
    public void getUpDateSettlement() {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSeriaNo", MyApplication.instance.StoreSerialNameDefault);
        params.put("Token", MyApplication.instance.Token);
        params.put("SettlementId", id);
        httpRequestService.doRequestData(aty, "User/UpdateSettlement", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    setResult(RESULT_OK);
                    finish();
                } else if (Constants.M01.equals(resultParam.resultId)) {
                    toast(R.string.accout_out);
                    doEmpLoginOut();
                } else {
                    toast(resultParam.message);
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
            case R.id.isFinished:
                getUpDateSettlement();
                break;
        }
    }
}
