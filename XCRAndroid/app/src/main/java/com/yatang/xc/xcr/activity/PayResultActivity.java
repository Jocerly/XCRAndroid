package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.switfpass.pay.MainApplication;
import com.switfpass.pay.activity.PayPlugin;
import com.switfpass.pay.bean.RequestMsg;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.umeng.analytics.MobclickAgent;
import com.yatang.plugin.ytpay.YTPay;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.dialog.NomalDialog;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 支付结果页面
 * Created by Jocerly on 2017/11/8.
 */
@ContentView(value = R.layout.activity_pay_result)
public class PayResultActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.textResult)
    private TextView textResult;
    @BindView(id = R.id.textTime)
    private TextView textTime;
    @BindView(id = R.id.textResult2)
    private TextView textResult2;
    @BindView(id = R.id.textMsg)
    private TextView textMsg;

    @BindView(id = R.id.llErrorToPay)
    private LinearLayout llErrorToPay;
    @BindView(id = R.id.textPayBond, click = true)
    private TextView textPayBond;

    /**
     * 1:支付，2:签合同
     */
    private int type = 1;
    private int errCode;
    private NomalDialog nomalDialog;
    private Timer timer;
    private int countDown = 5;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("支付结果");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("支付结果");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        btnRight.setVisibility(View.GONE);

        nomalDialog = new NomalDialog(aty);
        nomalDialog.setOnNoamlLickListener(onNoamlLickListener);
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getInt("type");
            errCode = bundle.getInt("errCode");
            textTitle.setText(type == 1 ? "支付结果" : "签订完成");
            btnLeft.setVisibility(View.VISIBLE);
            if (errCode == BaseResp.ErrCode.ERR_OK) {//成功
                btnLeft.setVisibility(View.GONE);
                llErrorToPay.setVisibility(View.GONE);
                textTime.setVisibility(View.VISIBLE);
                textMsg.setVisibility(View.VISIBLE);
                textResult.setText(type == 1 ? "支付成功" : "合同签订完成");
                textResult.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.pay_suc, 0, 0);
                textResult2.setText("后自动跳转...");

                timer = new Timer();
                timer.schedule(new MyTimerTask(), 1000);
                if (type == 1) {
                    doUpdatePayToOrder(0);
                }
            } else if (errCode == BaseResp.ErrCode.ERR_COMM) {//失败
                llErrorToPay.setVisibility(View.VISIBLE);
                textTime.setVisibility(View.GONE);
                textMsg.setVisibility(View.GONE);
                textResult.setText("支付失败");
                textResult.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.pay_error, 0, 0);
                textResult2.setText("请重新支付");
                doUpdatePayToOrder(-1);
            } else {
                toast("用户取消了支付");
                finish();
            }
        }
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            countDown--;
            if (countDown == 0) {
                timer.cancel();
                mHandler.sendEmptyMessage(0);
            } else {
                mHandler.sendEmptyMessage(1);
            }
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (StringUtils.isEmpty(MyApplication.instance.token_id)) {
                        showActivity(aty, LoginActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();
                    } else {
                        toast("支付信息反馈中...");
                        countDown = 5;
                        timer = new Timer();
                        timer.schedule(new MyTimerTask(), 1000);
                    }
                    break;
                case 1:
                    timer.schedule(new MyTimerTask(), 1000);
                    textTime.setText(countDown + "s");
                    break;
            }
        }
    };

    NomalDialog.OnNoamlLickListener onNoamlLickListener = new NomalDialog.OnNoamlLickListener() {
        @Override
        public void onOkClick() {
            showActivity(aty, LoginActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (errCode == BaseResp.ErrCode.ERR_OK) {
                } else {
                    nomalDialog.show("保证金未支付，确认关闭吗？");
                }
                break;
        }
        return true;
    }

    /**
     * 显示超时弹框
     */
    public void showTinmeOutDialog() {
        nomalDialog.showClose("我知道了", getResources().getString(R.string.bond_time_out));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLeft:
                if (errCode == BaseResp.ErrCode.ERR_OK) {
                } else {
                    nomalDialog.show("保证金未支付，确认关闭吗？");
                }
                break;
            case R.id.textPayBond:
                doPayToOrder();
                break;
        }
    }

    /**
     * 威富通支付预下单
     */
    private void doPayToOrder() {
        params.clear();
        params.put("MarketNo", MyApplication.instance.marketNo);
        params.put("Phone", MyApplication.instance.phone);
        params.put("BondValue", MyApplication.instance.bondValue);
        params.put("IPAddress", Common.getIPAddress(aty));
        httpRequestService.doRequestData(aty, "System/PayToOrder", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    //调用微信支付
                    MyApplication.instance.token_id = resultParam.mapData.get("token_id");
                    RequestMsg msg = new RequestMsg();
                    msg.setTokenId(MyApplication.instance.token_id);
                    msg.setTradeType(MainApplication.WX_APP_TYPE);
                    msg.setAppId(YTPay.APP_ID);//wx9c1bcd9ed6a316c4
                    PayPlugin.unifiedAppPay(aty, msg);
                } else if (Constants.M13.equals(resultParam.resultId)) {//加盟超时
                    showTinmeOutDialog();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    /**
     * 支付信息反馈
     */
    private void doUpdatePayToOrder(int statue) {
        params.clear();
        params.put("MarketNo", MyApplication.instance.marketNo);
        params.put("token_id", MyApplication.instance.token_id);
        params.put("Statue", statue);
        httpRequestService.doRequestData(aty, "System/UpdatePayToOrder", false, params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                } else {
                    longToast(resultParam.message);
                }
            }
        });
        MyApplication.instance.token_id = "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.instance.payType = 2;
    }
}
