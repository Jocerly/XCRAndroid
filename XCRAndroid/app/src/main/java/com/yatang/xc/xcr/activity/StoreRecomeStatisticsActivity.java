package com.yatang.xc.xcr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.config.Constants;
import com.yatang.xc.xcr.service.HttpRequestService;
import com.yatang.xc.xcr.uitls.ResultParam;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;

/**
 * 推荐统计
 * Created by Jocerly on 2017/10/19.
 */
@ContentView(R.layout.activity_store_recome_statistics)
public class StoreRecomeStatisticsActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.textPublicAttentionToday)
    private TextView textPublicAttentionToday;
    @BindView(id = R.id.textPublicAttentionYesterday)
    private TextView textPublicAttentionYesterday;
    @BindView(id = R.id.textPublicAttentionAll)
    private TextView textPublicAttentionAll;

    @BindView(id = R.id.textRegisterToday)
    private TextView textRegisterToday;
    @BindView(id = R.id.textRegisterYesterday)
    private TextView textRegisterYesterday;
    @BindView(id = R.id.textRegisterAll)
    private TextView textRegisterAll;

    @BindView(id = R.id.textOrderToday)
    private TextView textOrderToday;
    @BindView(id = R.id.textOrderYesterday)
    private TextView textOrderYesterday;
    @BindView(id = R.id.textOrderAll)
    private TextView textOrderAll;

    private String storeSerialNo;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("推荐统计");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("推荐统计");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        textTitle.setText("推荐统计");
        btnRight.setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            storeSerialNo = bundle.getString("StoreSerialNo");
            getRecomeStatistics();
        }
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
     * 获取信息
     */
    private void getRecomeStatistics() {
        params.clear();
        params.put("UserId", MyApplication.instance.UserId);
        params.put("StoreSerialNo", storeSerialNo);
        params.put("Token", MyApplication.instance.Token);
        httpRequestService.doRequestData(aty, "User/RecomeStatistics", params, new HttpRequestService.IHttpRequestCallback() {
            @Override
            public void onRequestCallBack(ResultParam resultParam) {
                if (Constants.M00.equals(resultParam.resultId)) {
                    textPublicAttentionToday.setText(resultParam.mapData.get("PublicAttentionToday"));
                    textPublicAttentionYesterday.setText(resultParam.mapData.get("PublicAttentionYesterday"));
                    textPublicAttentionAll.setText(resultParam.mapData.get("PublicAttentionAll"));

                    textRegisterToday.setText(resultParam.mapData.get("RegisterToday"));
                    textRegisterYesterday.setText(resultParam.mapData.get("RegisterYesterday"));
                    textRegisterAll.setText(resultParam.mapData.get("RegisterAll"));

                    textOrderToday.setText(resultParam.mapData.get("OrderToday"));
                    textOrderYesterday.setText(resultParam.mapData.get("OrderYesterday"));
                    textOrderAll.setText(resultParam.mapData.get("OrderAll"));
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
