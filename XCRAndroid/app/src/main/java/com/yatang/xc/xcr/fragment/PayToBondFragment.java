package com.yatang.xc.xcr.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.switfpass.pay.MainApplication;
import com.switfpass.pay.activity.PayPlugin;
import com.switfpass.pay.bean.RequestMsg;
import com.yatang.plugin.ytpay.YTPay;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.activity.BrowserActivity;
import com.yatang.xc.xcr.activity.ContractSettledActivity;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.Common;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 支付保证金
 * Created by Jocerly on 2017/11/2.
 */
public class PayToBondFragment extends BaseFragment {
    @BindView(id = R.id.textBondValue)
    private TextView textBondValue;
    @BindView(id = R.id.textPayBond, click = true)
    private TextView textPayBond;
    @BindView(id = R.id.textPayBondMsg)
    private TextView textPayBondMsg;

    private ConcurrentHashMap<String, String> mapData = new ConcurrentHashMap<>();

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_pay_to_bond, null);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        SpannableString str = new SpannableString(getResources().getString(R.string.pay_bond_msg));
        str.setSpan(new MyClickText(aty), str.length() - 6, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textPayBondMsg.setText(str);
        textPayBondMsg.setHighlightColor(Color.TRANSPARENT);
        textPayBondMsg.setMovementMethod(LinkMovementMethod.getInstance());//开始响应点击事件
        getBondPayInfo();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.textPayBond:
                MyApplication.instance.phone = ((ContractSettledActivity) getActivity()).getPhone();
                MyApplication.instance.marketNo = ((ContractSettledActivity) getActivity()).getMarketNo();
                MyApplication.instance.bondValue = mapData.get("BondValue");
                MyApplication.instance.payType = 1;
                if (StringUtils.isEmpty(MyApplication.instance.bondValue)) {
                    toast("获取支付信息出错");
                    return;
                }
                doPayToOrder();
                break;
        }
    }

    /**
     * 获取保证金支付信息
     */
    private void getBondPayInfo() {
        params.clear();
        params.put("MarketNo", ((ContractSettledActivity) getActivity()).getMarketNo());
        httpRequestService.doRequestData(aty, "System/GetBondPayInfo", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    mapData.clear();
                    mapData.putAll(resultParam.mapData);
                    textBondValue.setText("￥" + mapData.get("BondValue"));
                } else if (Constants.M13.equals(resultParam.resultId)) {//加盟超时
                    ((ContractSettledActivity) getActivity()).showTinmeOutDialog();
                } else {
                    toast(resultParam.message);
                }
            }
        });
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
                    ((ContractSettledActivity) getActivity()).showTinmeOutDialog();
                } else {
                    toast(resultParam.message);
                }
            }
        });
    }

    class MyClickText extends ClickableSpan {
        private Context context;

        public MyClickText(Context context) {
            this.context = context;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            //设置文本的颜色
            ds.setColor(getResources().getColor(R.color.red));
            //超链接形式的下划线，false 表示不显示下划线，true表示显示下划线
            ds.setUnderlineText(true);
        }

        @Override
        public void onClick(View widget) {
            Bundle bundle = new Bundle();
            bundle.putString("ClassUrl", mapData.get("ContractInfoUrl"));
            bundle.putString("ClassName", "合作协议");
            skipActivity(aty, BrowserActivity.class, bundle);
        }
    }
}
