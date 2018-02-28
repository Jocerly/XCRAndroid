package com.yatang.xc.xcr.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.yatang.plugin.ytpay.YTPay;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.activity.PayResultActivity;

import org.apache.cordova.CallbackContext;
import org.jocerly.jcannotation.utils.JCLoger;


/**
 * Created by liaoqinsen on 2017/8/11 0011.
 */

public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private CallbackContext callbackContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.yatang.xc.supchain.R.layout.activity_start);
        TextView textView = (TextView) findViewById(com.yatang.xc.supchain.R.id.start_text);
        textView.setText("正在发起微信支付...");
        textView.setVisibility(View.VISIBLE);
        MyApplication.api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        MyApplication.api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        JCLoger.debug("onReq------" + baseReq.toString());
    }

    @Override
    public void onResp(BaseResp baseResp) {
        JCLoger.debug("onResp:" + baseResp.errCode + "----" + baseResp.errStr + "-----" + baseResp.transaction);
        if (MyApplication.instance.payType == 1) {
            Bundle bundle = new Bundle();
            bundle.putInt("type", 1);
            bundle.putInt("errCode", baseResp.errCode);
            Intent intent = new Intent(this, PayResultActivity.class);
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else {
            YTPay.setWXPayResult(baseResp);
        }
        finish();
    }
}
