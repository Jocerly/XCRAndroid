package com.yatang.xc.xcr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.R;

import org.jocerly.jcannotation.ui.BindView;
import org.jocerly.jcannotation.ui.ContentView;
import org.jocerly.jcannotation.utils.StringUtils;

import java.util.HashMap;

/**
 * 店铺详情
 * Created by Jocerly on 2017/10/19.
 */
@ContentView(R.layout.activity_store_detial)
public class StoreDetialActivity extends BaseActivity {
    @BindView(id = R.id.btnLeft, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btnRight, click = true)
    private TextView btnRight;
    @BindView(id = R.id.textTitle)
    private TextView textTitle;

    @BindView(id = R.id.textStoreName)
    private TextView textStoreName;
    @BindView(id = R.id.textStoreAbbreName)
    private TextView textStoreAbbreName;
    @BindView(id = R.id.textProvinceName)
    private TextView textProvinceName;
    @BindView(id = R.id.textCityName)
    private TextView textCityName;
    @BindView(id = R.id.textStoreLocation)
    private TextView textStoreLocation;
    @BindView(id = R.id.textStoreAddress)
    private TextView textStoreAddress;
    @BindView(id = R.id.textStoreContact)
    private TextView textStoreContact;
    @BindView(id = R.id.textStorePhone)
    private TextView textStorePhone;
    @BindView(id = R.id.textIsSupportTakeOut)
    private TextView textIsSupportTakeOut;
    @BindView(id = R.id.textStoreCode, click = true)
    private TextView textStoreCode;
    @BindView(id = R.id.textJoinContractUrl, click = true)
    private TextView textJoinContractUrl;
    @BindView(id = R.id.textJoinBond)
    private TextView textJoinBond;

    private HashMap<String, String> mapData = new HashMap<>();

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("门店详情");
        MobclickAgent.onResume(aty);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("门店详情");
        MobclickAgent.onPause(aty);
    }

    @Override
    public void initWidget() {
        btnRight.setVisibility(View.GONE);
        textTitle.setText("门店详情");
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mapData = (HashMap<String, String>) bundle.getSerializable("mapData");
            if (mapData != null) {
                textStoreName.setText(mapData.get("StoreName"));
                textStoreAbbreName.setText(mapData.get("StoreAbbreName"));
                textProvinceName.setText(mapData.get("ProvinceName"));
                textCityName.setText(mapData.get("CityName"));
                textStoreLocation.setText(mapData.get("StoreLocation"));
                textStoreAddress.setText(mapData.get("StoreAddress"));
                textStoreContact.setText(mapData.get("StoreContact"));
                textStorePhone.setText(mapData.get("StorePhone"));
                textIsSupportTakeOut.setText("1".equals(mapData.get("IsSupportTakeOut")) ? "支持" : "不支持");
                textJoinBond.setText(mapData.get("JoinBond"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Bundle bundle;
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.textStoreCode:
                if (StringUtils.isEmpty(mapData.get("StoreCodeUrl"))) {
                    toast("数据有误");
                    return;
                }
                bundle = new Bundle();
                bundle.putString("StoreCodeUrl", mapData.get("StoreCodeUrl"));
                bundle.putString("StoreSerialNo", mapData.get("StoreSerialNo"));
                skipActivity(aty, StoreCodeActivity.class, bundle);
                break;
            case R.id.textJoinContractUrl:
                if (StringUtils.isEmpty(mapData.get("JoinContractUrl"))) {
                    toast("您的店铺是线下纸质合同签约，无电子合同");
                    return;
                }
                bundle = new Bundle();
                bundle.putString("ClassUrl", mapData.get("JoinContractUrl"));
                bundle.putString("ClassName", "电子合同");
                showActivity(aty, WebViewActivity.class, bundle);
                break;
        }
    }
}
